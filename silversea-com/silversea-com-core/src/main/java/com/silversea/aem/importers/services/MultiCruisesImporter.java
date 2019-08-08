package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

public interface MultiCruisesImporter {

    /**
     * Update the cruises based on the last import date stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateItems();

    /**
     * @return JSON mapping of cruises
     */
    JSONObject getJsonMapping();
}
