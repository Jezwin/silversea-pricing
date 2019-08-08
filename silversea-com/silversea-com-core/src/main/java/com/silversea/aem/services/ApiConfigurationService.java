package com.silversea.aem.services;

/**
 * TODO important service, add javadoc
 */
public interface ApiConfigurationService {

    /**
     * @return the API base path, in Swagger format (http://shop.silversea.com/apit
     * or http://shop.silversea.com/api)
     */
    String apiBasePath();

    /**
     * TODO rename
     *
     * Rename the AEM root folder corresponding to one of the following key :
     * <ul>
     *     <li>shipUrl</li>
     *     <li>brochureUrl</li>
     *     <li>featuresUrl</li>
     *     <li>countriesUrl</li>
     *     <li>exclusiveOffersUrl</li>
     *     <li>agenciesUrl</li>
     *     <li>citiesUrl</li>
     *     <li>cruisesUrl</li>
     * </ul>
     *
     * @param api endpoint
     * @return the AEM root folder corresponding to the <code>api</code> endpoint
     */
    String apiRootPath(final String api);

    /**
     * @return API login
     */
    String getLogin();

    /**
     * @return API username
     */
    String getPassword();

    /**
     * @return number of items per page from API
     */
    int getPageSize() ;

    /**
     * TODO rename
     *
     * @return number of transactions before saving session
     */
    int getSessionRefresh();

    /**
     * TODO split in two configurations
     *
     * @return the API timeout, used for connection timeout and read timeout
     */
    int getTimeout();
}
