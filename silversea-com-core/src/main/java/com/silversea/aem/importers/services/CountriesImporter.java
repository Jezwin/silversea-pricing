package com.silversea.aem.importers.services;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

public interface CountriesImporter {

    ImportResult importData(final boolean update) throws ImporterException;

    JSONObject getCountriesMapping();
}
