package com.silversea.aem.components.editorial;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.DestinationItem;
import com.silversea.aem.models.DestinationModelLight;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.FeatureModelLight;
import com.silversea.aem.models.PortItem;
import com.silversea.aem.models.PortModelLight;
import com.silversea.aem.models.ShipItem;
import com.silversea.aem.models.ShipModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.FindYourCruiseUtils;
import com.silversea.aem.utils.PathUtils;
import com.silversea.aem.ws.client.factory.WorldAndGrandVoyageCache;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;

/**
 * selectors : destination_all date_all duration_all ship_all cruisetype_all
 * port_all page_2
 */
public class FindYourCruiseUse extends AbstractGeolocationAwareUse {

    private static final int MAX_RESULT_PER_PAGE = 20;

    private static final String FILTER_ALL = "all";

    private Set<FeatureModelLight> featuresFromDesign = new HashSet<>();

    private String type = "v2";

    // list of all the cruises available for the lang
    private List<CruiseModelLight> allCruises = new ArrayList<>();

    // list of the cruises filtered of the current page
    private List<CruiseItem> cruises = new ArrayList<>();

    // destinations available for all the cruises
    private List<DestinationModelLight> destinations = new ArrayList<>();

    // destinations available for the subset of filtered cruises
    private Set<DestinationItem> availableDestinations = new TreeSet<>(Comparator.comparing(DestinationItem::getName));

    // ships available for all the cruises
    private List<ShipModelLight> ships = new ArrayList<>();

    // ports available for the subset of filtered cruises
    private Set<ShipItem> availableShips = new TreeSet<>(Comparator.comparing(ShipItem::getName));

    // ports available for all the cruises
    private List<PortModelLight> ports = new ArrayList<>();

    // ports available for the subset of filtered cruises
    private Set<PortItem> availablePorts = new TreeSet<>(Comparator.comparing(PortItem::getName));

    // departure dates available for the cruises
    private Set<YearMonth> dates = new TreeSet<>();

    // departure dates available for the subset of filtered cruises
    private Set<YearMonth> availableDepartureDates = new HashSet<>();

    // features available from design dialog + available for cruises
    private Set<FeatureModelLight> features = new TreeSet<>(Comparator.comparing(FeatureModelLight::getName));

    // features dates available for the subset of filtered cruises
    private Set<FeatureModelLight> availableFeatures = new TreeSet<>(Comparator.comparing(FeatureModelLight::getName));

    // durations available for the subset of filtered cruises
    private Set<String> availableDurations = new HashSet<>();

    // cruise types available for the subset of filtered cruises
    private Set<String> availableCruiseTypes = new HashSet<>();

    private int resultPerPage;

    // destination filter
    private String destinationFilter = FILTER_ALL;

    private String destinationIdFilter;

    // exclusive offer filter (not displayed, used on exclusive offer page)
    private String exclusiveOfferFilter = FILTER_ALL;

    // date filter
    private YearMonth dateFilter;

    // duration filter
    private String durationFilter;

    // minimum duration of the duration filter
    private Integer durationFilterMin;

    // maximum duration of the duration filter
    private Integer durationFilterMax;

    // ship filter
    private String shipFilter = FILTER_ALL;

    // cruise type filter
    private String cruiseTypeFilter = FILTER_ALL;

    // port filter
    private String portFilter = FILTER_ALL;

    // features filter
    private Set<FeatureModelLight> featuresFilter = new TreeSet<>(Comparator.comparing(FeatureModelLight::getTitle));

    // true if find your cruise is prefiltered by destination
    private boolean prefilterByDestination;

    // true if find your cruise is prefiltered by port
    private boolean prefilterByPort;

    // true if find your cruise is prefiltered by ship
    private boolean prefilterByShip;

    // true if find your cruise is prefiltered by feature
    private boolean prefilterByFeature;

    // current page in the component pagination
    private int activePage = 1;

    // total number of pages
    private int pageNumber;

    // true if the current page in pagination is the first page
    private boolean isFirstPage;

    // true if the current page in pagination is the last page
    private boolean isLastPage;

