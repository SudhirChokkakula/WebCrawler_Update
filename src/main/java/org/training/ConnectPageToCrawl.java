package org.training;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectPageToCrawl {
	
	private final static Logger logger = Logger.getLogger(ConnectPageToCrawl.class);
	
	@Autowired
	private CrawlerPropertiesBn pageCrawlerPropertiesBn;
	
	public CrawlerPropertiesBn getPageCrawlerPropertiesBn() {
		return pageCrawlerPropertiesBn;
	}

	public void setPageCrawlerPropertiesBn(
			CrawlerPropertiesBn pageCrawlerPropertiesBn) {
		this.pageCrawlerPropertiesBn = pageCrawlerPropertiesBn;
	}

	public Document connectToPage(String url) throws IOException {
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
				if (tryCount++ == pageCrawlerPropertiesBn.getNumberOfRetries()) {
					logger.debug("Retried to connect " + tryCount
							+ "no.of times. But not able to connect.");
					throw ex;
				}
			}
		}
		return document;
	}

}
