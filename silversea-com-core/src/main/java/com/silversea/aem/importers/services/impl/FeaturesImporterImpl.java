package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void importFeatures() throws IOException {

        final String authorizationHeader = getAuthorizationHeader(FEATURE_PATH);
        FeaturesApi featuresApi = new FeaturesApi();
        featuresApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            List<Feature> listFeatures = null;
            listFeatures = featuresApi.featuresGet(null);
        } catch (Exception e) {
            String errorMessage = "Some issues are happened for import builder ()";
            LOGGER.error(errorMessage, e);
        }
    }

}
