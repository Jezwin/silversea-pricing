package com.silversea.aem.importers.services.impl;

import java.io.IOException;
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
import org.apache.sling.api.resource.LoginException;
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
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.services.FeaturesUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.FeaturesApi;
import io.swagger.client.model.Feature;

@Component(immediate = true, label = "Silversea.com - features update importer")
@Service(value = FeaturesUpdateImporter.class)
public class FeaturesUpdateImporterImpl extends BaseImporter implements FeaturesUpdateImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesUpdateImporterImpl.class);

	private int errorNumber = 0;
	private int succesNumber = 0;
	private int sessionRefresh = 100;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private Replicator replicat;

	@Override
	public ImporterStatus updateImporData() throws IOException {
		ImporterStatus status = new ImporterStatus();

		Set<Integer> diff = new HashSet<Integer>();

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

		final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("featuresUrl"));
		FeaturesApi featuresApi = new FeaturesApi();
		featuresApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
		try {
			ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resourceResolver.adaptTo(Session.class);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page featuresRootPage = pageManager.getPage(apiConfig.apiRootPath("featuresUrl"));
			List<Feature> features;
			features = featuresApi.featuresGet(null);
			int i = 0;
			for (Feature feature : features) {
				try {

					LOGGER.debug("Importing Feature: {}", feature.getName());
					Iterator<Resource> resources = resourceResolver.findResources(
							"//element(*,cq:Page)[jcr:content/featureId=\"" + feature.getFeatureId() + "\"]", "xpath");
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
//							if (!replicat.getReplicationStatus(session, featuresRootPage.getPath()).isActivated()) {
								replicat.replicate(session, ReplicationActionType.ACTIVATE, featurePage.getPath());
//							}
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
					// errorNumber = errorNumber + 1;
					LOGGER.debug("Features error, number of faulures :", e);
					i++;
				}
			}
			if (session.hasPendingChanges()) {
				try {
					// save migration date
					Node rootNode = featuresRootPage.getContentResource().adaptTo(Node.class);
					rootNode.setProperty("lastModificationDate", Calendar.getInstance());
					session.save();
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}

			// TODO duplication des pages supprimé dans le retour d'api
			Iterator<Page> resourcess = featuresRootPage.listChildren();
			while (resourcess.hasNext()) {
				Page page = resourcess.next();

				if (page.getContentResource().getValueMap().get("featureId").toString() != null && !diff.contains(
						Integer.parseInt(page.getContentResource().getValueMap().get("featureId").toString()))) {
					try {
						replicat.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());
					} catch (ReplicationException e) {
						e.printStackTrace();
					}
				}
			}
			resourceResolver.close();
		} catch (ApiException | LoginException | RepositoryException e) {
			String errorMessage = "Import Feature Errors : {} ";
			LOGGER.error(errorMessage, e);
		}
		status.setErrorNumber(errorNumber);
		status.setSuccesNumber(succesNumber);

		return status;
	}

}
