package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface BrochuresImporter {

    /**
     * Import all brochures metadata from API
     *
     * @return import result
     */
    ImportResult importAllBrochures();

    /**
     * Update brochures metadata from API
     *
     * @return import result
     */
    ImportResult updateBrochures();
}
