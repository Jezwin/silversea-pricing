package com.silversea.aem.components.beans;

/**
 * This is the bean clas to hold lead resubmission related variables
 * 
 */
public class ResubmitLead {
    private String fileName;
    private String type;
    private String dateOfSubmission;
    
    
	public String getFileName() {
		return fileName;
	}
	public String getType() {
		return type;
	}
	public String getDateOfSubmission() {
		return dateOfSubmission;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setDateOfSubmission(String dateOfSubmission) {
		this.dateOfSubmission = dateOfSubmission;
	}
 
}