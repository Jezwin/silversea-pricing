package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                // Iterating over land programs received from API
                for (LandItinerary landProgram : landPrograms) {

                    // Trying to deal with one land program
                    try {
                        final Integer landProgramId = landProgram.getLandId();
                        boolean imported = false;

                        if (!landProgramsMapping.containsKey(landProgramId)) {
                            throw new ImporterException("Land program " + landProgramId + " is not present in land programs cache");
                        }

                        // Iterating over itineraries in cache to write land program
                        for (final ItineraryModel itineraryModel : itinerariesMapping) {

                            // Checking if the itinerary correspond to land programs informations
                            if (itineraryModel.isItinerary(landProgram.getVoyageId(), landProgram.getDate().toGregorianCalendar())) {

                                // Trying to write land program data on itinerary
                                try {
                                    final Resource itineraryResource = itineraryModel.getResource();

                                    LOGGER.trace("importing land program {} in itinerary {}", landProgramId, itineraryResource.getPath());

                                    final Node itineraryNode = itineraryResource.adaptTo(Node.class);
                                    final Node landProgramsNode = JcrUtils.getOrAddNode(itineraryNode, "land-programs", "nt:unstructured");

                                    // TODO to check : getLandItineraryId() is not unique over API
                                    final Node landProgramNode = landProgramsNode.addNode(JcrUtil.createValidChildName(landProgramsNode,
                                            String.valueOf(landProgram.getLandItineraryId())));
                                    final String lang = LanguageHelper.getLanguage(pageManager, itineraryResource);

                                    // associating landProgram page
                                    if (landProgramsMapping.get(landProgramId).containsKey(lang)) {
                                        landProgramNode.setProperty("landProgramReference", landProgramsMapping.get(landProgramId).get(lang));
                                    }

                                    landProgramNode.setProperty("landProgramId", landProgramId);
                                    landProgramNode.setProperty("landProgramItineraryId", landProgram.getLandItineraryId());
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
                                } catch (RepositoryException e) {
                                    LOGGER.error("Cannot write land program {}", landProgram.getLandId(), e);

                                    errorNumber++;
                                }
                            }

                            if (size != -1 && itemsWritten >= size) {
                                break;
                            }
                        }

                        LOGGER.trace("Land program {} voyage id: {} city id: {} imported : {}", landProgram.getLandId(), landProgram.getVoyageId(), landProgram.getCityId(), imported);
                    } catch (ImporterException e) {
                        LOGGER.warn("Cannot deal with land program {} - {}", landProgram.getLandId(), e.getMessage());
                    }
                }

                if (size != -1 && itemsWritten >= size) {
                    break;
                }

                apiPage++;
            } while (landPrograms.size() > 0);

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

        LOGGER.debug("Ending itineraries land programs import, success: {}, errors: {}, api calls : {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateItems() {
        return null;
    }
}
