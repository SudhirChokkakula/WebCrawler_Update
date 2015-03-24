package org.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	private static Map<String,List<String>> urlsMap;
	
	
	@BeforeClass
	public static void setup() {
		context = new ClassPathXmlApplicationContext("spring.xml");
		pageCrawler = (PageCrawler)context.getBean("pageCrawler");
		urlsMap = new LinkedHashMap<String, List<String>>();
		List<String> listofMailUrls = new ArrayList<String>();
		listofMailUrls.add(0, "http://mail-archives.apache.org/mod_mbox/maven-users/201503.mbox/%3c383A5304-FEA1-4032-B712-F771");
		listofMailUrls.add(1,"http://mail-archives.apache.org/mod_mbox/maven-users/201503.mbox/%3c54F366D2.1070206@artifact-software.com");
		urlsMap.put("http://mail-archives.apache.org/mod_mbox/maven-users/201503.mbox", listofMailUrls);
	}
	
	@Test
	public void loadMonthUrlsMapTest() throws IOException {
		Map<String,List<String>> mailUrlsMap = new LinkedHashMap<String, List<String>>();
		mailUrlsMap = pageCrawler.loadMonthUrlsMap("http://mail-archives.apache.org/mod_mbox/maven-users/", mailUrlsMap);
		if(mailUrlsMap.size() != 0) {
			Assert.assertTrue(true);
		}
	}
	@Test
	public void getMailUrlsListTest() throws IOException {
		List<String> mailList = pageCrawler.getMailUrlsList("http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/thread");
		if(!mailList.isEmpty()) {
			Assert.assertEquals(220, mailList.size());
		}
	}
	
	@Test
	public void serializeTheMapTest() throws IOException {
		try {
		pageCrawler.serializeTheMap(urlsMap);
		Assert.assertTrue(true);
		} catch(IOException ex) {
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void deSerializeTheMapTest() throws ClassNotFoundException, IOException {
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
