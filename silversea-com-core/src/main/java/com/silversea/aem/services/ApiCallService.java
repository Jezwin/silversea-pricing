package com.silversea.aem.services;

import java.io.IOException;
import java.util.List;

import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.api.CitiesApi;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Agency;
import io.swagger.client.model.City;
import io.swagger.client.model.City77;
import io.swagger.client.model.Feature;
import io.swagger.client.model.Hotel77;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.Land77;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Ship;
import io.swagger.client.model.Shorex77;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.SpecialOffer;
import io.swagger.client.model.SpecialVoyage;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyageSpecialOffer;

public interface ApiCallService {


    List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException;

    List<LandItinerary> getLandsProgram(Itinerary itinerary) throws IOException, ApiException ;

    List<VoyageSpecialOffer> getVoyageSpecialOffers(Integer voyageId) throws IOException, ApiException;

    List<VoyagePriceComplete> getVoyagePrices(List<VoyagePriceComplete> voyagePricesComplete) throws IOException, ApiException ;

    List<ShorexItinerary> getExcursions(Itinerary itinerary) throws IOException, ApiException;

    List<HotelItinerary> getHotels(Itinerary itinerary) throws IOException, ApiException;
    
    List<Voyage77> getChangedVoyages(int index,VoyagesApi voyageApi,String lastModificationDate)throws IOException, ApiException;

    List<Voyage> getVoyages(int index,VoyagesApi voyageApi)throws IOException, ApiException;
    
    List<SpecialVoyage> getSpecialVoyages()throws IOException, ApiException;
    
    List<Agency> getTravelAgencies(int index, int pageSize, AgenciesApi travelAgenciesApi)throws IOException, ApiException;
    
    List<Ship> getShips()throws IOException, ApiException;
    
    List<SpecialOffer> getExclusiveOffers(int index, int pageSize, SpecialOffersApi spetialOffersApi)throws IOException, ApiException;
    
    List<City> getCities(int index, int pageSize, CitiesApi citiesApi)throws IOException, ApiException;
    
    List<City77> getCitiesUpdates(String currentDate,int index, int pageSize, CitiesApi citiesApi)throws IOException, ApiException;
    
    List<Hotel77> getHotelsUpdate(String currentDate,int index, int pageSize, HotelsApi hotelsApi)throws IOException, ApiException;
    
    List<Shorex77> getShorexUpdate(String currentDate,int index, int pageSize, ShorexesApi shorexesApi)throws IOException, ApiException;
    
    List<Land77> getLandProgramUpdate(String currentDate,int index, int pageSize, LandsApi landsApi)throws IOException, ApiException;
    
    List<Feature> getFeatures()throws IOException, ApiException;

}
