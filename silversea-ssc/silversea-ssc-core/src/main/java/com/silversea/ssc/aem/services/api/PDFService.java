package com.silversea.ssc.aem.services.api;

import java.io.File;

public interface PDFService {

	
	/**
	 * This method creates a pdf file for the url passed.
	 * 
	 * @param url
	 * @returns the pdf file created
	 */
	public File createPdf(String page, String header, String footer, String fileName);

}
