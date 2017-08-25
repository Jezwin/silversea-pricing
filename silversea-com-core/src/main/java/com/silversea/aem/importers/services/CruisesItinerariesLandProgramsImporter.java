package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesItinerariesLandProgramsImporter {

    /**
     * Import all itineraries land programs items, used for first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Import a sample set of itineraries land programs, used for test purpose (import is full)
     *
     * @param size number of itineraries land programs to import
     *
     * @return result of the import
     */
    ImportResult importSampleSet(final int size);

    /**
     * Update itineraries land programs items based on the last import date stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateItems();
}
