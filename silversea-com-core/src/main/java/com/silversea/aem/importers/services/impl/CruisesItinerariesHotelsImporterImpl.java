package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesHotelsImporter;
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

            ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/hotel\"]");

            // Initializing elements necessary to import excursions
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImporterUtils.getItineraries(resourceResolver);

            // hotels
            final Map<Integer, Map<String, String>> hotelsMapping = ImporterUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/hotel\"]",
                    "hotelId");

            // Importing hotels
            List<HotelItinerary> hotels;
            int itemsWritten = 0;

            do {
                hotels = hotelsApi.hotelsGetItinerary(null, null, null, apiPage, pageSize, null);

                for (HotelItinerary hotel : hotels) {
                    final Integer hotelId = hotel.getHotelId();
                    boolean imported = false;

                    for (final ItineraryModel itineraryModel : itinerariesMapping) {
                        if (itineraryModel.isItinerary(hotel.getVoyageId(), hotel.getCityId(), hotel.getDate().toGregorianCalendar())) {
                            try {
                                final Resource itineraryResource = itineraryModel.getResource();

                                if (itineraryResource == null) {
                                    throw new ImporterException("Cannot get itinerary resource " + itineraryResource.getPath());
                                }

                                LOGGER.trace("importing hotel {} in itinerary {}", hotelId, itineraryResource.getPath());

                                final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                final Node hotelsNode = JcrUtils.getOrAddNode(itineraryNode, "hotels", "nt:unstructured");

                                if (hotelsNode.hasNode(String.valueOf(hotel.getHotelId()))) {
                                    throw new ImporterException("Hotel item already exists");
                                }

                                final Node hotelNode = hotelsNode.addNode(String.valueOf(hotel.getHotelId()));
                                final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                // associating port page
                                if (hotelsMapping.containsKey(hotelId)) {
                                    if (hotelsMapping.get(hotelId).containsKey(lang)) {
                                        hotelNode.setProperty("hotelReference", hotelsMapping.get(hotelId).get(lang));
                                    }
                                }

                                hotelNode.setProperty("hotelId", hotel.getHotelId());
                                hotelNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/hotel");

                                successNumber++;
                                itemsWritten++;
                                imported = true;

                                if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                    try {
                                        session.save();

                                        LOGGER.debug("{} hotels imported, saving session", +itemsWritten);
                                    } catch (RepositoryException e) {
                                        session.refresh(true);
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
                    }

                    LOGGER.trace("Hotel {} voyage id: {} city id: {} imported : {}", hotel.getHotelId(), hotel.getVoyageId(), hotel.getCityId(), imported);
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (hotels.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} itineraries hotels imported, saving session", +itemsWritten);
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

        LOGGER.info("Ending hotels import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        // TODO implement
        return null;
    }
}
