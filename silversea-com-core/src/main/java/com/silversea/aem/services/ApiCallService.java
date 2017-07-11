package com.silversea.aem.services;

import io.swagger.client.ApiException;
import io.swagger.client.model.*;

import java.io.IOException;
import java.util.List;

public interface ApiCallService {

    List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException;

    List<LandItinerary> getLandsProgram(Itinerary itinerary) throws IOException, ApiException;

    List<VoyageSpecialOffer> getVoyageSpecialOffers(Integer voyageId) throws IOException, ApiException;

    List<VoyagePriceComplete> getVoyagePrices(List<VoyagePriceComplete> voyagePricesComplete) throws IOException, ApiException;

    List<ShorexItinerary> getExcursions(Itinerary itinerary) throws IOException, ApiException;

    List<HotelItinerary> getHotels(Itinerary itinerary) throws IOException, ApiException;

    List<Voyage77> getChangedVoyages(int index, String lastModificationDate) throws IOException, ApiException;

    List<Voyage> getVoyages(int index) throws IOException, ApiException;

    List<SpecialVoyage> getSpecialVoyages() throws IOException, ApiException;

    List<Agency> getTravelAgencies(int index, int pageSize) throws IOException, ApiException;

//    List<Agency> getTravelAgenciesUpdate(int index, int pageSize)throws IOException, ApiException;

    List<Ship> getShips() throws ApiException;

    List<SpecialOffer> getExclusiveOffers(int index, int pageSize) throws IOException, ApiException;

    List<City> getCities(int index, int pageSize) throws ApiException;

    List<City77> getCitiesUpdates(String currentDate, int index, int pageSize) throws IOException, ApiException;

    List<Hotel> getHotels(int index, int pageSize) throws ApiException;

    List<Hotel77> getHotelsUpdate(String currentDate, int index, int pageSize) throws IOException, ApiException;

    List<Shorex> getShorex(int index, int pageSize) throws ApiException;

    List<Shorex77> getShorexUpdate(String currentDate, int index, int pageSize) throws IOException, ApiException;

    List<Land> getLandProgram(int index, int pageSize) throws ApiException;

    List<Land77> getLandProgramUpdate(String currentDate, int index, int pageSize) throws IOException, ApiException;

    List<Brochure> getBrochures(int index, int pageSize) throws IOException, ApiException;

    List<Feature> getFeatures() throws IOException, ApiException;

    List<Country> getCountries() throws IOException, ApiException;

}
