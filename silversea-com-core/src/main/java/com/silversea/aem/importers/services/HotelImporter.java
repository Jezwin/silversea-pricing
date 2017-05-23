package com.silversea.aem.importers.services;

import java.io.IOException;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
public interface HotelImporter {

    void importData() throws IOException;

    int getErrorNumber();
    
    int getSuccesNumber();
}