    // array containing the pages for the pagination
    private List<Integer> pagination;

    // total number of cruises for the current filters
    private int totalMatches;

    private String comboCruiseCodeInResults;

    private Map<String, String> mapCruiseCodeWcAnGV;

    private StringBuilder availableDestinationsJson;
    private StringBuilder availableShipsJson;
    private StringBuilder availablePortsJson;
    private StringBuilder availableDepartureDatesJson;
    private StringBuilder availableDurationsJson;
    private StringBuilder availableCruiseTypesJson;
    private StringBuilder availableFeaturesJson;
    private StringBuilder worldAndGrandVoyageCruiseJson;
    private List<Integer> paginationV2;

    @Override
    @SuppressWarnings("unchecked")
    public void activate() throws Exception {
        super.activate();
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        availableDestinationsJson = new StringBuilder();
        availableShipsJson = new StringBuilder();
        availablePortsJson = new StringBuilder();
        availableDepartureDatesJson = new StringBuilder();
        availableDurationsJson = new StringBuilder();
        availableCruiseTypesJson = new StringBuilder();
        availableFeaturesJson = new StringBuilder();
        worldAndGrandVoyageCruiseJson = new StringBuilder();

        availableDestinationsJson.append("{\"all\":true,\"gv\":true,\"wc\":true,");
        availableShipsJson.append("{\"all\":true,");
        availablePortsJson.append("{\"all\":true,");
        availableDepartureDatesJson.append("{\"all\":true,");
        availableDurationsJson.append("{\"all\":true,");
        availableCruiseTypesJson.append("{\"all\":true,");
        availableFeaturesJson.append("{");
        worldAndGrandVoyageCruiseJson.append("{");

        // Get type from configuration
        final Conf confRes = getResource().adaptTo(Conf.class);
        if (confRes != null) {
            final ValueMap fycConf = confRes.getItem("/findyourcruise/findyourcruise");

            if (fycConf != null) {
                type = fycConf.get("type", String.class);
            }
        }

        // Get tags to display
        if (tagManager != null) {
            final String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);

            if (tags != null) {
                for (String tagId : tags) {
                    final Tag tag = tagManager.resolve(tagId);

                    if (tag != null) {
                        final FeatureModel featureModel = tag.adaptTo(FeatureModel.class);

                        if (featureModel != null) {
                            featuresFromDesign.add(new FeatureModelLight(featureModel));
                        }
                    }
                }
            }
        }

