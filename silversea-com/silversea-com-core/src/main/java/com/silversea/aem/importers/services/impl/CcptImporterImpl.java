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

@Service
@Component
public class CcptImporterImpl implements CcptImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CcptImporterImpl.class);

	private static String PATH_TAGS_CCPT = "/etc/tags/ccpt";
	private static String CCPT_INFO = "ccptInfo";
	private static String CCPT_CSV_DATA = "ccptCSVData";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public ImportResult importAllItems() {
		List<String> ccptInfo = new ArrayList<>();
		int successNumber = 0;
		int errorNumber = 0;
		
		Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		LOGGER.debug("Starting ccpt import");

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			Session session = resourceResolver.adaptTo(Session.class);
			Resource pageResource = resourceResolver.getResource(PATH_TAGS_CCPT);
			Node tagNode = pageResource.adaptTo(Node.class);

			if (tagNode == null) {
				throw new ImporterException("Cannot find node");
			}

			Property ccptCSVDataProp = tagNode.getProperty(CCPT_CSV_DATA);
			
			if (ccptCSVDataProp.getValue() != null) {
				LOGGER.debug("Check ccpt value");
				String[] ccptCSVData = ccptCSVDataProp.getValue().toString().split("\n");
				// check correct format 34:William Laracuente~+1-954-468-3020~WilliamL@silversea.com
				for (String data : ccptCSVData) {
					boolean inserCCpt = false;
					String[] splitById = data.split(":");
					if (splitById != null && splitById.length > 0) {
						String[] splitOtherField = splitById[1].split("~");
						if (splitOtherField.length > 2) {
							inserCCpt = StringUtils.isNotEmpty(splitById[0])
									&& StringUtils.isNotEmpty(splitOtherField[0])
									&& StringUtils.isNotEmpty(splitOtherField[1])
									&& StringUtils.isNotEmpty(splitOtherField[2]);
						}
					}
					if (inserCCpt) {
						ccptInfo.add(data);
					}
				}
				if (ccptInfo.size() > 0) {
					String[] stringsValue = ccptInfo.stream().toArray(String[]::new);
					tagNode.setProperty(CCPT_INFO, stringsValue);
				} else {
					LOGGER.debug("No value saved inside ccptInfo");
				}
				successNumber = ccptInfo.size();
				errorNumber = ccptCSVData.length - ccptInfo.size();

				if (session.hasPendingChanges()) {
					try {
						session.save();
						LOGGER.info("Insert {} in ccptInfo and {} no insert", successNumber, errorNumber);
					} catch (RepositoryException e) {
						session.refresh(false);
					}
				} else {
					LOGGER.info("No updating ccptInfo");
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
