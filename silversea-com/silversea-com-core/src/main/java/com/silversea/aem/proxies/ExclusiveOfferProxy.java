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

    private final String url = "https://aws.lambda/api/v1/prices_promotions/{cruiseCode}/{currency}/{locale}";

    public ExclusiveOfferProxy(ApiClient apiClient){
        this.apiClient = apiClient;
    }

    public Map getExclusiveOfferTokens(String currency, String cruiseCode, Locale locale) throws IOException, JSONException {

            String resolvedUrl = url.replace("{cruiseCode}", cruiseCode).replace("{currency}", currency).replace("{locale}", locale.toString());

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

