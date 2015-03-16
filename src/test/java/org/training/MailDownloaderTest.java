package org.training;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.javatuples.Pair;
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
		} else {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void connectPageToDownloadMailTest() throws IOException {
		String mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201501.mbox/%3cCAAZU44=hcLViyKRupH0nSg5styj650rjWFoqmkVmVA6b9FYeuA@mail.gmail.com%3e";
		Document document = mailDownloader.connectPageToDownloadMail(mailUrl);
		if(document != null) {
			Assert.assertTrue(true);
		} else {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void downloadMailTest() {
		String mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201501.mbox/%3cCAAZU44=hcLViyKRupH0nSg5styj650rjWFoqmkVmVA6b9FYeuA@mail.gmail.com%3e";
		File file = new File("D:\\Work\\");
		try {
		mailDownloader.downloadMail(mailUrl, file);
		Assert.assertTrue(true);
		} catch (Exception ex) {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void isFileExistTest() {
		boolean isFileExist = mailDownloader.isFileExist(webCrawlerPropBn.getResumeFileName());
		if(isFileExist) {
			Assert.assertTrue(true);
		} else {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void readContentFromFileTest() throws IOException {
		Pair<Integer,String> indexAndUrl = mailDownloader.readContentFromFile(webCrawlerPropBn.getResumeFileName());
		if(indexAndUrl != null) {
			Assert.assertTrue(true);
		} else {
			Assert.assertFalse(true);
		}
	}

}
