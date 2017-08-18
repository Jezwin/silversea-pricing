package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesHotelsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.model.HotelItinerary;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            final HotelsApi hotelsApi = new HotelsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing hotels deletion
            LOGGER.debug("Cleaning already imported hotels");

            ImportersUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/hotel\"]");

            // Initializing elements necessary to import hotels
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImportersUtils.getItineraries(resourceResolver);

            // hotels
            final Map<Integer, Map<String, String>> hotelsMapping = ImportersUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]",
                    "hotelId");

            // Importing hotels
            List<HotelItinerary> hotels;
            int itemsWritten = 0;

            do {
                hotels = hotelsApi.hotelsGetItinerary(null, null, null, apiPage, pageSize, null);

                // Iterating over hotels received from API
                for (HotelItinerary hotel : hotels) {

                    // Trying to deal with one excursion
                    try {
                        final Integer hotelId = hotel.getHotelId();
                        boolean imported = false;

                        if (!hotelsMapping.containsKey(hotelId)) {
                            throw new ImporterException("Hotel " + hotelId + " is not present in hotels cache");
                        }

                        // Iterating over itineraries in cache to write hotel
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to hotel informations
                            if (itineraryModel.isItinerary(hotel.getVoyageId(), hotel.getDate().toGregorianCalendar())) {

                                // Trying to write hotel data on itinerary
                                try {
                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("importing hotel {} in itinerary {}", hotelId, itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node hotelsNode = JcrUtils.getOrAddNode(itineraryNode, "hotels", "nt:unstructured");

                                    // TODO to check : getHotelItineraryId() is not unique over API
                                    if (!hotelsNode.hasNode(String.valueOf(hotel.getHotelItineraryId()))) {
                                        final Node hotelNode = hotelsNode.addNode(JcrUtil.createValidChildName(hotelsNode,
                                                String.valueOf(hotel.getHotelItineraryId())));
                                        final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                        // associating port page
                                        if (hotelsMapping.get(hotelId).containsKey(lang)) {
                                            hotelNode.setProperty("hotelReference", hotelsMapping.get(hotelId).get(lang));
                                        }

                                        hotelNode.setProperty("hotelId", hotelId);
                                        hotelNode.setProperty("hotelItineraryId", hotel.getHotelItineraryId());
                                        hotelNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/hotel");

                                        successNumber++;
                                        itemsWritten++;

                                        imported = true;

                                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                            try {
                                                session.save();

                                                LOGGER.info("{} hotels imported, saving session", +itemsWritten);
                                            } catch (RepositoryException e) {
                                                session.refresh(true);
                                            }
                                        }
                                    }
                                } catch (RepositoryException e) {
                                    LOGGER.error("Cannot write hotel {}", hotel.getHotelId(), e);

                                    errorNumber++;
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }

                        LOGGER.trace("Hotel {} voyage id: {} city id: {} imported: {}", hotel.getHotelId(), hotel.getVoyageId(), hotel.getCityId(), imported);
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot deal with hotel {} - {}", hotel.getHotelId(), e.getMessage());

                        errorNumber++;
                    }
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (hotels.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruisesItinerariesHotels", false);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.info("{} itineraries hotels imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
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

        LOGGER.info("Ending itineraries hotels import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
