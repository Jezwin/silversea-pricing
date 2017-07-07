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
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Feature;

@Component(immediate = true, label = "Silversea.com - features update importer")
@Service(value = FeaturesUpdateImporter.class)
public class FeaturesUpdateImporterImpl implements FeaturesUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesUpdateImporterImpl.class);

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
	public ImporterStatus updateImporData() throws IOException {
		init();
		ImporterStatus status = new ImporterStatus();

		Set<Integer> diff = new HashSet<Integer>();

		int errorNumber = 0;
		int succesNumber = 0;

		if (apiConfig.getSessionRefresh() != 0) {
			sessionRefresh = apiConfig.getSessionRefresh();
		}

		try {
			Page featuresRootPage;
			List<Feature> features;

			Page RootPage = pageManager.getPage(apiConfig.apiRootPath("featuresUrl"));
			List<String> local = new ArrayList<>();
			local = ImporterUtils.finAllLanguageCopies(resourceResolver);

			for (String loc : local) {
				featuresRootPage = ImporterUtils.getPagePathByLocale(resourceResolver, RootPage, loc);
				LOGGER.debug("Importing Exclusive offers for langue : {}", loc);

				if (featuresRootPage != null) {

					features = apiCallService.getFeatures();
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
							} else {
								featurePage = pageManager.create(featuresRootPage.getPath(),
										StringHelper.getFormatWithoutSpecialCharcters(feature.getName()),
										TemplateConstants.PATH_FEATURE,
										StringHelper.getFormatWithoutSpecialCharcters(feature.getName()), false);
							}
							diff.add(feature.getFeatureId());
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
									ImporterUtils.updateReplicationStatus(replicat, session, false,
											featurePage.getPath());
									LOGGER.debug("Updated Feature with {} ", feature.getFeatureCod());
								}
							}
							LOGGER.debug("Check Feature with {} ", feature.getFeatureCod());
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
							LOGGER.debug("Features error, number of faulures :", e);
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

					Iterator<Page> resourcess = featuresRootPage.listChildren();
					while (resourcess.hasNext()) {
						Page page = resourcess.next();
						Integer id = Integer
								.parseInt(Objects.toString(page.getContentResource().getValueMap().get("featureId")));
						if (id != null && !diff.contains(id)) {
							ImporterUtils.updateReplicationStatus(replicat, session, true, page.getPath());
						}
					}
				}
			}
			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Import Feature Errors : {}", e);
		}
		status.setErrorNumber(errorNumber);
		status.setSuccesNumber(succesNumber);

		return status;
	}

}
