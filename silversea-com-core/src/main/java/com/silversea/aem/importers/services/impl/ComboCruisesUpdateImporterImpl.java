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
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.LowestPrice;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ComboCruisesUpdateImporter;
import com.silversea.aem.importers.services.CruiseService;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.SpecialVoyage;
import io.swagger.client.model.VoyageWithItinerary;

@Service
@Component(label = "Silversea.com - Combo Cruises update importer")
public class ComboCruisesUpdateImporterImpl  implements ComboCruisesUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruisesUpdateImporterImpl.class);

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

    private void init() throws LoginException {
        Map<String, Object> authenticationPrams = new HashMap<String, Object>();
        authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
        pageManager = resourceResolver.adaptTo(PageManager.class);
        session = resourceResolver.adaptTo(Session.class);
        cruiseService.init();
    }

    @Override
    public void importData() throws IOException {
        try {
            init();
            cruises = new HashSet<String>();
            segments = new HashSet<Integer>();
            List<SpecialVoyage> specialVoyages = apiCallService.getSpecialVoyages();
            processData(specialVoyages); 
            ImporterUtils.saveSession(session, false);

            LOGGER.debug("Cruise importer -- Importing data finished");

        } catch (ApiException | WCMException | RepositoryException | LoginException e) {
            LOGGER.error("Exception while importing cruises", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
                resourceResolver = null;
            }
        }
    }

    private void processData(List<SpecialVoyage> specialVoyages) throws WCMException, RepositoryException, IOException, ApiException{
        if(specialVoyages != null && !specialVoyages.isEmpty()){
            for (SpecialVoyage specialVoyage : specialVoyages) {
                if(specialVoyage != null){
                    LOGGER.debug("Combo cruise importer -- Sart import cruise with id {}",specialVoyage.getSpecialVoyageId());
                    // retrieve cruises root page dynamically
                    Page cruisePage = getComboCruisePage(specialVoyage.getSpecialVoyageId());

                    if (cruisePage != null) {

                        // Instantiate new hashMap which will contains
                        // lowest prices for the cruise
                        lowestPrice = new LowestPrice();
                        lowestPrice.initGlobalPrices();
                        String voyageId = specialVoyage.getSpecialVoyageId();
                        updateCruisePage(cruisePage, specialVoyage);

                        buildOrUpdateSegments(cruisePage,specialVoyage.getVoyages(),voyageId);
                        // Create or update suites nodes
                        cruiseService.buildOrUpdateSuiteNodes(lowestPrice,cruisePage,voyageId,getShipId(specialVoyage) ,specialVoyage.getPrices());
                        // Create or update lowest prices
                        cruiseService.buildLowestPrices(cruisePage.adaptTo(Node.class), lowestPrice.getGlobalPrices());
                        //Persist data
                        ImporterUtils.saveSession(session, false);
                        //Replicate page with segments
                        cruiseService.replicatePageWithChildren(cruisePage);
                        //Deactivate pages that no longer exist 
                        calculatePagesDiff();
                        LOGGER.debug("Combo cruise importer -- Import cruise with id {} finished",specialVoyage.getSpecialVoyageId());
                    } else {
                        LOGGER.error("Combo cruise importer -- Combo cruise page with id {} not found",specialVoyage.getSpecialVoyageId());
                    }
                }
            }
        }
        else {
            LOGGER.warn("Cruise importer -- List cruises is empty");
        }
    }

    private void updateCruisePage(Page cruisePage, SpecialVoyage specialVoyage)
            throws RepositoryException, IOException, ApiException {
        Node jcrContent = cruisePage.getContentResource().adaptTo(Node.class);
        String shipReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "shipId",
                Objects.toString(getShipId(specialVoyage)), resourceResolver);
        jcrContent.setProperty("apiTitle", specialVoyage.getSpecialVoyageName());
        jcrContent.setProperty("shipReference", shipReference);
        cruises.add(specialVoyage.getSpecialVoyageId());
    }

    private void buildOrUpdateSegments(Page cruisePage, List<VoyageWithItinerary> voyages,String voayageId) throws WCMException, RepositoryException{
        if(voyages != null && !voyages.isEmpty()){
            LOGGER.debug("Combo cruise -- Start updating segments");
            for (VoyageWithItinerary voyage : voyages) {
                if(voyage != null){
                    LOGGER.debug("Combo cruise -- Update segment with id {}",voyage.getVoyageId());
                    String cruiseReference = ImporterUtils.findReference(ImportersConstants.QUERY_CONTENT_PATH, "cruiseId",
                            Objects.toString(voyage.getVoyageId()),resourceResolver);

                    Page segementPage =  getSegmentPage(cruisePage,voyage.getVoyageId(),voyage.getVoyageName());
                    Node jcrContent = segementPage.getContentResource().adaptTo(Node.class);
                    String mapUrl = cruiseService.downloadAndSaveAsset(voyage.getMapUrl(), voyage.getVoyageName());
                    //Update node's properties
                    jcrContent.setProperty("cruiseReference", cruiseReference);
                    jcrContent.setProperty("CruiseSegmentId", voyage.getVoyageId());
                    jcrContent.setProperty("itineraryMap", mapUrl);
                    segments.add(voyage.getVoyageId());
                }
            }
            LOGGER.debug("Combo cruise -- Updating segments finished");
        }
        else{
            LOGGER.debug("Combo cruise -- No voyage found for special voyage with id {}", voayageId);
        }
    }
    //TODO
    public Page getSegmentPage(Page root,Integer voyageId,String voyageName) throws WCMException, RepositoryException{

        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "CruiseSegmentId", Objects.toString(voyageId),
                resourceResolver);
        Page segmentPage = ImporterUtils.adaptOrCreatePage(resources, ImportersConstants.CUISE_SEGEMENT_TEMPLATE, root,
                voyageName, pageManager);

        return segmentPage;
    }

    public Page getComboCruisePage(String voyageId) throws WCMException, RepositoryException{
        Page page = null;
        Iterator<Resource> resources = ImporterUtils.findResourceById(ImportersConstants.QUERY_CONTENT_PATH,
                NameConstants.NT_PAGE, "comboCruiseId", Objects.toString(voyageId),
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
      
    private void calculatePagesDiff() throws RepositoryException{
        LOGGER.debug("Combo Cruise importer -- Start calculate diff");
        List<Page> pages = cruiseService.getPagesByResourceType(ImportersConstants.COMBO_CRUISE_RESOURCE_TYPE);
        if(pages != null && !pages.isEmpty()){
            pages.forEach(page ->{
                
              String comboCruiseId = page.getProperties().get("comboCruiseId",String.class);
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
        }
        LOGGER.debug("Combo Cruise importer -- Finish combo cruises diff");
    }
}