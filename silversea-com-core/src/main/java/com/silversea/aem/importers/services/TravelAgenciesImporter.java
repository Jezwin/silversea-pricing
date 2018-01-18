package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

public interface TravelAgenciesImporter {

    /**
     * Import all travel agencies, used in first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Update the travel agencies based on the last import date stored in the travel agencies root page
     *
     * @return result of the import
     */
    ImportResult updateItems();

    /**
     * Import only one item
     *
     * @param itemId the agency ID of the agency to import
     */
    void importOneItem(final String itemId);

    /**
     * Get JSON object containing the mapping between agency id and path
     *
     * @return a JSON object with id/path mapping
     */
    JSONObject getJsonMapping();
}
