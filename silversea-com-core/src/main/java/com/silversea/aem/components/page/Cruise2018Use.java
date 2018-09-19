package com.silversea.aem.components.page;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;
import java.util.stream.Collectors;
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

    private List<ExclusiveOfferItem> exclusiveOffers;
    private List<String> exclusiveOffersCruiseFareAdditions;
    private boolean venetianSociety;

    private boolean isFeetSquare = false;

    private List<SuitePrice> prices;
    private PriceModel lowestPrice;
    private boolean waitlist;
    private String computedPriceFormatted;

    private String currentPath;
    private String ccptCode;

    private CruiseModel cruiseModel;


    private List<SilverseaAsset> assetsGallery;
    private List<SilverseaAsset> shipAssetGallery;
    private String bigItineraryMap;
    private String smallItineraryMap;

    private String previous;
    private String previousDeparture;
    private String previousArrival;
    private String next;
    private String nextDeparture;
    private String nextArrival;


    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        Locale locale = getCurrentPage().getLanguage(false);
        Lightbox typeLightbox = checkIsLightbox(selectors);
        switch (typeLightbox) {
            case ASSET_GALLERY:
                assetsGallery = retrieveAssetsGallery();
                return;
            case ASSET_MAP:
                List<String> newItineraryMap = retrieveItineraryMaps();
                if (newItineraryMap != null && !newItineraryMap.isEmpty()) {
                    bigItineraryMap = newItineraryMap.get(0);
                    smallItineraryMap = newItineraryMap.get(1);
                }
                return;
            case CRUISE_PAGE:
                break;
        }

        cruiseModel = retrieveCruiseModel();
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);
        exclusiveOffersCruiseFareAdditions = retrieveExclusiveOffersCruiseFareAdditions(exclusiveOffers);
        venetianSociety = retrieveVenetianSociety(cruiseModel);
        shipAssetGallery = retrieveShipAssetsGallery(cruiseModel);

        currentPath = retrieveCurrentPath();
        ccptCode = retrieveCcptCode(selectors);

        prices = retrievePrices(cruiseModel);
        lowestPrice = retrieveLowestPrice(prices);
        waitlist = lowestPrice == null;
        computedPriceFormatted = PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
        if (countryCode.equals("US")) {
            isFeetSquare = true;
        }
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

    private List<SilverseaAsset> retrieveShipAssetsGallery(CruiseModel cruiseModel) {
        if (cruiseModel != null && cruiseModel.getShip() != null) {
            String assetSelectionReference = cruiseModel.getShip().getAssetGallerySelectionReference();
            if (StringUtils.isNotBlank(assetSelectionReference)) {
                return AssetUtils
                        .buildSilverseaAssetList(assetSelectionReference, getResourceResolver(),
                                null);
            }
        }
        return null;
    }

    private Lightbox checkIsLightbox(String[] selectors) {
        for (String selector : selectors) {
            if (selector.contains(Lightbox.ASSET_GALLERY.getSelector())) {
                return Lightbox.ASSET_GALLERY;
            }
            if (selector.contains(Lightbox.ASSET_MAP.getSelector())) {
                return Lightbox.ASSET_MAP;
            }
        }
        return Lightbox.CRUISE_PAGE;
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

    private boolean retrieveVenetianSociety(CruiseModel cruise) {
        for (FeatureModel featureModel : cruise.getFeatures()) {
            String featureCode = featureModel.getFeatureCode();
            if (featureCode != null) {
                if (WcmConstants.FEATURE_CODE_VENETIAN_SOCIETY.equals(featureCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> retrieveExclusiveOffersCruiseFareAdditions(List<ExclusiveOfferItem> offers) {
        List<String> list = new ArrayList<>();
        for (ExclusiveOfferItem offer : offers) {
            if (offer.getCruiseFareAdditions() != null) {
                List<String> cruiseFareAdditions = offer.getCruiseFareAdditions();
                for (String string : cruiseFareAdditions) {
                    list.add(string);
                }
            }
        }
        return list;
    }

    private List<SuitePrice> retrievePrices(CruiseModel cruise) {
        Locale locale = getCurrentPage().getLanguage(false);
        List<SuitePrice> list = new ArrayList<>();
        Set<PriceModel> uniqueValues = new HashSet<>();
        for (PriceModel price : cruise.getPrices()) {
            if (geomarket.equals(price.getGeomarket())) {
                if (currency.equals(price.getCurrency())) {
                    if (uniqueValues.add(price)) {
                        boolean b = true;
                        for (SuitePrice t1 : list) {
                            if (t1.getSuite().equals(price.getSuite())) {
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            SuitePrice suitePrice = new SuitePrice(price.getSuite(), price, locale, price.getSuiteCategory());
                            list.add(suitePrice);
                        } else {
                            list.stream().filter(t -> t.getSuite().equals(price.getSuite())).findFirst().get().add(price);
                        }
                    }
                }
            }
        }
        return list;
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

    private List<SilverseaAsset> retrieveAssetsGallery() {
        Page currentPage = getCurrentPage();
        PageManager pageManager = currentPage.getPageManager();
        ShipModel ship = null;
        String assetSelectionReference = null;
        if (pageManager != null) {
            ValueMap vmProperties = currentPage.getProperties();
            if (vmProperties != null) {
                String shipReference = vmProperties.get("shipReference", String.class);
                if (StringUtils.isNotEmpty(shipReference)) {
                    Page shipPage = pageManager.getPage(shipReference);
                    if (shipPage != null) {
                        ship = shipPage.adaptTo(ShipModel.class);
                    }
                }
                assetSelectionReference = vmProperties.get("assetSelectionReference", String.class);
                List<SilverseaAsset> assetsListResult = new ArrayList<>();
                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    assetsListResult.addAll(AssetUtils
                            .buildSilverseaAssetList(assetSelectionReference, getResourceResolver(),
                                    null));
                }
                if (ship != null) {
                    assetsListResult.addAll(retrieveAssetsFromShip(ship));
                }
                return assetsListResult;
            }
        }
        return null;
    }

    private List<String> retrieveItineraryMaps() {
        Page currentPage = getCurrentPage();
        PageManager pageManager = currentPage.getPageManager();
        if (pageManager != null) {
            ValueMap vmProperties = currentPage.getProperties();
            if (vmProperties != null) {
                List<String> assetsListResult = new ArrayList<>();
                String itineraryMap = vmProperties.get("itinerary", String.class);
                assetsListResult.add(itineraryMap);
                assetsListResult.add(itineraryMap);
                String bigItineraryMap = vmProperties.get("bigItineraryMap", String.class);
                String smallItineraryMap = vmProperties.get("smallItineraryMap", String.class);
                if (StringUtils.isNotEmpty(bigItineraryMap)) {
                    assetsListResult.add(0, bigItineraryMap);
                }
                if (StringUtils.isNotEmpty(smallItineraryMap)) {
                    assetsListResult.add(1, smallItineraryMap);
                }
                return assetsListResult;
            }
        }
        return null;
    }

    private List<SilverseaAsset> retrieveAssetsFromShip(ShipModel shipModel) {
        List<SilverseaAsset> listShipAssets = new ArrayList<>();
        if (shipModel != null) {
            List<SilverseaAsset> virtualTourAssets = new ArrayList<>();
            if (StringUtils.isNotEmpty(shipModel.getPhotoVideoSuiteSelectionReference())) {
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
                .filter(cruise -> cruise.getStartDate() != null)
                .filter(cruise -> of(cruiseModel).map(CruiseModel::getShip)
                        .map(ShipModel::getName)
                        .map(name -> StringUtils.equals(name, cruise.getShip().getName()))
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

    public String getBigItineraryMap() {
        return bigItineraryMap;
    }

    public String getSmallItineraryMap() {
        return smallItineraryMap;
    }

    public List<SilverseaAsset> getShipAssetGallery() {
        return shipAssetGallery;
    }

    private enum Lightbox {
        ASSET_GALLERY("lg-gallery-assets"), ASSET_MAP("lg-map"), CRUISE_PAGE("");

        private String selector = "";

        public String getSelector() {
            return selector;
        }

        Lightbox(String selector) {
            this.selector = selector;
        }

        public String toString() {
            return selector;
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

    public List<SilverseaAsset> getAssetsGallery() {
        return assetsGallery;
    }

    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    public boolean isVenetianSociety() {
        return venetianSociety;
    }

    public boolean isFeetSquare() {
        return isFeetSquare;
    }

    public List<String> getExclusiveOffersCruiseFareAdditions() {
        return exclusiveOffersCruiseFareAdditions;
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