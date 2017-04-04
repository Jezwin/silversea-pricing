package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

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
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.FeaturesApi;
import io.swagger.client.model.Feature;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = FeaturesImporter.class)
public class FeaturesImporterImpl extends BaseImporter implements FeaturesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesImporterImpl.class);
    private static final String FEATURE_PATH = "/api/v1/features";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importFeatures() throws IOException {

        final String authorizationHeader = getAuthorizationHeader(FEATURE_PATH);
        FeaturesApi featuresApi = new FeaturesApi();
        featuresApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page featuresRootPage = pageManager.getPage(ImportersConstants.BASEPATH_FEATURES);
            List<Feature> features;
            features = featuresApi.featuresGet(null);
            int i = 0;
            for (Feature feature : features) {
                LOGGER.debug("Importing Feature: {}", feature.getFeatureCod());
                Iterator<Resource> resources = resourceResolver.findResources(
                        "//element(*,cq:Page)[jcr:content/featureCode=\"" + feature.getFeatureCod() + "\"]", "xpath");
                Page featurePage = null;

                if (resources.hasNext()) {
                    featurePage = resources.next().adaptTo(Page.class);
                } else {
                    featurePage = pageManager.create(featuresRootPage.getPath(), feature.getFeatureCod(),
                            "/apps/silversea/silversea-com/templates/feature", feature.getName());
                }

                if (featurePage != null) {
                    Node featurePageContentNode = featurePage.getContentResource().adaptTo(Node.class);
                    if (featurePageContentNode != null) {
                        featurePageContentNode.setProperty(JcrConstants.JCR_TITLE, feature.getName());
                        featurePageContentNode.setProperty("featureId", feature.getFeatureId());
                        featurePageContentNode.setProperty("featureCode", feature.getFeatureCod());
                        featurePageContentNode.setProperty("featureName", feature.getName());
                        featurePageContentNode.setProperty("featureOrder", feature.getOrder());
                        session.save();
                        LOGGER.debug("Updated Feature with {} ", feature.getFeatureCod());
                    }
                }
                LOGGER.debug("Check Feature with {} ", feature.getFeatureCod());
                i++;
                if (i % 100 == 0) {
                    if (session.hasPendingChanges()) {
                        try {
                            session.save();
                        } catch (RepositoryException e) {
                            session.refresh(true);
                        }
                    }
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
            resourceResolver.close();
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            String errorMessage = "Import Feature Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

}
