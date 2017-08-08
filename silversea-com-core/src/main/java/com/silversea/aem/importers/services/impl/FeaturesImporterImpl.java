package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.FeaturesApi;
import io.swagger.client.model.Feature;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class FeaturesImporterImpl implements FeaturesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Override
    public ImportResult importAllFeatures() {
        LOGGER.debug("Starting features import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final FeaturesApi featuresApi = new FeaturesApi(ImporterUtils.getApiClient(apiConfig));

            if (tagManager == null || session == null) {
                throw new ImporterException("Cannot initialize tagManager and session");
            }

            ImporterUtils.deleteResources(resourceResolver, 100, "/jcr:root/etc/tags/features"
                    + "//element(*,cq:Tag)");

            final List<Feature> features = featuresApi.featuresGet(null);
            int j = 0;

            for (Feature feature : features) {
                LOGGER.trace("Importing feature: {} ({})", feature.getName(), feature.getFeatureCod());

                try {
                    final String tagId = "features:" + JcrUtil.createValidName(feature.getName(), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
                    final Tag tag = tagManager.createTag(tagId, feature.getName(), null, false);

                    final Node tagNode = tag.adaptTo(Node.class);

                    if (tagNode == null) {
                        throw new ImporterException("Cannot get tag from node");
                    }

                    tagNode.setProperty("apiTitle", feature.getName());
                    tagNode.setProperty("featureCode", feature.getFeatureCod());
                    tagNode.setProperty("featureId", feature.getFeatureId());

                    successNumber++;
                    j++;
                } catch (RepositoryException e) {
                    errorNumber++;

                    LOGGER.error("Import error", e);
                } catch (InvalidTagFormatException e) {
                    errorNumber++;

                    LOGGER.error("Cannot create tag", e);
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} features imported, saving session", +j);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read features from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot save modification", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending features import, success: {}, errors: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateFeatures() {
        return null;
    }

    @Override
    public void importOneFeature(String featureCode) {
        // TODO implement
    }
}
