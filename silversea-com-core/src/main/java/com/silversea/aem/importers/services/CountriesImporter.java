package com.silversea.aem.importers.services;

import java.io.IOException;

import io.swagger.client.model.Country;

public interface CountriesImporter {

    void importCountries() throws IOException;

    void importCountry(final String iso2, Country country);

}
