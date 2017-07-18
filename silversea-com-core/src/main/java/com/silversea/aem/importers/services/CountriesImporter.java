package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

import java.io.IOException;

public interface CountriesImporter {

    ImportResult importData() throws IOException;

    void importCountry(final String id);

    JSONObject getCountriesMapping();
}
