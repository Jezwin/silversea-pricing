package com.silversea.aem.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
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

public interface CruiseService {

    public void init() ;
    
    public Page getCruisePage(Page destinationPage,Integer voyageId,String voyageName) throws WCMException, RepositoryException;

    public void setCruiseTags(List<Integer> features,Integer voyageId,boolean isExpedition, Page page) throws RepositoryException ;
    
    public void buildOrUpdateIteneraries(Page cruisePage,Integer voyageId,String url)
            throws RepositoryException, IOException, ApiException ;

    public void replicateResource(String path) throws RepositoryException;

    public void updateItineraryNode(Node itineraryNode, Itinerary itinerary) throws RepositoryException ;

    public void updateLandNodes(List<LandItinerary> landProgramList, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;
    
    public void updateHotelNodes(List<HotelItinerary> hotels, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;

    public void updateExcursionsNode(List<ShorexItinerary> excursions, Node itineraryNode, Itinerary itinerary)
            throws RepositoryException ;

    public VoyagePriceComplete getVoyagePriceById(List<VoyagePriceComplete> voyagePrices, Integer voyageId) ;

    public void buildOrUpdateSuiteNodes(Page cruisePage,Integer voyageId,Integer shipId,List<VoyagePriceComplete> voyagePricesComplete)
            throws RepositoryException, IOException, ApiException ;

    public void buildSuitesGrouping(Node rootNode, Page suiteRef, Price price, VoyagePriceComplete voyagePrice)
            throws RepositoryException ;

    public void buildOrUpdateVariationNodes(List<VoyagePriceMarket> voyagePriceMarketList, Node suiteNode,
            String suiteCategoryCode, String voyageCode) throws RepositoryException ;

    public Page findSuiteReference(Integer shipId, String suiteCategoryCode) throws RepositoryException;

    public Page getDestination(Integer destinationId);

    public String[] findSpecialOffersReferences(List<VoyageSpecialOffer> voyageSpecialOffers, Integer voyageId);

    public String downloadAndSaveAsset(String path, String imageName);
    
    public void calculateLowestPrice(Map<String, PriceData> lowestPrices, Price price, String marketCode);

    public void buildLowestPrices(Node rootNode, Map<String, PriceData> prices) throws RepositoryException;
    
    public void updateReplicationStatus(Boolean isDeleted, Boolean isVisible,Page page) throws RepositoryException;
  
}
