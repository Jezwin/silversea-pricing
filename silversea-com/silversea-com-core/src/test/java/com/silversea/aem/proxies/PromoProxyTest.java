package com.silversea.aem.proxies;

import com.silversea.aem.config.CoreConfig;
import com.silversea.aem.models.PromoPrice;
import com.silversea.aem.utils.AwsSecretsManager;
import com.silversea.aem.utils.AwsSecretsManagerImpl;
import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        assertEquals(3498, promo.businessClassPromoPrice);
    }

    @Test
    public void mapsBusinessAirFareFromResponseIntegration()throws IOException, JSONException {
        AwsSecretsManager secretManager = new AwsSecretsManagerImpl(getCoreConfig());
        ApiClient apiClient = new ApiClientImpl(secretManager);
        PromoProxy proxy = new PromoProxy(apiClient);

        PromoPrice promo=proxy.getPromoPrice("GBP","1925");

        //todo: make assert more generic
        assertEquals(1998, promo.businessClassPromoPrice);
    }

    private CoreConfig getCoreConfig(){
        BundleContext bundleContext=MockOsgi.newBundleContext();
        CoreConfig config = new CoreConfig();

        //todo get region and secretName from env specific config
        HashMap<String,String> properties = new HashMap<>();
        properties.put("awsRegion","us-east-1");
        properties.put("awsSecretName","dev/silversea-com");

        MockOsgi.activate(config,bundleContext,properties);
        return config;
    }

    private String GetFileContents(String path) throws IOException {
        return new String (Files.readAllBytes(Paths.get(path)));
    }
}