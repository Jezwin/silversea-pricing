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
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesImporter;

import io.swagger.client.api.FeaturesApi;
import io.swagger.client.model.Feature;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = FeaturesImporter.class)
public class FeaturesImporterImpl extends BaseImporter implements FeaturesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesImporterImpl.class);
    private static final String FEATURE_PATH = "/api/v1/features";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Session session;

    @Override
    public void importFeatures() throws IOException {

        final String authorizationHeader = getAuthorizationHeader(FEATURE_PATH);
        FeaturesApi featuresApi = new FeaturesApi();
        featuresApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            PageManager pageManager = getResourceResolver().adaptTo(PageManager.class);
            Page featuresRootPage = pageManager.getPage(ImportersConstants.BASEPATH_FEATURES);

            List<Feature> listFeatures = featuresApi.featuresGet(null);
            for (Feature feature : listFeatures) {
                Iterator<Resource> resources = getResourceResolver().findResources(
                        "//element(*,cq:Page)[jcr:content/featureCode=\"" + feature.getFeatureCod() + "\"]", "xpath");
                Page featurePage = null;

                if (resources.hasNext()) {
                    featurePage = resources.next().adaptTo(Page.class);
                } else {
                    featurePage = pageManager.create(featuresRootPage.getPath(), feature.getFeatureCod(),
                            "/apps/silversea/silversea-com/templates/feature", feature.getName());
                    LOGGER.debug("Creating Feature with code : {} ", feature.getFeatureCod());
                }

                if (featurePage != null) {
                    Node featurePageContentNode = featurePage.getContentResource().adaptTo(Node.class);
                    updateFeatureNode(featurePageContentNode, feature);
                }

            }
            updateRoot(featuresRootPage);
        } catch (Exception e) {
            String errorMessage = "Import Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private void updateFeatureNode(Node featurePageContentNode, Feature feature) {
        try {
            session = getResourceResolver().adaptTo(Session.class);
            if (featurePageContentNode != null) {
                featurePageContentNode.setProperty(JcrConstants.JCR_TITLE, feature.getName());
                featurePageContentNode.setProperty("featureId", feature.getFeatureId());
                featurePageContentNode.setProperty("featureCode", feature.getFeatureCod());
                featurePageContentNode.setProperty("featureName", feature.getName());
                featurePageContentNode.setProperty("featureOrder", feature.getOrder());
                session.save();
            }
            session.logout();
        } catch (LoginException | RepositoryException e) {
            String errorMessage = "Update Ship Errors : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private void updateRoot(Page page) {
        try {
            session = getResourceResolver().adaptTo(Session.class);
            // save migration date
            if (page != null) {
                Node rootShipNode = page.getContentResource().adaptTo(Node.class);
                rootShipNode.setProperty("lastModificationDate", Calendar.getInstance());
                session.save();
            }
            session.logout();
        } catch (RepositoryException | LoginException e) {
            String errorMessage = "Update Root Node Modification Date : {} ";
            LOGGER.error(errorMessage, e);
        }
    }

    private ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

}
