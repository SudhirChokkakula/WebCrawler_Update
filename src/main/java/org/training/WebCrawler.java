package org.training;

import java.io.IOException;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WebCrawler {

	private static Scanner sc;
	
	public static void main(String args[]) throws IOException {
		sc = new Scanner(System.in);
		System.out.println("Enter the year :");
		String year = sc.next();
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		MailDownloader mailDownloader = (MailDownloader)context.getBean("mailDownloader");
		mailDownloader.connectToPageAndDownloadMails(year);
	}
}
