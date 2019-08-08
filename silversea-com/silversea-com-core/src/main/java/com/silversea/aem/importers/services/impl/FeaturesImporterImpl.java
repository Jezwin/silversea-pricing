package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.FeaturesApi;
import io.swagger.client.model.Feature;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component
public class FeaturesImporterImpl implements FeaturesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(FeaturesImporterImpl.class);

    private static final String PN_API_TITLE = "apiTitle";
    private static final String PN_FEATURE_CODE = "featureCode";
    private static final String PN_FEATURE_ID = "featureId";

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

            final FeaturesApi featuresApi = new FeaturesApi(ImportersUtils.getApiClient(apiConfig));

            if (tagManager == null || session == null) {
                throw new ImporterException("Cannot initialize tagManager and session");
            }

            ImportersUtils.deleteResources(resourceResolver, 100, "/jcr:root/etc/tags/features"
                    + "//element(*,cq:Tag)");

            final List<Feature> features = featuresApi.featuresGet(null);
            int j = 0;

            for (Feature feature : features) {
                LOGGER.trace("Importing feature: {} ({})", feature.getName(), feature.getFeatureCod());

                try {
                    final String tagId = WcmConstants.TAG_NAMESPACE_FEATURES + JcrUtil.createValidName(feature.getName(), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
                    final Tag tag = tagManager.createTag(tagId, feature.getName(), null, false);

                    final Node tagNode = tag.adaptTo(Node.class);

                    if (tagNode == null) {
                        throw new ImporterException("Cannot get tag from node");
                    }

                    tagNode.setProperty(PN_API_TITLE, feature.getName());
                    tagNode.setProperty(PN_FEATURE_CODE, feature.getFeatureCod());
                    tagNode.setProperty(PN_FEATURE_ID, feature.getFeatureId());

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

            final FeaturesApi featuresApi = new FeaturesApi(ImportersUtils.getApiClient(apiConfig));

            if (tagManager == null || session == null) {
                throw new ImporterException("Cannot initialize tagManager and session");
            }

            final Iterator<Resource> existingFeaturesResources = resourceResolver.findResources(
                    "/jcr:root/etc/tags/features//element(*,cq:Tag)[featureCode]", "xpath");


            Map<String, Resource> existingFeatures = new HashMap<>();
            while (existingFeaturesResources.hasNext()) {
                final Resource existingFeatureResource = existingFeaturesResources.next();
                final String featureCode = existingFeatureResource.getValueMap().get("featureCode", String.class);

                if (StringUtils.isNotEmpty(featureCode)) {
                    existingFeatures.put(featureCode, existingFeatureResource);
                }
            }

            final List<Feature> features = featuresApi.featuresGet(null);

            for (Feature feature : features) {
                LOGGER.trace("Importing feature: {} ({})", feature.getName(), feature.getFeatureCod());

                // create the feature
                if (!existingFeatures.containsKey(feature.getFeatureCod())) {
                    existingFeatures.remove(feature.getFeatureCod());

                    try {
                        final String tagId = WcmConstants.TAG_NAMESPACE_FEATURES + JcrUtil.createValidName(feature.getName(), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
                        final Tag tag = tagManager.createTag(tagId, feature.getName(), null, false);

                        final Node tagNode = tag.adaptTo(Node.class);

                        if (tagNode == null) {
                            throw new ImporterException("Cannot get tag from node");
                        }

                        tagNode.setProperty(PN_API_TITLE, feature.getName());
                        tagNode.setProperty(PN_FEATURE_CODE, feature.getFeatureCod());
                        tagNode.setProperty(PN_FEATURE_ID, feature.getFeatureId());
                        tagNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                        successNumber++;

                        LOGGER.trace("Feature {} ({}) does not exists - created", feature.getName(), feature.getFeatureCod());
                    } catch (RepositoryException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    } catch (InvalidTagFormatException e) {
                        errorNumber++;

                        LOGGER.error("Cannot create tag", e);
                    }
                // update feature if exists
                } else {
                    final Resource featureResource = existingFeatures.get(feature.getFeatureCod());

                    existingFeatures.remove(feature.getFeatureCod());

                    try {
                        final ValueMap existingFeatureProperties = featureResource.getValueMap();

                        final String name = existingFeatureProperties.get(PN_API_TITLE, String.class);
                        final String featureCode = existingFeatureProperties.get("featureCode", String.class);
                        final Integer featureId = existingFeatureProperties.get(PN_FEATURE_ID, Integer.class);

                        if (name == null
                                || featureCode == null
                                || featureId == null
                                || !name.equals(feature.getName())
                                || !featureCode.equals(feature.getFeatureCod())
                                || !featureId.equals(feature.getFeatureId())) {
                            final Node featureNode = featureResource.adaptTo(Node.class);

                            if (featureNode != null) {
                                featureNode.setProperty(PN_API_TITLE, feature.getName());
                                featureNode.setProperty(PN_FEATURE_CODE, feature.getFeatureCod());
                                featureNode.setProperty(PN_FEATURE_ID, feature.getFeatureId());
                                featureNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                successNumber++;

                                existingFeatures.remove(feature.getFeatureCod());

                                LOGGER.trace("Feature {} ({}) already exists and have changed - modified",
                                        feature.getName(), feature.getFeatureCod());
                            } else {
                                errorNumber++;

                                LOGGER.warn("Cannot get feature node {}", featureResource.getPath());
                            }
                        } else {
                            LOGGER.trace("Feature {} ({}) already exists and doesn't change - untouched",
                                    feature.getName(), feature.getFeatureCod());
                        }
                    } catch (RepositoryException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }
            }

            // mark as deactivate non existing features
            for (Map.Entry<String, Resource> existingFeature : existingFeatures.entrySet()) {
                try {
                    final Node featureNode = existingFeature.getValue().adaptTo(Node.class);

                    if (featureNode != null) {
                        featureNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                    }

                    LOGGER.trace("Feature {} does not exists anymore - removed", existingFeature.getValue().getPath());
                } catch (RepositoryException e) {
                    LOGGER.error("Cannot mark feature as deactivated {}", existingFeature.getValue().getPath());
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} features imported, saving session", +successNumber);
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

        LOGGER.debug("Ending features update, success: {}, errors: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

}
