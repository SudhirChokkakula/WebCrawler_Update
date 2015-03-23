package org.training;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PageCrawlerTest {
	
	private static ApplicationContext context = null;
	private static PageCrawler pageCrawler = null;
	
	
	@BeforeClass
	public static void setup() {
		context = new ClassPathXmlApplicationContext("spring.xml");
		pageCrawler = (PageCrawler)context.getBean("pageCrawler");
	}
	
	@Test
	public void getMailListTest() throws IOException {
		List<String> mailList = pageCrawler.getMailUrlsList("http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/thread");
		if(!mailList.isEmpty()) {
			Assert.assertEquals(220, mailList.size());
		}
	}
	
	@Test
	public void getListOfUrlsTest() throws ClassNotFoundException, IOException {
		Map<String,List<String>> urlsMap = pageCrawler.deSerializeTheMap();
		if(!urlsMap.isEmpty()) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void getMonthUrlsForYearTest() {
		try {
			Elements els = pageCrawler.getMonthUrlsForYear("2015");
			if (!els.isEmpty()) {
				Assert.assertTrue(true);
			}
		} catch (IOException io) {
			Assert.assertFalse(true);
		}
	}
}
