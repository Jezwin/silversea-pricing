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
import java.util.HashMap;

import static org.junit.Assert.*;

public class PromoProxyIntegrationTest {
    @Before
    public void before(){

    }

    @Test
    public void mapsBusinessAirFareFromResponseInt()throws IOException, JSONException {
        AwsSecretsManager secretManager = new AwsSecretsManagerImpl(getCoreConfig());
        ApiClient apiClient = new ApiClientImpl(secretManager);
        PromoProxy proxy = new PromoProxy(apiClient);

        PromoPrice promo=proxy.getPromoByCurrencyAndCruiseCode("GBP","1925");

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
}