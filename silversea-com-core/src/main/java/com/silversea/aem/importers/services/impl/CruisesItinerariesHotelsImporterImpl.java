package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.services.CruisesItinerariesImporter;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.HotelItinerary;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

/**
 * TODO add last import date
 */
@Service
@Component
public class CruisesItinerariesHotelsImporterImpl implements CruisesItinerariesHotelsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesHotelsImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

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
        return importSampleSet(-1);
    }

    @Override
    public ImportResult importSampleSet(int size) {
        LOGGER.debug("Starting hotels import ({})", size == -1 ? "all" : size);

        int successNumber = 0;
        int errorNumber = 0;
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final HotelsApi hotelsApi = new HotelsApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing hotels deletion
            LOGGER.debug("Cleaning already imported hotels");

            final Iterator<Resource> existingHotels = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/hotel\"]", "xpath");

            int i = 0;
            while (existingHotels.hasNext()) {
                final Resource hotel = existingHotels.next();

                final Node hotelNode = hotel.adaptTo(Node.class);

                if (hotelNode != null) {
                    try {
                        hotelNode.remove();

                        i++;
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot remove existing hotel {}", hotel.getPath(), e);
                    }
                }

                if (i % sessionRefresh == 0 && session.hasPendingChanges()) {
                    try {
                        session.save();

                        LOGGER.debug("{} hotels cleaned, saving session", +i);
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} hotels cleaned, saving session", +i);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            // Initializing elements necessary to import hotels
            // itineraries
            final Iterator<Resource> itinerariesForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary\"]", "xpath");

            final Map<Integer, List<String>> itinerariesMapping = new HashMap<>();
            while (itinerariesForMapping.hasNext()) {
                final Resource itinerary = itinerariesForMapping.next();

                final Integer itineraryId = itinerary.getValueMap().get("itineraryId", Integer.class);

                if (itineraryId != null) {
                    if (itinerariesMapping.containsKey(itineraryId)) {
                        itinerariesMapping.get(itineraryId).add(itinerary.getPath());
                    } else {
                        final List<String> itineraryPaths = new ArrayList<>();
                        itineraryPaths.add(itinerary.getPath());
                        itinerariesMapping.put(itineraryId, itineraryPaths);
                    }

                    LOGGER.trace("Adding itinerary {} ({}) to cache", itinerary.getPath(), itineraryId);
                }
            }

            // hotels
            final Iterator<Resource> hotelsForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]", "xpath");

            final Map<Integer, Map<String, String>> hotelsMapping = new HashMap<>();
            while (hotelsForMapping.hasNext()) {
                final Resource hotel = hotelsForMapping.next();

                final Page hotelPage = hotel.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(hotelPage);

                final Integer hotelId = hotel.getValueMap().get("hotelId", Integer.class);

                if (hotelId != null) {
                    if (hotelsMapping.containsKey(hotelId)) {
                        hotelsMapping.get(hotelId).put(language, hotelPage.getPath());
                    } else {
                        final HashMap<String, String> hotelPaths = new HashMap<>();
                        hotelPaths.put(language, hotelPage.getPath());
                        hotelsMapping.put(hotelId, hotelPaths);
                    }

                    LOGGER.trace("Adding hotel {} ({}) with lang {} to cache", hotel.getPath(), hotelId, language);
                }
            }

            // Importing hotels
            List<HotelItinerary> hotels;
            int itemsWritten = 0;

            do {
                hotels = hotelsApi.hotelsGetItinerary(null, null, null, apiPage, pageSize, null);

                for (HotelItinerary hotel : hotels) {
                    LOGGER.trace("importing hotel {} in itinerary {}", hotel.getHotelId(), hotel.getHotelItineraryId());

                    try {
                        final List<String> itineraryPaths = itinerariesMapping.get(hotel.getHotelItineraryId());

                        if (itineraryPaths == null) {
                            throw new ImporterException("Cannot find itinerary with id " + hotel.getHotelItineraryId());
                        }

                        for (String itineraryPath : itineraryPaths) {
                            final Resource itineraryResource = resourceResolver.getResource(itineraryPath);

                            if (itineraryResource == null) {
                                throw new ImporterException("Cannot get itinerary resource " + itineraryPath);
                            }

                            final Node itineraryNode = itineraryResource.adaptTo(Node.class);

                            if (itineraryNode.hasNode(String.valueOf(hotel.getHotelId()))) {
                                throw new ImporterException("Hotel item already exists");
                            }

                            final Node hotelNode = itineraryNode.addNode(String.valueOf(hotel.getHotelId()));
                            final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                            // associating port page
                            final Integer hotelId = hotel.getHotelId();
                            if (hotelsMapping.containsKey(hotelId)) {
                                if (hotelsMapping.get(hotelId).containsKey(lang)) {
                                    hotelNode.setProperty("hotelReference", hotelsMapping.get(hotelId).get(lang));
                                }
                            }

                            itineraryNode.setProperty("hotelId", hotel.getHotelId());
                            itineraryNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/hotel");

                            successNumber++;
                            itemsWritten++;

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.error("Cannot write hotel {}", hotel.getHotelId(), e);

                        errorNumber++;
                    }
                }


                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (hotels.size() > 0 && size != -1 && itemsWritten < size);

        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import hotels", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read hotels from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending hotels import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        return null;
    }
}
