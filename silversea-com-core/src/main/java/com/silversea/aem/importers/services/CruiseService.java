package com.silversea.aem.importers.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.components.beans.LowestPrice;
import com.silversea.aem.components.beans.PriceData;

import io.swagger.client.ApiException;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Price;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageSpecialOffer;

@Deprecated
public interface CruiseService {

    void init() ;

    Page getCruisePage(Page destinationPage,Integer voyageId,String voyageName) throws WCMException, RepositoryException;

    void setCruiseTags(List<Integer> features,Integer voyageId,boolean isExpedition,Integer days, Page page) throws RepositoryException ;

    void buildOrUpdateIteneraries(Page cruisePage,Integer voyageId,String url)
            throws RepositoryException, IOException, ApiException ;

    void replicateResource(String path) throws RepositoryException;

    void replicatePageWithChildren(Page page)throws RepositoryException;

    void updateItineraryNode(Node itineraryNode, Itinerary itinerary) throws RepositoryException ;

    void updateLandNodes(List<LandItinerary> landProgramList, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;

    void updateHotelNodes(List<HotelItinerary> hotels, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;

    void updateExcursionsNode(List<ShorexItinerary> excursions, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;

    VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices, Integer voyageId) ;

    void buildOrUpdateSuiteNodes(LowestPrice lowestPrice,Page cruisePage,Integer voyageId,Integer shipId,List<VoyagePriceComplete> voyagePricesComplete)
            throws RepositoryException, IOException, ApiException ;

    String calculateDurationCategory(Integer days);

    void buildSuitesGrouping(LowestPrice lowestPrice,Node rootNode, Page suiteRef, Price price, VoyagePriceComplete voyagePrice)
            throws RepositoryException ;

    void buildOrUpdateVariationNodes(String suiteRef,LowestPrice lowestPrice,List<VoyagePriceMarket> voyagePriceMarketList, Node suiteNode,
            String suiteCategoryCode, String voyageCode) throws RepositoryException ;

    Page findSuiteReference(Integer shipId, String suiteCategoryCode) throws RepositoryException;

    Page getDestination(Integer destinationId);

    String[] findSpecialOffersReferences(List<VoyageSpecialOffer> voyageSpecialOffers, Integer voyageId);

    String downloadAndSaveAsset(String path, String imageName);

    void calculateLowestPrice(Map<String, PriceData> lowestPrices, Price price, String marketCode);

    void buildLowestPrices(Node rootNode, Map<String, PriceData> prices) throws RepositoryException;

    void updateReplicationStatus(Boolean isDeleted, Boolean isVisible,Page page);

    void buildVariationsLowestPrices(Node suitesNode, LowestPrice lowestPrice)throws RepositoryException;

    List<Page> getPagesByResourceType(String resourceType,String language) throws RepositoryException;

    void changeReferenceBylanguage(Node rootNode,String nodeName,String reference,String language)throws RepositoryException;

}
