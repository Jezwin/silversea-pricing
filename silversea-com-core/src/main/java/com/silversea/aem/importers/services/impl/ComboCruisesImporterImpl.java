package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.LowestPrice;
import com.silversea.aem.components.beans.VoyageWrapper;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ComboCruisesImporter;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Price;
import io.swagger.client.model.SpecialVoyage;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageWithItinerary;

@Service
@Component(label = "Silversea.com - Combo Cruises importer")
public class ComboCruisesImporterImpl  implements ComboCruisesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruisesImporterImpl.class);

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private ApiCallService apiCallService;

    @Reference
    private Replicator replicator;

    @Reference
    private CruiseService cruiseService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Session session;

    private LowestPrice lowestPrice;
    private Set<String> cruises; 
    private Set<Integer> segments;
    private Workspace workspace;

    private void init() throws LoginException {
        Map<String, Object> authenticationPrams = new HashMap<String, Object>();
        authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
        pageManager = resourceResolver.adaptTo(PageManager.class);
        session = resourceResolver.adaptTo(Session.class);
        workspace = session.getWorkspace();
        cruiseService.init();
    }

    @Override
    public void importData(boolean update) throws IOException {
        try {
            LOGGER.debug("Combo Cruise importer -- Start import data");
            init();
            initDiffSet(update);
            List<SpecialVoyage> specialVoyages = apiCallService.getSpecialVoyages();
            processData(specialVoyages,update); 
            ImporterUtils.saveSession(session, false);
            LOGGER.debug("Combo Cruise importer -- Importing data finished");

        } catch (ApiException | WCMException | RepositoryException | LoginException e) {
            LOGGER.error("Exception while importing cruises", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
                resourceResolver = null;
            }
        }
    }
    private void initDiffSet(boolean update){
        if(update){
            cruises = new HashSet<String>();
            segments = new HashSet<Integer>();
        }
    }
    private <T> void updateSet(boolean update,Set<T> set, T element){
        if(update && set != null){
            set.add(element);
        }
    }
    private void processData(List<SpecialVoyage> specialVoyages, boolean update) throws WCMException, RepositoryException, IOException, ApiException{
        if(specialVoyages != null && !specialVoyages.isEmpty()){
            for (SpecialVoyage specialVoyage : specialVoyages) {
                if(specialVoyage != null){
                    LOGGER.debug("Combo cruise importer -- Sart import combo cruise with id {}",specialVoyage.getSpecialVoyageId());
                    // retrieve cruises root page dynamically
                    Page cruisePage = getComboCruisePage(specialVoyage.getSpecialVoyageId());

                    if (cruisePage != null) {

                        Node cruisePageNode = cruisePage.adaptTo(Node.class);
                        // Instantiate new hashMap which will contains
                        // lowest prices for the cruise
                        lowestPrice = new LowestPrice();
                        lowestPrice.initGlobalPrices();
                        updateCruisePage(cruisePage, specialVoyage,update);

                        buildOrUpdateSegments(cruisePage,specialVoyage, update);
                        // Create or update suites nodes
                        buildOrUpdateSuiteNodes(lowestPrice,cruisePage, specialVoyage);
                        // Create or update lowest prices
                        cruiseService.buildLowestPrices(cruisePageNode, lowestPrice.getGlobalPrices());
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //TODO:Replicate page with segments
                        //cruiseService.replicatePageWithChildren(cruisePage);
                        //Deactivate pages that no longer exist 
                        calculatePagesDiff(update);
                        //Update page in other languages
                        updateLanguages(cruisePage, update,specialVoyage);
                        LOGGER.debug("Combo cruise importer -- Import cruise with id {} finished",specialVoyage.getSpecialVoyageId());
                    } else {
                        LOGGER.error("Combo cruise importer -- Combo cruise page with id {} not found",specialVoyage.getSpecialVoyageId());
                    }
                }
            }
        }
        else {
            LOGGER.warn("Combo Cruise importer -- List combo cruises is empty");
        }
    }

    private void updateCruisePage(Page cruisePage, SpecialVoyage specialVoyage,boolean update)
            throws RepositoryException, IOException, ApiException {
        Node jcrContent = cruisePage.getContentResource().adaptTo(Node.class);
        String shipReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(getShipId(specialVoyage)), resourceResolver);
        jcrContent.setProperty("apiTitle", specialVoyage.getSpecialVoyageName());
        jcrContent.setProperty("shipReference", shipReference);
        updateSet(update, cruises, specialVoyage.getSpecialVoyageId());
    }

    private void buildOrUpdateSegments(Page cruisePage,SpecialVoyage specialVoyage, boolean update) throws WCMException, RepositoryException{
        
        List<VoyageWithItinerary> voyages = specialVoyage.getVoyages();
        String voayageId = specialVoyage.getSpecialVoyageId();
        if(voyages != null && !voyages.isEmpty()){
            LOGGER.debug("Combo cruise -- Start updating segments");
            for (VoyageWithItinerary voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Combo cruise -- Update segment with id {}",voyage.getVoyageId());
                    String segementId = voayageId + voyage.getVoyageId();
                    String cruiseReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "cruiseId", Objects.toString(voyage.getVoyageId()),resourceResolver);

                    Page segementPage =  getSegmentPage(cruisePage,voyage.getVoyageName(),segementId);
                    Node jcrContent = segementPage.getContentResource().adaptTo(Node.class);
                    //Update node's properties
                    jcrContent.setProperty("cruiseReference", cruiseReference);
                    jcrContent.setProperty("CruiseSegmentId", segementId);
                    updateSet(update,segments,voyage.getVoyageId());
                }
            }
            LOGGER.debug("Combo cruise -- Updating segments finished");
        }
        else{
            LOGGER.debug("Combo cruise -- No voyage found for special voyage with id {}", voayageId);
        }
    }
 
    public Page getSegmentPage(Page root,String voyageName,String segmentId) throws WCMException, RepositoryException{
        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "CruiseSegmentId", segmentId,
                resourceResolver);
        Page segmentPage = ImporterUtils.adaptOrCreatePage(resources, ImportersConstants.CRUISE_SEGEMENT_TEMPLATE, root,
                voyageName, pageManager);

        return segmentPage;
    }

    public Page getComboCruisePage(String voyageId) throws WCMException, RepositoryException{
        Page page = null;
        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "comboCruiseCode", Objects.toString(voyageId),
                resourceResolver);
        if(resources!= null && resources.hasNext()){
            Resource resource = resources.next();
            if(resource!=null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)){
                page = resource.adaptTo(Page.class);
            }

        }

        return page;
    }

    private Integer getShipId(SpecialVoyage specialVoyage){
        Integer shipId = null;
        if(specialVoyage!=null && specialVoyage.getVoyages() !=null 
                && !specialVoyage.getVoyages().isEmpty()){
            Optional<VoyageWithItinerary> voyageWithItinerary =specialVoyage.getVoyages()
                    .stream()
                    .filter(item -> item.getShipId()!=null)
                    .findFirst();

            shipId = voyageWithItinerary.get().getShipId();
        }
        return shipId;
    }
    
    public void buildOrUpdateSuiteNodes(LowestPrice lowestPrice,Page cruisePage,SpecialVoyage specialVoyage)
            throws RepositoryException, IOException, ApiException {
        
        List<VoyagePriceMarket> voyagesPriceMarket = specialVoyage.getPrices();
        String voyageId = specialVoyage.getSpecialVoyageId();
        
        if (voyagesPriceMarket != null 
                && !voyagesPriceMarket.isEmpty()
                && voyagesPriceMarket.get(0) !=null 
                && !voyagesPriceMarket.get(0).getCruiseOnlyPrices().isEmpty()) {

            lowestPrice.initVariationPrices();
            List<Price> prices = voyagesPriceMarket.get(0).getCruiseOnlyPrices();
            Node suitesNode = ImporterUtils.findOrCreateNode(cruisePage.adaptTo(Node.class), ImportersConstants.SUITES_NODE);
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Combo cruise -- Start updating suites variations and prices for voyage with id {}", voyageId);
            for (Price price : prices) {
                Page suiteReference = cruiseService.findSuiteReference(getShipId(specialVoyage), price.getSuiteCategoryCod());
                if (suiteReference != null) {
                    buildSuitesGrouping(lowestPrice,suitesNode, suiteReference, price, voyageId, voyagesPriceMarket);
                }
            }
            //Build variation lowest prices
            cruiseService.buildVariationsLowestPrices(suitesNode,lowestPrice);
            LOGGER.debug("Combo cruise importer -- Updating suites variations and prices for voyage with id {} finished", voyageId);
        }
        else{
            LOGGER.debug("Combo cruise -- No price found for cruise with id {}", voyageId);
        }
    }

    public void buildSuitesGrouping(LowestPrice lowestPrice,Node rootNode, Page suiteRef, Price price,String voyageId, List<VoyagePriceMarket> voyagesPriceMarket)
            throws RepositoryException {

        Node suiteGroupingNode = ImporterUtils.findOrCreateNode(rootNode, suiteRef.getName());

        if(suiteGroupingNode != null){

            lowestPrice.addVariation(suiteRef.getName());
            suiteGroupingNode.setProperty("suiteReference", suiteRef.getPath());

            Iterator<Resource> res = ImporterUtils.findResourceById(ImportersConstants.QUERY_JCR_ROOT_PATH + rootNode.getPath(), JcrConstants.NT_UNSTRUCTURED,
                    "suiteCategoryCod", price.getSuiteCategoryCod(), resourceResolver);
            Node suiteNode = ImporterUtils.adaptOrCreateNode(res, suiteGroupingNode, price.getSuiteCategoryCod());
            if(suiteNode != null){
                suiteNode.setProperty("suiteCategoryCod", price.getSuiteCategoryCod());
                ImporterUtils.saveSession(session, false);
                // Create variationNode
                cruiseService.buildOrUpdateVariationNodes(suiteRef.getName(), lowestPrice,voyagesPriceMarket, suiteNode, price.getSuiteCategoryCod(),
                        voyageId);
            }
        }
    }
      
    private void calculatePagesDiff(boolean update) throws RepositoryException{
        List<Page> pages = cruiseService.getPagesByResourceType(ImportersConstants.COMBO_CRUISE_RESOURCE_TYPE);
        if(pages != null && !pages.isEmpty() && update){
            LOGGER.debug("Combo Cruise importer -- Start calculate diff");
            pages.forEach(page ->{
                
              String comboCruiseId = page.getProperties().get("comboCruiseCode",String.class);
              if(!cruises.contains(comboCruiseId)){
                  LOGGER.debug("Combo Cruise importer -- Combo cruise with id {} no longer exists",comboCruiseId);
                  cruiseService.updateReplicationStatus(true, false, page);
              }
              else{
                  Iterator<Page> children = page.listChildren();
                  while(children.hasNext()){
                      Page segement = children.next();
                      Integer cruiseSegmentId = segement.getProperties().get("CruiseSegmentId",Integer.class);
                      if(!segments.contains(cruiseSegmentId)){
                          LOGGER.debug("Combo Cruise importer -- Segement page with id {} no longer exists",cruiseSegmentId);
                          cruiseService.updateReplicationStatus(true, false, segement);
                      }
                  }
              }
            });
            ImporterUtils.saveSession(session, false);
            LOGGER.debug("Combo Cruise importer -- Finish combo cruises diff");
        }
    }
    
    public void updateLanguages(Page page,boolean update,SpecialVoyage voyage) {
        LOGGER.debug(" Combo Cruise importer -- Sart updates page [{}] in other languages",page.getPath());

        String path = page.getPath();
        List<String> languages = ImporterUtils.getSiteLocales(pageManager);
        if(languages != null && !languages.isEmpty()){
            languages.forEach(language ->{
                if(!language.equals(ImportersConstants.LANGUAGE_EN)){
                    try{
                        String destPath = StringUtils.replace(path, "/en/", "/" + language + "/");
                        LOGGER.debug("Combo Cruise importer -- Updtae to language [{}] , destination path [{}]",language,destPath);
                        Page destPage =  pageManager.getPage(destPath);
                        if(update && destPage != null){
                            updateCruisePage(destPage, voyage,update); 
                            cleanSegments(destPage);
                            cleanNodes(destPage,update);
                            //Update suites nodes
                            ImporterUtils.copyNode(page,destPath, ImportersConstants.SUITES_NODE, workspace);
                            //Update lowest prices
                            ImporterUtils.copyNode(page,destPath, ImportersConstants.LOWEST_PRICES_NODE, workspace);
                            //Updates cruise reference 
                            // exclusive offers,ship,port,suites,excursions,landprograms,hotels
                            updateReferences(destPage,language);
                            //TODO: Calculate diff
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
        LOGGER.debug("Combo Cruise importer -- Finish updates page [{}] in other languages",page.getPath());
    }
    
    private void cleanNodes(Page cruisePage,boolean update) throws RepositoryException{
        if(update){
            ImporterUtils.clean(cruisePage,ImportersConstants.SUITES_NODE,session);
            ImporterUtils.clean(cruisePage,ImportersConstants.LOWEST_PRICES_NODE,session);
        }
    }
    
    private void cleanSegments(Page page) {
        Iterator<Page> children = page.listChildren();
        if(children != null){
            children.forEachRemaining(element ->{
                try {
                    pageManager.delete(element,true);
                } catch (WCMException e) {
                   LOGGER.error("Error while deleting segments",e);
                }
            });
        } 
    }
    
    private void updateReferences(Page page,String language) throws RepositoryException{
        String shipReference = page.getProperties().get("shipReference", String.class);
        shipReference = formatPathByLanguage(shipReference,language);
        Node  pageContentNode = page.getContentResource().adaptTo(Node.class);
        pageContentNode.setProperty("shipReference", shipReference);
        Node pageNode = page.adaptTo(Node.class);
        updateCruisesReferences(page,language);
        cruiseService.changeReferenceBylanguage(pageNode,ImportersConstants.SUITES_NODE,"suiteReference",language);
    }
    
    private void updateCruisesReferences(Page page,String language) throws RepositoryException{
        Iterator<Page> children = page.listChildren();
        if(children != null){
            children.forEachRemaining(element ->{
                try {
                    String cruiseReference = element.getProperties().get("cruiseReference", String.class);
                    cruiseReference = formatPathByLanguage(cruiseReference,language);
                    Node  pageContentNode = element.getContentResource().adaptTo(Node.class);
                    pageContentNode.setProperty("cruiseReference", cruiseReference);
                }catch (RepositoryException e) {
                    LOGGER.error("Error while updating cruises references in segments",e);
                }
            });
        }
        ImporterUtils.saveSession(session, false);
    }

    private String formatPathByLanguage(String path,String language){
        return StringUtils.replace(path, "/en/", "/" + language + "/");   
   }
}