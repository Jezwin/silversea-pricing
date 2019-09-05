package com.silversea.aem.importers.services.impl;


import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.StyleCache;

@Service
@Component
public class StyleCacheImpl implements StyleCache {
	
	static final private Logger LOGGER = LoggerFactory.getLogger(StyleCacheImpl.class);

	private static Map<String, ValueTypeBean> styles;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	public Map<String, ValueTypeBean> getStyles() {
		if (styles == null) {
			buildCache();
		}
		return styles;
	}
	
	public ImportResult buildCache() {
		ImportResult importResult = new ImportResult();
		styles = new HashMap<>();
		Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		LOGGER.debug("Starting create style cache");

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			Resource pageResource = resourceResolver.getResource("/etc/tags/style");
			if(pageResource !=null) {
				Node tagNode = pageResource.adaptTo(Node.class);
				
				if (tagNode == null) {
					importResult.incrementErrorNumber();
					throw new ImporterException("Cannot find node");
				}
				
				Property styleListProp = tagNode.getProperty("styleList");
				
				if (styleListProp.getValues() != null) {
					LOGGER.debug("Check style value");
					ValueTypeBean valueType = null;
					for(Value value : styleListProp.getValues()) {
						String data = value.toString();
						String[] splitStyle = data.split("#");
						valueType = new ValueTypeBean(splitStyle[1], "style");
						styles.put(splitStyle[0], valueType);
					}
				}
				importResult.incrementSuccessNumber();
			}

		} catch (LoginException | ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException e) {
			LOGGER.error("Cannot save modification", e);
		}
		return importResult;
	}
}
