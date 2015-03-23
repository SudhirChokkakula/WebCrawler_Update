package org.training;

import java.io.IOException;

import junit.framework.Assert;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConnectPageToCrawlTest {
	
	private static ApplicationContext context = null;
	private static CrawlerPropertiesBn crawlerPropBn;
	private static ConnectPageToCrawl connectPageToCrawl;
	
	@BeforeClass
	public static void setup() {
		context = new ClassPathXmlApplicationContext("spring.xml");
		connectPageToCrawl = (ConnectPageToCrawl) context.getBean("connectPageToCrawl");
		crawlerPropBn = (CrawlerPropertiesBn) context.getBean("crawlerPropBn");
		connectPageToCrawl.setPageCrawlerPropertiesBn(crawlerPropBn);
	}
	
	@Test
	public void connectToPageTest() throws IOException {
		Document document = connectPageToCrawl.connectToPage(crawlerPropBn.getPageUrl());
		if(document != null) {
			Assert.assertTrue(true);
		}
	}

}
