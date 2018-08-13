package com.silversea.aem.components.page;

import com.day.cq.commons.Externalizer;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
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
    private PriceModel lowestPrice;
    private boolean waitlist;
    private String computedPriceFormatted;

    private String currentPath;
    private String ccptCode;

    private CruiseModel cruiseModel;
    private List<SilverseaAsset> assetsGallery;
    private String previous;
    private String previousDeparture;
    private String previousArrival;
    private String next;
    private String nextDeparture;
    private String nextArrival;


    @Override
    public void activate() throws Exception {
        super.activate();
        Locale locale = getCurrentPage().getLanguage(false);
        cruiseModel = retrieveCruiseModel();
        assetsGallery = retrieveAssetsGallery(cruiseModel);
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);

        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        currentPath = retrieveCurrentPath();
        ccptCode = retrieveCcptCode(selectors);

        prices = retrievePrices(cruiseModel);
        lowestPrice = retrieveLowestPrice(prices);
        waitlist = lowestPrice == null;
        computedPriceFormatted = PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());

        retrievePreviousCruise(cruiseModel).ifPresent(previous -> {
            this.previous = previous.getPath();
            this.previousArrival = previous.getArrivalPortName();
            this.previousDeparture = previous.getDeparturePortName();
        });
        retrieveNextCruise(cruiseModel).ifPresent(next -> {
            this.next = next.getPath();
            this.nextArrival = next.getArrivalPortName();
            this.nextDeparture = next.getDeparturePortName();
        });
    }

    private String retrieveCcptCode(String[] selectors) {
        for (String selectorInfo : selectors) {
            if (selectorInfo.contains("ccpt_")) {
                return selectorInfo.replace("ccpt_", ".");
            }
        }
        return "";
    }

    private String retrieveCurrentPath() {
        return getSlingScriptHelper().getService(Externalizer.class)
                .publishLink(getResourceResolver(), getCurrentPage().getPath());
    }

    private PriceModel retrieveLowestPrice(List<SuitePrice> prices) {
        return prices.stream().filter(price -> !price.isWaitList()).map(SuitePrice::getLowestPrice)
                .min(Comparator.comparing(PriceModel::getComputedPrice)).orElse(null);
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

    private List<SilverseaAsset> retrieveAssetsGallery(CruiseModel cruiseModel) {
        List<SilverseaAsset> assetsListResult = new ArrayList<>();
        if (cruiseModel != null) {
            if (StringUtils.isNotBlank(cruiseModel.getAssetSelectionReference())) {
                assetsListResult.addAll(AssetUtils
                        .buildSilverseaAssetList(cruiseModel.getAssetSelectionReference(), getResourceResolver(),
                                null));
            }
            assetsListResult.addAll(retrieveAssetsFromShip(cruiseModel.getShip()));
        }

        return assetsListResult;
    }

    private List<SilverseaAsset> retrieveAssetsFromShip(ShipModel shipModel) {
        List<SilverseaAsset> listShipAssets = new ArrayList<>();
        if (shipModel != null) {
            List<SilverseaAsset> virtualTourAssets = new ArrayList<>();
            if (StringUtils.isNotEmpty(cruiseModel.getShip().getPhotoVideoSuiteSelectionReference())) {
                listShipAssets.addAll(AssetUtils
                        .buildSilverseaAssetList(shipModel.getPhotoVideoSuiteSelectionReference(),
                                getResourceResolver(), null));
            } else {
                retrieveAssetsFromShip(shipModel.getSuites(), listShipAssets, virtualTourAssets);
            }
            retrieveAssetsFromShip(shipModel.getDinings(), listShipAssets, virtualTourAssets);
            retrieveAssetsFromShip(shipModel.getPublicAreas(), listShipAssets, virtualTourAssets);
            listShipAssets.addAll(virtualTourAssets);
        }
        return listShipAssets;
    }

    private void retrieveAssetsFromShip(List<? extends ShipAreaModel> shipEntitiy, List<SilverseaAsset> classicAssets,
                                        List<SilverseaAsset> virtualTourAssets) {
        if (shipEntitiy != null && !shipEntitiy.isEmpty()) {
            Map<String, List<SilverseaAsset>> mapAsset =
                    AssetUtils.addAllShipAreaAssets(getResourceResolver(), shipEntitiy);
            if (!mapAsset.isEmpty()) {
                classicAssets.addAll(mapAsset.get("assets"));
                virtualTourAssets.addAll(mapAsset.get("assetsVirtualTour"));
            }
        }
    }

    private Stream<CruiseModelLight> allSameShipCruises(CruiseModel cruiseModel) {
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
        return ofNullable(cruisesCacheService)
                .map(cache -> cache.getCruises(lang))
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(cruise -> of(cruiseModel).map(CruiseModel::getShip)
                        .map(ShipModel::getName)
                        .map(name -> name.equals(cruise.getShip().getName()))
                        .orElse(false));
    }

    private Optional<CruiseModelLight> retrievePreviousCruise(CruiseModel cruiseModel) {
        return allSameShipCruises(cruiseModel)
                .filter(cruise -> cruise.getStartDate().before(cruiseModel.getStartDate()))
                .max(Comparator.comparing(CruiseModelLight::getStartDate));
    }

    private Optional<CruiseModelLight> retrieveNextCruise(CruiseModel cruiseModel) {
        return allSameShipCruises(cruiseModel)
                .filter(cruise -> cruise.getStartDate().after(cruiseModel.getStartDate()))
                .min(Comparator.comparing(CruiseModelLight::getStartDate));
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

    public List<SilverseaAsset> getAssetsGallery() {
        return assetsGallery;
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

    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    public boolean isWaitlist() {
        return waitlist;
    }

    public String getComputedPriceFormatted() {

        return computedPriceFormatted;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public String getCcptCode() {
        return ccptCode;
    }
}