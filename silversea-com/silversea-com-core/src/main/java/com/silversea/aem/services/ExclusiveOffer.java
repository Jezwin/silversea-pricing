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
import java.util.function.Supplier;

import static com.silversea.aem.logging.JsonLog.jsonLog;

public class ExclusiveOffer {

    private ExclusiveOfferProxy exclusiveOfferProxy;
    private SSCLogger logger;

    public ExclusiveOffer(ExclusiveOfferProxy exclusiveOfferProxy, SSCLogger logger) {
        this.exclusiveOfferProxy = exclusiveOfferProxy;
        this.logger = logger;
    }

    public void resolveTokensByCruiseCode(Map<String, ValueTypeBean> tokens, String currency, Locale locale, String countryCode, String cruiseCode) {
        resolveTokens(
                tokens,
                () -> exclusiveOfferProxy.getTokensByCruiseCode(currency, locale, countryCode, cruiseCode),
                () -> getLogMessageByCruiseCode(cruiseCode, locale, currency));
    }

    public void resolveTokensByExclusiveOfferId(Map<String, ValueTypeBean> tokens, String currency, Locale locale, String countryCode, String exclusiveOfferId) {
        resolveTokens(
                tokens,
                () -> exclusiveOfferProxy.getTokensByExclusiveOfferId(currency, locale, countryCode, exclusiveOfferId),
                () -> getLogMessageByExclusiveOfferId(currency, locale, exclusiveOfferId));
    }

    private void resolveTokens(Map<String, ValueTypeBean> tokens, TokensSupplier getTokens, Supplier<JsonLog> getLogMessage) {
        try {
            Map tokenValues = getTokens.get();

            for (Object key : tokens.keySet()) {
                if(tokenValues.containsKey(key.toString())) {
                    tokens.put(key.toString(), new ValueTypeBean(tokenValues.get(key).toString(), "token"));
                }
            }
        } catch (IOException | JSONException e) {
            logger.logError(getLogMessage.get().with(e));
        } catch (UnsuccessfulHttpRequestException e) {
            logger.logError(getLogMessage.get()
                    .with("statusCode", e.getCode())
                    .with("url", e.getUrl())
                    .with(e));
        }

    }

    private JsonLog getLogMessageByCruiseCode(String cruiseCode, Locale locale, String currency) {
        return jsonLog("resolveTokensByCruiseCode")
                .with("currency", currency)
                .with("cruiseCode", cruiseCode)
                .with("locale", locale.toString());
    }

    private JsonLog getLogMessageByExclusiveOfferId(String currency, Locale locale, String exclusiveOfferId) {
        return jsonLog("resolveTokensByExclusiveOfferId")
                .with("currency", currency)
                .with("exclusiveOfferId", exclusiveOfferId)
                .with("locale", locale.toString());
    }

    @FunctionalInterface
    public interface TokensSupplier {
        Map get() throws IOException, JSONException, UnsuccessfulHttpRequestException;
    }
}