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
 * General Configuration Factory for SSC site.
 * 
 * @author 77Agency
 * @version 1.0
 */
@Component(metatype = true, label = "SSC - General Configuration Factory", enabled = true, immediate = true, description = "General configuration factory for storing the SSC wide configurable parameters in Felix.")
@Service(value = SSCConfigFactory.class)
@Properties({
		@Property(name = "port.genericblock.config.path", value = "", description = "Generic Block configurations path for port template", label = "Generic Block configurations path"),
		@Property(name = "tags.brochuregroup.location", value = "", description = "Brochure group Tags Path", label = "Brochure group Tags Path")
		
		 })
public class SSCConfigFactory {

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
		String property = (String) properties.get(key);
		LOGGER.info("exiting method: getStringPropertyValue()");
		return property;
	}

}
