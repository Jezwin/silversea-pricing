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
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyageSpecialOffer;

@Service
@Component(label = "Silversea.com - Cruises importer")
public class CruisesImporterImpl implements CruisesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImporterImpl.class);
   
    @Reference
    private CruiseService cruiseService;
    
    @Reference
    private ApiCallService apiCallService;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private Replicator replicator;
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Session session;

    private List<VoyagePriceComplete> voyagePricesComplete;
    private LowestPrice lowestPrice;
    
    private void init() {
        try {
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            session = resourceResolver.adaptTo(Session.class);
            cruiseService.init();
        } catch (LoginException e) {
            LOGGER.debug("Cruise importer -- login exception ", e);
        }
    }

    @Override
    public void loadData() throws IOException {
        try {
            init();
            voyagePricesComplete = new ArrayList<VoyagePriceComplete>();
            List<Voyage> voyages;
            int index = 1;            
            VoyagesApi voyageApi = new VoyagesApi();
          
            do {
                voyages = apiCallService.getVoyages(index,voyageApi);
                processData(voyages); 
                index++;
            } while (voyages!=null &&!voyages.isEmpty());
            
            Page destinationsPage = pageManager
                    .getPage(apiConfig.apiRootPath(ImportersConstants.CRUISES_DESTINATIONS_URL_KEY));
            //Save date of last modification
            ImporterUtils.saveUpdateDate(destinationsPage);
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise importer -- Importing data finished");

        } catch (ApiException | WCMException | RepositoryException e) {
            LOGGER.error("Exception importing cruises", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
                resourceResolver = null;
            }
        }
    }

    private void processData(List<Voyage> voyages) throws WCMException, RepositoryException, IOException, ApiException{
        if(voyages != null && !voyages.isEmpty()){
            for (Voyage voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Cruise importer -- Sart import cruise with id {}",voyage.getVoyageId());
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
                        cruiseService.buildOrUpdateIteneraries(cruisePage, voyage.getVoyageId(),voyage.getVoyageUrl());
                        // Create or update suites nodes
                        cruiseService.buildOrUpdateSuiteNodes(lowestPrice,cruisePage, voyage.getVoyageId(),voyage.getShipId(),voyagePricesComplete);
                        // Create or update lowest prices
                        cruiseService.buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrice.getGlobalPrices());
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //Replicate page
                        cruiseService.replicateResource(cruisePage.getPath());

                        LOGGER.debug("Cruise importer -- Import cruise with id {} finished",voyage.getVoyageId());
                    } else {
                        LOGGER.debug("Cruise importer -- Destination with id {} not found", voyage.getDestinationId());
                    }
                }
            }
        }
        else {
            LOGGER.debug("Cruise importer: List cruises is empty");
        }
    }

    private void updateCruisePage(Page cruisePage, Voyage voyage)
            throws RepositoryException, IOException, ApiException {

        List<VoyageSpecialOffer> voyageSpecialOffers = apiCallService.getVoyageSpecialOffers(voyage.getVoyageId());
        Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);
        cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());
        //set cruise's tags
        cruiseService.setCruiseTags(voyage.getFeatures(),voyage.getVoyageId(),voyage.getIsExpedition(), cruisePage);
        
        String mapUrl = cruiseService.downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName());
        String[] specialOffers = cruiseService.findSpecialOffersReferences(voyageSpecialOffers, voyage.getVoyageId());
        String shipReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(voyage.getShipId()), resourceResolver);
        //Update node properties
        cruisePageContentNode.setProperty("voyageHighlights", voyage.getVoyageHighlights());
        cruisePageContentNode.setProperty("exclusiveOffers",specialOffers);
        cruisePageContentNode.setProperty("startDate", voyage.getArriveDate().toString());
        cruisePageContentNode.setProperty("endDate", voyage.getDepartDate().toString());
        cruisePageContentNode.setProperty("duration", voyage.getDays());
        cruisePageContentNode.setProperty("shipReference", shipReference);
        cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
        cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
        cruisePageContentNode.setProperty("itinerary",mapUrl);

    }

}
