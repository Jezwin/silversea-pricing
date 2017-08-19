package com.silversea.aem.services;

import io.swagger.client.ApiException;
import io.swagger.client.model.*;

import java.io.IOException;
import java.util.List;

@Deprecated
public interface ApiCallService {

    /**
     * TODO typo
     */
    List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException;

    List<LandItinerary> getLandsProgram(Itinerary itinerary) throws IOException, ApiException;

    List<VoyageSpecialOffer> getVoyageSpecialOffers(Integer voyageId) throws IOException, ApiException;

    List<VoyagePriceComplete> getVoyagePrices(List<VoyagePriceComplete> voyagePricesComplete) throws IOException, ApiException;

    List<ShorexItinerary> getExcursions(Itinerary itinerary) throws IOException, ApiException;

    List<HotelItinerary> getHotels(Itinerary itinerary) throws IOException, ApiException;

    List<Voyage77> getChangedVoyages(int index, String lastModificationDate) throws IOException, ApiException;

    List<SpecialVoyage> getSpecialVoyages() throws IOException, ApiException;

    List<Agency> getTravelAgencies(int index, int pageSize) throws IOException, ApiException;
}
