package com.silversea.aem.proxies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silversea.aem.models.PromoPrice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PromoProxy {

    private ApiClient apiClient;

    private final String url = "https://shop.silversea.com/api/v1/prices_promotions/{cruiseCode}/{currency}";

    public PromoProxy(ApiClient apiClient){
        this.apiClient = apiClient;
    }

    public PromoPrice getPromoPrice(String currency, String cruiseCode) throws IOException, JSONException {

            String resolvedUrl = url.replace("{cruiseCode}", cruiseCode).replace("{currency}", currency);

            String response = apiClient.Get(resolvedUrl);

            return MapPromoPrice(response);
    }

    private PromoPrice MapPromoPrice(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray airPrices = jsonResponse.getJSONArray("air_prices");

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        return gson.fromJson(airPrices.getJSONObject(0).toString(), PromoPrice.class);
    }
}
