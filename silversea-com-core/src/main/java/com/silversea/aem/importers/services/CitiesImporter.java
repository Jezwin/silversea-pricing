package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

/**
 * @author aurelienolivier
 */
public interface CitiesImporter {

    /**
     * Import all cities, used in first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Update the cities based on the last import date
     * stored in the cities root page
     *
     * @return result of the import
     */
    ImportResult updateItems();

    /**
     * Import only one item
     *
     * @param itemId the city ID of the city to import
     */
    void importOneItem(final String itemId);

    /**
     * Get JSON object containing the mapping between city id and path
     *
     * @return a JSON object with id/path mapping
     */
    JSONObject getJsonMapping();
}
