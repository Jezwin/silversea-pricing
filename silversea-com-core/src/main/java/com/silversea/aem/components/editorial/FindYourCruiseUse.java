package com.silversea.aem.components.editorial;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;
import org.apache.sling.api.resource.ValueMap;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * selectors : destination_all date_all duration_all ship_all cruisetype_all port_all page_2
 */
public class FindYourCruiseUse extends AbstractGeolocationAwareUse {

    private final static int PAGE_SIZE = 15;

    private final static String FILTER_ALL = "all";

    private Set<FeatureModel> featuresFromDesign = new HashSet<>();

    private String type = "v2";

    // list of all the cruises available for the lang
    private List<CruiseModel> allCruises = new ArrayList<>();

    // list of the cruises filtered of the current page
    private List<CruiseItem> cruises = new ArrayList<>();

    // destinations available for all the cruises
    private List<DestinationModel> destinations = new ArrayList<>();

    // destinations available for the subset of filtered cruises
    private Set<DestinationModel> availableDestinations = new TreeSet<>(Comparator.comparing
            (DestinationModel::getName));

    // ships available for all the cruises
    private List<ShipModel> ships = new ArrayList<>();

    // ports available for the subset of filtered cruises
    private Set<ShipModel> availableShips = new TreeSet<>(Comparator.comparing(ShipModel::getName));

    // ports available for all the cruises
    private List<PortModel> ports = new ArrayList<>();

    // ports available for the subset of filtered cruises
    private Set<PortModel> availablePorts = new TreeSet<>(Comparator.comparing(PortModel::getName));

    // departure dates available for the cruises
    private Set<YearMonth> dates = new TreeSet<>();

    // departure dates available for the subset of filtered cruises
    private Set<YearMonth> availableDepartureDates = new HashSet<>();

    // features available from design dialog + available for cruises
    private Set<FeatureModel> features = new HashSet<>();

    // features dates available for the subset of filtered cruises
    private Set<FeatureModel> availableFeatures = new TreeSet<>(Comparator.comparing(FeatureModel::getName));

    // durations available for the subset of filtered cruises
    private Set<String> availableDurations = new HashSet<>();

    // cruise types available for the subset of filtered cruises
    private Set<String> availableCruiseTypes = new HashSet<>();

    // destination filter
    private String destinationFilter = FILTER_ALL;

    private Integer destinationIdFilter;

    // true if find your cruise is prefiltered by destination
    private boolean prefilterByDestination;

    // true if find your cruise is prefiltered by port
    private boolean prefilterByPort;

    // true if find your cruise is prefiltered by ship
    private boolean prefilterByShip;

    // true if find your cruise is prefiltered by feature
    private boolean prefilterByFeature;

    // exclusive offer filter (not displayed, used on exclusive offer page)
    private String exclusiveOfferFilter = FILTER_ALL;

    // date filter
    private YearMonth dateFilter;

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
    private Set<FeatureModel> featuresFilter = new TreeSet<>(Comparator.comparing(FeatureModel::getName));

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

