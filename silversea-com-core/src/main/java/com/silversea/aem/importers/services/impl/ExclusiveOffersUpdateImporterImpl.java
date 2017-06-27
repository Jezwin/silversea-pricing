package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
import com.day.cq.commons.jcr.JcrUtil;
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
import com.silversea.aem.importers.services.ExclusiveOffersUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.SpecialOffer;

/**
 * Created by mbennabi on 09/03/2017.
 */
@Service
@Component(label = "Silversea.com - Exclusive Offers importer")
public class ExclusiveOffersUpdateImporterImpl implements ExclusiveOffersUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersUpdateImporterImpl.class);

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
			LOGGER.debug("Exclusive offers importer login exception ", e);
		}
	}

	@Override
	public ImporterStatus updateImporData() throws IOException {
		init();
		ImporterStatus status = new ImporterStatus();

		Set<Integer> diff = new HashSet<Integer>();

		int errorNumber = 0;
		int succesNumber = 0;

		if (apiConfig.getSessionRefresh() != 0) {
			sessionRefresh = apiConfig.getSessionRefresh();
		}

		if (apiConfig.getPageSize() != 0) {
			pageSize = apiConfig.getPageSize();
		}

		try {

			Page offersRootPage = pageManager.getPage(apiConfig.apiRootPath("spetialOffersUrl"));

			int i = 1;

			List<SpecialOffer> specialOffers;

			do {
				specialOffers = apiCallService.getExclusiveOffers(i, pageSize);

				int j = 0;

				if (specialOffers != null) {
					for (SpecialOffer offers : specialOffers) {

						try {

							Iterator<Resource> resources = resourceResolver
									.findResources("//element(*,cq:Page)[jcr:content/exclusiveOfferId=\""
											+ offers.getVoyageSpecialOfferId() + "\"]", "xpath");

							Page offersPage = null;

							if (resources.hasNext()) {
								offersPage = resources.next().adaptTo(Page.class);
							} else {
								offersPage = pageManager.create(offersRootPage.getPath(),
										JcrUtil.createValidChildName(offersRootPage.adaptTo(Node.class),
												StringHelper.getFormatWithoutSpecialCharcters(
														offers.getVoyageSpecialOffer())),
										TemplateConstants.PATH_EXCLUSIVE_OFFERT,
										StringHelper.getFormatWithoutSpecialCharcters(offers.getVoyageSpecialOffer()),
										false);
								LOGGER.debug("Create of exclusive offers : {} ", offers.getVoyageSpecialOffer());
							}

							diff.add(offers.getVoyageSpecialOfferId());

							if (offersPage != null) {
								Node offersContentNode = offersPage.getContentResource().adaptTo(Node.class);
								offersContentNode.setProperty(JcrConstants.JCR_TITLE, offers.getVoyageSpecialOffer());
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
								session.save();
								ImporterUtils.updateReplicationStatus(replicat, session, false, offersPage.getPath());
								j++;
								LOGGER.debug("Update of exclusive offers : {} , succes numbers {}",
										offers.getVoyageSpecialOffer(), +succesNumber);
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
							LOGGER.debug("Exclusive offer failure error, number of failures : {}", errorNumber);
							j++;
						}
					}
				}
				i++;
			} while (specialOffers.size() > 0 && specialOffers != null);

			// TODO duplication of deleted page
			Iterator<Page> resourcess = offersRootPage.listChildren();
			while (resourcess.hasNext()) {
				Page page = resourcess.next();
				Integer id = Integer
						.parseInt(Objects.toString(page.getContentResource().getValueMap().get("exclusiveOfferId")));
				if (id != null && !diff.contains(id)) {
					ImporterUtils.updateReplicationStatus(replicat, session, true, page.getPath());
				}
			}

			if (session.hasPendingChanges()) {
				try {
					Node rootNode = offersRootPage.getContentResource().adaptTo(Node.class);
					rootNode.setProperty("lastModificationDate", Calendar.getInstance());
					session.save();
				} catch (RepositoryException e) {
					session.refresh(false);
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
