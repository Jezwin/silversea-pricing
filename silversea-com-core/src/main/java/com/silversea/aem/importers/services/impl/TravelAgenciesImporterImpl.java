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

import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.utils.StringsUtils;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.TravelAgenciesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Agency;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Travel Agencies importer")
public class TravelAgenciesImporterImpl implements TravelAgenciesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(TravelAgenciesImporterImpl.class);

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;
	private int pageSize = 100;

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
			LOGGER.debug("travel agencies importer login exception ", e);
		}
	}

	@Override
	public void importData() throws IOException {
		init();

		if (apiConfig.getSessionRefresh() != 0) {
			sessionRefresh = apiConfig.getSessionRefresh();
		}

		if (apiConfig.getPageSize() != 0) {
			pageSize = apiConfig.getPageSize();
		}

		try {

			Page travelRootPage;

			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("agenciesUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				if (loc != null) {
					travelRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
					LOGGER.debug("Importing Exclusive offers for langue : {}", loc);

					if (travelRootPage != null) {

						int i = 1;
						List<Agency> travelAgencies;
						do {

							travelAgencies = apiCallService.getTravelAgencies(i, pageSize);

							int j = 0;

							for (Agency agency : travelAgencies) {

								try {

									Iterator<Resource> resources = resourceResolver
											.findResources("/jcr:root/content/silversea-com/" + loc
													+ "//element(*,cq:Page)[jcr:content/agencyId=\""
													+ agency.getAgencyId() + "\"]", "xpath");

									Page agencyTravelPage = null;

									if (resources.hasNext()) {
										agencyTravelPage = resources.next().adaptTo(Page.class);
									}

									else {
										Page agencyTravelContryPage = pageManager
												.getPage(travelRootPage.getPath() + "/" +agency.getCountryIso3().toLowerCase());
										if (agencyTravelContryPage == null) {
											agencyTravelContryPage = pageManager.create(travelRootPage.getPath(),
													JcrUtil.createValidChildName(travelRootPage.adaptTo(Node.class),
															StringsUtils.getFormatWithoutSpecialCharcters(
																	agency.getCountryIso3())),
													"/apps/silversea/silversea-com/templates/page", StringsUtils
															.getFormatWithoutSpecialCharcters(agency.getCountryIso3()),
													false);
											LOGGER.debug("createa travel agency contry page : {}",
													agency.getCountryIso3());
										}

										if (agencyTravelContryPage != null) {
											agencyTravelPage = pageManager.create(agencyTravelContryPage.getPath(),
													JcrUtil.createValidChildName(
															agencyTravelContryPage.adaptTo(Node.class),
															StringsUtils.getFormatWithoutSpecialCharcters(
																	agency.getAgency())),
													WcmConstants.PAGE_TEMPLATE_TRAVEL_AGENCY,
													StringsUtils.getFormatWithoutSpecialCharcters(agency.getAgency()),
													false);
											LOGGER.debug("create a  travel agency  page : {} for language : {}",
													agency.getAgency(), loc);
										}
									}

									if (agencyTravelPage != null) {
										Node agencyContentNode = agencyTravelPage.getContentResource()
												.adaptTo(Node.class);
										agencyContentNode.setProperty(JcrConstants.JCR_TITLE, agency.getAgency());
										agencyContentNode.setProperty("agencyId", agency.getAgencyId());
										agencyContentNode.setProperty("address", agency.getAddress());
										agencyContentNode.setProperty("city", agency.getCity());
										agencyContentNode.setProperty("zip", agency.getZip());
										agencyContentNode.setProperty("zip4", agency.getZip4());
										agencyContentNode.setProperty("countryIso3", agency.getCountryIso3());
										agencyContentNode.setProperty("stateCode", agency.getStateCod());
										agencyContentNode.setProperty("county", agency.getCounty());
										agencyContentNode.setProperty("phone", agency.getPhone());
										agencyContentNode.setProperty("latitude", agency.getLat());
										agencyContentNode.setProperty("longitude", agency.getLon());
										succesNumber = succesNumber + 1;
										j++;
										LOGGER.debug("update a  travel agency  page : {} for language : {}",
												agency.getAgency(), loc);

									}
									// session.save();

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
									LOGGER.debug("Travel agency error, number of faulures : {}", +errorNumber);
									j++;
								}
							}

							i++;
						} while (travelAgencies.size() > 0);

						if (session.hasPendingChanges()) {
							try {
								Node rootNode = travelRootPage.getContentResource().adaptTo(Node.class);
								rootNode.setProperty("lastModificationDate", Calendar.getInstance());
								session.save();
							} catch (RepositoryException e) {
								session.refresh(false);
							}
						}
					}
				}
			}
			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Exception importing shorexes", e);
		}
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public int getSuccesNumber() {
		return succesNumber;
	}
}
