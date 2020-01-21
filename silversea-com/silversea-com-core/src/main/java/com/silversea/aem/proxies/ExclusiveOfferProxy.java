package com.silversea.aem.proxies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ExclusiveOfferProxy {

    private ApiClient apiClient;

    private final String urlByCruiseCode = "{baseUrl}/exclusive-offers/{cruiseCode}?currency={currency}&locale={locale}&countryCode={countryCode}";
    private final String urlByExclusiveOfferId = "{baseUrl}/exclusive-offers/group/{exclusiveOfferId}?currency={currency}&locale={locale}&countryCode={countryCode}";
    private String domain;

    public ExclusiveOfferProxy(ApiClient apiClient, String domain){
        this.domain = domain;
        this.apiClient = apiClient;
    }

    public Map getTokensByCruiseCode(String currency, Locale locale, String countryCode, String cruiseCode) throws IOException, JSONException, UnsuccessfulHttpRequestException {

        String template = urlByCruiseCode.replace("{cruiseCode}", cruiseCode);
        return getTokens(currency, locale, countryCode, template);
    }

    public Map getTokensByExclusiveOfferId(String currency, Locale locale, String countryCode, String exclusiveOfferId) throws IOException, JSONException, UnsuccessfulHttpRequestException {

        String template = urlByExclusiveOfferId.replace("{exclusiveOfferId}", exclusiveOfferId);
        return getTokens(currency, locale, countryCode, template);
    }

    private Map getTokens(String currency, Locale locale, String countryCode, String template) throws IOException, UnsuccessfulHttpRequestException, JSONException {
        String resolvedUrl = template
                .replace("{baseUrl}", domain)
                .replace("{currency}", currency)
                .replace("{locale}", locale.toString())
                .replace("{countryCode}", countryCode);
        String response = apiClient.Get(resolvedUrl);

        return MapExclusiveOffer(response);
    }

    private Map MapExclusiveOffer(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        return gson.fromJson(jsonResponse.toString(), Map.class);
    }
}

