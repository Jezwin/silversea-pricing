package com.silversea.aem.importers.services;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesItinerariesExcursionsImporter {

    /**
     * Import all itineraries excursions items, used for first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems(final boolean update) throws ImporterException;
}
