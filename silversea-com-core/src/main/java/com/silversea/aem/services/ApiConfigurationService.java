package com.silversea.aem.services;

/**
 * TODO important service, add javadoc
 */
public interface ApiConfigurationService {

    String apiUrlConfiguration(String api);

    String apiRootPath(String api);

    String getApiBaseDomain();

    String getLogin();

    String getPassword();
    
    int getPageSize() ;

    int getSessionRefresh();

    int getTimeout() ;
}
