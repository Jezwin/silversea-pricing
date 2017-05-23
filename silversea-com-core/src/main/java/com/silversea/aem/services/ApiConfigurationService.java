package com.silversea.aem.services;

import java.io.IOException;
import java.util.Map;

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
