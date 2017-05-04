package com.silversea.aem.services;

import java.io.IOException;
import java.util.Map;

public interface ApiConfigurationService {

    String apiUrlConfiguration(String api);

    String apiRootPath(String api);

    String getApiBaseDomain();

    String getLogin();

    String getPassword();

    // String getShipUrl();
    //
    // String getBrochureUrl();
    //
    // String getContriesUrl();
    //
    // String getSpetialOffersUrl();
    //
    // String getLandProgramUrl();
    //
    // String getShorexUrl();
    //
    // String getAgenciesUrl();
    //
    // String getCitiesUrl();

}
