package com.silversea.aem.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.api.HotelsApi;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.PricesApi;
import io.swagger.client.api.ShipsApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.api.SpecialVoyagesApi;
import io.swagger.client.api.VoyageSpecialOffersApi;
import io.swagger.client.api.VoyagesApi;
import io.swagger.client.model.Agency;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.Itinerary;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.Ship;
import io.swagger.client.model.ShorexItinerary;
import io.swagger.client.model.SpecialOffer;
import io.swagger.client.model.SpecialVoyage;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;
import io.swagger.client.model.VoyagePriceComplete;
import io.swagger.client.model.VoyageSpecialOffer;

@Service
@Component(label = "Silversea.com - Api call service")
public class ApiCallServiceImpl  implements ApiCallService {

    static final private Logger LOGGER = LoggerFactory.getLogger(ApiCallServiceImpl.class);

    //TODO : use apiConfig
    //Used temporary
    private static final int PER_PAGE = 10;

    @Reference
    private ApiConfigurationService apiConfig;

    public List<Itinerary> getCruiseIteneraries(String apiUrl, Integer voyageId) throws IOException, ApiException {
        VoyagesApi voyageItiniraryApi = new VoyagesApi();
        configureClient(voyageItiniraryApi.getApiClient());
        return voyageItiniraryApi.voyagesGetItinerary(voyageId, null, null);
    }

    public List<LandItinerary> getLandsProgram(Itinerary itinerary) throws IOException, ApiException {
        LandsApi landProgramApi = new LandsApi();
        configureClient(landProgramApi.getApiClient());
        return landProgramApi.landsGetItinerary(itinerary.getCityId(),
                itinerary.getVoyageId(), null, null, null, null);
    }

    public List<VoyageSpecialOffer> getVoyageSpecialOffers(Integer voyageId) throws IOException, ApiException {
        VoyageSpecialOffersApi voyageSpecialOffersApi = new VoyageSpecialOffersApi();
        configureClient(voyageSpecialOffersApi.getApiClient());
        return voyageSpecialOffersApi.voyageSpecialOffersGet(null, voyageId, null, null, null)
                ;    }

    public List<VoyagePriceComplete> getVoyagePrices(List<VoyagePriceComplete> voyagePricesComplete)
            throws IOException, ApiException {
        if (voyagePricesComplete.isEmpty()) {
            List<VoyagePriceComplete> result = new ArrayList<VoyagePriceComplete>();
            PricesApi pricesApi = new PricesApi();
            configureClient(pricesApi.getApiClient());
            int index = 1;
            do {
                result = pricesApi.pricesGet3(index, PER_PAGE, null);
                voyagePricesComplete.addAll(result);
                index++;
            } while (result != null && !result.isEmpty());

        }
        return voyagePricesComplete;
    }

    public List<ShorexItinerary> getExcursions(Itinerary itinerary) throws IOException, ApiException {
        ShorexesApi shorexApi = new ShorexesApi();
        configureClient(shorexApi.getApiClient());
        return shorexApi.shorexesGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null, null, null, null);
    }

    public List<HotelItinerary> getHotels(Itinerary itinerary) throws IOException, ApiException {
        HotelsApi hotelsApi = new HotelsApi();
        configureClient(hotelsApi.getApiClient());
        return hotelsApi.hotelsGetItinerary(itinerary.getCityId(), itinerary.getVoyageId(), null, null, null, null);
    }

    public List<Voyage> getVoyages(int index,VoyagesApi voyageApi)throws IOException, ApiException{
        configureClient(voyageApi.getApiClient());
        return voyageApi.voyagesGet(null, null, null, null, null, index, PER_PAGE, null, null);
    }

    public List<Voyage77> getChangedVoyages(int index,VoyagesApi voyageApi,String lastModificationDate)throws IOException, ApiException{
        configureClient(voyageApi.getApiClient());
        return voyageApi.voyagesGetChanges(lastModificationDate, index, PER_PAGE, null, null);
    }
    
    public List<SpecialVoyage> getSpecialVoyages()throws IOException, ApiException{
        SpecialVoyagesApi specialVoyagesApi = new SpecialVoyagesApi();
        configureClient(specialVoyagesApi.getApiClient());
        return specialVoyagesApi.specialVoyagesGet(null);
    }

    public void configureClient(ApiClient apiClient){
        if(apiClient !=null){
            final DigestAuthenticator authenticator = new DigestAuthenticator(new Credentials(apiConfig.getLogin(), apiConfig.getPassword()));
            final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
            apiClient.setDebugging(LOGGER.isDebugEnabled());
            apiClient.setConnectTimeout(apiConfig.getTimeout());
            apiClient.getHttpClient().interceptors().add(new AuthenticationCacheInterceptor(authCache));
            apiClient.getHttpClient().setAuthenticator(new CachingAuthenticatorDecorator(authenticator, authCache));
        }
        else{
            LOGGER.error("Api call service -- Api client is null");
        }
    }

	@Override
	public List<Agency> getTravelAgencies(int index, int pageSize, AgenciesApi travelAgenciesApi) throws IOException, ApiException {
		configureClient(travelAgenciesApi.getApiClient());
		return travelAgenciesApi.agenciesGet(null, null, null, null, null, index, pageSize);
	}

	@Override
	public List<Ship> getShips() throws IOException, ApiException {
		ShipsApi shipsApi = new ShipsApi();
		configureClient(shipsApi.getApiClient());
		return shipsApi.shipsGet(null);
	}

	@Override
	public List<SpecialOffer> getExclusiveOffers(int index, int pageSize, SpecialOffersApi spetialOffersApi)
			throws IOException, ApiException {
		configureClient(spetialOffersApi.getApiClient());
		return spetialOffersApi.specialOffersGet(index, pageSize, null);
	}
}
