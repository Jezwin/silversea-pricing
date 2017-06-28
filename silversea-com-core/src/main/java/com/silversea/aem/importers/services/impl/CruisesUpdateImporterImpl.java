package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.LowestPrice;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.importers.services.CruisesUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyageSpecialOffer;

@Service
@Component(label = "Silversea.com - Cruises update importer")
public class CruisesUpdateImporterImpl  implements CruisesUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesUpdateImporterImpl.class);

    @Reference
    private CruiseService cruiseService;

    @Reference
    private ApiCallService apiCallService;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Session session;

    private List<VoyagePriceComplete> voyagePricesComplete;
    private LowestPrice lowestPrice;

    private void init() {
        try {
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);            pageManager = resourceResolver.adaptTo(PageManager.class);
            session = resourceResolver.adaptTo(Session.class);
            cruiseService.init();
        } catch (LoginException e) {
            LOGGER.debug("Cruise update importer -- login exception ", e);
        }
    }

    @Override
    public void importData() throws IOException {
        try {
            init();
            voyagePricesComplete = new ArrayList<VoyagePriceComplete>();
            List<Voyage77> voyages;
            int index = 1;
            Page destinationPage = pageManager
                    .getPage(apiConfig.apiRootPath(ImportersConstants.CRUISES_DESTINATIONS_URL_KEY));
            String lastModificationDate = ImporterUtils.getLastModificationDate(destinationPage,"lastModificationDate");
            LOGGER.debug("Cruise update importer -- Start import data");
            do {
                voyages = apiCallService.getChangedVoyages(index, lastModificationDate);
                processData(voyages); 
                index++;
            } while (voyages!=null && !voyages.isEmpty());

            //Save date of last modification
            ImporterUtils.saveUpdateDate(destinationPage);
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise update importer -- Importing data finished");

        } catch (ApiException | WCMException | RepositoryException e) {
            LOGGER.error("Exception importing cruises", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
                resourceResolver = null;
            }
        }
    }

    private void processData(List<Voyage77> voyages) throws WCMException, RepositoryException, IOException, ApiException{
        if(voyages != null && !voyages.isEmpty()){
            for (Voyage77 voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Cruise update importer -- Start import cruise with id {}",voyage.getVoyageId());
                    // retrieve cruises root page dynamically
                    Page destinationPage = cruiseService.getDestination(voyage.getDestinationId());

                    if (destinationPage != null) {
                        // Instantiate new lowest price
                        // lowest prices for the cruise
                        lowestPrice = new LowestPrice();
                        lowestPrice.initGlobalPrices();

                        Page cruisePage = cruiseService.getCruisePage(destinationPage,voyage.getVoyageId(),voyage.getVoyageName());

                        updateCruisePage(cruisePage, voyage);

                        // build itineraries nodes
                        ImporterUtils.clean(cruisePage,ImportersConstants.ITINERARIES_NODE,session);
                        cruiseService.buildOrUpdateIteneraries(cruisePage, voyage.getVoyageId(),voyage.getVoyageUrl());
                        // Create or update suites nodes
                        ImporterUtils.clean(cruisePage,ImportersConstants.SUITES_NODE,session);
                        cruiseService.buildOrUpdateSuiteNodes(lowestPrice,cruisePage,voyage.getVoyageId(),voyage.getShipId(),voyagePricesComplete);
                        // Create or update lowest prices
                        ImporterUtils.clean(cruisePage,ImportersConstants.LOWEST_PRICES_NODE,session);
                        cruiseService.buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrice.getGlobalPrices());
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //Replication management
                        cruiseService.updateReplicationStatus(voyage.getIsDeleted(), voyage.getIsVisible(), cruisePage);

                        LOGGER.debug("Cruise update importer -- Import cruise with id {} finished",voyage.getVoyageId());
                    } else {
                        LOGGER.error("Cruise update importer -- Error destination with id {} not found", voyage.getDestinationId());
                    }
                }
            }
        }
        else {
            LOGGER.warn("Cruise update importer -- List cruises is empty");
        }
    }

    private void updateCruisePage(Page cruisePage, Voyage77 voyage)
            throws RepositoryException, IOException, ApiException {

        List<VoyageSpecialOffer> voyageSpecialOffers = apiCallService.getVoyageSpecialOffers(voyage.getVoyageId());
        Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);
        cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());
        //set cruise's tags
        cruiseService.setCruiseTags(voyage.getFeatures(),voyage.getVoyageId(),voyage.getIsExpedition(),voyage.getDays(), cruisePage);
        String durationCategory = cruiseService.calculateDurationCategory(voyage.getDays());
        String mapUrl = cruiseService.downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName());
        String[] specialOffers = cruiseService.findSpecialOffersReferences(voyageSpecialOffers, voyage.getVoyageId());
        String shipReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(voyage.getShipId()), resourceResolver);
        // Update node properties
        cruisePageContentNode.setProperty("voyageHighlights", voyage.getVoyageHighlights());
        cruisePageContentNode.setProperty("exclusiveOffers", specialOffers);
        cruisePageContentNode.setProperty("startDate", ImporterUtils.convertToCalendar(voyage.getDepartDate()));
        cruisePageContentNode.setProperty("endDate", ImporterUtils.convertToCalendar(voyage.getArriveDate()));
        cruisePageContentNode.setProperty("duration", voyage.getDays());
        cruisePageContentNode.setProperty("shipReference",shipReference);
        cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
        cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
        cruisePageContentNode.setProperty("itinerary",mapUrl);
        //All properties prefixed by cmp(computed data) are used by search service
        cruisePageContentNode.setProperty("cmp-destinationId", voyage.getDestinationId());
        cruisePageContentNode.setProperty("cmp-ship", voyage.getShipId());
        cruisePageContentNode.setProperty("cmp-duration",durationCategory);
        cruisePageContentNode.setProperty("cmp-date",ImporterUtils.formatDateForSeach(voyage.getDepartDate()));
    }
}
