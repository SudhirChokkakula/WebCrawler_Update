package org.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Downloader implements Runnable {
	
	private String absUrlOfMnth = null;
	private Map<String, List<String>> mailUrlsMap = null;
	private WebCrawlerPropertiesBn webCrawlerPropBn;
	private File file;
	private final static Logger logger = Logger.getLogger(Downloader.class);
	private int fileCount;
	private int count;
	private File monthDirectory;
	private Writer writer;
	
	public Downloader(String absUrlOfMnth,
			Map<String, List<String>> mailUrlsMap, 
			File monthDirectory,
			WebCrawlerPropertiesBn webCrawlerPropBn) {
		this.absUrlOfMnth = absUrlOfMnth;
		this.mailUrlsMap = mailUrlsMap;
		this.monthDirectory = monthDirectory;
		this.webCrawlerPropBn = webCrawlerPropBn;
	}

	@Override
	public void run() {
		try {
			downloadMails();
		} catch (IOException ex) {
			logger.error("Exception occured while downloading mails", ex);
		}
	}
	
	public void downloadMails() throws IOException{
		int index = 0;
		String resumeURL = null;
		List<String> listOfUrls;
		List<String> listOfResumeUrls;
			if (isFileExist(webCrawlerPropBn.getResumeFileName())) {
				listOfResumeUrls = readContentFromFile(webCrawlerPropBn.getResumeFileName());
				for(String resumeUrlWithIndex : listOfResumeUrls) {
					String urlAndIndex[] = resumeUrlWithIndex.split(",");
					index = Integer.parseInt(urlAndIndex[0]);
					resumeURL = urlAndIndex[1];
					if(resumeURL.equals(absUrlOfMnth)) {
						fileCount = index;
						break;
					}
				}
			} else {
				monthDirectory.mkdir();
			}
			listOfUrls = mailUrlsMap.get(absUrlOfMnth);
			for (count = fileCount; count < listOfUrls.size(); count++) {
				downloadMail(listOfUrls.get(count), monthDirectory);
			}
			if(isFileExist(webCrawlerPropBn.getResumeFileName())) {
				deleteFile(webCrawlerPropBn.getResumeFileName());
			}
	}
	
	public boolean isFileExist(String fileName) {
		file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	public void deleteFile(String fileName) {
		file = new File(fileName);
		file.delete();
	}

	public List<String> readContentFromFile(String fileName)
			throws IOException {
		List<String> resumedUrlsWithIndex = null;
		try {
		Path path = Paths.get(fileName);
		resumedUrlsWithIndex =  Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			logger.error("Exception occured while reading the content from file "+fileName, ex);
			throw ex;
		}
		return resumedUrlsWithIndex;
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
						writer = new BufferedWriter(new FileWriter(file,true));
						writer.write(Integer.toString(count));
						writer.write(",");
						writer.write(absUrlOfMnth);
						writer.write("\r\n");
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
}
