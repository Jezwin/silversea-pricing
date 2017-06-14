package com.silversea.aem.services;

import java.io.IOException;
import java.util.List;

import io.swagger.client.ApiException;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.ShorexItinerary;
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

}