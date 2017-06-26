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
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Feature;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = FeaturesImporter.class)
public class FeaturesImporterImpl implements FeaturesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesImporterImpl.class);

	private int sessionRefresh = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private Replicator replicat;

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
			LOGGER.debug("Features importer login exception ", e);
		}
	}

	@Override
	public void importData() throws IOException {
		init();

		if (apiConfig.getSessionRefresh() != 0) {
			sessionRefresh = apiConfig.getSessionRefresh();
		}

		try {

			Page featuresRootPage;
			List<Feature> features;
			features = apiCallService.getFeatures();

			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("featuresUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				featuresRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
				LOGGER.debug("Importing features for langue : {}", loc);

				if (featuresRootPage != null) {

					int i = 0;
					for (Feature feature : features) {
						try {

							LOGGER.debug("Importing Feature: {}", feature.getName());
							Iterator<Resource> resources = resourceResolver
									.findResources("/jcr:root/content/silversea-com/" + loc
											+ "//element(*,cq:Page)[jcr:content/featureId=\"" + feature.getFeatureId()
											+ "\"]", "xpath");
							Page featurePage = null;

							if (resources.hasNext()) {
								featurePage = resources.next().adaptTo(Page.class);
								LOGGER.debug(" Feature with {} existe for langue : {}", feature.getFeatureCod(), loc);
							} else {
								featurePage = pageManager.create(featuresRootPage.getPath(),
										StringHelper.getFormatWithoutSpecialCharcters(feature.getName()),
										TemplateConstants.PATH_FEATURE,
										StringHelper.getFormatWithoutSpecialCharcters(feature.getName()), false);
								LOGGER.debug("create Feature with {} for langue : {}", feature.getFeatureCod(), loc);
							}

							if (featurePage != null) {
								Node featurePageContentNode = featurePage.getContentResource().adaptTo(Node.class);
								if (featurePageContentNode != null) {
									featurePageContentNode.setProperty(JcrConstants.JCR_TITLE, feature.getName());
									featurePageContentNode.setProperty("featureId", feature.getFeatureId());
									featurePageContentNode.setProperty("featureCode", feature.getFeatureCod());
									featurePageContentNode.setProperty("featureName", feature.getName());
									featurePageContentNode.setProperty("apiTitle", feature.getName());
									featurePageContentNode.setProperty("featureOrder", feature.getOrder());
									session.save();
									LOGGER.debug("Updated Feature with {} for langue : {}", feature.getFeatureCod(),
											loc);

									try {
										session.save();
										if (!replicat.getReplicationStatus(session, featuresRootPage.getPath())
												.isActivated()) {
											replicat.replicate(session, ReplicationActionType.ACTIVATE,
													featuresRootPage.getPath());
										}
										replicat.replicate(session, ReplicationActionType.ACTIVATE,
												featurePage.getPath());
										LOGGER.debug("replicate Feature with {} for langue : {}",
												feature.getFeatureCod(), loc);
									} catch (RepositoryException e) {
										LOGGER.debug("replication ERROR Feature with {} for langue : {}",
												feature.getFeatureCod(), loc);
										session.refresh(true);
									}
								}
							}
							i++;
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
							LOGGER.debug("Features error, number of failures :", e);
							i++;
						}
					}
					if (session.hasPendingChanges()) {
						try {
							Node rootNode = featuresRootPage.getContentResource().adaptTo(Node.class);
							rootNode.setProperty("lastModificationDate", Calendar.getInstance());
							session.save();
						} catch (RepositoryException e) {
							session.refresh(false);
						}
					}

				}
				LOGGER.debug("************************************************************************");
			}
			LOGGER.debug("****************************End of features import******************************");
			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			String errorMessage = "Import Feature Errors : {} ";
			LOGGER.error(errorMessage, e);
		}
	}

}
