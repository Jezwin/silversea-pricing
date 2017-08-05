package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface FeaturesImporter {

    /**
     * Import all features, used in first data import
     * @return import result
     */
    ImportResult importAllFeatures();

    /**
     * Update the features
     * @return import result
     */
    ImportResult updateFeatures();

    /**
     * Update only one feature, based on <code>featureCode</code>
     * @param featureCode feature code of the feature to update
     */
    void importOneFeature(final String featureCode);
}