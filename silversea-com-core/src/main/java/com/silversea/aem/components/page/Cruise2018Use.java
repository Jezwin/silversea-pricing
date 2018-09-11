package com.silversea.aem.components.page;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.*;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.emptyToNull;
import static com.silversea.aem.utils.AssetUtils.buildAssetList;
import static java.util.Comparator.comparing;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
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

    private boolean isFeetSquare;
    private int totalNumberOfOffers;

    private List<SuitePrice> prices;

    private PriceModel lowestPrice;
    private boolean waitlist;
    private String computedPriceFormatted;
    private String currentPath;

    private String ccptCode;
    private CruiseModel cruiseModel;

    private List<CruiseItinerary> itinerary;

    private int numPorts;
    private int numCountries;

    private List<SilverseaAsset> assetsGallery;
    private List<SilverseaAsset> shipAssetGallery;
    private String bigItineraryMap;
    private String bigThumbnailItineraryMap;
    private String smallItineraryMap;

    private String previous;

    private String previousDeparture;
    private String previousArrival;
    private String next;
    private String nextDeparture;
    private String nextArrival;
    private String highlights;

    private ItineraryExcursionModel itineraryShorexExcursionLightbox;
    private ExcursionModel shorexExcursionLightbox;
    private ItineraryHotelModel hotelLightbox;
    private ItineraryLandProgramModel landProgramLightbox;
    private String typeLandShorexHotelLB;

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
            case HIGHLIGHTS:
                highlights = retrieveHighlights();
                return;
            case ASSET_MAP:
                List<String> newItineraryMap = retrieveItineraryMaps();
                if (newItineraryMap != null && !newItineraryMap.isEmpty()) {
                    bigItineraryMap = newItineraryMap.get(0);
                    bigThumbnailItineraryMap = newItineraryMap.get(1);
                    smallItineraryMap = newItineraryMap.get(2);
                }
                return;
            case LAND_SHOREX_HOTEL:
                if (selectors.length > 4) {
                    typeLandShorexHotelLB = selectors[2];
                    if (Lightbox.LAND_PROGRAM.toString().equals(typeLandShorexHotelLB)) {
                        landProgramLightbox = retrieveLandProgramModel(selectors);
                    } else if (Lightbox.SHOREX_EXCURSION.toString().equals(typeLandShorexHotelLB)) {
                        shorexExcursionLightbox = retrieveShorexExcursion(selectors);
                    } else if (Lightbox.ITINERARY_SHOREX_EXCURSION.toString().equals(typeLandShorexHotelLB)) {
                        itineraryShorexExcursionLightbox = retrieveItineraryShorexExcursion(selectors);
                    } else if (Lightbox.HOTEL.toString().equals(typeLandShorexHotelLB)) {
                        hotelLightbox = retrieveHotelModel(selectors);
                    }
                }
                return;
            case CRUISE_PAGE:
                break;
        }

        cruiseModel = retrieveCruiseModel();
        exclusiveOffers =  retrieveExclusiveOffers(cruiseModel);
        exclusiveOffersCruiseFareAdditions = retrieveExclusiveOffersCruiseFareAdditions(exclusiveOffers);
        venetianSociety = retrieveVenetianSociety(cruiseModel);
        totalNumberOfOffers = exclusiveOffers.size() + (isVenetianSociety() ? 1 : 0);
        shipAssetGallery = retrieveShipAssetsGallery(cruiseModel);

        itinerary = retrieveItinerary(cruiseModel);

        currentPath = retrieveCurrentPath();
        ccptCode = retrieveCcptCode(selectors);

        int[] numPortsCountries = retrieveNumberPortsAndCountries(cruiseModel);
        numPorts = numPortsCountries[0];
        numCountries = numPortsCountries[1];

        prices = retrievePrices(cruiseModel);
        lowestPrice = retrieveLowestPrice(prices);
        if (lowestPrice != null) {
            waitlist = false;
            computedPriceFormatted = PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
        } else {
            waitlist = true;
            computedPriceFormatted = null;//not shown
        }
        isFeetSquare = "US".equals(countryCode);
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

    public List<CruiseItinerary> getItinerary() {
        return itinerary;
    }


    private LinkedList<String> portAssets(PortModel portModel) {
        String assetSelectionReference = portModel.getAssetSelectionReference();
        Stream<String> assets =
                ofNullable(emptyToNull(assetSelectionReference)).map(reference -> buildAssetList(reference,
                        getResourceResolver())).map(list -> list.stream().map(Asset::getPath)).orElseGet(Stream::empty);
        return concat(Stream.of(portModel.getThumbnail()), assets).distinct().collect(toCollection(LinkedList::new));
    }

    private List<CruiseItinerary> retrieveItinerary(CruiseModel cruiseModel) {
        List<CruiseItinerary> result = new ArrayList<>();
        List<ItineraryModel> itineraries = cruiseModel.getItineraries();
        int size = itineraries.size();
        Map<Integer, LinkedList<String>> portAssets = itineraries.stream().map(ItineraryModel::getPort).distinct()
                .collect(Collectors.toMap(PortModel::getCityId, this::portAssets, (l1, l2) -> l1));
        for (int day = 0; day < size; day++) {
            ItineraryModel itinerary = itineraries.get(day);
            Integer portId = itinerary.getPortId();
            boolean isNextDaySamePort =
                    day != size - 1 && itineraries.get(day + 1).getPortId().equals(portId);
            result.add(
                    new CruiseItinerary(day + 1, day == 0, day == size - 1,
                            ofNullable(portAssets.get(portId).poll()).orElse(itinerary.getPort().getThumbnail()),
                            isNextDaySamePort, itinerary));
        }
        return result;
    }

    private int[] retrieveNumberPortsAndCountries(CruiseModel cruiseModel) {
        int[] numCountriesAndPorts = new int[2];
        if (cruiseModel != null && cruiseModel.getItineraries() != null) {
            List<Integer> ports = new ArrayList<>();
            List<String> countries = new ArrayList<>();
            for (ItineraryModel itineraryModel : cruiseModel.getItineraries()) {
                if (!countries.contains(itineraryModel.getPort().getCountryIso3())) {
                    countries.add(itineraryModel.getPort().getCountryIso3());
                }
                if (!ports.contains(itineraryModel.getItineraryId())) {
                    ports.add(itineraryModel.getItineraryId());
                }
            }
            numCountriesAndPorts[0] = !ports.isEmpty() ? ports.size() : null;
            numCountriesAndPorts[1] = !countries.isEmpty() ? countries.size() : null;
        }
        return numCountriesAndPorts;
    }


    private String retrieveHighlights() {
        return ofNullable(getCurrentPage()).map(Page::getProperties)
                .map(props -> props.get("voyageHighlights", String.class)).orElse("");
    }


    private ItineraryHotelModel retrieveHotelModel(String[] selectors) {
        if (selectors != null && selectors.length > 4) {
            Long itineraryID = Long.valueOf(selectors[3]);
            Long hotelID = Long.valueOf(selectors[4]);

            ItineraryModel itineraryModel = retrieveItineraryModel(itineraryID);
            if (itineraryModel != null) {
                List<ItineraryHotelModel> hotels = itineraryModel.getHotels();
                for (ItineraryHotelModel hotel : hotels) {
                    if (hotel.getHotelId().equals(hotelID)) {
                        return hotel;
                    }
                }
            }
        }
        return null;
    }

    private ExcursionModel retrieveShorexExcursion(String[] selectors) {
        if (selectors != null && selectors.length > 4) {
            Long itineraryID = Long.valueOf(selectors[3]);
            Long shorexID = Long.valueOf(selectors[4]);

            ItineraryModel itineraryModel = retrieveItineraryModel(itineraryID);
            if (itineraryModel != null) {
                List<ExcursionModel> shorexExcursions = itineraryModel.getPort().getExcursions();
                for (ExcursionModel shorex : shorexExcursions) {
                    if (shorex.getShorexId().equals(shorexID)) {
                        return shorex;
                    }
                }
            }
        }
        return null;
    }

    private ItineraryExcursionModel retrieveItineraryShorexExcursion(String[] selectors) {
        if (selectors != null && selectors.length > 4) {
            Long itineraryID = Long.valueOf(selectors[3]);
            Long shorexID = Long.valueOf(selectors[4]);

            ItineraryModel itineraryModel = retrieveItineraryModel(itineraryID);
            if (itineraryModel != null) {
                List<ItineraryExcursionModel> itShorexExcursions = itineraryModel.getExcursions();
                for (ItineraryExcursionModel shorex : itShorexExcursions) {
                    if (shorex.getShorexId().equals(shorexID)) {
                        return shorex;
                    }
                }
            }
        }
        return null;
    }

    private ItineraryLandProgramModel retrieveLandProgramModel(String[] selectors) {
        if (selectors != null && selectors.length > 4) {
            Long itineraryID = Long.valueOf(selectors[3]);
            Long landID = Long.valueOf(selectors[4]);
            ItineraryModel itineraryModel = retrieveItineraryModel(itineraryID);
            if (itineraryModel != null) {
                List<ItineraryLandProgramModel> landPrograms = itineraryModel.getLandPrograms();
                for (ItineraryLandProgramModel landProgram : landPrograms) {
                    if (landProgram.getLandId().equals(landID)) {
                        return landProgram;
                    }
                }
            }
        }
        return null;
    }

    private ItineraryModel retrieveItineraryModel(Long id) {
        if (id != null) {
            Resource itinerariesResource = getResource().hasChildren() ? getResource().getChild("itineraries") : null;
            if (itinerariesResource.hasChildren()) {
                Iterator<Resource> children = itinerariesResource.getChildren().iterator();
                ItineraryModel itineraryModel = null;
                while (children.hasNext()) {
                    Resource it = children.next();
                    ValueMap itMap = it.getValueMap();
                    Long itineraryID = itMap.get("itineraryId", Long.class);
                    if (itineraryID != null && itineraryID.equals(id)) {
                        return it.adaptTo(ItineraryModel.class);
                    }
                }
            }
        }
        return null;
    }

    private List<SilverseaAsset> retrieveShipAssetsGallery(CruiseModel cruiseModel) {
        if (cruiseModel != null && cruiseModel.getShip() != null) {
            String assetSelectionReference = cruiseModel.getShip().getAssetGallerySelectionReference();
            if (StringUtils.isNotBlank(assetSelectionReference)) {
                return AssetUtils.buildSilverseaAssetList(assetSelectionReference, getResourceResolver(), null);
            }
        }
        return null;
    }

    private Lightbox checkIsLightbox(String[] selectors) {
        for (String selector : selectors) {
            for (Lightbox lightbox : Lightbox.values()) {
                if (selector.equalsIgnoreCase(lightbox.getSelector())) {
                    return lightbox;
                }
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
            if (WcmConstants.FEATURE_CODE_VENETIAN_SOCIETY.equals(featureCode)) {
                return true;
            }
        }
        return false;
    }

    private List<String> retrieveExclusiveOffersCruiseFareAdditions(List<ExclusiveOfferItem> offers) {
        List<String> list = new ArrayList<>();
        for (ExclusiveOfferItem offer : offers) {
            if (offer.getCruiseFareAdditions() != null) {
                list.addAll(offer.getCruiseFareAdditions());
            }
        }
        return list;
    }

    private List<SuitePrice> retrievePrices(CruiseModel cruise) {
        Locale locale = getCurrentPage().getLanguage(false);
        List<SuitePrice> suites = new ArrayList<>();
        Set<PriceModel> uniqueValues = new HashSet<>();
        for (PriceModel price : cruise.getPrices()) {
            if (geomarket.equals(price.getGeomarket())) {
                if (currency.equals(price.getCurrency())) {
                    if (uniqueValues.add(price)) {
                        boolean newSuite = true;
                        for (SuitePrice suite : suites) {
                            if (suite.getSuite().equals(price.getSuite())) {
                                newSuite = false;
                                break;
                            }
                        }
                        if (newSuite) {
                            SuitePrice suitePrice =
                                    new SuitePrice(price.getSuite(), price, locale, price.getSuiteCategory());
                            suites.add(suitePrice);
                        } else {
                            suites.stream().filter(t -> t.getSuite().equals(price.getSuite())).findFirst()
                                    .ifPresent(suite -> suite.add(price));
                        }
                    }
                }
            }
        }
        return suites;
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
        String assetSelectionReference;
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
                String bigThumbnailItineraryMap = vmProperties.get("bigThumbnailItineraryMap", String.class);
                String smallItineraryMap = vmProperties.get("smallItineraryMap", String.class);
                assetsListResult.add(bigItineraryMap);
                assetsListResult.add(bigThumbnailItineraryMap);
                assetsListResult.add(smallItineraryMap);
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


    public ItineraryHotelModel getHotelLightbox() {
        return hotelLightbox;
    }

    public ItineraryLandProgramModel getLandProgramLightbox() {
        return landProgramLightbox;
    }

    public String getTypeLandShorexHotelLB() {
        return typeLandShorexHotelLB;
    }

    public ItineraryExcursionModel getItineraryShorexExcursionLightbox() {
        return itineraryShorexExcursionLightbox;
    }

    public String getBigThumbnailItineraryMap() {
        return bigThumbnailItineraryMap;
    }

    public int getNumCountries() {
        return numCountries;
    }

    public int getNumPorts() {
        return numPorts;
    }

    private enum Lightbox {
        ASSET_GALLERY("lg-gallery-assets"), ASSET_MAP("lg-map"), LAND_PROGRAM("lg-land"),
        ITINERARY_SHOREX_EXCURSION("lg-itShorex"), SHOREX_EXCURSION("lg-shorex"), HOTEL("lg-hotel"),
        LAND_SHOREX_HOTEL
                ("lg-land-shorex-hotel"), HIGHLIGHTS("highlights"),

        CRUISE_PAGE("");

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

    public String getHighlights() {
        return highlights;
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

    public int getTotalNumberOfOffers() {
        return totalNumberOfOffers;
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

    public List<CruisePrePost> getPrePost() {
        return getItinerary().stream().flatMap(itinerary -> itinerary.getPrePosts().stream()).distinct()
                .collect(toList());
    }

    public ExcursionModel getShorexExcursionLightbox() {
        return shorexExcursionLightbox;
    }

}

