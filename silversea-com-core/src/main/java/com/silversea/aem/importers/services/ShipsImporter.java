package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

/**
 *
 */
@Deprecated
public interface ShipsImporter {

    /**
     * Import all ships, used in first data import
     */
    ImportResult importAllShips();

    /**
     * Update the ships based on the last import date
     * stored in the ships root page
     */
    void updateShips();

    /**
     * Import only one ship
     *
     * @param shipId the ship ID of the ship to import
     */
    void importOneShip(final String shipId);
}
