package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesImporter {

    /**
     * Import all cruises, used in first data import
     *
     * @return result of the import
     */
    ImportResult importAllCruises();

    /**
     * Import a sample set of cruises, used for test purpose (import is full)
     *
     * @param cruisesNumber number of cruises to import
     * @return result of the import
     */
    ImportResult importSampleSetCruises(final int cruisesNumber);

    /**
     * Update the cruises based on the last import date
     * stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateCruises();
}
