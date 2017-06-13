package com.silversea.aem.services;

public interface ApiConfigurationService {

    String apiUrlConfiguration(String api);

    String apiRootPath(String api);

    String getApiBaseDomain();

    String getLogin();

    String getPassword();
    
    public int getPageSize() ;

    public int getSessionRefresh();

    public int getTimeout() ;
}
