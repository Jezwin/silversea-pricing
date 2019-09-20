package com.silversea.aem.proxies;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PromoProxyTest {

    @Before
    public void before() {

    }

    @Test
    public void mapsBusinessAirFareFromResponse() throws IOException, JSONException {

        ApiClient apiClientMock = mock(ApiClient.class);
        when(apiClientMock
                .Get("https://shop.silversea.com/api/v1/prices_promotions/1925/GBP"))
                .thenReturn(GetFileContents("src/test/resources/pricePromoApiResponse.json"));
        PromoProxy proxy = new PromoProxy(apiClientMock);

        PromoPrice promo = proxy.getPromoByCurrencyAndCruiseCode("GBP", "1925");

        assertEquals(3498, promo.businessClassPromoPrice);
    }

    private String GetFileContents(String path) throws IOException {
        return new String (Files.readAllBytes(Paths.get(path)));
    }
}