package com.silversea.aem.components.page;

import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.CruiseModel;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class Cruise2018Use extends EoHelper {

    private static final EoConfigurationBean EO_CONFIG = new EoConfigurationBean();

    static {
        EO_CONFIG.setTitleVoyage(true);
        EO_CONFIG.setShortDescriptionVoyage(true);
        EO_CONFIG.setDescriptionMain(true);
        EO_CONFIG.setFootnoteVoyage(true);
        EO_CONFIG.setMapOverheadVoyage(true);
        EO_CONFIG.setCruiseFareVoyage(true);
        EO_CONFIG.setPriorityWeight(true);
        EO_CONFIG.setIconVoyage(true);
    }

    private List<ExclusiveOfferItem> exclusiveOffers = new ArrayList<>();

    private List<SuitePrice> prices = new ArrayList<>();
    private CruiseModel cruiseModel;

    @Override
    public void activate() throws Exception {
        super.activate();
        cruiseModel = retrieveCruiseModel();
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);
        prices = retrievePrices(cruiseModel);
    }

    private List<SuitePrice> retrievePrices(CruiseModel cruise) {
        Locale locale = getCurrentPage().getLanguage(false);
        return cruise.getPrices().stream()
                .filter(price -> geomarket.equals(price.getGeomarket()))
                .filter(price -> currency.equals(price.getCurrency()))
                .distinct()
                .map(price -> new SuitePrice(price.getSuite(), price, locale, price.getSuiteCategory()))
                .collect(toList());
    }

    private List<ExclusiveOfferItem> retrieveExclusiveOffers(CruiseModel cruise) {
        return cruise.getExclusiveOffers().stream()
                .filter(eo -> eo.getGeomarkets() != null && eo.getGeomarkets().contains(geomarket))
                .map(exclusiveOfferModel -> {
                    EO_CONFIG.setActiveSystem(exclusiveOfferModel.getActiveSystem());
                    EoBean result = super.parseExclusiveOffer(EO_CONFIG, exclusiveOfferModel);
                    String destinationPath = cruise.getDestination().getPath();
                    return new ExclusiveOfferItem(exclusiveOfferModel, countryCode, destinationPath, result);
                })
                .sorted(comparing((ExclusiveOfferItem eo) -> firstNonNull(eo.getPriorityWeight(), 0)).reversed())
                .collect(toList());
    }

    private CruiseModel retrieveCruiseModel() {
        if (getRequest().getAttribute("cruiseModel") != null) {
            return (CruiseModel) getRequest().getAttribute("cruiseModel");
        } else {
            CruiseModel cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
            getRequest().setAttribute("cruiseModel", cruiseModel);
            return cruiseModel;
        }
    }

    public List<ExclusiveOfferItem> getExclusiveOffers() {
        return exclusiveOffers;
    }

    public List<SuitePrice> getPrices() {
        return prices;
    }

    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

}
