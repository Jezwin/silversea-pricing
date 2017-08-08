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
     * Import a sample set of cruises, used for test purpose (import is full)
     *
     * @param size number of cruises to import
     * @return result of the import
     */
    ImportResult importSampleSet(final int size);

    /**
     * Update the cruises based on the last import date
     * stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateItems();

    JSONObject getJsonMapping();
}
