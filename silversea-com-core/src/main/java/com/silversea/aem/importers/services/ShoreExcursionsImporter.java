package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface ShoreExcursionsImporter {

    /**
     * Import all shore excursions, used in first data import
     *
     * @return import result
     */
    ImportResult importAllShoreExcursions();

    /**
     * Update the shore excursions based on the last import date stored in the cities root page
     *
     * @return import result
     */
    ImportResult updateShoreExcursions();

    /**
     * Import only one shore excursion
     *
     * @param shoreExcursionId the shore excursion ID of the shore excursion to import
     */
    void importOneShoreExcursion(final String shoreExcursionId);
    
    /**
     * Disactive all shore excursion not present in API
     *
     */
    ImportResult disactiveAllItemDeltaByAPI();
}
