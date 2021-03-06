package com.silversea.aem.components.page;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Strings;
import com.silversea.aem.components.beans.*;
import com.silversea.aem.components.included.combo.AssetGalleryCruiseUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.CruiseCodeHelper;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.CruiseUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Strings.emptyToNull;
import static com.silversea.aem.utils.AssetUtils.buildAssetList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;


public class Cruise2018Use extends EoHelper {

    private static final EoConfigurationBean EO_CONFIG = new EoConfigurationBean();
    private final static String LAST_MINUTE_SAVINGS_ID = "70";

    static {
        EO_CONFIG.setTitleVoyage(true);
        EO_CONFIG.setShortDescriptionVoyage(true);
        EO_CONFIG.setDescriptionMain(true);
        EO_CONFIG.setFootnoteVoyage(true);
        EO_CONFIG.setMapOverheadVoyage(true);
        EO_CONFIG.setCruiseFareVoyage(true);
        EO_CONFIG.setPriorityWeight(true);
        EO_CONFIG.setPostPrice(true);
        EO_CONFIG.setIconVoyage(true);
    }

    private List<ExclusiveOfferItem> exclusiveOffers;
    private List<String> exclusiveOffersCruiseFareAdditions;


    private String exclusiveOfferPostPrice;
    private boolean venetianSociety;
    private String VSLBPath;
    private boolean lastMinuteSavings;

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
    private String taCode;
    private CruiseModel cruiseModel;

    private List<CruiseItinerary> itinerary;

    private long numPorts;
    private long numCountries;

    private long dayUntilDeparture;

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

    private List<String> cruiseFareAdditions;

    //caching
    private ItineraryLandProgramModel midlandShorexLightbox;

    private int hasexcursionsCounter;
    private int numExcursions;

    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        Resource itinerariesResource = getResource().hasChildren() ? getResource().getChild("itineraries") : null;
        Locale locale = getCurrentPage().getLanguage(false);
        Lightbox typeLightbox = checkIsLightbox(selectors);
        selector = typeLightbox.getSelector();
        switch (typeLightbox) {
            case ASSET_GALLERY:
                assetsGallery = AssetGalleryCruiseUse.retrieveAssetsGallery(itinerariesResource, getResourceResolver(), getCurrentPage(), false);
                arrivalPortName = AssetGalleryCruiseUse.retrieveArrivalPortName(itinerariesResource);
                departurePortName = StringUtils.isEmpty(departurePortName) ? arrivalPortName : departurePortName;
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

        assetsGallery = AssetGalleryCruiseUse.retrieveAssetsGallery(itinerariesResource, getResourceResolver(), getCurrentPage(), false);
        arrivalPortName = AssetGalleryCruiseUse.retrieveArrivalPortName(itinerariesResource);
        departurePortName = StringUtils.isEmpty(departurePortName) ? arrivalPortName : departurePortName;
        cruiseModel = retrieveCruiseModel();
        exclusiveOffers = retrieveExclusiveOffers(cruiseModel);

        exclusiveOffersCruiseFareAdditions = retrieveExclusiveOffersCruiseFareAdditions(exclusiveOffers);
        exclusiveOfferPostPrice = retrieveExclusiveOfferPostPriceAndFindLMS(exclusiveOffers);
        venetianSociety = retrieveVenetianSociety(cruiseModel);
        VSLBPath = retrieveVenetianSocietyLBPath();
        totalNumberOfOffers = exclusiveOffers.size() + (isVenetianSociety() ? 1 : 0);
        shipAssetGallery = retrieveShipAssetsGallery(cruiseModel, getResourceResolver());

        cruiseFareAdditions = retrieveCruiseFareAdditions(cruiseModel);

        itinerary = retrieveItinerary(cruiseModel, getResourceResolver());
        portsGallery = retrievePortsGalleryAndVideo(cruiseModel);

        showCruiseBeforeName = retrieveShowCruiseBeforeName(locale);

        currentPath = retrieveCurrentPath();
        ccptCode = retrieveCcptCode(selectors);
        taCode = retrieveTaCode(selectors);

        numPorts = retrieveNumberOfPorts(cruiseModel);
        numCountries = retrieveNumberOfCountries(cruiseModel);

        dayUntilDeparture = ChronoUnit.DAYS.between(Instant.now(), cruiseModel.getStartDate().toInstant());
        prePosts = retrievePrePosts(itinerary);
        prices = retrievePrices(getCurrentPage(), geomarket, currency, cruiseModel.getPrices());
        lowestPrice = retrieveLowestPrice(prices);
        if (lowestPrice != null) {
            waitlist = false;
            computedPriceFormatted = PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
        } else {
            waitlist = true;
            computedPriceFormatted = null;//not shown
        }
        isFeetSquare = "US".equals(countryCode);

        CruiseModelLight[] prevNext =
                retrievePrevNext(cruiseModel, comparing(CruiseModelLight::getStartDate).thenComparing(CruiseModelLight::getDuration).thenComparing(CruiseModelLight::getCruiseCode));

        ofNullable(prevNext[0]).ifPresent(previous -> {
            this.previous = previous.getPath();
            this.previousArrival = previous.getArrivalPortName();
            this.previousDeparture = previous.getDeparturePortName();
        });
        ofNullable(prevNext[1]).ifPresent(next -> {
            this.next = next.getPath();
            this.nextArrival = next.getArrivalPortName();
            this.nextDeparture = next.getDeparturePortName();
        });
        this.hasexcursionsCounter = firstExcursionsCounter();
    }

