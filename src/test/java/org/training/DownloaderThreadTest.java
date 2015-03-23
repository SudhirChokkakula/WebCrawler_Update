package org.training;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DownloaderThreadTest {
	
	private static ApplicationContext context = null;
	private static CrawlerPropertiesBn crawlerPropBn;
	private static DownloaderThread downloader;
	private static ConnectPageToCrawl connectPageToCrawl;
	
	@BeforeClass
	public static void setup() throws ClassNotFoundException, IOException {
		context = new ClassPathXmlApplicationContext("spring.xml");
		downloader = (DownloaderThread) context.getBean("downloaderThread");
		connectPageToCrawl = (ConnectPageToCrawl) context.getBean("connectPageToCrawl");
		crawlerPropBn = (CrawlerPropertiesBn) context.getBean("crawlerPropBn");
		File file = new File("D:\\Work\\");
		downloader.setMonthDirectory(file);
		downloader.setCrawlerPropBn(crawlerPropBn);
		downloader.setConnectPageToCrawl(connectPageToCrawl);
	}
	
	@Test
	public void connectPageToDownloadMailTest() throws IOException {
		String mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201501.mbox/%3cCAAZU44=hcLViyKRupH0nSg5styj650rjWFoqmkVmVA6b9FYeuA@mail.gmail.com%3e";
		Document document = downloader.connectPageToDownloadMail(mailUrl);
		if(document != null) {
			Assert.assertTrue(true);
		} 
	}
	
	@Test
	public void downloadMailTest() {
		String mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201501.mbox/%3cCAAZU44=hcLViyKRupH0nSg5styj650rjWFoqmkVmVA6b9FYeuA@mail.gmail.com%3e";
		File file = new File("D:\\Work\\");
		try {
			downloader.downloadMail(mailUrl, file);
		Assert.assertTrue(true);
		} catch (Exception ex) {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void isFileExistTest() {
		boolean isFileExist = downloader.isFileExist(crawlerPropBn.getResumeFileName());
		if(isFileExist) {
			Assert.assertTrue(true);
		} 
	}
	
	@Test
	public void readContentFromFileTest() throws IOException {
		boolean isFileExist = downloader.isFileExist(crawlerPropBn.getResumeFileName());
		if(isFileExist) {
		List<String> indexAndUrl = downloader.readContentFromFile(crawlerPropBn.getResumeFileName());
		if(!indexAndUrl.isEmpty()) {
			Assert.assertTrue(true);
		}
		}
	}
	
	@Test
	public void deleteFileTest() {
		boolean isFileExist = downloader.isFileExist(crawlerPropBn.getResumeFileName());
		if(isFileExist) {
		downloader.deleteFile(crawlerPropBn.getResumeFileName());
		Assert.assertTrue(true);
		}
	}


}
