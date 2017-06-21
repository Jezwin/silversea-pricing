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

import org.apache.felix.scr.annotations.Activate;
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
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.SpecialOffer;

/**
 * Created by mbennabi on 09/03/2017.
 */
@Service
@Component(label = "Silversea.com - Exclusive Offers importer")
public class ExclusiveOffersImporterImpl extends BaseImporter implements ExclusiveOffersImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersImporterImpl.class);

	private int sessionRefresh = 100;
	private int pageSize = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private Replicator replicat;

	private List<Tag> market;

	private List<String> geoMarket;

	@Reference
	private ApiCallService apiCallService;

	private ResourceResolver resourceResolver;
	private PageManager pageManager;
	private Session session;
	TagManager tagManager;

	public void init() {
		try {
			Map<String, Object> authenticationPrams = new HashMap<String, Object>();
			authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			tagManager = resourceResolver.adaptTo(TagManager.class);
			session = resourceResolver.adaptTo(Session.class);
		} catch (LoginException e) {
			LOGGER.debug("travel agencies importer login exception ", e);
		}
	}

	@Override
	public ImporterStatus importData() throws IOException {
		init();
		ImporterStatus status = new ImporterStatus();

		int errorNumber = 0;
		int succesNumber = 0;
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
		/**
		 * Récuperation de per page
		 */
		if (apiConfig.getPageSize() != 0) {
			pageSize = apiConfig.getPageSize();
		}
		try {

			Page offersRootPage;
			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("spetialOffersUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				offersRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
				LOGGER.debug("Importing Exclusive offers for langue : {}", loc);

				if (offersRootPage != null) {

					int i = 1;
					List<SpecialOffer> specialOffers;
					do {

						specialOffers = apiCallService.getExclusiveOffers(i, pageSize);

						int j = 0;

						for (SpecialOffer offers : specialOffers) {

							try {

								Iterator<Resource> resources = resourceResolver
										.findResources("/jcr:root/content/silversea-com/" + loc
												+ "//element(*,cq:Page)[jcr:content/exclusiveOfferId=\""
												+ offers.getVoyageSpecialOfferId() + "\"]", "xpath");

								Page offersPage = null;

								if (resources.hasNext()) {
									offersPage = resources.next().adaptTo(Page.class);
								} else {
									offersPage = pageManager.create(offersRootPage.getPath(),
											JcrUtil.createValidChildName(offersRootPage.adaptTo(Node.class),
													StringHelper.getFormatWithoutSpecialCharcters(
															offers.getVoyageSpecialOffer())),
											TemplateConstants.PATH_EXCLUSIVE_OFFERT, StringHelper
													.getFormatWithoutSpecialCharcters(offers.getVoyageSpecialOffer()),
											false);
									LOGGER.debug("create page {} Exclusive offers for langue : {}",
											offers.getVoyageSpecialOfferId(), loc);
								}

								if (offersPage != null) {
									Node offersContentNode = offersPage.getContentResource().adaptTo(Node.class);
									offersContentNode.setProperty(JcrConstants.JCR_TITLE,
											offers.getVoyageSpecialOffer());
									offersContentNode.setProperty("exclusiveOfferId", offers.getVoyageSpecialOfferId());
									offersContentNode.setProperty("startDate", offers.getValidFrom().toString());
									offersContentNode.setProperty("endDate", offers.getValidTo().toString());

									offersPage.adaptTo(Node.class).addMixin("cq:Taggable");

									geoMarket = offers.getMarkets();
									market = new ArrayList<Tag>();
									if (GeolocationHelper.getGeoMarketCode(tagManager, geoMarket) != null) {
										market = GeolocationHelper.getGeoMarketCode(tagManager, geoMarket);
									}
									tagManager.setTags(offersPage.getContentResource(),
											market.stream().toArray((Tag[]::new)));

									succesNumber = succesNumber + 1;
									j++;

									LOGGER.debug("update page {} Exclusive offers for langue : {}",
											offers.getVoyageSpecialOfferId(), loc);

									if (!replicat.getReplicationStatus(session, offersRootPage.getPath())
											.isActivated()) {
										replicat.replicate(session, ReplicationActionType.ACTIVATE,
												offersRootPage.getPath());
									}
									try {
										session.save();
										replicat.replicate(session, ReplicationActionType.ACTIVATE,
												offersPage.getPath());
										LOGGER.debug("replication page Exclusive offers id {} ,  for langue : {}",
												offers.getVoyageSpecialOfferId(), loc);
									} catch (RepositoryException e) {
										LOGGER.debug(
												"replication page Exclusive offers id {} *** ERROR ***,  for langue : {}",
												offers.getVoyageSpecialOfferId(), loc);
										session.refresh(true);
									}
								}

								if (j % sessionRefresh == 0) {
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
								LOGGER.debug("Exclusive offer falure error, number of faulures :", +errorNumber);
								j++;
							}
						}

						i++;
					} while (specialOffers.size() > 0);

					if (session.hasPendingChanges()) {
						try {
							// save migration date
							Node rootNode = offersRootPage.getContentResource().adaptTo(Node.class);
							rootNode.setProperty("lastModificationDate", Calendar.getInstance());
							session.save();
						} catch (RepositoryException e) {
							session.refresh(false);
						}
					}

				}
			}

			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Exception importing Exclusive offers", e);
		}

		status.setErrorNumber(errorNumber);
		status.setSuccesNumber(succesNumber);

		return status;
	}

}