    private List<String> retrieveCruiseFareAdditions(CruiseModel cruiseModel) {
        List<String> cruiseFare = cruiseModel.getCruiseFareAdditions();
        List<String> voyageFare = cruiseModel.getVoyageCruiseFareAdditions();
        List<String> destinationFare = retrieveDestinationFareAdditions(cruiseModel);
        List<String> result = new ArrayList<String>();
        boolean isDestinationFareEmpty = destinationFare == null || destinationFare.size() == 0;
        boolean isVoyageFareEmpty = voyageFare == null || voyageFare.size() == 0;
        boolean isOldCruiseFareEmpty = cruiseFare == null || cruiseFare.size() == 0;
        boolean isNewFareToShow = !isDestinationFareEmpty || !isVoyageFareEmpty;
        if (isNewFareToShow) {
            if (!isVoyageFareEmpty) {
                result.addAll(voyageFare);
            }
            if (!isDestinationFareEmpty) {
                result.addAll(destinationFare);
            }
        } else {
            if (!isOldCruiseFareEmpty) {
                result.addAll(cruiseFare);
            }
        }
        return result;
    }

    private List<String> retrieveDestinationFareAdditions(CruiseModel cruiseModel) {
        boolean isExpedition = cruiseModel.getCruiseType().equalsIgnoreCase("silversea-expedition");
        if (isExpedition) {
            return cruiseModel.getDestination().getDestinationFareAdditionsExpedition();
        } else {
            return cruiseModel.getDestination().getDestinationFareAdditionsClassic();
        }
    }

    private String retrieveExclusiveOfferPostPriceAndFindLMS(List<ExclusiveOfferItem> exclusiveOffers) {
        Integer priorityWeight = Integer.MIN_VALUE;
        String postPrice = null;
        for (ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
            boolean isTheRightPostPrice = StringUtils.isNotEmpty(exclusiveOffer.getPostPrice()) && exclusiveOffer.getPriorityWeight() > priorityWeight;
            if (isTheRightPostPrice) {
                priorityWeight = exclusiveOffer.getPriorityWeight();
                postPrice = exclusiveOffer.getPostPrice();
            }
            if (exclusiveOffer.getId().equals(LAST_MINUTE_SAVINGS_ID)) {
                setLastMinuteSavings(true);
            }
        }
        return postPrice;
    }


    private List<SilverseaAsset> retrievePortsGalleryAndVideo(CruiseModel cruiseModel) {
        String assetSelectionReference;
        ValueMap vmProperties = getCurrentPage().getProperties();
        Map<Integer, LinkedList<String>> portsAssets = retrievePortsAssets(cruiseModel.getItineraries(), false, getResourceResolver());
        List<SilverseaAsset> PortGalleryAndVideo = cruiseModel.getItineraries().stream().filter(port -> portsAssets.containsKey(port.getPortId()))
                .flatMap(port -> portsAssets.get(port.getPortId()).stream().map(path -> AssetUtils.buildSilverseaAsset(path, getResourceResolver(), "", ""))).distinct().collect(toList());
        assetSelectionReference = vmProperties.get("assetSelectionReference", String.class);
        List<SilverseaAsset> assetsListResult = new ArrayList<>();
        if (StringUtils.isNotBlank(assetSelectionReference)) {
            assetsListResult.addAll(AssetUtils
                    .buildSilverseaAssetListVideoOnly(assetSelectionReference, getResourceResolver(),
                            null));
            PortGalleryAndVideo.addAll(0, assetsListResult);
        }
        return PortGalleryAndVideo;
    }

