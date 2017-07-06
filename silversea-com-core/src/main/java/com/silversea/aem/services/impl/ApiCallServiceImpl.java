package com.silversea.aem.services.impl;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.silversea.aem.constants.ServiceConstants;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.*;
import io.swagger.client.model.*;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO indentation
 * TODO unchecked
 * TODO typos
 * TOd
 */
@Service
@Component(label = "Silversea.com - Api call service")
@SuppressWarnings("unchecked")
public class ApiCallServiceImpl<T> implements ApiCallService {

    static final private Logger LOGGER = LoggerFactory.getLogger(ApiCallServiceImpl.class);

    // TODO use apiConfig
    // Used temporary
    private static final int PER_PAGE = 10;

    @Reference
    private ApiConfigurationService apiConfig;

    private Map<String, T> apiInstances;

    @Override
    public List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start voyage itinineraries api");
        VoyagesApi voyagesApi = (VoyagesApi) apiInstances.get(ServiceConstants.VOYAGE_API_KEY);
        List<Itinerary> itinineraries = voyagesApi.voyagesGetItinerary(voyageId, null, null);
        LOGGER.debug("Api call service -- Finish voyage itinineraries  api");
        return itinineraries;
    }

    @Override
    public List<LandItinerary> getLandsProgram(Itinerary itinerary) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call land program api");
        LandsApi landProgramApi = (LandsApi) apiInstances.get(ServiceConstants.LAND_API_KEY);
        List<LandItinerary> landPrograms = landProgramApi.landsGetItinerary(itinerary.getCityId(),
                itinerary.getVoyageId(), null, null, null, null);
        LOGGER.debug("Api call service -- finish call land program api");
        return landPrograms;
    }

    @Override
    public List<VoyageSpecialOffer> getVoyageSpecialOffers(Integer voyageId) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call voyage special offers api");
        VoyageSpecialOffersApi voyageSpecialOffersApi = (VoyageSpecialOffersApi) apiInstances
                .get(ServiceConstants.SPECIAL_OFFERS_API_KEY);
        List<VoyageSpecialOffer> specialOffers = voyageSpecialOffersApi.voyageSpecialOffersGet(null, voyageId, null,
                null, null);
        LOGGER.debug("Api call service -- Finish call voyage special offers api");
        return specialOffers;
    }

    @Override
    public List<VoyagePriceComplete> getVoyagePrices(List<VoyagePriceComplete> voyagePricesComplete)
            throws IOException, ApiException {
        if (voyagePricesComplete.isEmpty()) {
            LOGGER.debug("Api call service -- Start call price api");
            List<VoyagePriceComplete> result = new ArrayList<VoyagePriceComplete>();
            PricesApi pricesApi = (PricesApi) apiInstances.get(ServiceConstants.PRICES_API_KEY);
            configureClient(pricesApi.getApiClient());
            int index = 1;
            do {
                result = pricesApi.pricesGet3(index, PER_PAGE, null);
                voyagePricesComplete.addAll(result);
                index++;
            } while (!result.isEmpty());
            LOGGER.debug("Api call service -- Finish call price api");
        }
        return voyagePricesComplete;
    }

    @Override
    public List<ShorexItinerary> getExcursions(Itinerary itinerary) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call shorex api");
        ShorexesApi shorexApi = (ShorexesApi) apiInstances.get(ServiceConstants.SHOREX_API_KEY);
        List<ShorexItinerary> excurions = shorexApi.shorexesGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(),
                null, null, null, null);
        LOGGER.debug("Api call service -- Finish call shorex api");
        return excurions;
    }

    @Override
    public List<HotelItinerary> getHotels(Itinerary itinerary) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call hotel api");
        HotelsApi hotelsApi = (HotelsApi) apiInstances.get(ServiceConstants.HOTELS_API_KEY);
        List<HotelItinerary> hotels = hotelsApi.hotelsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null,
                null, null, null);
        LOGGER.debug("Api call service -- Finish call hotel api");
        return hotels;
    }

    @Override
    public List<Voyage> getVoyages(int index) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call voyage api");
        VoyagesApi voyageApi = (VoyagesApi) apiInstances.get(ServiceConstants.VOYAGE_API_KEY);
        List<Voyage> voyages = voyageApi.voyagesGet(null, null, null, null, null, index, PER_PAGE, null, null);
        LOGGER.debug("Api call service -- Finish call voyage api");
        return voyages;
    }

    @Override
    public List<Voyage77> getChangedVoyages(int index, String lastModificationDate) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call changed voyage api");
        VoyagesApi voyageApi = (VoyagesApi) apiInstances.get(ServiceConstants.VOYAGE_API_KEY);
        List<Voyage77> voyages = voyageApi.voyagesGetChanges(lastModificationDate, index, PER_PAGE, null, null);
        LOGGER.debug("Api call service -- Finish call changed voyage api");
        return voyages;
    }

    @Override
    public List<SpecialVoyage> getSpecialVoyages() throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call special voyage api");
        SpecialVoyagesApi specialVoyagesApi = (SpecialVoyagesApi) apiInstances
                .get(ServiceConstants.SPECIAL_VOYAGES_API_KEY);
        List<SpecialVoyage> specialVoyageList = specialVoyagesApi.specialVoyagesGet(null);
        LOGGER.debug("Api call service -- Finish call special voyage api");
        return specialVoyageList;
    }

    @Override
    public List<Land> getLandProgram(int index, int pageSize) throws ApiException {
        LOGGER.debug("Api call service -- Start call land program api");
        LandsApi landProgramApi = (LandsApi) apiInstances.get(ServiceConstants.LAND_API_KEY);
        LOGGER.debug("Api call service -- Finish call land program api");
        return landProgramApi.landsGet(null, index, PER_PAGE, null);
    }

    @Override
    public List<Land77> getLandProgramUpdate(String currentDate, int index, int pageSize)
            throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call land program update api");
        LandsApi landProgramApi = (LandsApi) apiInstances.get(ServiceConstants.LAND_API_KEY);
        LOGGER.debug("Api call service -- Finish call land program update api");
        return landProgramApi.landsGetChanges(currentDate, null, index, pageSize, null);
    }

    public List<Shorex> getShorex(int index, int pageSize) throws ApiException {
        LOGGER.debug("Api call service -- Start call shorex api");
        ShorexesApi shorexApi = (ShorexesApi) apiInstances.get(ServiceConstants.SHOREX_API_KEY);
        LOGGER.debug("Api call service -- Finish call shorex api");
        return shorexApi.shorexesGet(null, index, PER_PAGE, null);
    }

    @Override
    public List<Shorex77> getShorexUpdate(String currentDate, int index, int pageSize)
            throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call excursions update api");
        ShorexesApi shorexApi = (ShorexesApi) apiInstances.get(ServiceConstants.SHOREX_API_KEY);
        LOGGER.debug("Api call service -- Finish call excursions update api");
        return shorexApi.shorexesGetChanges(currentDate, index, pageSize, null);
    }

    @Override
    public List<Hotel> getHotels(int index, int pageSize) throws ApiException {
        LOGGER.debug("Api call service -- Start call hotels api");
        HotelsApi hotelsApi = (HotelsApi) apiInstances.get(ServiceConstants.HOTELS_API_KEY);
        LOGGER.debug("Api call service -- Finish call hotels api");
        return hotelsApi.hotelsGet(null, index, PER_PAGE, null);
    }

    @Override
    public List<Hotel77> getHotelsUpdate(String currentDate, int index, int pageSize) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call hotels updates api");
        HotelsApi hotelsApi = (HotelsApi) apiInstances.get(ServiceConstants.HOTELS_API_KEY);
        LOGGER.debug("Api call service -- Finish call hotels updates api");
        return hotelsApi.hotelsGetChanges(currentDate, index, pageSize, null);
    }

    @Override
    public List<City> getCities(int index, int pageSize) throws ApiException {
        LOGGER.debug("Api call service -- Start call cities api");
        CitiesApi citiesApi = (CitiesApi) apiInstances.get(ServiceConstants.CITIES_API_KEY);
        LOGGER.debug("Api call service -- Finish call cities api");
        return citiesApi.citiesGet(null, null, index, pageSize, null, null, null);
    }

    @Override
    public List<City77> getCitiesUpdates(String currentDate, int index, int pageSize) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call cities update api");
        CitiesApi citiesApi = (CitiesApi) apiInstances.get(ServiceConstants.CITIES_API_KEY);
        LOGGER.debug("Api call service -- Finish call cities updates api");
        return citiesApi.citiesGetChanges(currentDate, index, pageSize, null, null, null);
    }

    @Override
    public List<Agency> getTravelAgencies(int index, int pageSize) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call Travel agencies api");
        AgenciesApi travelAgenciesApi = (AgenciesApi) apiInstances.get(ServiceConstants.TRAVEL_AGENCIES_API_KEY);
        LOGGER.debug("Api call service -- Finish call voyage api");
        return travelAgenciesApi.agenciesGet(null, null, null, null, null, index, pageSize);
    }

    @Override
    public List<Ship> getShips() throws ApiException {
        LOGGER.debug("Api call service -- Start call ships api");
        ShipsApi shipsApi = (ShipsApi) apiInstances.get(ServiceConstants.SHIPS_API_KEY);
        LOGGER.debug("Api call service -- Finish call ships api");
        return shipsApi.shipsGet(null);
    }

    @Override
    public List<SpecialOffer> getExclusiveOffers(int index, int pageSize) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call exclusive offers api");
        SpecialOffersApi spetialOffersApi = (SpecialOffersApi) apiInstances
                .get(ServiceConstants.EXCLUSIVE_OFFERS_API_KEY);
        LOGGER.debug("Api call service -- Finish call exclusive offers api");
        return spetialOffersApi.specialOffersGet(index, pageSize, null);
    }

    @Override
    public List<Feature> getFeatures() throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call features api");
        FeaturesApi featuresApi = (FeaturesApi) apiInstances.get(ServiceConstants.FEATURES_API_KEY);
        LOGGER.debug("Api call service -- Finish call features api");
        return featuresApi.featuresGet(null);
    }

    @Override
    public List<Brochure> getBrochures(int index, int pageSize) throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call brochures api");
        BrochuresApi brochuresApi = (BrochuresApi) apiInstances.get(ServiceConstants.BROCHURES_API_KEY);
        LOGGER.debug("Api call service -- Finish call brochures api");
        return brochuresApi.brochuresGet(null, index, PER_PAGE, null);
    }

    @Override
    public List<Country> getContries() throws IOException, ApiException {
        LOGGER.debug("Api call service -- Start call contry api");
        CountriesApi countriesApi = (CountriesApi) apiInstances.get(ServiceConstants.COUNTRY_API_KEY);
        LOGGER.debug("Api call service -- Finish call contry api");
        return countriesApi.countriesGet(null, null);
    }

    private void configureClient(ApiClient apiClient) {
        if (apiClient != null) {
            final DigestAuthenticator authenticator = new DigestAuthenticator(
                    new Credentials(apiConfig.getLogin(), apiConfig.getPassword()));
            final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
            apiClient.setDebugging(LOGGER.isDebugEnabled());
            apiClient.setConnectTimeout(apiConfig.getTimeout());
            apiClient.getHttpClient().setReadTimeout(apiConfig.getTimeout(), TimeUnit.MILLISECONDS);
            apiClient.getHttpClient().interceptors().add(new AuthenticationCacheInterceptor(authCache));
            apiClient.getHttpClient().setAuthenticator(new CachingAuthenticatorDecorator(authenticator, authCache));
        } else {
            LOGGER.error("Api call service -- Api client is null");
        }
    }

    @Activate
    @Modified
    public void activate(final ComponentContext context) {
        apiInstances = new HashMap<>();

        VoyagesApi voyagesApi = new VoyagesApi();
        configureClient(voyagesApi.getApiClient());
        HotelsApi hotelsApi = new HotelsApi();
        configureClient(hotelsApi.getApiClient());
        ShorexesApi shorexesApi = new ShorexesApi();
        configureClient(shorexesApi.getApiClient());
        PricesApi pricesApi = new PricesApi();
        configureClient(pricesApi.getApiClient());
        LandsApi LandsApi = new LandsApi();
        configureClient(LandsApi.getApiClient());
        VoyageSpecialOffersApi voyageSpecialOffersApi = new VoyageSpecialOffersApi();
        configureClient(LandsApi.getApiClient());
        SpecialVoyagesApi specialVoyagesApi = new SpecialVoyagesApi();
        configureClient(specialVoyagesApi.getApiClient());

        CitiesApi citiesApi = new CitiesApi();
        configureClient(citiesApi.getApiClient());
        ShipsApi shipsApi = new ShipsApi();
        configureClient(shipsApi.getApiClient());
        AgenciesApi travelAgenciesApi = new AgenciesApi();
        configureClient(travelAgenciesApi.getApiClient());
        FeaturesApi featuresApi = new FeaturesApi();
        configureClient(featuresApi.getApiClient());
        BrochuresApi brochuresApi = new BrochuresApi();
        configureClient(brochuresApi.getApiClient());
        SpecialOffersApi spetialOffersApi = new SpecialOffersApi();
        configureClient(spetialOffersApi.getApiClient());
        CountriesApi countriesApi = new CountriesApi();
        configureClient(countriesApi.getApiClient());

        apiInstances.put(ServiceConstants.VOYAGE_API_KEY, (T) voyagesApi);
        apiInstances.put(ServiceConstants.HOTELS_API_KEY, (T) hotelsApi);
        apiInstances.put(ServiceConstants.SHOREX_API_KEY, (T) shorexesApi);
        apiInstances.put(ServiceConstants.PRICES_API_KEY, (T) pricesApi);
        apiInstances.put(ServiceConstants.LAND_API_KEY, (T) LandsApi);
        apiInstances.put(ServiceConstants.SPECIAL_OFFERS_API_KEY, (T) voyageSpecialOffersApi);
        apiInstances.put(ServiceConstants.SPECIAL_VOYAGES_API_KEY, (T) specialVoyagesApi);

        apiInstances.put(ServiceConstants.CITIES_API_KEY, (T) citiesApi);
        apiInstances.put(ServiceConstants.SHIPS_API_KEY, (T) shipsApi);
        apiInstances.put(ServiceConstants.TRAVEL_AGENCIES_API_KEY, (T) travelAgenciesApi);
        apiInstances.put(ServiceConstants.FEATURES_API_KEY, (T) featuresApi);
        apiInstances.put(ServiceConstants.BROCHURES_API_KEY, (T) brochuresApi);
        apiInstances.put(ServiceConstants.EXCLUSIVE_OFFERS_API_KEY, (T) spetialOffersApi);
        apiInstances.put(ServiceConstants.COUNTRY_API_KEY, (T) countriesApi);
    }
}
