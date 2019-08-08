package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

public interface CruisesImporter {

    /**
     * Import all cruises, used in first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Update the cruises based on the last import date stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateItems();

    ImportResult updateCheckAlias();

    /**
     * @return JSON mapping of cruises
     */
    JSONObject getJsonMapping();
}
