package com.silversea.ssc.aem.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.silversea.ssc.aem.constants.SSCConstants;
import com.silversea.ssc.aem.services.api.PDFService;
import com.silversea.ssc.aem.utils.SSCUtils;

@SuppressWarnings("serial")
@SlingServlet(selectors = "rendition", methods = "GET", extensions = "pdf", resourceTypes = "sling/servlet/default")
public class PDFRenderServlet extends SlingSafeMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFRenderServlet.class);
	

	@Reference
	private transient PDFService pdfService;

	@Reference
	public Externalizer externalizer;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		// <page-path>.rendition.download.geolocation.userlanguage.cruiseCode.excusrion.pdf
		String[] selectors = request.getRequestPathInfo().getSelectors();
		if(!selectors[selectors.length-1].equalsIgnoreCase("excursion")) {

			if (null != selectors && (selectors.length == 3 || selectors.length == 4 || selectors.length == 5)
					&& StringUtils.isNotEmpty(selectors[1]) && StringUtils.isNotEmpty(selectors[2])) {

				String actionType = selectors[1];
				String geoLocation = StringUtils.upperCase(selectors[2]);
				String ccptCode = StringUtils.EMPTY;
				String cruiseCode = StringUtils.EMPTY;
				// String userLanguage = selectors[3];
				//TODO 5 and 4 if new javascript download implementation
				if (selectors.length == 5) {
					ccptCode = selectors[3];
					cruiseCode = selectors[4];
				} else if (selectors.length == 4) {
					cruiseCode = selectors[3];
				}

				String PDFBody = SSCConstants.PDF_BODY_2018_SELECTOR_FOR_PAGE;
				String PDFHeader = SSCConstants.PDF_HEADER_STYLE1_SELECTOR_FOR_PAGE;
				String PDFFooter = SSCConstants.PDF_FOOTER_STYLE1_SELECTOR_FOR_PAGE;
				LOGGER.debug("The actionType, Geolocation, CCPTCode are :- {} {} {}", actionType, geoLocation, ccptCode);
				invokePDFCreation(request, response, PDFBody, PDFHeader, PDFFooter, actionType, geoLocation, ccptCode,
						cruiseCode);

			}
		} else if(selectors[selectors.length-1].equalsIgnoreCase("excursion")) {
			if (null != selectors && (selectors.length == 3 || selectors.length == 4 || selectors.length == 5 || selectors.length == 6)
					&& StringUtils.isNotEmpty(selectors[1]) && StringUtils.isNotEmpty(selectors[2])) {
	
				String actionType = selectors[1];
				String geoLocation = StringUtils.upperCase(selectors[2]);
				String ccptCode = StringUtils.EMPTY;
				String cruiseCode = StringUtils.EMPTY;
				// String userLanguage = selectors[3];
				//TODO 5 and 4 if new javascript download implementation
				if (selectors.length == 6) {
					ccptCode = selectors[3];
					cruiseCode = selectors[4];
				} else if (selectors.length == 5) {
					cruiseCode = selectors[3];
				}
				
				String PDFBody = SSCConstants.PDF_EXCURSION_BODY_SELECTOR_FOR_PAGE;
				String PDFHeader = SSCConstants.PDF_HEADER_STYLE1_SELECTOR_FOR_PAGE;
				String PDFFooter = SSCConstants.PDF_FOOTER_STYLE1_SELECTOR_FOR_PAGE;
				LOGGER.debug("The actionType, Geolocation, CCPTCode are :- {} {} {}", actionType, geoLocation, ccptCode);
				invokePDFCreation(request, response, PDFBody, PDFHeader, PDFFooter, actionType, geoLocation, ccptCode,
						cruiseCode);
	
			}
		} else if(selectors[selectors.length-1].equalsIgnoreCase("2018")) {
			if (null != selectors && (selectors.length == 3 || selectors.length == 4 || selectors.length == 5 || selectors.length == 6)
					&& StringUtils.isNotEmpty(selectors[1]) && StringUtils.isNotEmpty(selectors[2])) {

				String actionType = selectors[1];
				String geoLocation = StringUtils.upperCase(selectors[2]);
				String ccptCode = StringUtils.EMPTY;
				String cruiseCode = StringUtils.EMPTY;
				// String userLanguage = selectors[3];
				//TODO 5 and 4 if new javascript download implementation
				if (selectors.length == 6) {
					ccptCode = selectors[3];
					cruiseCode = selectors[4];
				} else if (selectors.length == 5) {
					cruiseCode = selectors[3];
				}

				String PDFBody = SSCConstants.PDF_BODY_2018_SELECTOR_FOR_PAGE;
				String PDFHeader = SSCConstants.PDF_HEADER_STYLE1_SELECTOR_FOR_PAGE;
				String PDFFooter = SSCConstants.PDF_FOOTER_STYLE1_SELECTOR_FOR_PAGE;
				LOGGER.debug("The actionType, Geolocation, CCPTCode are :- {} {} {}", actionType, geoLocation, ccptCode);
				invokePDFCreation(request, response, PDFBody, PDFHeader, PDFFooter, actionType, geoLocation, ccptCode,
						cruiseCode);

			}
		}
		else {
			
				LOGGER.warn("Invalid number of selectors.");
		}
	}

	private void invokePDFCreation(SlingHttpServletRequest request, SlingHttpServletResponse response, String PDFBody,
			String PDFHeader, String PDFFooter, String actionType, String geoLocation, String ccptCode,
			String cruiseCode) throws ServletException {
		String path = request.getResource().getPath();
		// filename would contain the selectors as is.
		String fileName = SSCUtils.getSanitizedFilename(request.getRequestURI());
		LOGGER.debug("The filename is :- {} ", fileName);
		try {
			// utilizing selectors here along with externalizers, also URL
			// object could be used.
			File pdfFile = pdfService.createPdf(
					externalizer.publishLink(request.getResourceResolver(), "https", path) + SSCConstants.DOT
							+ PDFBody + SSCConstants.DOT + SSCConstants.SELECTOR_COUNTRY_PREFIX + geoLocation
							+ SSCUtils.isPresentCCPTCode(ccptCode),
					externalizer.publishLink(request.getResourceResolver(), "https", path) + SSCConstants.DOT
							+ PDFHeader + SSCConstants.DOT + SSCConstants.SELECTOR_COUNTRY_PREFIX + geoLocation
							+ SSCUtils.isPresentCCPTCode(ccptCode),
					externalizer.publishLink(request.getResourceResolver(), "https", path) + SSCConstants.DOT
							+ PDFFooter + SSCConstants.DOT + SSCConstants.SELECTOR_COUNTRY_PREFIX + geoLocation
							+ SSCUtils.isPresentCCPTCode(ccptCode),
					fileName);
			// to hide or show pdf loading icon		
			Cookie userCookie = new Cookie("ss_pdf_download-"+cruiseCode, "1");
			userCookie.setMaxAge(1000); 
			response.addCookie(userCookie);
			// for no-crawl, no-follow
			response.setHeader("X-Robots-Tag: noindex, nofollow", "true");
			if (pdfFile != null) {
				response.setContentType("application/pdf");
				response.setContentLength((int) pdfFile.length());
				switch (actionType) {
				case "download":
					LOGGER.debug("Adding download headers.");
					response.setHeader(SSCConstants.CONTENT_DISPOSITION, String.format(SSCConstants.DOWNLOAD_HEADER, pdfFile.getName()));
					break;
				case "print":
					LOGGER.debug("Adding printing headers.");
					response.setHeader(SSCConstants.CONTENT_DISPOSITION, String.format(SSCConstants.PRINT_HEADER, pdfFile.getName()));
					break;
				}
				OutputStream out = response.getOutputStream();
				FileInputStream in = null;
				try{
					in = new FileInputStream(pdfFile);
					byte[] buffer = new byte[4096];
					int length;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
				} catch (Exception e) {
					LOGGER.error(e.getClass().getName()
							+ " Occured in PDFRenderServlet : "
							+ e.getMessage());

						throw new ServletException(e.getClass().getName()
								+ " Occured in PDFRenderServlet : "
								+ e.getMessage(), e);
					} finally {
						if (in != null){
							in.close();	
						}
					}
	

	
			}
		} catch (Exception e) {
			throw new ServletException("Error", e);
		}
		} 
	}
