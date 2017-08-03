package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesItinerariesImporter {

    /**
     * Import all itineraries items, used for first data import
     *
     * @return result of the import
     */
    ImportResult importAllCruisesItineraries();

    /**
     * Import a sample set of itineraries, used for test purpose (import is full)
     *
     * @param itinerariesNumber number of itineraries to import
     * @return result of the import
     */
    ImportResult importSampleSetItineraries(final int itinerariesNumber);

    /**
     * Update itineraries items based on the last import date
     * stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateCruisesItineraries();
}
