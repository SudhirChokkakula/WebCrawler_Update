package org.training;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {

	private static Scanner sc;
	private static ApplicationContext context;
	private static PageCrawler pageCrawler;
	private static String year;

	public static void main(String args[]) throws IOException,
			ClassNotFoundException {
		sc = new Scanner(System.in);
		System.out.println("Enter the year :");
		year = sc.next();
		context = new ClassPathXmlApplicationContext(
				"spring.xml");
		pageCrawler = (PageCrawler) context.getBean("pageCrawler");
		
		callDownloaderThread();
		
	}
	
	private static void callDownloaderThread() throws IOException, ClassNotFoundException {
		String absUrlofMonth;
		Map<String, List<String>> mailUrlsMap;
		mailUrlsMap = pageCrawler.deSerializeTheMap();
		Elements elementsofMonths = pageCrawler.getMonthUrlsForYear(year);
		CrawlerPropertiesBn crawlerPropBn = pageCrawler.getCrawlerPropBn();
		new File(crawlerPropBn.getDestination() + year).mkdirs();

		int monthcounter = (elementsofMonths.size() / 3) + 1;

		for (Element eleofMonths : elementsofMonths) {
			absUrlofMonth = eleofMonths.absUrl("href");
			if (absUrlofMonth.contains(year)
					&& absUrlofMonth.contains("thread")) {
				monthcounter--;
				File monthDirectory = new File(
						crawlerPropBn.getDestination() + year + "\\"
								+ (monthcounter));
				
				DownloaderThread downloader = (DownloaderThread) context.getBean("downloaderThread");
				downloader.setAbsUrlOfMnth(absUrlofMonth);
				downloader.setMailUrlsMap(mailUrlsMap);
				downloader.setMonthDirectory(monthDirectory);
				downloader.setCrawlerPropBn(crawlerPropBn);
				
				Thread t = new Thread(downloader);
				t.start();
			}
		}
	}
}
