package org.training;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

public class PageCrawler {

	private final static Logger logger = Logger.getLogger(PageCrawler.class);
	private CrawlerPropertiesBn crawlerPropBn;
	private Map<String, List<String>> mailUrlsMap;
	
	@Autowired
	private ConnectPageToCrawl connectPageToCrawl;
	
	public ConnectPageToCrawl getConnectPageToCrawl() {
		return connectPageToCrawl;
	}

	public void setConnectPageToCrawl(ConnectPageToCrawl connectPageToCrawl) {
		this.connectPageToCrawl = connectPageToCrawl;
	}

	public CrawlerPropertiesBn getCrawlerPropBn() {
		return crawlerPropBn;
	}

	public void setCrawlerPropBn(CrawlerPropertiesBn crawlerPropBn) {
		this.crawlerPropBn = crawlerPropBn;
	}

	public void init() throws IOException, ClassNotFoundException {
		mailUrlsMap = new LinkedHashMap<String, List<String>>();
		File file = new File(crawlerPropBn.getSerializeFileName());
		if (!file.exists()) {
			mailUrlsMap = loadMonthUrlsMap(crawlerPropBn.getPageUrl(),
					mailUrlsMap);
			serializeTheMap(mailUrlsMap);
		}
	}

	public Map<String, List<String>> loadMonthUrlsMap(String URL,
			Map<String, List<String>> mailUrlsMap) throws IOException {

		Document document = connectPageToCrawl.connectToPage(URL);
		Elements els = document.select("#grid").get(0)
				.getElementsByAttributeValueContaining("href", "thread");
		for (Element el : els) {
			String absUrlOfMonth = el.absUrl("href");
			mailUrlsMap.put(absUrlOfMonth, getMailUrlsList(absUrlOfMonth));
		}
		return mailUrlsMap;
	}

	public List<String> getMailUrlsList(String absUrlOfMnth) throws IOException {
		
		Document document = connectPageToCrawl.connectToPage(absUrlOfMnth);
		List<String> listOfUrls = new ArrayList<String>();
		int index = 0;
		int numberOfPages = document.select("#msglist > thead > tr > th")
				.get(1).getElementsByTag("a").size();
		if (numberOfPages == 0) {
			numberOfPages = 1;
		}
		for (int j = 0; j < numberOfPages; j++) {
			document = connectPageToCrawl.connectToPage(absUrlOfMnth + "?" + j);
			Elements elementsofMails = document.select("#msglist > tbody")
					.get(0).getElementsByTag("a");
			for (Element eleofMails : elementsofMails) {
				listOfUrls.add(index++, eleofMails.absUrl("href"));
			}
		}
		return listOfUrls;
	}

	public Map<String, List<String>> deSerializeTheMap() throws IOException,
			ClassNotFoundException {
		Map<String, List<String>> storedMap = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(crawlerPropBn.getSerializeFileName());
			in = new ObjectInputStream(fileIn);
			storedMap = (Map<String, List<String>>) in.readObject();
		} catch (IOException ex) {
			logger.error("Exception occured while De-Serializing the file", ex);
			throw ex;
		} catch (ClassNotFoundException cfe) {
			logger.error("Exception occured while De-Serializing the file", cfe);
			throw cfe;
		} finally {
			try {
				in.close();
				fileIn.close();
			} catch (IOException ex) {
				logger.error(
						"Exception occured while closing the resources in De-Serialization part",
						ex);
				throw ex;
			}
		}
		return storedMap;
	}

	public void serializeTheMap(Map<String, List<String>> listOfUrls)
			throws IOException {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(crawlerPropBn.getSerializeFileName());
			out = new ObjectOutputStream(fileOut);
			out.writeObject(listOfUrls);
		} catch (IOException ex) {
			logger.error("Exception occured while serializing the Map", ex);
			throw ex;
		} finally {
			try {
				out.close();
				fileOut.close();
			} catch (IOException ex) {
				logger.error(
						"Exception occured while closing the resources in Serialization part",
						ex);
				throw ex;
			}
		}
	}

	public Elements getMonthUrlsForYear(String year) throws IOException {
		Document document;
		Elements elementsofMonths;
		document = connectPageToCrawl.connectToPage(crawlerPropBn.getPageUrl());
		elementsofMonths = document.getElementsByAttributeValueContaining(
				"href", year);
		return elementsofMonths;
	}
}
