package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesItinerariesLandProgramsImporter;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.LandsApi;
import io.swagger.client.model.LandItinerary;
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

@Service
@Component
public class CruisesItinerariesLandProgramsImporterImpl implements CruisesItinerariesLandProgramsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesItinerariesLandProgramsImporterImpl.class);

    protected int sessionRefresh = 100;
    protected int pageSize = 100;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected ApiConfigurationService apiConfig;

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
        LOGGER.debug("Starting land programs import ({})", size == -1 ? "all" : size);

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

            final LandsApi landsApi = new LandsApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Existing landPrograms deletion
            LOGGER.debug("Cleaning already imported land programs");

            ImporterUtils.deleteResources(resourceResolver, sessionRefresh, "/jcr:root/content/silversea-com"
                    + "//element(*,nt:unstructured)[sling:resourceType=\"silversea/silversea-com/components/subpages/itinerary/landprogram\"]");

            // Initializing elements necessary to import landPrograms
            // itineraries
            final List<ItineraryModel> itinerariesMapping = ImporterUtils.getItineraries(resourceResolver);

            // landPrograms
            final Map<Integer, Map<String, String>> landProgramsMapping = ImporterUtils.getItemsMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"]",
                    "landId");

            // Importing landPrograms
            List<LandItinerary> landPrograms;
            int itemsWritten = 0;

            do {
                landPrograms = landsApi.landsGetItinerary(null, null, null, apiPage, pageSize, null);

                for (LandItinerary landProgram : landPrograms) {
                    final Integer landProgramId = landProgram.getLandId();
                    boolean imported = false;

                    for (final ItineraryModel itineraryModel : itinerariesMapping) {
                        if (itineraryModel.isItinerary(landProgram.getVoyageId(), landProgram.getCityId(), landProgram.getDate().toGregorianCalendar())) {
                            try {
                                final Resource itineraryResource = itineraryModel.getResource();

                                if (itineraryResource == null) {
                                    throw new ImporterException("Cannot get itinerary resource " + itineraryResource.getPath());
                                }

                                LOGGER.trace("importing hotel {} in itinerary {}", landProgramId, itineraryResource.getPath());

                                final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                final Node landProgramsNode = JcrUtils.getOrAddNode(itineraryNode, "land-programs", "nt:unstructured");

                                if (landProgramsNode.hasNode(String.valueOf(landProgramId))) {
                                    throw new ImporterException("Excursion item already exists");
                                }

                                final Node landProgramNode = landProgramsNode.addNode(String.valueOf(landProgramId));
                                final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                // associating landProgram page
                                if (landProgramsMapping.containsKey(landProgramId)) {
                                    if (landProgramsMapping.get(landProgramId).containsKey(lang)) {
                                        landProgramNode.setProperty("landProgramReference", landProgramsMapping.get(landProgramId).get(lang));
                                    }
                                }

                                landProgramNode.setProperty("landProgramId", landProgramId);
                                landProgramNode.setProperty("date", landProgram.getDate().toGregorianCalendar());
                                landProgramNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/itinerary/landprogram");

                                successNumber++;
                                itemsWritten++;
                                imported = true;

                                if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                    try {
                                        session.save();

                                        LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
                                    } catch (RepositoryException e) {
                                        session.refresh(true);
                                    }
                                }
                            } catch (RepositoryException | ImporterException e) {
                                LOGGER.error("Cannot write land program {}", landProgram.getLandId(), e);

                                errorNumber++;
                            }
                        }

                        if (size != -1 && itemsWritten >= size) {
                            break;
                        }
                    }

                    LOGGER.trace("Land program {} voyage id: {} city id: {} imported : {}", landProgram.getLandId(), landProgram.getVoyageId(), landProgram.getCityId(), imported);
                }

                apiPage++;
            }
            while (landPrograms.size() > 0);

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
            LOGGER.error("Cannot import land programs", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending land programs import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        return null;
    }
}
