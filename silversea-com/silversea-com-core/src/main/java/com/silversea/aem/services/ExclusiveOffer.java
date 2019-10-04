package com.silversea.aem.services;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.models.PromoPrice;
import com.silversea.aem.proxies.PromoProxy;
import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class ExclusiveOffer {

    private final PromoProxy promoProxy;

    public ExclusiveOffer(PromoProxy promoProxy) {
        this.promoProxy = promoProxy;
    }

    public void ResolveExclusiveOfferTokens(Map<String, ValueTypeBean> tokens, String currency, String cruiseCode, Locale locale) {
        try {
            PromoPrice promoprice = promoProxy.getPromoPrice(currency, cruiseCode);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            String formattedPrice = currencyFormatter.format(promoprice.getBusinessClassPromoPriceEachWay());

            tokens.put("air_price", new ValueTypeBean(formattedPrice, "token"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


