package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

public interface ExclusiveOffersImporter {

    /**
     * Import all exclusive offers, manage the diff update as there is no specific endpoint in the API
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Get JSON object containing the mapping between exclusive offer id and path
     *
     * @return a JSON object with id/path mapping
     */
    JSONObject getJsonMapping();
}