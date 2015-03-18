package org.training;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MailDownloaderTest {
	
	private static ApplicationContext context = null;
	private static MailDownloader mailDownloader = null;
	private static WebCrawlerPropertiesBn webCrawlerPropBn;
	
	@BeforeClass
	public static void setup() {
		context = new ClassPathXmlApplicationContext("spring.xml");
		mailDownloader = (MailDownloader)context.getBean("mailDownloader");
		webCrawlerPropBn = mailDownloader.getWebCrawlerPropBn();
	}
	
	@Test
	public void getMailListTest() throws IOException {
		List<String> mailList = mailDownloader.getMailList("http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/thread");
		if(!mailList.isEmpty()) {
			Assert.assertEquals(220, mailList.size());
		}
	}
	
	@Test
	public void getListOfUrlsTest() throws ClassNotFoundException, IOException {
		Map<String,List<String>> urlsMap = mailDownloader.getListOfUrls();
		if(!urlsMap.isEmpty()) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void downloadMailsForYearTest() {
		try {
		mailDownloader.downloadMailsForYear("2015");
		Assert.assertTrue(true);
		} catch (IOException io) {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void connectToPageToGetUrlsTest() throws IOException {
		Document document = mailDownloader.connectToPageToGetUrls(webCrawlerPropBn.getPageUrl());
		if(document != null) {
			Assert.assertTrue(true);
		}
	}
	
}
