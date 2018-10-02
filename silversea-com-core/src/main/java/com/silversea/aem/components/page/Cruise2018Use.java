package com.silversea.aem.components.page;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.base.Strings;
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
import java.util.function.BiFunction;
import java.util.function.Function;
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

    private boolean showCruiseBeforeName;

    private List<SuitePrice> prices;
    private Collection<CruisePrePost> prePosts;

    private PriceModel lowestPrice;
    private boolean waitlist;
    private String computedPriceFormatted;
    private String currentPath;

    private String ccptCode;
    private CruiseModel cruiseModel;

    private List<CruiseItinerary> itinerary;

    private long numPorts;
    private long numCountries;

    private List<SilverseaAsset> assetsGallery;
    private List<SilverseaAsset> portsGallery;
    private String departurePortName;
    private String arrivalPortName;
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
    private String selector;

    //caching
    private List<CruiseModelLight> allSameShipCruises;
    private ItineraryLandProgramModel midlandShorexLightbox;

    @Override
    public void activate() throws Exception {
        super.activate();
        allSameShipCruises = null;
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        Locale locale = getCurrentPage().getLanguage(false);
        Lightbox typeLightbox = checkIsLightbox(selectors);
        selector = typeLightbox.getSelector();
        switch (typeLightbox) {
            case ASSET_GALLERY:
                assetsGallery = retrieveAssetsGallery();
                return;
            case HIGHLIGHTS:
                highlights = retrieveHighlights();
                return;
            case ASSET_MAP:
                Optional.ofNullable(getCurrentPage().getProperties()).ifPresent(vmProperties -> {
                    bigItineraryMap = vmProperties.get("bigItineraryMap", String.class);
                    bigThumbnailItineraryMap = vmProperties.get("bigThumbnailItineraryMap", String.class);
                    smallItineraryMap = vmProperties.get("smallItineraryMap", String.class);
                });
                return;
            case LAND_PROGRAM:
                landProgramLightbox = retrieveLandProgramModel(selectors);
                return;
            case ITINERARY_SHOREX_EXCURSION:
                itineraryShorexExcursionLightbox = retrieveItineraryShorexExcursion(selectors);
                return;
            case MIDLAND:
                midlandShorexLightbox = retrieveMidlandLightbox(selectors);
                return;
            case SHOREX_EXCURSION:
                shorexExcursionLightbox = retrieveShorexExcursion(selectors);
                return;
            case HOTEL:
                hotelLightbox = retrieveHotelModel(selectors);
                return;
            case CRUISE_PAGE:
                break;
        }

        assetsGallery = retrieveAssetsGallery();
        cruiseModel = retrieveCruiseModel();
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);
        exclusiveOffersCruiseFareAdditions = retrieveExclusiveOffersCruiseFareAdditions(exclusiveOffers);
        venetianSociety = retrieveVenetianSociety(cruiseModel);
        totalNumberOfOffers = exclusiveOffers.size() + (isVenetianSociety() ? 1 : 0);
        shipAssetGallery = retrieveShipAssetsGallery(cruiseModel);

        itinerary = retrieveItinerary(cruiseModel);
        portsGallery = retrievePortsGallery(cruiseModel);

        showCruiseBeforeName = retrieveShowCruiseBeforeName(locale);

        currentPath = retrieveCurrentPath();
        ccptCode = retrieveCcptCode(selectors);

        numPorts = retrieveNumberOfPorts(cruiseModel);
        numCountries = retrieveNumberOfCountries(cruiseModel);

        prePosts = retrievePrePosts(itinerary);
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

    private List<SilverseaAsset> retrievePortsGallery(CruiseModel cruiseModel) {
        Map<Integer, LinkedList<String>> portsAssets = retrievePortsAssets(cruiseModel.getItineraries());
        return cruiseModel.getItineraries().stream()
                .flatMap(port -> portsAssets.get(port.getPortId()).stream().map(path -> {
                    SilverseaAsset sscAsset = new SilverseaAsset();
                    sscAsset.setPath(path);
                    sscAsset.setName(port.getPort().getTitle());
                    return sscAsset;
                })).distinct().collect(toList());
    }

    private Collection<CruisePrePost> retrievePrePosts(List<CruiseItinerary> itinerary) {
        Set<CruisePrePost> uniqueValues = new HashSet<>();
        for (CruiseItinerary cruiseItinerary : itinerary) {
            uniqueValues.addAll(cruiseItinerary.getPrePosts());
        }
        return uniqueValues.stream().sorted(Comparator.comparing(CruisePrePost::getPrePost).reversed())
                .collect(toList());
    }


    private boolean retrieveShowCruiseBeforeName(Locale locale) {
        switch (locale.getLanguage().toLowerCase()) {
            case "en":
            case "de":
                return false;
            default:
                return true;
        }
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
        Map<Integer, LinkedList<String>> portAssets = retrievePortsAssets(itineraries);
        Set<Calendar> days = new HashSet<>();
        for (int counter = 0; counter < size; counter++) {
            ItineraryModel itinerary = itineraries.get(counter);
            days.add(itinerary.getDate());
            Integer portId = itinerary.getPortId();
            boolean isNextDaySamePort =
                    counter != size - 1 && itineraries.get(counter + 1).getPortId().equals(portId);
            result.add(
                    new CruiseItinerary(days.size(), counter == 0, counter == size - 1,
                            ofNullable(portAssets.get(portId).poll()).orElse(itinerary.getPort().getThumbnail()),
                            isNextDaySamePort, itinerary));
        }
        result.sort(comparing(CruiseItinerary::getDate));
        return result;
    }

    private Map<Integer, LinkedList<String>> retrievePortsAssets(List<ItineraryModel> itineraries) {
        return itineraries.stream().map(ItineraryModel::getPort).distinct()
                .collect(Collectors.toMap(PortModel::getCityId, this::portAssets, (l1, l2) -> l1));
    }

    private long retrieveNumberOfPorts(CruiseModel cruiseModel) {
        Set<PortModel> uniqueValues = new HashSet<>();
        for (ItineraryModel itineraryModel : cruiseModel.getItineraries()) {
            PortModel port = itineraryModel.getPort();
            if (!(port.getCountry() == null || "day-at-sea".equals(port.getName()))) {
                uniqueValues.add(port);
            }
        }
        return uniqueValues.size();
    }

    private long retrieveNumberOfCountries(CruiseModel cruiseModel) {
        Set<String> uniqueValues = new HashSet<>();
        for (ItineraryModel itineraryModel : cruiseModel.getItineraries()) {
            PortModel port = itineraryModel.getPort();
            if (!Strings.isNullOrEmpty(port.getCountryIso3())) {
                String countryIso3 = port.getCountryIso3();
                uniqueValues.add(countryIso3);
            }
        }
        return uniqueValues.size();
    }

    private String retrieveHighlights() {
        return ofNullable(getCurrentPage()).map(Page::getProperties)
                .map(props -> props.get("voyageHighlights", String.class)).orElse("");
    }

    private <T> T retrieveFromList(String[] selectors, Function<ItineraryModel, List<T>> listFactory,
                                   BiFunction<Long, T, Boolean> test) {
        if (selectors != null && selectors.length > 4) {
            Long itineraryID = Long.valueOf(selectors[3]);
            Long objectId = Long.valueOf(selectors[4]);
            ItineraryModel itineraryModel = retrieveItineraryModel(itineraryID);
            if (itineraryModel != null) {
                for (T element : listFactory.apply(itineraryModel)) {
                    if (test.apply(objectId, element)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    private ItineraryHotelModel retrieveHotelModel(String[] selectors) {
        return retrieveFromList(selectors, ItineraryModel::getHotels, (id, hotel) -> hotel.getHotelId().equals(id));
    }

    private ExcursionModel retrieveShorexExcursion(String[] selectors) {
        return retrieveFromList(selectors, CruiseItinerary::retrieveExcursions,
                (id, shorex) -> shorex.getShorexId().equals(id));

    }

    private ItineraryExcursionModel retrieveItineraryShorexExcursion(String[] selectors) {
        return retrieveFromList(selectors, ItineraryModel::getExcursions,
                (id, shorex) -> shorex.getShorexId().equals(id));
    }


    private ItineraryLandProgramModel retrieveLandProgramModel(String[] selectors) {
        return retrieveFromList(selectors, ItineraryModel::getLandPrograms,
                (id, landProgram) -> landProgram.getLandId().equals(id));
    }

    private ItineraryLandProgramModel retrieveMidlandLightbox(String[] selectors) {
        return retrieveFromList(selectors, ItineraryModel::getLandPrograms,
                (id, landProgram) -> landProgram.getLandId().equals(id));
    }

    private ItineraryModel retrieveItineraryModel(Long id) {
        if (id != null) {
            Resource itinerariesResource = getResource().hasChildren() ? getResource().getChild("itineraries") : null;
            if (itinerariesResource != null && itinerariesResource.hasChildren()) {
                for (Resource it : itinerariesResource.getChildren()) {
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
        boolean seen = false;
        PriceModel best = null;
        Comparator<PriceModel> comparator = comparing(PriceModel::getComputedPrice);
        for (SuitePrice price : prices) {
            if (!price.isWaitList()) {
                PriceModel suitePriceLowestPrice = price.getLowestPrice();
                if (!seen || comparator.compare(suitePriceLowestPrice, best) < 0) {
                    seen = true;
                    best = suitePriceLowestPrice;
                }
            }
        }
        return seen ? best : null;
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
        if (pageManager == null) {
            return null;
        }
        ValueMap vmProperties = currentPage.getProperties();
        if (vmProperties == null) {
            return null;
        }
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
        List<SilverseaAsset> portsAssetsList = retrieveAssetsFromPort();
        assetsListResult.addAll(portsAssetsList);
        if (ship != null) {
            assetsListResult.addAll(retrieveAssetsFromShip(ship));
        }
        String map = firstNonNull(vmProperties.get("bigItineraryMap", String.class),
                vmProperties.get("bigThumbnailItineraryMap", String.class),
                vmProperties.get("smallItineraryMap", String.class));
        String type = null;
        if (map == null) {
            map = vmProperties.get("itinerary", String.class);
            type = "itinerary";
        }
        if (map != null) {
            assetsListResult.add(0, AssetUtils.buildSilverseaAsset(map, getResourceResolver(), null, type));
        }

        return assetsListResult.stream().distinct().collect(toList());
    }


    private List<SilverseaAsset> retrieveAssetsFromPort() {
        Resource itinerariesResource = getResource().hasChildren() ? getResource().getChild("itineraries") : null;
        List<SilverseaAsset> portsAssetsList = new ArrayList<>();
        if (itinerariesResource != null && itinerariesResource.hasChildren()) {
            Iterator<Resource> children = itinerariesResource.getChildren().iterator();
            ItineraryModel itineraryModel;
            while (children.hasNext()) {
                Resource it = children.next();
                itineraryModel = it.adaptTo(ItineraryModel.class);
                if (itineraryModel != null && itineraryModel.getPort() != null) {
                    PortModel portModel = itineraryModel.getPort();
                    if (StringUtils.isEmpty(this.departurePortName)) {
                        this.departurePortName = portModel.getApiTitle();
                    }
                    this.arrivalPortName = portModel.getApiTitle();
                    String assetSelectionReference = portModel.getAssetSelectionReference();
                    if (StringUtils.isNotBlank(assetSelectionReference)) {
                        List<SilverseaAsset> portAssets = AssetUtils
                                .buildSilverseaAssetList(assetSelectionReference, getResourceResolver(),
                                        portModel.getTitle());
                        if (portAssets != null && !portAssets.isEmpty()) {
                            portsAssetsList.addAll(portAssets);
                        }
                    }
                }
            }
        }
        return portsAssetsList;
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

    private List<CruiseModelLight> allSameShipCruises(CruiseModel cruiseModel) {
        if (allSameShipCruises != null) {
            return allSameShipCruises;
        }

        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
        allSameShipCruises = ofNullable(cruisesCacheService)
                .map(cache -> cache.getCruises(lang))
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(cruise -> cruise.getStartDate() != null)
                .filter(cruise -> of(cruiseModel).map(CruiseModel::getShip)
                        .map(ShipModel::getName)
                        .map(name -> StringUtils.equals(name, cruise.getShip().getName()))
                        .orElse(false)).collect(toList());
        return allSameShipCruises;
    }

    private Optional<CruiseModelLight> retrievePreviousCruise(CruiseModel cruiseModel) {
        boolean seen = false;
        CruiseModelLight best = null;
        Comparator<CruiseModelLight> comparator = comparing(CruiseModelLight::getStartDate);
        for (CruiseModelLight cruise : allSameShipCruises(cruiseModel)) {
            if (cruise.getStartDate().before(cruiseModel.getStartDate())) {
                if (!seen || comparator.compare(cruise, best) > 0) {
                    seen = true;
                    best = cruise;
                }
            }
        }
        return Optional.ofNullable(best);
    }

    private Optional<CruiseModelLight> retrieveNextCruise(CruiseModel cruiseModel) {
        boolean seen = false;
        CruiseModelLight best = null;
        Comparator<CruiseModelLight> comparator = comparing(CruiseModelLight::getStartDate);
        for (CruiseModelLight cruise : allSameShipCruises(cruiseModel)) {
            if (cruise.getStartDate().after(cruiseModel.getStartDate())) {
                if (!seen || comparator.compare(cruise, best) < 0) {
                    seen = true;
                    best = cruise;
                }
            }
        }
        return Optional.ofNullable(best);
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

    public List<SilverseaAsset> getPortsGallery() {
        return portsGallery;
    }

    public ItineraryLandProgramModel getLandProgramLightbox() {
        return landProgramLightbox;
    }

    public String getSelector() {
        return selector;
    }

    public ItineraryExcursionModel getItineraryShorexExcursionLightbox() {
        return itineraryShorexExcursionLightbox;
    }

    public String getBigThumbnailItineraryMap() {
        return bigThumbnailItineraryMap;
    }

    public long getNumCountries() {
        return numCountries;
    }

    public long getNumPorts() {
        return numPorts;
    }

    public boolean getAreOffersOdd() {
        return this.totalNumberOfOffers % 2 != 0;
    }

    public String getDeparturePortName() {
        return departurePortName;
    }

    public String getArrivalPortName() {
        return arrivalPortName;
    }

    public enum Lightbox {
        ASSET_GALLERY("lg-gallery-assets"), ASSET_MAP("lg-map"), LAND_PROGRAM("lg-land"),
        ITINERARY_SHOREX_EXCURSION("lg-itShorex"), SHOREX_EXCURSION("lg-shorex"), HOTEL("lg-hotel"),
        HIGHLIGHTS("lg-highlights"), MIDLAND("lg-midShorex"),

        CRUISE_PAGE("");

        private String selector;

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

    public Collection<CruisePrePost> getPrePost() {
        return prePosts;
    }

    public ExcursionModel getShorexExcursionLightbox() {
        return shorexExcursionLightbox;
    }


    public boolean isShowCruiseBeforeName() {
        return showCruiseBeforeName;
    }

    public ItineraryLandProgramModel getMidlandShorexLightbox() {
        return midlandShorexLightbox;
    }
}

