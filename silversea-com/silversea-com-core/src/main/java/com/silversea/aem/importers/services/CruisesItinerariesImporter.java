package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesItinerariesImporter {

    /**
     * Import all itineraries items, used for first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems(final boolean update);
}