    @Override
    @SuppressWarnings("unchecked")
    public void activate() throws Exception {
        super.activate();

        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final String lang = LanguageHelper.getLanguage(getCurrentPage());

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
                            featuresFromDesign.add(featureModel);
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
                        final String durationFilter = splitSelector[1];
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
                                final Tag featureTag = tagManager.resolve(
                                        WcmConstants.TAG_NAMESPACE_FEATURES + splitFeature);

                                if (featureTag != null) {
                                    final FeatureModel feature = featureTag.adaptTo(FeatureModel.class);

                                    if (feature != null) {
                                        this.featuresFilter.add(feature);
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
                                featuresFilter.add(featureModel);
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

        // init list of filtered cruises and available values for filters
        final Set<CruiseModel> filteredCruises = new TreeSet<>(Comparator.comparing(CruiseModel::getStartDate));
        for (final CruiseModel cruise : allCruises) {
            boolean includeCruise = true;
            boolean includeCruiseNotFilteredByDestination = true;
            boolean includeCruiseNotFilteredByShip = true;
            boolean includeCruiseNotFilteredByPort = true;
            boolean includeCruiseNotFilteredByDepartureDate = true;
            boolean includeCruiseNotFilteredByDuration = true;
            boolean includeCruiseNotFilteredByFeatures = true;
            boolean includeCruiseNotFilteredByCruiseTypes = true;

            if (!destinationFilter.equals(FILTER_ALL) && !cruise.getDestination().getName().equals(destinationFilter)) {
                if (destinationFilter == null) {
                    destinationIdFilter = cruise.getDestination().getDestinationId();
                }

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
                for (final ItineraryModel itinerary : cruise.getItineraries()) {
                    if (itinerary.getPort() != null && itinerary.getPort().getName().equals(portFilter)) {
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

            final YearMonth cruiseStartDate = YearMonth.of(cruise.getStartDate().get(Calendar.YEAR),
                    cruise.getStartDate().get(Calendar.MONTH) + 1);
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
                includeCruiseNotFilteredByDepartureDate = includeCruiseNotFilteredByDepartureDate && exclusiveOfferInCruise;
                includeCruiseNotFilteredByDuration = includeCruiseNotFilteredByDuration && exclusiveOfferInCruise;
                includeCruiseNotFilteredByFeatures = includeCruiseNotFilteredByFeatures && exclusiveOfferInCruise;
                includeCruiseNotFilteredByCruiseTypes = includeCruiseNotFilteredByCruiseTypes && exclusiveOfferInCruise;
            }

            // include cruise in the filtered cruises list
            if (includeCruise) {
                filteredCruises.add(cruise);
            }

            // add elements to the filter of filters lists
            if (includeCruiseNotFilteredByDestination) {
                availableDestinations.add(cruise.getDestination());
            }

            if (includeCruiseNotFilteredByShip) {
                availableShips.add(cruise.getShip());
            }

            if (includeCruiseNotFilteredByPort) {
                for (ItineraryModel itinerary : cruise.getItineraries()) {
                    if (itinerary.getPort() != null) {
                        availablePorts.add(itinerary.getPort());
                    }
                }
            }

            if (includeCruiseNotFilteredByDepartureDate) {
                availableDepartureDates.add(YearMonth.of(cruise.getStartDate().get(Calendar.YEAR),
                        cruise.getStartDate().get(Calendar.MONTH) + 1));
            }

            if (includeCruiseNotFilteredByDuration) {
                try {
                    final int cruiseDuration = Integer.parseInt(cruise.getDuration());
                    if (cruiseDuration < 9) {
                        availableDurations.add("1-8");
                    } else if (cruiseDuration > 8 && cruiseDuration < 13) {
                        availableDurations.add("9-12");
                    } else if (cruiseDuration > 13 && cruiseDuration < 19) {
                        availableDurations.add("13-19");
                    } else if (cruiseDuration > 19) {
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

        // build the cruises list for the current page
        int pageSize = PAGE_SIZE; // TODO replace by configuration

        Locale locale = getCurrentPage().getLanguage(false);

        int i = 0;
        for (final CruiseModel cruise : filteredCruises) {
            if (i >= (activePage - 1) * pageSize) {
                cruises.add(new CruiseItem(cruise, geomarket, currency, locale));
            }

            if (i > activePage * pageSize) {
                break;
            }

            i++;
        }

        // Setting convenient booleans for building pagination
        totalMatches = filteredCruises.size();
        pageNumber = (int) Math.ceil((float) totalMatches / (float) PAGE_SIZE);
        isFirstPage = activePage == 1;
        isLastPage = activePage == getPagesNumber();

        // Build pagination
        pagination = buildPagination();
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
    public List<DestinationModel> getDestinations() {
        return destinations;
    }

    /**
     * @return destinations available for filtered cruises for this lang
     */
    public Set<DestinationModel> getAvailableDestinations() {
        return availableDestinations;
    }

    /**
     * @return ships available for all cruises for this lang
     */
    public List<ShipModel> getShips() {
        return ships;
    }

    /**
     * @return ships available for filtered cruises for this lang
     */
    public Set<ShipModel> getAvailableShips() {
        return availableShips;
    }

    /**
     * @return ports available for all cruises for this lang
     */
    public List<PortModel> getPorts() {
        return ports;
    }

    /**
     * @return ports available for filtered cruises for this lang
     */
    public Set<PortModel> getAvailablePorts() {
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
     * @return intersection of features configured in design mode and feature of all cruises for this lang
     */
    public Set<FeatureModel> getFeatures() {
        return features;
    }

    /**
     * @return features available for filtered cruises for this lang
     */
    public Set<FeatureModel> getAvailableFeatures() {
        return availableFeatures;
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
     * @return true if the component is prefiltered by destination (e.g. present in page with resource type destination)
     */
    public boolean isPrefilteredByDestination() {
        return prefilterByDestination;
    }

    /**
     * @return true if the component is prefiltered by port (e.g. present in page with resource type port)
     */
    public boolean isPrefilteredByPort() {
        return prefilterByPort;
    }

    /**
     * @return true if the component is prefiltered by ship (e.g. present in page with resource type ship)
     */
    public boolean isPrefilteredByShip() {
        return prefilterByShip;
    }

    /**
     * @return true if the component is prefiltered by feature (e.g. present in page with resource type feature)
     */
    public boolean isPrefilteredByFeature() {
        return prefilterByFeature;
    }

    /**
     * @return the destination filter value of destination (filter or prefilter), {@link #FILTER_ALL} is not filled
     */
    public String getDestinationFilter() {
        return destinationFilter;
    }

    /**
     * @return the destination ID filter value of destination (filter or prefilter), {@link #FILTER_ALL} is not filled
     */
    public Integer getDestinationIdFilter() {
        return destinationIdFilter;
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
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }

    /**
     * Represent a cruise item used to display cruise informations (especially geolocated) in find your cruise
     */
    public class CruiseItem {

        private CruiseModel cruiseModel;

        private PriceModel lowestPrice;

        private boolean isWaitList = true;
        
        private Locale locale;

        private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();

        public CruiseItem(final CruiseModel cruiseModel, final String market, final String currency, final Locale locale) {
            this.cruiseModel = cruiseModel;

            // init lowest price and waitlist based on geolocation
            for (PriceModel priceModel : cruiseModel.getPrices()) {
                if (priceModel.getGeomarket() != null
                        && priceModel.getGeomarket().equals(market)
                        && priceModel.getCurrency().equals(currency)
                        && !priceModel.isWaitList()) {
                    // Init lowest price
                    if (lowestPrice == null) {
                        lowestPrice = priceModel;
                    } else if (priceModel.getComputedPrice() < lowestPrice.getComputedPrice()) {
                        lowestPrice = priceModel;
                    }

                    // Init wait list
                    isWaitList = false;
                }
            }

            // init exclusive offers based on geolocation
            for (ExclusiveOfferModel exclusiveOffer : cruiseModel.getExclusiveOffers()) {
                if (exclusiveOffer.getGeomarkets() != null
                        && exclusiveOffer.getGeomarkets().contains(market.toLowerCase())) {
                    exclusiveOffers.add(exclusiveOffer);
                }
            }

            this.locale = locale;
        }

        public CruiseModel getCruiseModel() {
            return cruiseModel;
        }

        public PriceModel getLowestPrice() {
            return lowestPrice;
        }

        public String getLowestPriceFormated() {
            return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
        }

        public boolean isWaitList() {
            return isWaitList;
        }

        public String getPricePrefix() {
            for (ExclusiveOfferModel exclusiveOffer : exclusiveOffers) {
                if (exclusiveOffer.getPricePrefix() != null) {
                    return exclusiveOffer.getPricePrefix();
                }
            }

            return null;
        }

        public List<ExclusiveOfferModel> getExclusiveOffers() {
            return exclusiveOffers;
        }
    }
}
