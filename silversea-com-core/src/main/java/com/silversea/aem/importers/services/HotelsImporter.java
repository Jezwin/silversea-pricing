package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
public interface HotelsImporter {

    /**
     * Import all cities, used in first data import
     */
    ImportResult importAllHotels();

    /**
     * Update the hotels based on the last import date
     * stored in the cities root page
     */
    ImportResult updateHotels();

    /**
     * Import only one hotel
     * @param hotelId the hotel ID of the hotel to import
     */
    void importOneHotel(final String hotelId);
}
