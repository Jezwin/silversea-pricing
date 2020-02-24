package com.silversea.aem.proxies;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.ExclusiveOffer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExclusiveOfferProxyTest {

    private ExclusiveOffer exclusiveOffer;

    @Before
    public void before() throws IOException, UnsuccessfulHttpRequestException {
        ApiClient apiClientMock = mock(ApiClient.class);
        SSCLogger logger = new NullLogger();
        when(apiClientMock
                .Get("http://notUsed/exclusive-offers/1925?currency=GBP&locale=en_GB&countryCode=GB"))
                .thenReturn("{\"air_price\":\"£1'199\", \"non_use_air_credit\": \"£100\"}");

        ExclusiveOfferProxy proxy = new ExclusiveOfferProxy(apiClientMock, "http://notUsed");
        exclusiveOffer = new ExclusiveOffer(proxy, logger);
    }

    @Test
    public void ShouldOverrideTokenValueWhenJsonKeyMatches() {
        Locale locale = new Locale("en", "GB");

        Map<String, ValueTypeBean> token = new HashMap<>();
        token.put("air_price", new ValueTypeBean("", "token"));

        exclusiveOffer.resolveTokensByCruiseCode(token, "GBP", locale, "GB", "1925");

        assertEquals("£1'199", token.get("air_price").getValue());
    }

    @Test
    public void ShouldIgnoreTokenValueWhenJsonKeyDoesNotExist() {
        Locale locale = new Locale("en", "GB");

        Map<String, ValueTypeBean> token = new HashMap<>();
        token.put("key_that_does_not_exist", new ValueTypeBean("original_value", "token"));

        exclusiveOffer.resolveTokensByCruiseCode(token, "GBP", locale, "GB", "1925");

        assertEquals("original_value", token.get("key_that_does_not_exist").getValue());
    }

    @Test
    public void ShouldAddTokenValueWhenKeyDoesNotExist() {
        Locale locale = new Locale("en", "GB");

        Map<String, ValueTypeBean> emptyToken = new HashMap<>();

        exclusiveOffer.resolveTokensByCruiseCode(emptyToken, "GBP", locale, "GB", "1925");

        assertEquals("£1'199", emptyToken.get("air_price").getValue());
    }
}