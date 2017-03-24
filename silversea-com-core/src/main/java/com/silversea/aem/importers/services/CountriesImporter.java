package com.silversea.aem.importers.services;

import java.io.IOException;

public interface CountriesImporter {
    
    void importCities() throws IOException;
    
    void importCity(final String cityId);
    
}
