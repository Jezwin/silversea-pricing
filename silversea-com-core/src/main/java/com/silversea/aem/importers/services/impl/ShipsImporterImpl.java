package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ShipsImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Ship importer", metatype = true)
@Service(value = ShipsImporter.class)
public class ShipsImporterImpl implements ShipsImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ShipsImporterImpl.class);

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private ApiCallService apiCallService;

	private ResourceResolver resourceResolver;
	private PageManager pageManager;
	private Session session;

	public void init() {
		try {
			Map<String, Object> authenticationPrams = new HashMap<String, Object>();
			authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			session = resourceResolver.adaptTo(Session.class);
		} catch (LoginException e) {
			LOGGER.debug("Ships importer login exception ", e);
		}
	}

	@Override
	public void importData() throws IOException {
		init();
		LOGGER.debug("Début de l'import");

		try {

			if (apiConfig.getSessionRefresh() != 0) {
				sessionRefresh = apiConfig.getSessionRefresh();
			}

			Page shipsRootPage = pageManager.getPage(apiConfig.apiRootPath("shipUrl"));

			List<Ship> listShips;
			listShips = apiCallService.getShips();
			int i = 0;
			for (Ship ship : listShips) {
				try {
					Iterator<Resource> resources = resourceResolver.findResources(
							"//element(*,cq:Page)[jcr:content/shipId=\"" + ship.getShipId() + "\"]", "xpath");

					List<Page> shipPages = new ArrayList<Page>();
					List<String> local = new ArrayList<>();
					local = ImporterUtils.finAllLanguageCopies(resourceResolver);
					if (resources.hasNext()) {
						shipPages = ImporterUtils.findPagesById(resources);
					} else {
						shipPages = ImporterUtils.createPagesALLLanguageCopies(pageManager, resourceResolver,
								shipsRootPage, local, TemplateConstants.PATH_SHIP, ship.getShipName());
					}

					for (Page shiPage : shipPages) {
						if (shiPage != null) {
							Node shipPageContentNode = shiPage.getContentResource().adaptTo(Node.class);
							if (shipPageContentNode != null) {
								shipPageContentNode.setProperty(JcrConstants.JCR_TITLE, ship.getShipName());
								shipPageContentNode.setProperty("shipId", ship.getShipId());
								shipPageContentNode.setProperty("shipCode", ship.getShipCod());
								shipPageContentNode.setProperty("shipName", ship.getShipName());
								shipPageContentNode.setProperty("shipType", ship.getShipType());
								shipPageContentNode.setProperty("shipUrl", ship.getShipUrl());
								session.save();
								LOGGER.debug("Updated ship with {} ", ship.getShipCod());
							}
						}
					}
					LOGGER.debug("Check ship with {} ", ship.getShipCod());
					i++;
					succesNumber = succesNumber + 1;
					if (i % sessionRefresh == 0) {
						if (session.hasPendingChanges()) {
							try {
								session.save();
							} catch (RepositoryException e) {
								session.refresh(true);
							}
						}

					}
				} catch (Exception e) {
					errorNumber = errorNumber + 1;
					LOGGER.debug("Ship import error, number of faulures :", errorNumber);
					i++;
				}
			}

			if (session.hasPendingChanges()) {
				try {
					// save migration date
					Node rootNode = shipsRootPage.getContentResource().adaptTo(Node.class);
					rootNode.setProperty("lastModificationDate", Calendar.getInstance());
					session.save();
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
			LOGGER.debug("Fin de l'import");

			resourceResolver.close();
		} catch (Exception e) {
			String errorMessage = "Import Ship Errors : {} ";
			LOGGER.error(errorMessage, e);
		}
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}

}
