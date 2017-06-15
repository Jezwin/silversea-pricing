package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.services.ShipsUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.api.ShipsApi;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Ship importer Update", metatype = true)
@Service(value = ShipsUpdateImporter.class)
public class ShipsUpdateImporterImpl extends BaseImporter implements ShipsUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ShipsUpdateImporterImpl.class);
	private int sessionRefresh = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private ApiConfigurationService apiConfig;
	@Reference
	private Replicator replicat;
    @Reference
    private ApiCallService apiCallService;

	@Override
	public ImporterStatus updateImporData() throws IOException {

		ImporterStatus status = new ImporterStatus();

		Set<Integer> diff = new HashSet<Integer>();

		int errorNumber = 0;
		int succesNumber = 0;
		LOGGER.debug("Début de l'import");

		try {
			/**
			 * authentification pour le swagger
			 */
			getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
			/**
			 * Récuperation du domain de l'api Swager
			 */
			getApiDomain(apiConfig.getApiBaseDomain());
			/**
			 * Récuperation de la session refresh
			 */
			if (apiConfig.getSessionRefresh() != 0) {
				sessionRefresh = apiConfig.getSessionRefresh();
			}

			

			ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resourceResolver.adaptTo(Session.class);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page shipsRootPage = pageManager.getPage(apiConfig.apiRootPath("shipUrl"));
			List<Ship> listShips;
			
//			final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("shipUrl"));
//			ShipsApi shipsApi = new ShipsApi();
//			shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
//			
//			listShips = shipsApi.shipsGet(null);
			
			listShips = apiCallService.getShips();
			
			Page rootPathByLocal;
			List<Page> shipPages = new ArrayList<Page>();
			List<String> local = new ArrayList<>();

			int i = 0;
			for (Ship ship : listShips) {
				try {
					Iterator<Resource> resources = resourceResolver.findResources(
							"//element(*,cq:Page)[jcr:content/shipId=\"" + ship.getShipId() + "\"]", "xpath");
					Page shipPage = null;

					local = ImporterUtils.finAllLanguageCopies(resourceResolver);

					if (resources.hasNext()) {
						// shipPage = resources.next().adaptTo(Page.class);
						shipPages = ImporterUtils.findPagesById(resources);
					} else {
						// shipPage =
						// pageManager.create(shipsRootPage.getPath(),
						// JcrUtil.createValidChildName(shipsRootPage.adaptTo(Node.class),
						// StringHelper.getFormatWithoutSpecialCharcters(ship.getShipName())),
						// TemplateConstants.PATH_SHIP,
						// StringHelper.getFormatWithoutSpecialCharcters(ship.getShipName()),
						// false);
						shipPages = ImporterUtils.createPagesLanguageCopies(pageManager, resourceResolver,
								shipsRootPage, local, TemplateConstants.PATH_SHIP, ship.getShipName());
					}
					diff.add(ship.getShipId());

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
								try {
									session.save();
									replicat.replicate(session, ReplicationActionType.ACTIVATE, shiPage.getPath());
								} catch (RepositoryException e) {
									session.refresh(true);
								}
								LOGGER.debug("Updated ship with {} ", ship.getShipCod());
							}
						}
					}

					// if (shipPage != null) {
					// Node shipPageContentNode =
					// shipPage.getContentResource().adaptTo(Node.class);
					// if (shipPageContentNode != null) {
					// shipPageContentNode.setProperty(JcrConstants.JCR_TITLE,
					// ship.getShipName());
					// shipPageContentNode.setProperty("shipId",
					// ship.getShipId());
					// shipPageContentNode.setProperty("shipCode",
					// ship.getShipCod());
					// shipPageContentNode.setProperty("shipName",
					// ship.getShipName());
					// shipPageContentNode.setProperty("shipType",
					// ship.getShipType());
					// shipPageContentNode.setProperty("shipUrl",
					// ship.getShipUrl());
					// session.save();
					// LOGGER.debug("Updated ship with {} ", ship.getShipCod());
					// }
					// }
					// if (replicat.getReplicationStatus(session,
					// shipsRootPage.getPath()).isActivated()) {
					// replicat.replicate(session,
					// ReplicationActionType.ACTIVATE, shipPage.getPath());
					// }
					// LOGGER.debug("Check ship with {} ", ship.getShipCod());

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
			
			for (String loc : local) {
				rootPathByLocal = ImporterUtils.findPagesLanguageCopies(pageManager, resourceResolver, shipsRootPage, loc);
//				for (Page ship : shipPages) {
					Iterator<Page> resourcess = rootPathByLocal.listChildren();
					while (resourcess.hasNext()) {
						Page page = resourcess.next();

						if (page.getContentResource().getValueMap().get("shipId") != null && !diff.contains(
								Integer.parseInt(page.getContentResource().getValueMap().get("shipId").toString()))) {
							try {
								replicat.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());
							} catch (ReplicationException e) {
								e.printStackTrace();
							}
						}
					}
//				}
			}

			// // TODO duplication des pages supprimé dans le retour d'api
			// Iterator<Page> resourcess = shipsRootPage.listChildren();
			// while (resourcess.hasNext()) {
			// Page page = resourcess.next();
			//
			// if (page.getContentResource().getValueMap().get("shipId") != null
			// && !diff
			// .contains(Integer.parseInt(page.getContentResource().getValueMap().get("shipId").toString())))
			// {
			// try {
			// replicat.replicate(session, ReplicationActionType.DEACTIVATE,
			// page.getPath());
			// } catch (ReplicationException e) {
			// e.printStackTrace();
			// }
			// }
			// }

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

		status.setErrorNumber(errorNumber);
		status.setSuccesNumber(succesNumber);
		return status;
	}

}
