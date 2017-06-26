package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

/**
 * @author aurelienolivier
 */
public interface CitiesImporter {

    /**
     * Import all cities, used in first data import
     */
    ImportResult importAllCities();

    /**
     * Update the cities based on the last import date
     * stored in the cities root page
     */
    void updateCities();

    /**
     * Import only one city
     *
     * @param cityId the city ID of the city to import
     */
    void importOneCity(final String cityId);
}
