package com.silversea.aem.importers.services;

import java.io.IOException;

public interface CountriesImporter {
    void importData() throws IOException;

    void importCountry(final String id);

}
