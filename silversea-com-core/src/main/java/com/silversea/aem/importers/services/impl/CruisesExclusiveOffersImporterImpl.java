package com.silversea.aem.importers.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesExclusiveOffersImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.VoyageSpecialOffersApi;
import io.swagger.client.model.SpecialOfferByMarket;
import io.swagger.client.model.VoyageSpecialOffer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import java.util.*;

@Component
@Service
public class CruisesExclusiveOffersImporterImpl implements CruisesExclusiveOffersImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesExclusiveOffersImporterImpl.class);

    protected int sessionRefresh = 100;
    protected int pageSize = 100;

    private boolean importRunning;

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
    public ImportResult importAllItems() throws ImporterException {
        if (importRunning) {
            throw new ImporterException("Import is already running");
        }

        LOGGER.debug("Starting mapping exclusive offers/cruises import");
        importRunning = true;

        final ImportResult importResult = new ImportResult();
        int apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final VoyageSpecialOffersApi voyageSpecialOffersApi = new VoyageSpecialOffersApi(
                    ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Initializing elements necessary to import exclusive offers
            // cruises mapping
            final Map<Integer, Map<String, Page>> cruisesMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]",
                    "cruiseId");

            // exclusive offers mapping
            final Map<Integer, Map<String, String>> exclusiveOffersMapping = ImportersUtils.getItemsMapping
                    (resourceResolver, "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                                    "[sling:resourceType=\"silversea/silversea-com/components/pages/exclusiveoffer\"]",
                            "exclusiveOfferId");

            List<VoyageSpecialOffer> voyageSpecialOffers;
            int itemsWritten = 0;

            // iterate over API pages
            do {
                voyageSpecialOffers = voyageSpecialOffersApi.voyageSpecialOffersGet(null, null, apiPage, pageSize,
                        null);

                LOGGER.trace("Page : {}, voyages/exclusive offers for this page : {}", apiPage,
                        voyageSpecialOffers.size());

                // iterate over exclusive offers of the current page
                for (VoyageSpecialOffer voyageSpecialOffer : voyageSpecialOffers) {
                    LOGGER.trace("Importing exclusive offers infos for cruise ID {}", voyageSpecialOffer.getVoyageId());

                    try {
                        final Integer voyageId = voyageSpecialOffer.getVoyageId();

                        // check if cruise exists in CRX
                        if (!cruisesMapping.containsKey(voyageId)) {
                            throw new ImporterException("Cruise " + voyageId + " not present in mapping");
                        }

                        // iterate over the cruises found in the mapping
                        for (Map.Entry<String, Page> cruisePages : cruisesMapping.get(voyageId).entrySet()) {
                            try {
                                // getting cruise informations
                                final Page cruisePage = cruisePages.getValue();

                                final ValueMap cruiseProperties = cruisePage.getProperties();
                                final String[] exclusiveOffersPty = cruiseProperties.get("offer",
                                        String[].class);
                                final Node cruiseContentNode = cruisePage.getContentResource().adaptTo(Node.class);

                                if (cruiseContentNode == null) {
                                    throw new ImporterException("Cannot get cruise content node for cruise " + cruisePage.getPath());
                                }

                                // build list of the exclusive paths for the current lang of the cruise
                                final Set<String> voyageSpecialOffersPaths = new HashSet<>();
                                for (SpecialOfferByMarket specialOffer : voyageSpecialOffer.getSpecialOffers()) {
                                    if (exclusiveOffersMapping.containsKey(specialOffer.getVoyageSpecialOfferId())) {
                                        final Map<String, String> exclusiveOfferPaths = exclusiveOffersMapping.get(
                                                specialOffer.getVoyageSpecialOfferId());

                                        if (exclusiveOfferPaths.containsKey(cruisePages.getKey())) {
                                            voyageSpecialOffersPaths.add(exclusiveOfferPaths.get(cruisePages.getKey()));
                                        }
                                    }
                                }

                                // nothing in exclusive offers property
                                if (exclusiveOffersPty == null || exclusiveOffersPty.length == 0) {
                                    if (voyageSpecialOffersPaths.size() > 0) {
                                        cruiseContentNode.setProperty("offer", voyageSpecialOffersPaths
                                                .toArray(new String[voyageSpecialOffersPaths.size()]));

                                        cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                        LOGGER.trace("Writing exclusive offers paths {} in cruise {}",
                                                voyageSpecialOffersPaths, cruisePage.getPath());

                                        importResult.incrementSuccessNumber();
                                        itemsWritten++;

                                        batchSave(session, itemsWritten);
                                    }
                                } else { // compare existing values with values coming from API
                                    final Set<String> exclusiveOffersListPty = new HashSet<>();
                                    exclusiveOffersListPty.addAll(Arrays.asList(exclusiveOffersPty));

                                    final Collection disjunction = CollectionUtils.disjunction(exclusiveOffersListPty, voyageSpecialOffersPaths);

                                    // if different write property
                                    if (disjunction.size() > 0) {
                                    	cruiseContentNode.setProperty("offer",(Value)null);
                                        cruiseContentNode.setProperty("offer", voyageSpecialOffersPaths
                                                .toArray(new String[voyageSpecialOffersPaths.size()]));
                                        final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
                                        final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

                                        if (startDate.after(Calendar.getInstance()) && isVisible) {
                                        	cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                                        }else{
                                        	cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                                        }
                                        LOGGER.trace("Writing exclusive offers paths {} in cruise {}",
                                                voyageSpecialOffersPaths, cruisePage.getPath());

                                        importResult.incrementSuccessNumber();
                                        itemsWritten++;

                                        batchSave(session, itemsWritten);
                                    } else { // else do nothing
                                        LOGGER.trace("Cruise {} already contains exclusive offers paths {}",
                                                cruisePage.getPath(), voyageSpecialOffersPaths);
                                    }
                                }

                                // removing the cruise from mapping
                                cruisesMapping.remove(voyageId);
                            } catch (ImporterException e) {
                                importResult.incrementErrorNumber();

                                LOGGER.warn(e.getMessage());
                            }
                         catch (RepositoryException e) {
                        	importResult.incrementErrorNumber();
                            LOGGER.error("Cannot import exclusive offers ", e);
                           
                        }
                        }
                    } catch (ImporterException e) {
                        importResult.incrementErrorNumber();

                        LOGGER.warn(e.getMessage());
                    }
                }

                apiPage++;
            } while (voyageSpecialOffers.size() > 0);

            // cleaning exclusive offers of cruises not present in the API
            for (Map<String, Page> cruisesPages : cruisesMapping.values()) {
                for (Page cruisePage : cruisesPages.values()) {
                    final Node cruiseContentNode = cruisePage.getContentResource().adaptTo(Node.class);

                    try {
                        if (cruiseContentNode == null) {
                            throw new ImporterException("Cannot get cruise content node for cruise " +
                                    cruisePage.getPath());
                        }

                        if (cruiseContentNode.hasProperty("offer")) {
                            cruiseContentNode.getProperty("offer").remove();
                            final Calendar startDate = cruiseContentNode.getProperty("startDate").getDate();
                            final Boolean isVisible = cruiseContentNode.getProperty("isVisible").getBoolean();

                            if (startDate.after(Calendar.getInstance()) && isVisible) {
                            	cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                            }else{
                            	cruiseContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                            }

                            importResult.incrementSuccessNumber();
                            itemsWritten++;

                            batchSave(session, itemsWritten);

                            LOGGER.trace("Cruise {} have no exclusive offers, cleaning it",
                                    cruisePage.getPath());
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.warn("Cannot clean exclusiveOffers property for {} - {}", cruisePage.getPath(),
                                e.getMessage());
                    }
                }
            }

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("cruisesUrl"),
                    "lastModificationDateCruisesExclusiveOffers", false);

            // last save before closing the session
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} cruises updated, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import exclusive offers ", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read exclusive offers from API", e);
        } finally {
            importRunning = false;
        }

        LOGGER.info("Ending mapping exclusive offers/cruises import, success: {}, errors: {}, api calls : {}",
                +importResult.getSuccessNumber(), +importResult.getErrorNumber(), apiPage);

        return importResult;
    }

    private void batchSave(Session session, int itemsWritten) throws RepositoryException {
        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
            try {
                session.save();

                LOGGER.debug("{} cruises updated, saving session", +itemsWritten);
            } catch (RepositoryException e) {
                session.refresh(true);
            }
        }
    }
}
