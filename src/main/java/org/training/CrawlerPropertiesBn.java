package org.training;

import java.io.Serializable;

public class CrawlerPropertiesBn implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String pageUrl;
	private int numberOfRetries;
	private String resumeFileName;
	private String destination;
	private String serializeFileName;
	
	public String getSerializeFileName() {
		return serializeFileName;
	}

	public void setSerializeFileName(String serializeFileName) {
		this.serializeFileName = serializeFileName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getNumberOfRetries() {
		return numberOfRetries;
	}

	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

	public String getResumeFileName() {
		return resumeFileName;
	}

	public void setResumeFileName(String resumeFileName) {
		this.resumeFileName = resumeFileName;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

}
