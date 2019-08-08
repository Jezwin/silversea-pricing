package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.model.City;
import io.swagger.client.model.City77;

@Service
@Component
public class CitiesImporterImpl implements CitiesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CitiesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;
    
    @Reference
    private MimeTypeService mimeTypeService;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
    }

    @Override
    public ImportResult importAllItems() {
        LOGGER.debug("Starting cities import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CitiesApi citiesApi = new CitiesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/portslist\"]");

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("citiesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            // Iterating over locales to import cities
            for (String locale : locales) {
                final Page citiesRootPage = ImportersUtils.getPagePathByLocale(pageManager, rootPage, locale);

                if (citiesRootPage == null) {
                    throw new ImporterException("Cities root page does not exists");
                }

                LOGGER.debug("Importing cities for locale \"{}\"", locale);

                int itemsWritten = 0, apiPage = 1;
                List<City> cities;

                do {
                    cities = citiesApi.citiesGet(null, null, apiPage, pageSize, null, null, null);

                    for (City city : cities) {
                        LOGGER.trace("Importing city: {}", city.getCityName());

                        try {
                            final Page portPage = createPortPage(pageManager, citiesRootPage, city.getCityName());

                            LOGGER.trace("Creating port {}", city.getCityName());

                            // If port is created, set the properties
                            if (portPage == null) {
                                throw new ImporterException("Cannot create port page for city " + city.getCityName());
                            }

                            Node portPageContentNode = portPage.getContentResource().adaptTo(Node.class);

                            if (portPageContentNode == null) {
                                throw new ImporterException("Cannot set properties for city " + city.getCityName());
                            }

                            portPageContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
                            portPageContentNode.setProperty("apiTitle", city.getCityName());
                            portPageContentNode.setProperty("apiDescription", city.getShortDescription());
                            portPageContentNode.setProperty("apiLongDescription", city.getDescription());
                            portPageContentNode.setProperty("cityCode", city.getCityCod());
                            portPageContentNode.setProperty("cityId", city.getCityId());
                            portPageContentNode.setProperty("latitude", city.getLatitude());
                            portPageContentNode.setProperty("longitude", city.getLongitude());
                            portPageContentNode.setProperty("countryId", city.getCountryId());
                            portPageContentNode.setProperty("countryIso2", city.getCountryIso2());
                            portPageContentNode.setProperty("countryIso3", city.getCountryIso3());

                            // Set livecopy mixin
                            if (!locale.equals("en")) {
                                portPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Port {} successfully created", portPage.getPath());

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} cities imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        } catch (RepositoryException | ImporterException e) {
                            errorNumber++;

                            LOGGER.error("Import error", e);
                        }
                    }

                    apiPage++;
                } while (cities.size() > 0);
            }

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"), "lastModificationDate");
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cities imported, saving session");
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException | RepositoryException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        }

        LOGGER.debug("Ending cities import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }
    
    public ImportResult DesactivateUselessPort(){
    	int successNumber = 0;
    	int errorNumber = 0;
    	
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
    	
    	 try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
             final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
             final Session session = resourceResolver.adaptTo(Session.class);
             

             if (pageManager == null || session == null) {
                 throw new ImporterException("Cannot initialize pageManager and session");
             }

             final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));

             final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils.getItemsPageMapping(resourceResolver, //<cityid,portpage>
                     "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                             "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "cityId");
             
             final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                     "/jcr:root/content/silversea-com/en//element(*,cq:PageContent)" +
                             "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                     "cruiseId");

             
             List<CruiseModel> allCruises = new ArrayList<>();
             
             for (Integer cruiseMapIds : cruisesMapping.keySet()) {
            	 for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(cruiseMapIds).entrySet()) {
            		 if(cruisePages.getValue().getContentResource().isResourceType(WcmConstants.RT_CRUISE)){
	                     CruiseModel cruiseTemp = cruisePages.getValue().adaptTo(CruiseModel.class);
	                     if(cruiseTemp != null){
	                    	 allCruises.add(cruiseTemp);
	                    	 //LOGGER.debug("adding a cruise in all cruises");
	                     }
            		 }
            	 }
			}
             
             LOGGER.debug("all cruises built");
         	
         	 for (Integer portKeyId : portsMapping.keySet()) {
				//Check for each port if we should desactivate the page
         		 Boolean shouldDesactivate = true;
         		 
         		 for (CruiseModel cruiseModelLight : allCruises) {
         			 if(cruiseModelLight != null && cruiseModelLight.getItineraries() != null) {
	         			 for (ItineraryModel portItem : cruiseModelLight.getItineraries()) {
	         				 if(portItem != null) {
		         				 if(portItem.getPort() != null){
		         					if(portItem.getPort().getCityId() != null){
										if(portItem.getPort().getCityId().equals(portKeyId)){ //pk t null ?
											if( cruiseModelLight.getStartDate().after(Calendar.getInstance())){
												shouldDesactivate = false;
												break;
											}
										}
		         					}
		         				 }
	         				 }
							if(!shouldDesactivate){
								break;
							}
						}
         			}
				}
         		 
         		if(shouldDesactivate){
         			//Mark the page as to be deactivated.
         			for (Map.Entry<String, Page> portMapPage : portsMapping.get(portKeyId).entrySet()) {
         				Page portPage = portMapPage.getValue();
         				final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

                        if (portContentNode != null) {
                        	if(portContentNode.hasProperty("cq:lastReplicationAction")){
	                        	if(portContentNode.getProperty("cq:lastReplicationAction").getString().equals("Activate")){
		                        	LOGGER.debug("{} city will be desactivated", portPage.getPath());
		                        	portContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
		                        	successNumber++;
	                        	}
                        	}else{
                        		LOGGER.debug("{} city will be desactivated", portPage.getPath());
	                        	portContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
	                        	successNumber++;
                        	}
                        }else{
                        	errorNumber++;
                        }
         			}
         			
         			if (successNumber % sessionRefresh == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} cities desactivated, saving session", +successNumber);
                        } catch (RepositoryException e) {
                            session.refresh(true);
                        }
                    }
         		}else{ //Activate if we now have a cruise that we were not having before	
         			for (Map.Entry<String, Page> portMapPage : portsMapping.get(portKeyId).entrySet()) {
         				Page portPage = portMapPage.getValue();
         				final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

                        if (portContentNode != null) {
                        	if(portContentNode.hasProperty("cq:lastReplicationAction")){
	                        	if(!portContentNode.getProperty("cq:lastReplicationAction").getString().equals("Activate")){
		                        	LOGGER.debug("{} city will be activated", portPage.getPath());
		                        	portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
		                        	successNumber++;
	                        	}
                        	}else{
                        		LOGGER.debug("{} city will be activated", portPage.getPath());
	                        	portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
	                        	successNumber++;
                        	}
                        }else{
                        	errorNumber++;
                        }
         			}
         			
         			if (successNumber % sessionRefresh == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} cities desactivated/activated, saving session", +successNumber);
                        } catch (RepositoryException e) {
                            session.refresh(true);
                        }
                    }
         		}
			}
         	 
         	 if (session.hasPendingChanges()) {
                 try {
                     session.save();

                     LOGGER.debug("{} cities desactived, saving session", +successNumber);
                 } catch (RepositoryException e) {
                     session.refresh(false);
                 }
             }
             
             
    	 } catch (LoginException | ImporterException | RepositoryException e) {
             LOGGER.error("Cannot create resource resolver", e);
         } 
    	
    	return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateItems() {
        LOGGER.debug("Starting cities update");

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final CitiesApi citiesApi = new CitiesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDate");

            LOGGER.debug("Last import date for ports {}", lastModificationDate);

            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "cityId");

            int itemsWritten = 0;
            List<City77> cities;

            do {
                final ApiResponse<List<City77>> apiResponse = citiesApi.citiesGetChangesWithHttpInfo(lastModificationDate, apiPage, pageSize, null, null, null);
                cities = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total cities : {}, page : {}, cities for this page : {}", cities.size(), apiPage, cities.size());

                for (City77 city : cities) {
                    LOGGER.debug("Updating city: {}", city.getCityName());

                    try {
                        if (portsMapping.containsKey(city.getCityId())) {
                            // if ports are found, update it
                            for (Map.Entry<String, Page> ports : portsMapping.get(city.getCityId()).entrySet()) {
                                final Page portPage = ports.getValue();

                                LOGGER.trace("Updating port {}", city.getCityName());

                                if (portPage == null) {
                                    throw new ImporterException("Cannot set port page " + city.getCityName());
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(city.getIsDeleted())) {
                                    final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

                                    if (portContentNode == null) {
                                        throw new ImporterException("Cannot set properties for city " + city.getCityName());
                                    }

                                    portContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                                    LOGGER.trace("Port {} is marked to be deactivated", city.getCityName());
                                } else {
                                    final Node portContentNode = updatePortContentNode(city, portPage, session, mimeTypeService,resourceResolver, false);

                                    portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Port {} is marked to be activated", city.getCityName());
                                }
                            }
                        } else {
                            // else create port page for each language
                            for (final String locale : locales) {
                                final Page citiesRootPage = ImportersUtils
                                        .getPagePathByLocale(pageManager, rootPage, locale);
                                final Page portPage = createPortPage(pageManager, citiesRootPage, city.getCityName());

                                LOGGER.trace("Creating port {}", city.getCityName());

                                // If port is created, set the properties
                                if (portPage == null) {
                                    throw new ImporterException("Cannot create port page for city " + city.getCityName());
                                }

                                final Node portContentNode = updatePortContentNode(city, portPage, session, mimeTypeService, resourceResolver, true);
                                portContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Port {} successfully created", portPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} cities imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.warn("Import error {}", e.getMessage());
                    }
                }

                apiPage++;
            } while (cities.size() > 0);
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cities imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("citiesUrl"), "lastModificationDate", true);
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending cities update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }
    
    @Override
	public ImportResult importAllPortImages() {
		LOGGER.info("Starting import all port images API");

		int successNumber = 0, errorNumber = 0;

		Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Session session = resourceResolver.adaptTo(Session.class);

			CitiesApi citiesApi = new CitiesApi(ImportersUtils.getApiClient(apiConfig));
			
			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}  

			// excursions mapping
			Map<Integer, Map<String, Page>> citiesMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
					 "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
	                            "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "cityId");

			int itemsWritten = 0, page = 1, perPage = 100;
			City itemToCheck = null;
			List<City> citiesAPI = citiesApi.citiesGet(null, null, page, perPage, null, null, null);
			List<City> citiesListAPI = new ArrayList<>();
			LOGGER.info("Check all cities in jcr: {}", citiesListAPI.size());
			
			while(citiesAPI.size() != 0){
				citiesListAPI.addAll(citiesAPI);
				page++;
				citiesAPI = citiesApi.citiesGet(null, null, page, perPage, null, null, null);
			}
			
			for (Map.Entry<Integer, Map<String, Page>> city : citiesMapping.entrySet()) {
				itemToCheck = null;
				Integer cityID = city.getKey();
				LOGGER.debug("Check cityID: {}", cityID);

				for (City cAPI : citiesListAPI) {

					if (cityID.equals(cAPI.getCityId())) {
						itemToCheck = cAPI;
					}
				}
				if (itemToCheck != null) {
					for (Map.Entry<String, Page> citiiePages : citiesMapping
							.get(cityID).entrySet()) {
						Page cityPage = citiiePages.getValue();

						LOGGER.debug("Updating port {}", cityID);
						
						if (cityPage == null) {
							throw new ImporterException(
									"Cannot set city page " + cityID);
						}
						Node portContentNode = cityPage.getContentResource().adaptTo(Node.class);
						associateThumbnail(session, portContentNode, itemToCheck.getPicUrl(), mimeTypeService, resourceResolver);
					}

					successNumber++;
					itemsWritten++;

				}
				try {
					if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
						try {
							session.save();

							LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
						} catch (RepositoryException e) {
							session.refresh(true);
						}
					}
				} catch (RepositoryException e) {
					errorNumber++;

					LOGGER.warn("Import error {}", e.getMessage());
				}
			}


			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.debug("{} port image imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException | ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read excursions from API", e);
		} catch (RepositoryException e) {
			LOGGER.error("Error writing data", e);
		}

		LOGGER.debug("Ending port image imported, success: {}, error: {}", +successNumber, +errorNumber);

		return new ImportResult(successNumber, errorNumber);
	}

    @Override
    public void importOneItem(final String cityId) {
        // TODO
    }

    @Override
    public JSONObject getJsonMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            Iterator<Resource> cities = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "xpath");

            while (cities.hasNext()) {
                Resource city = cities.next();

                Resource childContent = city.getChild(JcrConstants.JCR_CONTENT);

                if (childContent != null) {
                    ValueMap childContentProperties = childContent.getValueMap();
                    String cityId = childContentProperties.get("cityId", String.class);

                    if (cityId != null) {
                        try {
                            if (jsonObject.has(cityId)) {
                                final JSONArray jsonArray = jsonObject.getJSONArray(cityId);
                                jsonArray.put(city.getPath());

                                jsonObject.put(cityId, jsonArray);
                            } else {
                                jsonObject.put(cityId, Collections.singletonList(city.getPath()));
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add city {} with path {} to cities array", cityId, city.getPath(), e);
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        }

        return jsonObject;
    }

    /**
     * Create a port page, based on path convention
     * /cities/root/page/first-port-name-letter/port-name
     *
     * @param pageManager pageManager used to create the pages
     * @param citiesRootPage root page of the cities in the content tree
     * @param cityName the name of the city
     * @return the port created page
     * @throws ImporterException if the page cannot be created
     */
    private Page createPortPage(PageManager pageManager, Page citiesRootPage, String cityName) throws ImporterException {
        String portFirstLetter;

        try {
            // Port parent page initialization
            portFirstLetter = String.valueOf(cityName.charAt(0));

            final String portFirstLetterName = JcrUtil.createValidName(portFirstLetter);

            Page portFirstLetterPage = pageManager
                    .getPage(citiesRootPage.getPath() + "/" + portFirstLetterName);

            if (portFirstLetterPage == null) {
                portFirstLetterPage = pageManager.create(citiesRootPage.getPath(),
                        portFirstLetterName, WcmConstants.PAGE_TEMPLATE_PORTS_LIST, portFirstLetter,
                        false);

                // Set livecopy mixin
                if (!LanguageHelper.getLanguage(citiesRootPage).equals("en")) {
                    portFirstLetterPage.getContentResource().adaptTo(Node.class).addMixin("cq:LiveRelationship");
                }

                LOGGER.trace("{} page is not existing, creating it", portFirstLetterName);
            }

            // Creating port page
            return pageManager.create(portFirstLetterPage.getPath(),
                    JcrUtil.createValidChildName(portFirstLetterPage.adaptTo(Node.class),
                            StringsUtils.getFormatWithoutSpecialCharacters(cityName)),
                    WcmConstants.PAGE_TEMPLATE_PORT,
                    StringsUtils.getFormatWithoutSpecialCharacters(cityName), false);
        } catch (RepositoryException | WCMException e) {
            throw new ImporterException("Port page cannot be created", e);
        }
    }

    /**
     * Update port properties from API
     *
     * @param city city object from API
     * @param portPage page of the port
     * @return the content node of the port page, updated
     * @throws ImporterException if the port page cannot be updated
     */
    private Node updatePortContentNode(City77 city, Page portPage, Session session, final MimeTypeService mimeTypeService,
            final ResourceResolver resourceResolver, Boolean isCreate ) throws ImporterException {
        final Node portContentNode = portPage.getContentResource().adaptTo(Node.class);

        if (portContentNode == null) {
            throw new ImporterException("Cannot set properties for city " + city.getCityName());
        }

        try {
        	if(isCreate) {
        		portContentNode.setProperty(JcrConstants.JCR_TITLE, city.getCityName());
        	}
            portContentNode.setProperty("apiTitle", city.getCityName());
            portContentNode.setProperty("apiDescription", city.getShortDescription());
            portContentNode.setProperty("apiLongDescription", city.getDescription());
            portContentNode.setProperty("cityCode", city.getCityCod());
            portContentNode.setProperty("cityId", city.getCityId());
            portContentNode.setProperty("latitude", city.getLatitude());
            portContentNode.setProperty("longitude", city.getLongitude());
            portContentNode.setProperty("countryId", city.getCountryId());
            portContentNode.setProperty("countryIso2", city.getCountryIso2());
            portContentNode.setProperty("countryIso3", city.getCountryIso3());
            associateThumbnail(session, portContentNode, city.getPicUrl(), mimeTypeService, resourceResolver);

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                portContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for city " + city.getCityName(), e);
        }

        return portContentNode;
    }
    
    public static void associateThumbnail(final Session session, final Node portContentNode,
            String mapUrl, final MimeTypeService mimeTypeService,
            final ResourceResolver resourceResolver) throws RepositoryException {

		final AssetManager assetManager = resourceResolver
				.adaptTo(AssetManager.class);

		// download and associate thumbnail
		if (StringUtils.isNotEmpty(mapUrl)) {
			try {
				final String filename = StringUtils.substringAfterLast(mapUrl,
						"/");

				final String assetPath = "/content/dam/silversea-com/api-provided/ports/"
						+ portContentNode.getParent().getName().charAt(0)
						+ "/"
						+ portContentNode.getParent().getName()
						+ "/"
						+ filename;


				boolean updateAsset = false;
				if (portContentNode.hasNode("image") == false) {
					updateAsset = true;
				}

				if (updateAsset) {
					LOGGER.info("Creating thumbnail asset {}", assetPath);
					mapUrl = mapUrl.replace("http:", "https:");
					final InputStream mapStream = new URL(mapUrl).openStream();
					final Asset asset = assetManager.createAsset(assetPath,
							mapStream, mimeTypeService.getMimeType(mapUrl),
							false);
					LOGGER.info("Creating thumbnail asset {} SAVED.", assetPath);
					// setting to activate flag on asset
					final Node assetNode = asset.adaptTo(Node.class);
					if (assetNode != null) {
						final Node assetContentNode = assetNode
								.getNode(JcrConstants.JCR_CONTENT);

						if (assetContentNode != null) {
							assetContentNode.setProperty(
									ImportersConstants.PN_TO_ACTIVATE, true);
							portContentNode.setProperty(
									ImportersConstants.PN_TO_ACTIVATE, true);
						}
					}
					Node imageNode = JcrUtils.getOrAddNode(portContentNode, "image", "nt:unstructured");
					
					imageNode.setProperty("fileReference", asset.getPath());

					LOGGER.trace("Creating port thumbnail asset {}", assetPath);
				}

			} catch (IOException e) {
				LOGGER.warn("Cannot import port thumbnail image {}", mapUrl);
			} catch (Exception e){
				LOGGER.warn("Error while importing port thumbnail {}", mapUrl);
			}
		}
    }
}
