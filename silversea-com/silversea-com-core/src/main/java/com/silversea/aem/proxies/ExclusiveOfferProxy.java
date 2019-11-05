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

    private final String url = "{baseUrl}/exclusive-offers/{cruiseCode}/{currency}/{locale}";
    private String domain;

    public ExclusiveOfferProxy(ApiClient apiClient, String domain){
        this.domain = domain;
        this.apiClient = apiClient;
    }

    public Map getExclusiveOfferTokens(String currency, String cruiseCode, Locale locale) throws IOException, JSONException, UnsuccessfulHttpRequestException {

            String resolvedUrl = url.replace("{baseUrl}", domain)
                    .replace("{cruiseCode}", cruiseCode)
                    .replace("{currency}", currency)
                    .replace("{locale}", locale.toString());

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

