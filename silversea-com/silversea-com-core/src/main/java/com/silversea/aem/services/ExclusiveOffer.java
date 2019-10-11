package com.silversea.aem.services;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.proxies.ExclusiveOfferProxy;
import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ExclusiveOffer {

    private ExclusiveOfferProxy exclusiveOfferProxy;

    public ExclusiveOffer(ExclusiveOfferProxy exclusiveOfferProxy) {
        this.exclusiveOfferProxy = exclusiveOfferProxy;
    }

    public void ResolveExclusiveOfferTokens(Map<String, ValueTypeBean> tokens, String currency, String cruiseCode, Locale locale) {
        try {

            Map tokenValues = exclusiveOfferProxy.getExclusiveOfferTokens(currency, cruiseCode, locale);

            for (Object key : tokens.keySet())
            {
                if(tokenValues.containsKey(key.toString())) {
                    tokens.put(key.toString(), new ValueTypeBean(tokenValues.get(key).toString(), "token"));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}