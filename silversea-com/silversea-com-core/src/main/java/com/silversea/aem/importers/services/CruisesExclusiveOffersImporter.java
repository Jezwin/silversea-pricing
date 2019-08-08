package com.silversea.aem.importers.services;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesExclusiveOffersImporter {

    /**
     * Import all links between cruises and exclusive offers, manage the diff update as there is no specific endpoint in
     * the API
     *
     * @return result of the import
     */
    ImportResult importAllItems() throws ImporterException;
}
