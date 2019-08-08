package com.silversea.ssc.aem.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.ssc.aem.exceptions.SystemException;
import com.silversea.ssc.aem.factory.PDFConfigFactory;
import com.silversea.ssc.aem.helper.PDFServiceHelper.PDFServiceHelperNested;
import com.silversea.ssc.aem.services.api.PDFService;

/**
 * The Class PdfServiceImpl.
 * 
 * @author 77Agency
 */

@Component(immediate = true, metatype = true)
@Service(value = PDFService.class)
@Properties({ @Property(name = "service.description", value = "Pdf Service"),
		@Property(name = "label", value = "Pdf Service") })
public class PDFServiceImpl implements PDFService {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PDFServiceImpl.class);

	/**
	 * Injecting PDF Configuration Service into this service to fetch Phantom
	 * env specific properties
	 */
	@Reference
	private PDFConfigFactory pdfConfService;

	
	@Reference
	private ResourceResolverFactory rFactory;
	
	/** String to store the path of phantomJS executable file location. */
	private String phantomExeLocation;

	/** String to store the path of rasterize.js file location. */
	private String jsScriptLocation;

	/** String to destination PDF file location. */
	private String destPdfLocation;
	
	/** String to header rendering script. */
	private String headerLocation;

	/** String to footer rendering script. */
	private String footerLocation;
	/**
	 * https sslIgoneErrors .
	 */
	private String sslIgnoreErrors;

	/**
	 * Constant for Phantom exe location
	 */
	public static final String PHANTOM_EXECUTABLE_LOCATION = "pdf.phantomexe.location";

	/**
	 * Constant for Phantom Js Location
	 */
	public static final String JS_SCRIPT_LOCATION = "pdf.customjs.location";

	/**
	 * Constant for Dest Pdf Location
	 */
	public static final String DESTINATION_PDF_LOCATION = "pdf.destination.path";

	/**
	 * 
	 * Constant for SSl Ignore Errors
	 */
	public static final String SSL_IGNORE_ERRORS = "pdf.ignoressl.erros";

	/*
	 * @see com.aem.commonsrelaunch.pdf.services.PDFService#createPdf
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public final File createPdf(String url, String header, String footer, String fileName) throws SystemException {
		LOGGER.debug("The variables URL, Header URL and Fotoer URL are: - {} {} {}", url, header, footer);
		
		InputStreamReader inputStreamReader = null;
		
		if (null != url) {
			try {
				String decodedUrl = URLDecoder.decode(url, "UTF-8");
				Process process;
				initiliazePhantomConfigurations();
				
				String cmdString = this.phantomExeLocation
						+ " --ignore-ssl-errors=true --load-images=true --local-to-remote-url-access=true "
						+ this.jsScriptLocation + " " + decodedUrl
						+ " "
						+ this.destPdfLocation + "/" + fileName + " " + header + " " + footer;
				LOGGER.info("Phantom script command:::::" + cmdString);
				
				//HOTFIX : Security to avoid having too many phantomJS
				LOGGER.info("Currently " + PDFServiceHelperNested.PhantomJSInstanceRunning + " instance of phantomJS are running");
				while(PDFServiceHelperNested.PhantomJSInstanceRunning > 3){
					try {
						LOGGER.info("Wait in queue for free PhantomJS instance");
						Thread.sleep(2000);
						if(PDFServiceHelperNested.LastPhantomCall.plusSeconds(60).isBeforeNow()){
							PDFServiceHelperNested.PhantomJSInstanceRunning = 0;
						}
					} catch (InterruptedException e1) {
						 LOGGER.error("Cannot wait");
					}
				}
				
				PDFServiceHelperNested.LastPhantomCall = DateTime.now();
				PDFServiceHelperNested.PhantomJSInstanceRunning = PDFServiceHelperNested.PhantomJSInstanceRunning + 1;
				process = Runtime.getRuntime().exec(cmdString);
				final boolean exitStatus = process.waitFor(30, TimeUnit.SECONDS);
				PDFServiceHelperNested.PhantomJSInstanceRunning = PDFServiceHelperNested.PhantomJSInstanceRunning - 1;
				if(PDFServiceHelperNested.PhantomJSInstanceRunning < 0){
					PDFServiceHelperNested.PhantomJSInstanceRunning = 0;
				}
				LOGGER.info("exit status : ", exitStatus);
				inputStreamReader = new InputStreamReader(
						process.getInputStream());
				final BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String currentLine = null;
				final StringBuilder stringBuilder = new StringBuilder(
						exitStatus ? "SUCCESS:" : "ERROR:");
				currentLine = bufferedReader.readLine();
				while (currentLine != null) {
					stringBuilder.append(currentLine);
					currentLine = bufferedReader.readLine();
				}
				bufferedReader.close();
				inputStreamReader.close();
				
				try {
				Thread.sleep(1000);
				process.destroy();
				Thread.sleep(1000);
				if(process.isAlive()) {
					process.destroyForcibly();
					process.waitFor();
				} 
				}catch (Exception e) {
					LOGGER.error(e.getClass().getName()
							+ " Occured in process destroy in PDF Service: "
							+ e.getMessage());
					}
				
			} catch (IOException e) {
				LOGGER.error(e.getClass().getName()
						+ " Occured in create file in PDF Service: "
						+ e.getMessage());

				throw new SystemException(e.getClass().getName()
						+ " Occurred in create file in PDF Service: "
						+ e.getMessage(), e);
			} catch (Exception e) {
				LOGGER.error(e.getClass().getName()
						+ " Occured in create file in PDF Service: "
						+ e.getMessage());

				throw new SystemException(e.getClass().getName()
						+ " Occurred in create file in PDF Service: "
						+ e.getMessage(), e);
			}

			final File file = new File(this.destPdfLocation + "/" + fileName); 
			return file;
		}

		return null;
	}

	private void initiliazePhantomConfigurations() {

		this.phantomExeLocation = pdfConfService
				.getPropertyValue(PHANTOM_EXECUTABLE_LOCATION);
		LOGGER.debug("Phantom Exe location::::" + phantomExeLocation);
		this.jsScriptLocation = pdfConfService
				.getPropertyValue(JS_SCRIPT_LOCATION);
		LOGGER.debug("Rasterize location::::" + jsScriptLocation);
		this.destPdfLocation = pdfConfService
				.getPropertyValue(DESTINATION_PDF_LOCATION);
		LOGGER.debug("Dest Pdf Location::::" + destPdfLocation);
	}
	
	

	/**
	 * Gets the stores the path of phantomJS executable file location.
	 * 
	 * @return String
	 */
	public final String getPhantomExeLocation() {
		return phantomExeLocation;
	}

	/**
	 * Gets the stores the path of rasterize.
	 * 
	 * @return String
	 */
	public final String getJsScriptLocation() {
		return jsScriptLocation;
	}

	/**
	 * Gets the destination file location.
	 * 
	 * @return String
	 */
	public final String getDestPdfLocation() {
		return destPdfLocation;
	}

	/**
	 * Gets the https sslIgoneErrors .
	 * 
	 * @return the https sslIgoneErrors
	 */
	public String getSslIgnoreErrors() {
		return sslIgnoreErrors;
	}

	public String getHeaderLocation() {
		return headerLocation;
	}

	public void setHeaderLocation(String headerLocation) {
		this.headerLocation = headerLocation;
	}

	public String getFooterLocation() {
		return footerLocation;
	}

	public void setFooterLocation(String footerLocation) {
		this.footerLocation = footerLocation;
	}

}
