package org.training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MailDownloader {

	private int fileCount;
	private final static Logger logger = Logger.getLogger(MailDownloader.class);
	private File file;
	private Writer writer;
	private int count;
	private String absUrlofMonth;
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
		File file = new File(webCrawlerPropBn.getDestination()
				+ webCrawlerPropBn.getSerializeFileName());
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
			fileIn = new FileInputStream(webCrawlerPropBn.getDestination()
					+ webCrawlerPropBn.getSerializeFileName());
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
			fileOut = new FileOutputStream(webCrawlerPropBn.getDestination()
					+ webCrawlerPropBn.getSerializeFileName());
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
		Pair<Integer, String> indexAndUrl = null;
		List<String> listOfUrls;
		int index = 0;
		String resumeURL = null;
		boolean resumeFlag = false;

		if (isFileExist(webCrawlerPropBn.getResumeFileName())) {
			indexAndUrl = readContentFromFile(webCrawlerPropBn.getResumeFileName());
			index = indexAndUrl.getValue0();
			resumeURL = indexAndUrl.getValue1();
			resumeFlag = true;
		}			
			document = connectToPageToGetUrls(webCrawlerPropBn.getPageUrl());
			elementsofMonths = document.getElementsByAttributeValueContaining("href", year);
			new File(webCrawlerPropBn.getDestination() + year).mkdir();
			int monthcounter = (elementsofMonths.size() / 3) + 1;

			for (Element eleofMonths : elementsofMonths) {
				absUrlofMonth = eleofMonths.absUrl("href");
				if (absUrlofMonth.contains(year) && absUrlofMonth.contains("thread")) {
					monthcounter--;
					fileCount = 0;
					File monthDirectory = new File(webCrawlerPropBn.getDestination() + year + "\\" + (monthcounter));					
					if (resumeFlag) {
						if (resumeURL.equals(absUrlofMonth)) {
							fileCount = index;
							resumeFlag = false;					
						} else {
							continue;
						}
					}
					else {
						monthDirectory.mkdir();
					}					
					listOfUrls = mailUrlsMap.get(absUrlofMonth);
					for (count = fileCount; count < listOfUrls.size(); count++) {
						downloadMail(listOfUrls.get(count), monthDirectory);
					}
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

	public Document connectPageToDownloadMail(String url) throws IOException {
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
					try {
						file = new File(webCrawlerPropBn.getResumeFileName());
						writer = new BufferedWriter(new FileWriter(file));
						writer.write(Integer.toString(count));
						writer.write("\r\n");
						writer.write(absUrlofMonth);
					} catch (IOException io) {
						logger.error(
								"Exception occured while writing the content in to file",
								io);
					} finally {
						writer.close();
					}
					throw ex;
				}
			}
		}
		return document;
	}

	public void downloadMail(String absUrlofMail, File monthDirectory)
			throws IOException {
		Element message = null;
		Document document = connectPageToDownloadMail(absUrlofMail);
		message = document.select("#msgview > tbody").get(0);
		file = new File(monthDirectory.getAbsoluteFile() + "\\" + "File-"
				+ (fileCount++) + ".txt");
		try {
			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			writer.write(message.text());
		} catch (IOException ex) {
			logger.error(
					"Exception occured while writing message in to the file",
					ex);
			throw ex;
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				logger.error("Exception occured while closing the writer", ex);
				throw ex;
			}
		}
	}

	public boolean isFileExist(String fileName) {
		file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public Pair<Integer, String> readContentFromFile(String fileName)
			throws IOException {
		file = new File(fileName);
		BufferedReader reader = null;
		String resumedUrl = null;
		Pair<Integer, String> indexAndUrl = null;
		if (file.exists()) {
			try {
				reader = new BufferedReader(new FileReader(file));
				int index = Integer.parseInt(reader.readLine());
				resumedUrl = reader.readLine();
				indexAndUrl = Pair.with(index, resumedUrl);
			} catch (IOException ex) {
				logger.error(
						"Exception occured while reading the content from file "
								+ fileName, ex);
				throw ex;
			} finally {
				try {
					reader.close();
				} catch (IOException ex) {
					logger.error(
							"Exception occured while closing the resource reader",
							ex);
					throw ex;
				}
			}
			file.delete();
		}
		return indexAndUrl;
	}
}
