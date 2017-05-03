package com.silversea.aem.services;

import java.io.IOException;
import java.util.Map;

public interface ApiConfigurationService {

    String apiUrlConfiguration(String api);

    void passwordSwagger();

    String getLogin();

    String getPassword();

    Map<String, String> getApi_path();

//    String getShipUrl();
//
//    String getBrochureUrl();
//
//    String getContriesUrl();
//
//    String getSpetialOffersUrl();
//
//    String getLandProgramUrl();
//
//    String getShorexUrl();
//
//    String getAgenciesUrl();
//
//    String getCitiesUrl();

}
