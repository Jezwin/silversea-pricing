package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface CruisesPricesImporter {

    /**
     * Import all prices, used in first data import
     *
     * @return result of the import
     */
    ImportResult importAllItems();

    /**
     * Update the prices based on the last import date stored in the cruises root page
     *
     * @return result of the import
     */
    ImportResult updateItems();
}