        // Parse selectors
        final String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        for (final String selector : selectors) {
            final String[] splitSelector = selector.split("_");

            if (splitSelector.length == 2) {
                switch (splitSelector[0]) {
                    case "psize":
                        resultPerPage = Integer.parseInt(splitSelector[1]);
                        break;
                    case "destination":
                        destinationFilter = splitSelector[1];
                        break;
                    case "date":
                        try {
                            dateFilter = YearMonth.parse(splitSelector[1]);
                        } catch (DateTimeParseException ignored) {
                        }
                        break;
                    case "duration":
                        durationFilter = splitSelector[1];
                        final String[] splitDuration = durationFilter.split("-");

                        if (splitDuration.length == 2) {
                            try {
                                durationFilterMin = Integer.parseInt(splitDuration[0]);
                                durationFilterMax = Integer.parseInt(splitDuration[1]);
                            } catch (NumberFormatException ignored) {
                            }
                        } else {
                            try {
                                durationFilterMin = Integer.parseInt(durationFilter);
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        break;
                    case "ship":
                        shipFilter = splitSelector[1];
                        break;
                    case "cruisetype":
                        cruiseTypeFilter = splitSelector[1];
                        break;
                    case "port":
                        portFilter = splitSelector[1];
                        break;
                    case "features":
                        final String featuresFilter = splitSelector[1];
                        final String[] splitFeatures = featuresFilter.split("\\|");

                        if (splitFeatures.length > 0 && tagManager != null) {
                            for (final String splitFeature : splitFeatures) {
                                final Tag featureTag = tagManager
                                        .resolve(WcmConstants.TAG_NAMESPACE_FEATURES + splitFeature);

                                if (featureTag != null) {
                                    final FeatureModel feature = featureTag.adaptTo(FeatureModel.class);

                                    if (feature != null) {
                                        this.featuresFilter.add(new FeatureModelLight(feature));
                                    }
                                }
                            }
                        }

                        break;
                    case "page":
                        try {
                            activePage = Integer.parseInt(splitSelector[1]);
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                }
            }
        }
        if (resultPerPage == 0) {
            resultPerPage = Optional.ofNullable(get("psize", String.class)).map(Integer::parseInt).orElse(15);
        }
        if (resultPerPage > MAX_RESULT_PER_PAGE) {
            resultPerPage = MAX_RESULT_PER_PAGE;
        }

        // Apply default filtering for specific pages types
        final String currentPageResourceType = getCurrentPage().getContentResource().getResourceType();
        switch (currentPageResourceType) {
            case WcmConstants.RT_DESTINATION:
                destinationFilter = getCurrentPage().getName();
                prefilterByDestination = true;
                break;
            case WcmConstants.RT_PORT:
                portFilter = getCurrentPage().getName();
                prefilterByPort = true;
                break;
            case WcmConstants.RT_SHIP:
                shipFilter = getCurrentPage().getName();
                prefilterByShip = true;
                break;
            case WcmConstants.RT_EXCLUSIVE_OFFER:
                exclusiveOfferFilter = getCurrentPage().getPath();
                break;
            case WcmConstants.RT_FEATURE:
                final Tag[] pageTags = getCurrentPage().getTags();

                if (pageTags != null) {
                    for (final Tag pageTag : pageTags) {
                        if (pageTag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_FEATURES)) {
                            final FeatureModel featureModel = pageTag.adaptTo(FeatureModel.class);

                            if (featureModel != null) {
                                featuresFilter.add(new FeatureModelLight(featureModel));
                            }
                        }
                    }
                }

                prefilterByFeature = true;
                break;
        }

        // get cruises from cache
        final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (cruisesCacheService != null) {
            allCruises = cruisesCacheService.getCruises(lang);
            destinations = cruisesCacheService.getDestinations(lang);
            ships = cruisesCacheService.getShips(lang);
            ports = cruisesCacheService.getPorts(lang);
            dates.addAll(cruisesCacheService.getDepartureDates(lang));

            features.addAll(featuresFromDesign);
            features.retainAll(cruisesCacheService.getFeatures(lang));
        }

        // Security adaptation : If voyage is in the past - do not display them
        List<CruiseModelLight> newAllCruises = new ArrayList<>();
        for (CruiseModelLight cruise : allCruises) {
            if (cruise.getStartDate().after(Calendar.getInstance())) {
                newAllCruises.add(cruise);
                if ((destinationFilter.equalsIgnoreCase("wc") || destinationFilter.equalsIgnoreCase(("gv")))
                        && shipFilter.equals(FILTER_ALL) && dateFilter == null) {
                    availableDestinations.add(cruise.getDestination());
                }
            }
        }
        allCruises = newAllCruises;

        allCruises = checkWorldCruiseGrandVoyages("wc", allCruises);
        allCruises = checkWorldCruiseGrandVoyages("gv", allCruises);

        // init list of filtered cruises and available values for filters
        final List<CruiseModelLight> filteredCruises = new ArrayList<>();
        for (final CruiseModelLight cruise : allCruises) {
            boolean includeCruise = true;
            boolean includeCruiseNotFilteredByDestination = true;
            boolean includeCruiseNotFilteredByShip = true;
            boolean includeCruiseNotFilteredByPort = true;
            boolean includeCruiseNotFilteredByDepartureDate = true;
            boolean includeCruiseNotFilteredByDuration = true;
            boolean includeCruiseNotFilteredByFeatures = true;
            boolean includeCruiseNotFilteredByCruiseTypes = true;

            if (!cruise.isVisible() && !destinationFilter.equalsIgnoreCase("wc")
                    && !destinationFilter.equalsIgnoreCase(("gv"))) {
                includeCruise = false;
                includeCruiseNotFilteredByDestination = false;
                includeCruiseNotFilteredByShip = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDepartureDate = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByFeatures = false;
                includeCruiseNotFilteredByCruiseTypes = false;
            }

            if (destinationIdFilter == null && !destinationFilter.equals(FILTER_ALL)
                    && destinationFilter.equals(cruise.getDestination().getName())
                    && !destinationFilter.equalsIgnoreCase("wc") && !destinationFilter.equalsIgnoreCase(("gv"))) {
                destinationIdFilter = cruise.getDestinationId();
            }

            if (!destinationFilter.equals(FILTER_ALL) && !cruise.getDestination().getName().equals(destinationFilter)
                    && !destinationFilter.equalsIgnoreCase("wc") && !destinationFilter.equalsIgnoreCase(("gv"))) {
                includeCruise = false;
                includeCruiseNotFilteredByShip = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDepartureDate = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByFeatures = false;
                includeCruiseNotFilteredByCruiseTypes = false;
            }

            if (!shipFilter.equals(FILTER_ALL) && !cruise.getShip().getName().equals(shipFilter)) {
                includeCruise = false;
                includeCruiseNotFilteredByDestination = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDepartureDate = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByFeatures = false;
                includeCruiseNotFilteredByCruiseTypes = false;
            }

            if ((destinationFilter.equalsIgnoreCase("wc") || destinationFilter.equalsIgnoreCase(("gv")))
                    && shipFilter.equals(FILTER_ALL) && dateFilter == null) {
                includeCruiseNotFilteredByDestination = false;
            }

            try {
                final int cruiseDuration = Integer.parseInt(cruise.getDuration());

                // TODO merge if
                if (durationFilterMin != null && durationFilterMax != null
                        && (cruiseDuration < durationFilterMin || cruiseDuration > durationFilterMax)) {
                    includeCruise = false;
                    includeCruiseNotFilteredByDestination = false;
                    includeCruiseNotFilteredByShip = false;
                    includeCruiseNotFilteredByPort = false;
                    includeCruiseNotFilteredByDepartureDate = false;
                    includeCruiseNotFilteredByFeatures = false;
                    includeCruiseNotFilteredByCruiseTypes = false;
                } else if (durationFilterMin != null && durationFilterMax == null
                        && cruiseDuration < durationFilterMin) {
                    includeCruise = false;
                    includeCruiseNotFilteredByDestination = false;
                    includeCruiseNotFilteredByShip = false;
                    includeCruiseNotFilteredByPort = false;
                    includeCruiseNotFilteredByDepartureDate = false;
                    includeCruiseNotFilteredByFeatures = false;
                    includeCruiseNotFilteredByCruiseTypes = false;
                }
            } catch (NumberFormatException ignored) {
            }

            if (!portFilter.equals(FILTER_ALL)) {
                boolean portInItinerary = false;
                for (final PortItem port : cruise.getPorts()) {
                    if (port.getName().equals(portFilter)) {
                        portInItinerary = true;
                        break;
                    }
                }

                includeCruise = includeCruise && portInItinerary;
                includeCruiseNotFilteredByDestination = includeCruiseNotFilteredByDestination && portInItinerary;
                includeCruiseNotFilteredByShip = includeCruiseNotFilteredByShip && portInItinerary;
                includeCruiseNotFilteredByDepartureDate = includeCruiseNotFilteredByDepartureDate && portInItinerary;
                includeCruiseNotFilteredByDuration = includeCruiseNotFilteredByDuration && portInItinerary;
                includeCruiseNotFilteredByFeatures = includeCruiseNotFilteredByFeatures && portInItinerary;
                includeCruiseNotFilteredByCruiseTypes = includeCruiseNotFilteredByCruiseTypes && portInItinerary;
            }

            if (!cruiseTypeFilter.equals(FILTER_ALL) && !cruise.getCruiseType().equals(cruiseTypeFilter)) {
                includeCruise = false;
                includeCruiseNotFilteredByDestination = false;
                includeCruiseNotFilteredByShip = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDepartureDate = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByFeatures = false;
            }

            final YearMonth cruiseStartDate = FindYourCruiseUtils.getYearMonthWithTimeZone(cruise.getStartDate());

            if (dateFilter != null && !cruiseStartDate.equals(dateFilter)) {
                includeCruise = false;
                includeCruiseNotFilteredByDestination = false;
                includeCruiseNotFilteredByShip = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByFeatures = false;
                includeCruiseNotFilteredByCruiseTypes = false;
            }

            if (this.features.size() > 0 && !cruise.getFeatures().containsAll(this.featuresFilter)) {
                includeCruise = false;
                includeCruiseNotFilteredByDestination = false;
                includeCruiseNotFilteredByShip = false;
                includeCruiseNotFilteredByPort = false;
                includeCruiseNotFilteredByDepartureDate = false;
                includeCruiseNotFilteredByDuration = false;
                includeCruiseNotFilteredByCruiseTypes = false;
            }

            if (!exclusiveOfferFilter.equals(FILTER_ALL)) {
                boolean exclusiveOfferInCruise = false;
                for (final ExclusiveOfferModel exclusiveOffer : cruise.getExclusiveOffers()) {
                    if (exclusiveOffer.getPath().equals(exclusiveOfferFilter)) {
                        exclusiveOfferInCruise = true;
                        break;
                    }
                }

                includeCruise = includeCruise && exclusiveOfferInCruise;
                includeCruiseNotFilteredByDestination = includeCruiseNotFilteredByDestination && exclusiveOfferInCruise;
                includeCruiseNotFilteredByShip = includeCruiseNotFilteredByShip && exclusiveOfferInCruise;
                includeCruiseNotFilteredByPort = includeCruiseNotFilteredByPort && exclusiveOfferInCruise;
                includeCruiseNotFilteredByDepartureDate = includeCruiseNotFilteredByDepartureDate
                        && exclusiveOfferInCruise;
                includeCruiseNotFilteredByDuration = includeCruiseNotFilteredByDuration && exclusiveOfferInCruise;
                includeCruiseNotFilteredByFeatures = includeCruiseNotFilteredByFeatures && exclusiveOfferInCruise;
                includeCruiseNotFilteredByCruiseTypes = includeCruiseNotFilteredByCruiseTypes && exclusiveOfferInCruise;
            }

            // include cruise in the filtered cruises list
            if (includeCruise) {
                filteredCruises.add(cruise);
                if (mapCruiseCodeWcAnGV != null && mapCruiseCodeWcAnGV.get(cruise.getCruiseCode()) != null) {
                    String comboCruiseCode = mapCruiseCodeWcAnGV.get(cruise.getCruiseCode());
                    if (this.comboCruiseCodeInResults == null) {
                        this.comboCruiseCodeInResults = comboCruiseCode;
                    } else if (!(this.comboCruiseCodeInResults.equalsIgnoreCase(comboCruiseCode))) {
                        this.comboCruiseCodeInResults = "";
                    }
                }
            }

            // add elements to the filter of filters lists
            if (includeCruiseNotFilteredByDestination) {
                availableDestinations.add(cruise.getDestination());
            }

            if (includeCruiseNotFilteredByShip) {
                availableShips.add(cruise.getShip());
            }

            if (includeCruiseNotFilteredByPort) {
                for (PortItem port : cruise.getPorts()) {
                    availablePorts.add(port);
                }
            }

            if (includeCruiseNotFilteredByDepartureDate) {
                availableDepartureDates.add(cruiseStartDate);
            }

            if (includeCruiseNotFilteredByDuration) {
                try {
                    final int cruiseDuration = Integer.parseInt(cruise.getDuration());
                    if (cruiseDuration < 9) {
                        availableDurations.add("1-8");
                    } else if (cruiseDuration > 9 && cruiseDuration < 13) {
                        availableDurations.add("9-12");
                    } else if (cruiseDuration > 12 && cruiseDuration < 19) {
                        availableDurations.add("13-18");
                    } else if (cruiseDuration >= 19) {
                        availableDurations.add("19");
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            if (includeCruiseNotFilteredByFeatures) {
                availableFeatures.addAll(cruise.getFeatures());
            }

            if (includeCruiseNotFilteredByCruiseTypes) {
                availableCruiseTypes.add(cruise.getCruiseType());
            }
        }

        filteredCruises.sort(Comparator.comparing(CruiseModelLight::getStartDate));

        // build the cruises list for the current page

        Locale locale = getCurrentPage().getLanguage(false);

        int i = 0;
        for (final CruiseModelLight cruise : filteredCruises) {
            if (i >= activePage * resultPerPage) {
                break;
            }

            if (i >= (activePage - 1) * resultPerPage) {
                cruises.add(new CruiseItem(cruise, geomarket, currency, locale));
            }

            i++;
        }

        for (DestinationItem dest : availableDestinations) {
            availableDestinationsJson.append("\"" + dest.getName() + "\":true,");
        }

        for (ShipItem ship : availableShips) {
            availableShipsJson.append("\"" + ship.getName() + "\":true,");
        }

        for (PortItem port : availablePorts) {
            availablePortsJson.append("\"" + port.getName() + "\":true,");
        }

        for (YearMonth depart : availableDepartureDates) {
            availableDepartureDatesJson.append("\"" + depart.toString() + "\":true,");
        }

        for (String duration : availableDurations) {
            availableDurationsJson.append("\"" + duration + "\":true,");
        }

        for (FeatureModelLight feat : availableFeatures) {
            availableFeaturesJson.append("\"" + feat.getName() + "\":true,");
        }

        for (String cruiseType : availableCruiseTypes) {
            availableCruiseTypesJson.append("\"" + cruiseType + "\":true,");
        }

        worldAndGrandVoyageCruiseJson.append("\"worldCruisePath\":\"" + getWorldCruisesPagePath() + "\",");
        worldAndGrandVoyageCruiseJson.append("\"grandVoyageCruisePath\":\"" + getGrandVoyagesPagePath() + "\"");

        availableDestinationsJson.deleteCharAt(availableDestinationsJson.length() - 1);
        availableShipsJson.deleteCharAt(availableShipsJson.length() - 1);
        availablePortsJson.deleteCharAt(availablePortsJson.length() - 1);
        availableDepartureDatesJson.deleteCharAt(availableDepartureDatesJson.length() - 1);
        availableDurationsJson.deleteCharAt(availableDurationsJson.length() - 1);
        availableCruiseTypesJson.deleteCharAt(availableCruiseTypesJson.length() - 1);
        availableFeaturesJson.deleteCharAt(availableFeaturesJson.length() - 1);
        availableDestinationsJson.append("}");
        availableShipsJson.append("}");
        availablePortsJson.append("}");
        availableDepartureDatesJson.append("}");
        availableDurationsJson.append("}");
        availableCruiseTypesJson.append("}");
        availableFeaturesJson.append("}");
        worldAndGrandVoyageCruiseJson.append("}");

        // Setting convenient booleans for building pagination
        totalMatches = filteredCruises.size();
        pageNumber = (int) Math.ceil((float) totalMatches / (float) resultPerPage);
        isFirstPage = activePage == 1;
        isLastPage = activePage == getPagesNumber();

        // Build pagination
        pagination = buildPagination();
        paginationV2 = buildPaginationV2();
    }

    private List<CruiseModelLight> checkWorldCruiseGrandVoyages(String filter, List<CruiseModelLight> allCruises) {
        if (destinationFilter.equalsIgnoreCase(filter)) {
            mapCruiseCodeWcAnGV = new TreeMap<>();
            Map<String, Map<String, CruiseModelLight>> mapWcAndGv = WorldAndGrandVoyageCache
                    .getInstance(getResourceResolver()).getCache();
            allCruises = new ArrayList<>();
            for (Entry<String, Map<String, CruiseModelLight>> entry : mapWcAndGv.entrySet()) {
                String key = entry.getKey();
                if (key.contains(filter.toUpperCase())) {
                    for (Entry<String, CruiseModelLight> entryCruise : entry.getValue().entrySet()) {
                        mapCruiseCodeWcAnGV.put(entryCruise.getValue().getCruiseCode(), key);
                        allCruises.add(entryCruise.getValue());
                    }
                }
            }
        }
        return allCruises;
    }

    private List<Integer> buildPaginationV2() {
        //negatives are "..."
        List<Integer> pagination = new ArrayList<>();
        for (int page = 1; page <= pageNumber; page++) {
            if (activePage - 1 <= page && page <= activePage + 2) {//middle
                pagination.add(page);
            } else if (page == 1 || page == activePage || page == pageNumber) { //fst current last
                pagination.add(page);
            } else if (page == 2 || page == pageNumber - 1) {//dots
                pagination.add(-1);
            }
        }
        return pagination;

    }

    /**
     * @return pagination according to the active page
     */
    private List<Integer> buildPagination() {
        pagination = new ArrayList<>();

        // Add active page
        pagination.add(activePage);

        // Add two previous pages
        pagination.add(0, activePage - 1 > 0 ? activePage - 1 : null);
        pagination.add(0, activePage - 2 > 0 ? activePage - 2 : null);

        // Add previous page and first
        pagination.add(0, isFirstPage() ? null : activePage - 1);
        pagination.add(0, isFirstPage() ? null : 1);

        // Add two next pages
        pagination.add(activePage + 1 > pageNumber ? null : activePage + 1);
        pagination.add(activePage + 2 > pageNumber ? null : activePage + 2);

        // Add next and last page
        pagination.add(isLastPage() ? null : activePage + 1);
        pagination.add(isLastPage() ? null : pageNumber);

        return pagination;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the list of cruises
     */
    public List<CruiseItem> getCruises() {
        return cruises;
    }

    /**
     * @return destinations available for all cruises for this lang
     */
    public List<DestinationModelLight> getDestinations() {
        return destinations;
    }

    /**
     * @return destinations available for filtered cruises for this lang
     */
    public Set<DestinationItem> getAvailableDestinations() {
        return availableDestinations;
    }

    /**
     * @return ships available for all cruises for this lang
     */
    public List<ShipModelLight> getShips() {
        return ships;
    }

    /**
     * @return ships available for filtered cruises for this lang
     */
    public Set<ShipItem> getAvailableShips() {
        return availableShips;
    }

    /**
     * @return ports available for all cruises for this lang
     */
    public List<PortModelLight> getPorts() {
        return ports;
    }

    /**
     * @return number of results per page.
     */
    public int getResultPerPage() {
        return resultPerPage;
    }

    /**
     * @return ports available for filtered cruises for this lang
     */
    public Set<PortItem> getAvailablePorts() {
        return availablePorts;
    }

    /**
     * @return dates available for all cruises for this lang
     */
    public Set<YearMonth> getDates() {
        return dates;
    }

    /**
     * @return dates available for filtered cruises for this lang
     */
    public Set<YearMonth> getAvailableDepartureDates() {
        return availableDepartureDates;
    }

    /**
     * @return intersection of features configured in design mode and feature of all
     * cruises for this lang
     */
    public Set<FeatureModelLight> getFeatures() {
        return features;
    }

    /**
     * @return features available for filtered cruises for this lang
     */
    public Set<FeatureModelLight> getAvailableFeatures() {
        return availableFeatures;
    }

    public Collection<FeatureModelLight> getInitialDisplayedFeatures() {
        return CollectionUtils.intersection(availableFeatures, features);
    }

    /**
     * @return durations available for filtered cruises for this lang
     */
    public Set<String> getAvailableDurations() {
        return availableDurations;
    }

    /**
     * @return cruise types available for filtered cruises for this lang
     */
    public Set<String> getAvailableCruiseTypes() {
        return availableCruiseTypes;
    }

    /**
     * @return true if the component is prefiltered by destination (e.g. present in
     * page with resource type destination)
     */
    public boolean isPrefilteredByDestination() {
        return prefilterByDestination;
    }

    /**
     * @return true if the component is prefiltered by port (e.g. present in page
     * with resource type port)
     */
    public boolean isPrefilteredByPort() {
        return prefilterByPort;
    }

    /**
     * @return true if the component is prefiltered by ship (e.g. present in page
     * with resource type ship)
     */
    public boolean isPrefilteredByShip() {
        return prefilterByShip;
    }

    /**
     * @return true if the component is prefiltered by feature (e.g. present in page
     * with resource type feature)
     */
    public boolean isPrefilteredByFeature() {
        return prefilterByFeature;
    }

    /**
     * @return the destination filter value of destination (filter or prefilter),
     * {@link #FILTER_ALL} is not filled
     */
    public String getDestinationFilter() {
        return destinationFilter;
    }

    /**
     * @return the destination ID filter value of destination (filter or prefilter),
     * {@link #FILTER_ALL} is not filled
     */
    public String getDestinationIdFilter() {
        return destinationIdFilter;
    }

    public String getExclusiveOfferFilter() {
        return exclusiveOfferFilter;
    }

    public YearMonth getDateFilter() {
        return dateFilter;
    }

    public String getDurationFilter() {
        return durationFilter;
    }

    public String getShipFilter() {
        return shipFilter;
    }

    public String getCruiseTypeFilter() {
        return cruiseTypeFilter;
    }

    public String getPortFilter() {
        return portFilter;
    }

    public Set<FeatureModelLight> getFeaturesFilter() {
        return featuresFilter;
    }

    /**
     * @return the number of pages
     */
    public int getPagesNumber() {
        return pageNumber;
    }

    /**
     * @return the index of the active page
     */
    public int getActivePage() {
        return activePage;
    }

    /**
     * @return true if the page is the first in the pagination
     */
    public boolean isFirstPage() {
        return isFirstPage;
    }

    /**
     * @return true if the page is the last in the pagination
     */
    public boolean isLastPage() {
        return isLastPage;
    }

    /**
     * @return the pagination
     */
    public List<Integer> getPagination() {
        return pagination;
    }


    public List<Integer> getPaginationV2() {
        return paginationV2;
    }

    /**
     * @return the totalMatches
     */
    public int getTotalMatches() {
        return totalMatches;
    }

    /**
     * @return the request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    public String getWorldCruisesPagePath() {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        String path = PathUtils.getWorldCruisesPagePath(getResource(), getCurrentPage());
        if (StringUtils.isNotEmpty(this.comboCruiseCodeInResults)
                && !(shipFilter.equals(FILTER_ALL) && dateFilter == null)) {
            path = PathUtils.getWorldCruisesPagePath(getResource(), getCurrentPage(), this.comboCruiseCodeInResults);
        }
        return externalizer.relativeLink(getRequest(), path);
    }

    public String getGrandVoyagesPagePath() {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        String path = PathUtils.getGrandVoyagesPagePath(getResource(), getCurrentPage());
        if (StringUtils.isNotEmpty(this.comboCruiseCodeInResults)
                && !(shipFilter.equals(FILTER_ALL) && dateFilter == null)) {
            path = PathUtils.getGrandVoyagesPagePath(getResource(), getCurrentPage(), this.comboCruiseCodeInResults);
        }
        return externalizer.relativeLink(getRequest(), path);
    }

    public String getAvailableDestinationsJson() {
        return availableDestinationsJson.toString();
    }

    public String getAvailableShipsJson() {
        return availableShipsJson.toString();
    }

    public String getAvailablePortsJson() {
        return availablePortsJson.toString();
    }

    public String getAvailableDepartureDatesJson() {
        return availableDepartureDatesJson.toString();
    }

    public String getAvailableDurationsJson() {
        return availableDurationsJson.toString();
    }

    public String getAvailableCruiseTypesJson() {
        return availableCruiseTypesJson.toString();
    }

    public String getAvailableFeaturesJson() {
        return availableFeaturesJson.toString();
    }

    public String getComboCruiseCodeInResults() {
        return comboCruiseCodeInResults;
    }

    public String getWorldAndGrandVoyageCruiseJson() {
        return worldAndGrandVoyageCruiseJson.toString();
    }
}
