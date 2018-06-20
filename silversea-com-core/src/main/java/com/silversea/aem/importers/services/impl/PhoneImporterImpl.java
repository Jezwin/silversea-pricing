package com.silversea.aem.importers.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CcptImporter;
import com.silversea.aem.importers.services.PhoneImporter;

@Service
@Component
public class PhoneImporterImpl implements PhoneImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(PhoneImporterImpl.class);

	private static String PATH_TAGS_PHONE = "/etc/tags/phone";
	private static String PHONE_CSV_DATA = "phoneCSVData";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public ImportResult importAllItems() {
		int successNumber = 0;
		int errorNumber = 0;
		
		Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		LOGGER.debug("Starting phone import");

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			Session session = resourceResolver.adaptTo(Session.class);
			Resource pageResource = resourceResolver.getResource(PATH_TAGS_PHONE);
			if(pageResource == null) {
				throw new ImporterException("Cannot find pageResource");
			}
			
			Node tagNode = pageResource.adaptTo(Node.class);

			if (tagNode == null) {
				throw new ImporterException("Cannot find node");
			}

			Property phoneCSVDataProp = tagNode.getProperty(PHONE_CSV_DATA);
			
			if (phoneCSVDataProp.getValue() != null) {
				LOGGER.debug("Check phone value");
				String[] phoneCSVData = phoneCSVDataProp.getValue().toString().split("\n");
				// check correct format US:generic~06689215884
				for (String data : phoneCSVData) {
					boolean insertPhone = false;
					String[] splitById = data.split(":");
					if (splitById != null && splitById.length > 0) {
						String[] splitOtherField = splitById[1].split("~");
						if (splitOtherField.length > 0) {
							insertPhone = StringUtils.isNotEmpty(splitById[0])
									&& StringUtils.isNotEmpty(splitOtherField[0])
									&& StringUtils.isNotEmpty(splitOtherField[1]);
							
							if (insertPhone) {
								if(!tagNode.hasNode(splitById[0])) {
									tagNode.addNode(splitById[0]);
								}
								
								Node countryNode = tagNode.getNode(splitById[0]);
								
								countryNode.setProperty(splitOtherField[0], splitOtherField[1]);
								
								successNumber++;
							}else {
								
								errorNumber++;
							}
						}
					}					
				}				

				if (session.hasPendingChanges()) {
					try {
						session.save();
						LOGGER.info("Insert {} in phone and {} no insert", successNumber, errorNumber);
					} catch (RepositoryException e) {
						session.refresh(false);
					}
				} else {
					LOGGER.info("No updating Phone");
				}
			}

		} catch (LoginException | ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (RepositoryException e) {
			LOGGER.error("Cannot save modification", e);
		}

		return new ImportResult(successNumber, errorNumber);
	}

}
