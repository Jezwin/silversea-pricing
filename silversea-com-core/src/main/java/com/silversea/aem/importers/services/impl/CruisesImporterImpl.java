package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.silversea.aem.components.beans.VoyageWrapper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.importers.services.CruisesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;
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
    private Workspace workspace;

    private List<VoyagePriceComplete> voyagePricesComplete;
    private LowestPrice lowestPrice;
    
    private void init() {
        try {
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            pageManager = resourceResolver.adaptTo(PageManager.class);
            session = resourceResolver.adaptTo(Session.class);
            workspace = session.getWorkspace();
            cruiseService.init();
        } catch (LoginException e) {
            LOGGER.debug("Cruise importer -- login exception ", e);
        }
    }

    @Override
    public void importData(boolean mode) throws IOException {
        try {
            LOGGER.debug("Cruise importer -- Start import data");
            init();
            voyagePricesComplete = new ArrayList<VoyagePriceComplete>();
            Page destinationsPage = pageManager
                    .getPage(apiConfig.apiRootPath(ImportersConstants.CRUISES_DESTINATIONS_URL_KEY));
            //Full import strategy
            if(!mode){
                fullStrategy();
            }
            //Update import starategy
            else{
                String lastModificationDate = ImporterUtils.getDateFromPageProperties(destinationsPage,"lastModificationDate");
                updateStrategy(lastModificationDate);
            }
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
    
    private void fullStrategy() throws IOException, ApiException, WCMException, RepositoryException{
        LOGGER.debug("Cruise importer -- Start full strategy import");
        List<Voyage> voyages;
        int index = 1;            
        
        do {
            voyages = apiCallService.getVoyages(index);
            processData(voyages,false); 
            index++;
        } while (voyages!=null &&!voyages.isEmpty() && index==1);
        
        LOGGER.debug("Cruise importer -- End full strategy import");
    }
    
    private void updateStrategy(String lastModificationDate) throws IOException, ApiException, WCMException, RepositoryException{
        LOGGER.debug("Cruise importer -- Start update strategy import");
        List<Voyage77> voyages;
        int index = 1; 
        do {
            voyages = apiCallService.getChangedVoyages(index, lastModificationDate);
            processData(voyages,true); 
            index++;
        } while (voyages!=null && !voyages.isEmpty());

        LOGGER.debug("Cruise importer -- End update strategy import");
    }
    
    private <T> void processData(List<T> voyages,boolean mode) throws WCMException, RepositoryException, IOException, ApiException{
        if(voyages != null && !voyages.isEmpty()){     
            for (T voyage : voyages) {
                if(voyage != null ){
                    VoyageWrapper voyageWrapper = new VoyageWrapper();
                    voyageWrapper.init(voyage);
                    execute(voyageWrapper,mode);        
                }  
            } 
        }
        else {
            LOGGER.warn("Cruise importer -- List cruises is empty");
        }
    }

    private void execute(VoyageWrapper voyage,boolean update)throws WCMException, RepositoryException, IOException, ApiException{
        LOGGER.debug("Cruise importer -- Sart import cruise with id {}",voyage.getVoyageId());
        // retrieve cruises root page dynamically
        Page destinationPage = cruiseService.getDestination(voyage.getDestinationId());
        if (destinationPage != null) {
            // Instantiate new lowest price
            // lowest prices for the cruise
            lowestPrice = new LowestPrice();
            lowestPrice.initGlobalPrices();

            Page cruisePage = cruiseService.getCruisePage(destinationPage,voyage.getVoyageId(),voyage.getVoyageName());
            buildCruisePage(cruisePage,voyage,update);
            //Replication management for diff
            cruiseService.updateReplicationStatus(voyage.getIsDeleted(), voyage.getIsVisible(), cruisePage);
            //Updates pages in other languages
            updateLanguages(cruisePage,update,voyage);
            LOGGER.debug("Cruise importer -- Import cruise with id {} finished",voyage.getVoyageId());
        } else {
            LOGGER.error("Cruise importer -- Destination with id {} not found", voyage.getDestinationId());
        }
    }
 
    private void buildCruisePage(Page cruisePage, VoyageWrapper voyage,boolean update) throws RepositoryException, IOException, ApiException{
        updateProperties(cruisePage, voyage,false);
        //Clean nodes for diff
        cleanNodes(cruisePage, update);
        // build itineraries nodes
        cruiseService.buildOrUpdateIteneraries(cruisePage, voyage.getVoyageId(),voyage.getVoyageUrl());
        // Create or update suites nodes
        cruiseService.buildOrUpdateSuiteNodes(lowestPrice,cruisePage, voyage.getVoyageId(),voyage.getShipId(),voyagePricesComplete);
        // Create or update lowest prices
        cruiseService.buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrice.getGlobalPrices());
        //Persist data
        ImporterUtils.saveSession(session, false);
    }
    
    private void updateProperties(Page cruisePage, VoyageWrapper voyage,boolean isCopy)
            throws RepositoryException, IOException, ApiException {
       
        Node cruisePageContentNode = cruisePage.getContentResource().adaptTo(Node.class);
        //If is not a copy for other language
        //Download map url and retrive special offers from api for the first time
        //It will be the same data for copies in other languages
        if(!isCopy){
            List<VoyageSpecialOffer> voyageSpecialOffers = apiCallService.getVoyageSpecialOffers(voyage.getVoyageId());
            String[] specialOffers = cruiseService.findSpecialOffersReferences(voyageSpecialOffers, voyage.getVoyageId());
            String mapUrl = cruiseService.downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName()+" "+voyage.getVoyageId());
            cruisePageContentNode.setProperty("itinerary",mapUrl);
            cruisePageContentNode.setProperty("exclusiveOffers",specialOffers);
        }
        cruisePageContentNode.setProperty(JcrConstants.JCR_TITLE, voyage.getVoyageName());
        //set cruise's tags
        cruiseService.setCruiseTags(voyage.getFeatures(),voyage.getVoyageId(),voyage.getIsExpedition(),voyage.getDays(), cruisePage);
        String durationCategory = cruiseService.calculateDurationCategory(voyage.getDays());
        String shipReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(voyage.getShipId()), resourceResolver);
        //Update node properties
        cruisePageContentNode.setProperty("voyageHighlights", voyage.getVoyageHighlights());
        cruisePageContentNode.setProperty("startDate", ImporterUtils.convertToCalendar(voyage.getDepartDate()));
        cruisePageContentNode.setProperty("endDate", ImporterUtils.convertToCalendar(voyage.getArriveDate()));
        cruisePageContentNode.setProperty("duration", voyage.getDays());
        cruisePageContentNode.setProperty("shipReference", shipReference);
        cruisePageContentNode.setProperty("cruiseCode", voyage.getVoyageCod());
        cruisePageContentNode.setProperty("cruiseId", voyage.getVoyageId());
        //All properties prefixed by cmp(computed data) are used by search service
        cruisePageContentNode.setProperty("cmp-destinationId", voyage.getDestinationId());
        cruisePageContentNode.setProperty("cmp-ship", voyage.getShipId());
        cruisePageContentNode.setProperty("cmp-duration",durationCategory);
        cruisePageContentNode.setProperty("cmp-date",ImporterUtils.formatDateForSeach(voyage.getDepartDate()));
    } 
    
    public void updateLanguages(Page page,boolean update,VoyageWrapper voyage){
        LOGGER.debug("Cruise importer -- Sart updates page [{}] in other languages",page.getPath());

        String path = page.getPath();
        List<String> languages = ImporterUtils.getSiteLocales(pageManager);
        if(languages != null && !languages.isEmpty()){
            languages.forEach(language ->{
                if(!language.equals(ImportersConstants.LANGUAGE_EN)){
                    try{
                        String destPath = StringUtils.replace(path, "/en/", "/" + language + "/");
                        LOGGER.debug("Cruise importer -- Updtae to language [{}] , destination path [{}]",language,destPath);
                        Page destPage =  pageManager.getPage(destPath);
                        if(update && destPage != null){
                            updateProperties(destPage, voyage,true);
                            cleanNodes(destPage,update);
                            //Update itineraries nodes
                            ImporterUtils.copyNode(page,destPath.concat("/"+ImportersConstants.ITINERARIES_NODE), ImportersConstants.ITINERARIES_NODE, workspace);
                            //Update suites nodes
                            ImporterUtils.copyNode(page,destPath.concat("/"+ImportersConstants.SUITES_NODE), ImportersConstants.SUITES_NODE, workspace);
                            //Update lowest prices
                            ImporterUtils.copyNode(page,destPath.concat("/"+ImportersConstants.LOWEST_PRICES_NODE), ImportersConstants.LOWEST_PRICES_NODE, workspace);
                            //Updates cruise reference 
                            // exclusive offers,ship,port,suites,excursions,landprograms,hotels
                            updateReferences(destPage,language);
                            cruiseService.updateReplicationStatus(voyage.getIsDeleted(), voyage.getIsVisible(), destPage);
                        }
                        else{
                            workspace.copy(path, destPath);
                            Page pa = pageManager.getPage(destPath);
                            updateReferences(pa,language);
                        }

                    }catch(RepositoryException | IOException | ApiException e){
                        LOGGER.error("Exception while updating pages in other languages",e);
                    }
                }
            });
        }
        LOGGER.debug("Cruise importer -- Finish updates page [{}] in other languages",page.getPath());

    }
    
    private void cleanNodes(Page cruisePage,boolean update) throws RepositoryException{
        if(update){
            ImporterUtils.clean(cruisePage,ImportersConstants.ITINERARIES_NODE,session);
            ImporterUtils.clean(cruisePage,ImportersConstants.SUITES_NODE,session);
            ImporterUtils.clean(cruisePage,ImportersConstants.LOWEST_PRICES_NODE,session);
        }
    }
    
    private void updateReferences(Page page,String language) throws RepositoryException{
        
        String shipReference = page.getProperties().get("shipReference", String.class);
        shipReference = formatPathByLanguage(shipReference,language);
        Node  pageContentNode = page.getContentResource().adaptTo(Node.class);
        pageContentNode.setProperty("shipReference", shipReference);
        //Update exclusive offers references by language
        changeOffersReferencesBylanguage(page, language);
        Node pageNode = page.adaptTo(Node.class);
        //Update port reference by language
        cruiseService.changeReferenceBylanguage(pageNode,"itineraries","portReference",language);
        
        Node itinerariesNode = pageNode.getNode("itineraries");
        NodeIterator itinerariesNodes = itinerariesNode.getNodes();
        
        while (itinerariesNodes.hasNext()) {
            Node itinerary = itinerariesNodes.nextNode();
            cruiseService.changeReferenceBylanguage(itinerary,"excursions","excursionReference",language);
            cruiseService.changeReferenceBylanguage(itinerary,"hotels","hotelReference",language);
            cruiseService.changeReferenceBylanguage(itinerary,"land-programs","landProgramReference",language);
        }
      
        cruiseService.changeReferenceBylanguage(pageNode,"suites","suiteReference",language);
    }
    
    private void changeOffersReferencesBylanguage(Page page,String language)throws RepositoryException{
        String[] exclusiveOfferUrls = page.getProperties().get("exclusiveOffers", String[].class);
        if (exclusiveOfferUrls != null && ArrayUtils.isNotEmpty(exclusiveOfferUrls)) {
            String[] urls = Arrays.asList(exclusiveOfferUrls)
                    .stream()
                    .map((item) -> {
                        String path = null;
                        if (!StringUtils.isEmpty(item)) {
                            path = formatPathByLanguage(item,language);
                        }
                        return path;
                    })
                    .toArray(String[]::new);
            Node  pageContentNode = page.getContentResource().adaptTo(Node.class);
            pageContentNode.setProperty("exclusiveOffers", urls);
        }
        ImporterUtils.saveSession(session, false);
    }
    
    private String formatPathByLanguage(String path,String language){
        return StringUtils.replace(path, "/en/", "/" + language + "/");   
    }
}
