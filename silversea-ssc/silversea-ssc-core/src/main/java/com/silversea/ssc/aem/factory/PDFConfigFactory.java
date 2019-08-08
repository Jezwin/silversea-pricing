package com.silversea.ssc.aem.factory;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Factory for BASF and PhantomJS Module.
 * 
 * @author 77Agency
 * @version 1.0
 */
@Component(metatype = true, label = "PDF Configuration Factory", enabled = true, immediate = true, description = "PDF configuration factory for storing the configurable parameters in Felix.")
@Service(value = PDFConfigFactory.class)
@Properties({
		@Property(name = "pdf.phantomexe.location", value = "", description = "Phantom JS Exe file location", label = "Phantom JS Exe file location"),
		@Property(name = "pdf.customjs.location", value = "", description = "Phantom JS Custom file location", label = "Phantom JS Custom file location"),
		@Property(name = "pdf.destination.path", value = "", description = "Destination PDF Folder", label = "Destination PDF Folder")
		 })
public class PDFConfigFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PDFConfigFactory.class);

	/** The properties. */
	public static Dictionary<?, ?> properties = null;

	/**
	 * Activate method: re-instantiates the singleton object for config.
	 *
	 * @param componentContext
	 *            the component context
	 */
	@Activate
	protected void activate(final ComponentContext componentContext) {
		LOGGER.info("inside method: activate()");
		properties = componentContext.getProperties();
		LOGGER.info("exiting method: activate()");
	}

	/**
	 * Re-initializes the property map on changes to the config dictionary.
	 * 
	 * @param componentContext
	 */
	@Modified
	protected void modified(final ComponentContext componentContext) {
		LOGGER.info("inside method: modified()");
		properties = componentContext.getProperties();
		LOGGER.info("exiting method: modified()");
	}

	public String getPropertyValue(String key) {
		LOGGER.info("inside method: getStringPropertyValue()");
		String property = (String) PDFConfigFactory.properties.get(key);
		LOGGER.info("exiting method: getStringPropertyValue()");
		return property;
	}

}
