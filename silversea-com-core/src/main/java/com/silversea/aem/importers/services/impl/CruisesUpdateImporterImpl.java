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
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruisesUpdateImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.CruiseService;

import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
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
    Replicator replicator;

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Session session;

    private List<VoyagePriceComplete> voyagePricesComplete;
    private Map<String, PriceData> lowestPrices;

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
            List<Voyage77> voyages;
            int index = 1;
            VoyagesApi voyageApi = new VoyagesApi();
            Page destinationPage = pageManager
                    .getPage(apiConfig.apiRootPath(ImportersConstants.CRUISES_DESTINATIONS_URL_KEY));
            String lastModificationDate = ImporterUtils.getLastModificationDate(destinationPage);
            LOGGER.debug("Cruise importer -- Start import data");
            do {
                voyages = apiCallService.getChangedVoyages(index,voyageApi, lastModificationDate);
                processData(voyages); 
                index++;
            } while (voyages!=null && !voyages.isEmpty());

            //Save date of last modification
            ImporterUtils.saveUpdateDate(destinationPage);
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

    private void processData(List<Voyage77> voyages) throws WCMException, RepositoryException, IOException, ApiException{
        if(voyages != null && !voyages.isEmpty()){
            for (Voyage77 voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Cruise importer -- Start import cruise with id {}",voyage.getVoyageId());
                    // retrieve cruises root page dynamically
                    Page destinationPage = cruiseService.getDestination(voyage.getDestinationId());

                    if (destinationPage != null) {
                        // Instantiate new hashMap which will contains
                        // lowest prices for the cruise
                        lowestPrices = new HashMap<String, PriceData>();

                        Page cruisePage = cruiseService.getCruisePage(destinationPage,voyage.getVoyageId(),voyage.getVoyageName());

                        updateCruisePage(cruisePage, voyage);

                        // build itineraries nodes
                        ImporterUtils.clean(cruisePage,ImportersConstants.ITINERARIES_NODE,session);
                        cruiseService.buildOrUpdateIteneraries(cruisePage, voyage.getVoyageId(),voyage.getVoyageUrl());
                        // Create or update suites nodes
                        ImporterUtils.clean(cruisePage,ImportersConstants.SUITES_NODE,session);
                        cruiseService.buildOrUpdateSuiteNodes(cruisePage, voyage.getVoyageId(),voyage.getShipId(),voyagePricesComplete);
                        // Create or update lowest prices
                        ImporterUtils.clean(cruisePage,ImportersConstants.LOWEST_PRICES_NODE,session);
                        cruiseService.buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrices);
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //Replication management
                        cruiseService.updateReplicationStatus(voyage.getIsDeleted(), voyage.getIsVisible(), cruisePage);

                        LOGGER.debug("Cruise importer -- Import cruise with id {} finished",voyage.getVoyageId());
                    } else {
                        LOGGER.debug("Cruise importer -- Error destination with id {} not found", voyage.getDestinationId());
                    }
                }
            }
        }
        else {
            LOGGER.debug("Cruise importer -- List cruises is empty");
        }
    }

    private void updateCruisePage(Page cruisePage, Voyage77 voyage)
            throws RepositoryException, IOException, ApiException {

        List<VoyageSpecialOffer> voyageSpecialOffers = apiCallService.getVoyageSpecialOffers(voyage.getVoyageId());
        Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);
        cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());

        cruiseService.setCruiseTags(voyage.getFeatures(),voyage.getVoyageId(),voyage.getIsExpedition(), cruisePage);
        // Update node properties
        cruisePageContentNode.setProperty("voyageHighlights", voyage.getVoyageHighlights());
        cruisePageContentNode.setProperty("exclusiveOffers",
                cruiseService.findSpecialOffersReferences(voyageSpecialOffers, voyage.getVoyageId()));
        cruisePageContentNode.setProperty("startDate", voyage.getArriveDate().toString());
        cruisePageContentNode.setProperty("endDate", voyage.getDepartDate().toString());
        cruisePageContentNode.setProperty("duration", voyage.getDays());
        cruisePageContentNode.setProperty("shipReference", ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(voyage.getShipId()), resourceResolver));
        cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
        cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
        cruisePageContentNode.setProperty("itinerary",
                cruiseService.downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName()));

    }
}
