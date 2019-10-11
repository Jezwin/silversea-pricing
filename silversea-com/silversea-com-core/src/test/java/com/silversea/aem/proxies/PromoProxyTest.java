package com.silversea.aem.proxies;

import com.silversea.aem.models.PromoPrice;
import com.silversea.aem.utils.AwsSecretsManager;
import com.silversea.aem.utils.AwsSecretsManagerClientWrapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
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

        PromoPrice promo = proxy.getPromoPrice("GBP", "1925");

        assertEquals(3498, promo.getBusinessClassPromoPrice());
    }

    //todo: Move to a separate project for integration tests
    @Test
    @Ignore
    public void mapsBusinessAirFareFromResponseIntegration()throws IOException, JSONException {
        //todo: get env specific config
        AwsSecretsManager secretManager = new AwsSecretsManagerClientWrapper("us-east-1", "dev/silversea-com");
        ApiClient apiClient = new OkHttpClientWrapper(secretManager);
        PromoProxy proxy = new PromoProxy(apiClient);

        PromoPrice promo=proxy.getPromoPrice("GBP","1925");

        //todo: make assert more generic
        assertEquals(1998, promo.getBusinessClassPromoPrice());
    }

    private String GetFileContents(String path) throws IOException {
        return new String (Files.readAllBytes(Paths.get(path)));
    }
}