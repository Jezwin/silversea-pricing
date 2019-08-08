package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface FeaturesImporter {

    /**
     * Import all features, used in first data import
     *
     * @return import result
     */
    ImportResult importAllFeatures();

    /**
     * Update the features
     *
     * @return import result
     */
    ImportResult updateFeatures();
}