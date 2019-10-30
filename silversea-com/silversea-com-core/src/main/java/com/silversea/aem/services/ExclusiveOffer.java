package com.silversea.aem.services;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.proxies.ExclusiveOfferProxy;
import com.silversea.aem.proxies.UnsuccessfulHttpRequestException;
import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.silversea.aem.logging.JsonLog.jsonLog;

public class ExclusiveOffer {

    private ExclusiveOfferProxy exclusiveOfferProxy;
    private SSCLogger logger;

    public ExclusiveOffer(ExclusiveOfferProxy exclusiveOfferProxy, SSCLogger logger) {
        this.exclusiveOfferProxy = exclusiveOfferProxy;
        this.logger = logger;
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
            logger.logError(getLogMessage(currency, cruiseCode, locale).with(e));
        } catch (JSONException e) {
            logger.logError(getLogMessage(currency, cruiseCode, locale).with(e));
        } catch (UnsuccessfulHttpRequestException e) {
            logger.logError(getLogMessage(currency, cruiseCode, locale)
                .with("statusCode", e.getCode())
                .with("url", e.getUrl())
                .with(e));
        }
    }

    private JsonLog getLogMessage(String currency, String cruiseCode, Locale locale) {
        return jsonLog("ResolveExclusiveOfferTokens")
                .with("currency", currency)
                .with("cruiseCode", cruiseCode)
                .with("locale", locale.toString());
    }
}