    public int getTotalFareAddition() {
        int total = 0;
        if (exclusiveOffersCruiseFareAdditions != null) {
            total += exclusiveOffersCruiseFareAdditions.size();
        }
        if (cruiseFareAdditions != null) {
            total += cruiseFareAdditions.size();
        }
        return total;
    }

    public static Collection<CruisePrePost> retrievePrePosts(List<CruiseItinerary> itinerary) {
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


    private static LinkedList<String> portAssets(PortModel portModel, ResourceResolver resourceResolver) {
        String assetSelectionReference = portModel.getAssetSelectionReference();
        LinkedList<String> assets =
                ofNullable(emptyToNull(assetSelectionReference)).map(reference -> buildAssetList(reference,
                        resourceResolver)).map(list -> list.stream().map(Asset::getPath)).orElseGet(Stream::empty)
                        .distinct()
                        .collect(toCollection(LinkedList::new));
        if (assets.isEmpty()) {
            assets.add(portModel.getThumbnail());

        }
        return assets;
    }


    public static List<CruiseItinerary> retrieveItinerary(CruiseModel cruiseModel, ResourceResolver resourceResolver) {
        final InheritanceValueMap propertiesInherited =
                new HierarchyNodeInheritanceValueMap(cruiseModel.getPage().getAbsoluteParent(2).getContentResource());
        String thumbnailInherited = propertiesInherited.getInherited("image/fileReference", String.class);
        List<CruiseItinerary> result = new ArrayList<>();
        List<ItineraryModel> itineraries = cruiseModel.getItineraries();
        int size = itineraries.size();
        Map<Integer, LinkedList<String>> portAssets = retrievePortsAssets(itineraries, true, resourceResolver);
        Set<Calendar> days = new HashSet<>();
        for (int counter = 0; counter < size; counter++) {
            ItineraryModel itinerary = itineraries.get(counter);
            days.add(itinerary.getDate());
            Integer portId = itinerary.getPortId();
            result.add(
                    new CruiseItinerary(days.size(), counter == 0, counter == size - 1,
                            ofNullable(portAssets.get(portId).poll())
                                    .orElse(ofNullable(itinerary.getPort().getThumbnail()).orElse(thumbnailInherited)),
                            itinerary.isOvernight(), itinerary, cruiseModel.getCruiseType(), cruiseModel.getPath(), resourceResolver));
        }
        result.sort(comparing(CruiseItinerary::getDate));
        return result;
    }

    private static Map<Integer, LinkedList<String>> retrievePortsAssets(List<ItineraryModel> itineraries,
                                                                        boolean withDayAtSea, ResourceResolver resourceResolver) {
        return itineraries.stream().map(ItineraryModel::getPort).distinct()
                .filter(port -> withDayAtSea || isNotDayAtSea(port))
                .collect(Collectors.toMap(PortModel::getCityId, (portModel) -> portAssets(portModel, resourceResolver), (l1, l2) -> l1));
    }

    private static boolean isNotDayAtSea(PortModel port) {
        return !"day-at-sea".equals(port.getName());
    }

    private long retrieveNumberOfPorts(CruiseModel cruiseModel) {
        Set<PortModel> uniqueValues = new HashSet<>();
        for (ItineraryModel itineraryModel : cruiseModel.getItineraries()) {
            PortModel port = itineraryModel.getPort();
            if (isNotDayAtSea(port)) {
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

    public static List<SilverseaAsset> retrieveShipAssetsGallery(CruiseModel cruiseModel, ResourceResolver resourceResolver) {
        if (cruiseModel != null && cruiseModel.getShip() != null) {
            String assetSelectionReference = cruiseModel.getShip().getAssetGallerySelectionReference();
            if (StringUtils.isNotBlank(assetSelectionReference)) {
                return AssetUtils.buildSilverseaAssetList(assetSelectionReference, resourceResolver, null);
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

    private String retrieveTaCode(String[] selectors) {
        for (String selectorInfo : selectors) {
            if (selectorInfo.contains("ta_")) {
                return selectorInfo.replace("ta_", ".");
            }
        }
        return "";
    }

    private String retrieveCurrentPath() {
        return getSlingScriptHelper().getService(Externalizer.class)
                .publishLink(getResourceResolver(), getCurrentPage().getPath());
    }

    public static PriceModel retrieveLowestPrice(List<SuitePrice> prices) {
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

    private String retrieveVenetianSocietyLBPath() {
        String pathLB = "/content/silversea-com/" + LanguageHelper.getLanguage(getCurrentPage()) + "/VSLB";
        if (getResourceResolver().getResource(pathLB) == null) {
            return null;
        }
        return pathLB;
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

    public static List<SuitePrice> retrievePrices(Page page, String geomarket, String currency, List<PriceModel> prices) {
        Locale locale = page.getLanguage(false);
        List<SuitePrice> suites = new ArrayList<>();
        Set<PriceModel> uniqueValues = new HashSet<>();
        for (PriceModel price : prices) {
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
        Function<ExclusiveOfferModel, ExclusiveOfferItem> exclusiveOfferModelExclusiveOfferItemFunction = exclusiveOfferModel -> {
            EO_CONFIG.setActiveSystem(exclusiveOfferModel.getActiveSystem());
            EoBean result = super.parseExclusiveOffer(EO_CONFIG, exclusiveOfferModel);
            String destinationPath = cruise.getDestination().getPath();
            return new ExclusiveOfferItem(exclusiveOfferModel, countryCode, destinationPath, result);
        };

        return cruise.getExclusiveOffers().stream()
                .filter(eo -> eo.getGeomarkets() != null && eo.getGeomarkets().contains(geomarket))
                .map(exclusiveOfferModelExclusiveOfferItemFunction)
                .sorted(comparing((ExclusiveOfferItem eo) -> CruiseUtils.firstNonNull(eo.getPriorityWeight(), 0)).reversed())
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

    private CruiseModelLight[] retrievePrevNext(CruiseModel cruiseModel, Comparator<CruiseModelLight> comparator) {
        final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        CruiseModelLight[] cruises = new CruiseModelLight[]{null, null};

        if (cruisesCacheService == null) {
            return cruises;
        }
        CruiseModelLight givenCruise = cruisesCacheService.getCruiseByCruiseCode(lang, cruiseModel.getCruiseCode());
        if (givenCruise == null || givenCruise.getShip() == null) {
            return cruises;
        }
        String shipName = givenCruise.getShip().getName();
        for (CruiseModelLight cruise : cruisesCacheService.getCruises(lang)) {
            if (cruise.getStartDate() != null && StringUtils.equals(shipName, cruise.getShip().getName())) {
                double compared = comparator.compare(cruise, givenCruise);
                if (compared < 0 && (cruises[0] == null || comparator.compare(cruise, cruises[0]) > 0)) {
                    cruises[0] = cruise;
                }
                if (compared > 0 && (cruises[1] == null || comparator.compare(cruise, cruises[1]) < 0)) {
                    cruises[1] = cruise;
                }
            }
        }
        return cruises;
    }

    private int firstExcursionsCounter() {
        int counter = -1;
        if (this.itinerary.size() > 0) {
            for (CruiseItinerary cruiseItinerary : this.itinerary) {
                counter++;
                if (cruiseItinerary.isHasExcursions()) {
                    this.numExcursions++;
                    return counter;
                }
            }
        }

        return counter;
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

    public String getExclusiveOfferPostPrice() {
        return exclusiveOfferPostPrice;
    }

    public void setExclusiveOfferPostPrice(String exclusiveOfferPostPrice) {
        this.exclusiveOfferPostPrice = exclusiveOfferPostPrice;
    }

    public boolean getNumExcursions() {
        return numExcursions > 0;
    }

    public boolean isLastMinuteSavings() {
        return lastMinuteSavings;
    }

    public void setLastMinuteSavings(boolean lastMinuteSavings) {
        this.lastMinuteSavings = lastMinuteSavings;
    }

    public List<String> getCruiseFareAdditions() {
        return cruiseFareAdditions;
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

    public String getVSLBPath() {
        return VSLBPath;
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

    public String getTaCode() {
        return taCode;
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

    public long getDayUntilDeparture() {
        return dayUntilDeparture;
    }

    public Integer getHasexcursionsCounter() {
        return hasexcursionsCounter;
    }

    public String cruiseCodeUrlIdentifier() { return CruiseCodeHelper.urlIdentifierFor(cruiseModel.getCruiseCode()); }
}

