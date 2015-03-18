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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MailDownloader {

	private final static Logger logger = Logger.getLogger(MailDownloader.class);
	private WebCrawlerPropertiesBn webCrawlerPropBn;
	private Map<String, List<String>> mailUrlsMap;

	public WebCrawlerPropertiesBn getWebCrawlerPropBn() {
		return webCrawlerPropBn;
	}

	public void setWebCrawlerPropBn(WebCrawlerPropertiesBn webCrawlerPropBn) {
		this.webCrawlerPropBn = webCrawlerPropBn;
	}

	public void init() throws IOException, ClassNotFoundException {
		mailUrlsMap = new LinkedHashMap<String, List<String>>();
		File file = new File(webCrawlerPropBn.getSerializeFileName());
		if (file.exists()) {
			mailUrlsMap = getListOfUrls();
		} else {
			mailUrlsMap = loadMonthUrlsMap(webCrawlerPropBn.getPageUrl(),
					mailUrlsMap);
			setListOfUrls(mailUrlsMap);
		}
	}

	public Map<String, List<String>> loadMonthUrlsMap(String URL,
			Map<String, List<String>> mailUrlsMap) throws IOException {

		Document document = connectToPageToGetUrls(URL);
		Elements els = document.select("#grid").get(0)
				.getElementsByAttributeValueContaining("href", "thread");
		for (Element el : els) {
			String absUrlOfMonth = el.absUrl("href");
			mailUrlsMap.put(absUrlOfMonth, getMailList(absUrlOfMonth));
		}
		return mailUrlsMap;
	}

	public List<String> getMailList(String absUrlOfMnth) throws IOException {
		
		Document document = connectToPageToGetUrls(absUrlOfMnth);
		List<String> listOfUrls = new ArrayList<String>();
		int index = 0;
		int numberOfPages = document.select("#msglist > thead > tr > th")
				.get(1).getElementsByTag("a").size();
		if (numberOfPages == 0) {
			numberOfPages = 1;
		}
		for (int j = 0; j < numberOfPages; j++) {
			document = connectToPageToGetUrls(absUrlOfMnth + "?" + j);
			Elements elementsofMails = document.select("#msglist > tbody")
					.get(0).getElementsByTag("a");
			for (Element eleofMails : elementsofMails) {
				listOfUrls.add(index++, eleofMails.absUrl("href"));
			}
		}
		return listOfUrls;
	}

	public Map<String, List<String>> getListOfUrls() throws IOException,
			ClassNotFoundException {
		Map<String, List<String>> storedMap = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(webCrawlerPropBn.getSerializeFileName());
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

	public void setListOfUrls(Map<String, List<String>> listOfUrls)
			throws IOException {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(webCrawlerPropBn.getSerializeFileName());
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

	public void downloadMailsForYear(String year) throws IOException {
		Document document;
		Elements elementsofMonths;
		String absUrlofMonth;

		document = connectToPageToGetUrls(webCrawlerPropBn.getPageUrl());
		elementsofMonths = document.getElementsByAttributeValueContaining(
				"href", year);
		new File(webCrawlerPropBn.getDestination() + year).mkdirs();
		int monthcounter = (elementsofMonths.size() / 3) + 1;
		
		for (Element eleofMonths : elementsofMonths) {
			absUrlofMonth = eleofMonths.absUrl("href");
			if (absUrlofMonth.contains(year)
					&& absUrlofMonth.contains("thread")) {
				monthcounter--;
				File monthDirectory = new File(
						webCrawlerPropBn.getDestination() + year + "\\"
								+ (monthcounter));
				Downloader downlader = new Downloader(absUrlofMonth,
						mailUrlsMap, monthDirectory, webCrawlerPropBn);
				Thread t = new Thread(downlader);
				t.start();
			}
		}
	}

	public Document connectToPageToGetUrls(String url) throws IOException {
		Document document = null;
		int tryCount = 0;
		while (true) {
			try {
				logger.debug("Connecting to :" + url);
				document = Jsoup.connect(url).get();
				logger.debug("Connected to :" + url);
				break;
			} catch (IOException ex) {
				logger.error("Exception occured while connecting to " + url, ex);
				logger.debug("Retrying to connect to: " + url);
				if (tryCount++ == webCrawlerPropBn.getNumberOfRetries()) {
					logger.debug("Retried to connect " + tryCount
							+ "no.of times. But not able to connect.");
					throw ex;
				}
			}
		}
		return document;
	}
}
