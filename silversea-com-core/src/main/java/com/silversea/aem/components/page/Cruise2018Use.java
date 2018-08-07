package com.silversea.aem.components.page;

import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.utils.PathUtils;
import java.util.Locale;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.ObjectUtils;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private String previous;
    private String previousDeparture;
    private String previousArrival;
    private String next;
    private String nextDeparture;
    private String nextArrival;

    @Override
    public void activate() throws Exception {
        super.activate();
        cruiseModel = retrieveCruiseModel();
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);
        prices = retrievePrices(cruiseModel);

        //Init the Previous and Next cruise (navigation pane)
        String shipName = (cruiseModel.getShip() != null) ? cruiseModel.getShip().getName() : null;
        searchPreviousAndNextCruise(shipName);
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

    private void searchPreviousAndNextCruise(String shipName) {
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);

        List<CruiseModelLight> allCruises = (cruisesCacheService != null) ? cruisesCacheService.getCruises(lang) : null;

        if (allCruises != null) {
            // needed inside filter
            AtomicInteger indexCurrentCruise = new AtomicInteger(-1);
            AtomicInteger indexLoop = new AtomicInteger(-1);
            /*
             * Sort all cruise based on departed date before filter by ship becuase we will
             * save the index of the current voyage inside the list
             */
            allCruises.sort((Comparator.comparing(CruiseModelLight::getStartDate)));

            /*
             * We filter by ship based on current cruise and we save the index of current
             * cruise inside the list (to get previous and next)
             */
            List<CruiseModelLight> listCruiseFilterByShip = allCruises.stream().filter(cruise -> {
                String shipNameElement = cruise.getShip().getName();
                boolean isToInsert = shipName.equalsIgnoreCase(shipNameElement);
                boolean isCurrentCruise = cruise.getCruiseCode().equalsIgnoreCase(cruiseModel.getCruiseCode());
                if (isToInsert) {
                    indexLoop.incrementAndGet();
                    if (isCurrentCruise && indexCurrentCruise.get() < 0) {
                        indexCurrentCruise.set(indexLoop.get());
                    }
                }
                return isToInsert;
            }).collect(Collectors.toList());

            // Get Previous and Next Cruise
            if (indexCurrentCruise.get() > -1) {
                Integer previousCruiseIndex = indexCurrentCruise.get() - 1;
                Integer nextCruiseIndex = indexCurrentCruise.get() + 1;
                this.previous = (previousCruiseIndex >= 0) ? listCruiseFilterByShip.get(previousCruiseIndex).getPath()
                        : null;
                this.previousDeparture = (previousCruiseIndex >= 0) ? listCruiseFilterByShip.get(previousCruiseIndex).getDeparturePortName()
                        : null;
                this.previousArrival = (previousCruiseIndex >= 0) ? listCruiseFilterByShip.get(previousCruiseIndex).getArrivalPortName()
                        : null;
                this.next = (nextCruiseIndex < listCruiseFilterByShip.size())
                        ? listCruiseFilterByShip.get(nextCruiseIndex).getPath()
                        : null;
                this.nextDeparture = (nextCruiseIndex < listCruiseFilterByShip.size())
                        ? listCruiseFilterByShip.get(nextCruiseIndex).getDeparturePortName()
                        : null;
                this.nextArrival = (nextCruiseIndex < listCruiseFilterByShip.size())
                        ? listCruiseFilterByShip.get(nextCruiseIndex).getArrivalPortName()
                        : null;
            }
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

    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    /**
     * @return get previous cruise in the destination
     */
    public String getPrevious() {
        return previous;
    }

    /**
     * @return get next cruise in the destination
     */
    public String getNext() {
        return next;
    }

    /**
     * @return get previous cruise in the destination
     */
    public String getPreviousDeparture() {
        return previousDeparture;
    }

    /**
     * @return get next cruise in the destination
     */
    public String getNextDeparture() {
        return nextDeparture;
    }

    /**
     * @return get previous cruise in the destination
     */
    public String getPreviousArrival() {
        return previousArrival;
    }

    /**
     * @return get next cruise in the destination
     */
    public String getNextArrival() {
        return nextArrival;
    }
}
