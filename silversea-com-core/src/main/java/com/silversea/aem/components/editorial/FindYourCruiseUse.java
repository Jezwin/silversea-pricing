package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.sling.api.resource.ValueMap;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * selectors : destination_all date_all duration_all ship_all cruisetype_all port_all page_2
 */
public class FindYourCruiseUse extends WCMUsePojo {

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

    // ships available for all the cruises
    private List<ShipModel> ships = new ArrayList<>();

    // ports available for all the cruises
    private List<PortModel> ports = new ArrayList<>();

    // departure dates available for the cruises
    private Set<YearMonth> dates = new TreeSet<>();

    // features available from design dialog + available for cruises
    private Set<FeatureModel> features = new HashSet<>();

    // destination filter
    private String destinationFilter = FILTER_ALL;

    // true if find your cruise is prefiltered by destination
    private boolean prefilterByDestination;

    // true if find your cruise is prefiltered by port
    private boolean prefilterByPort;

    // true if find your cruise is prefiltered by ship
    private boolean prefilterByShip;

    // date filter
    private YearMonth dateFilter;

    private Integer durationFilterMin;

    private Integer durationFilterMax;

    // ship filter
    private String shipFilter = FILTER_ALL;

    // cruise type filter
    private String cruiseTypeFilter = FILTER_ALL;

    // port filter
    private String portFilter = FILTER_ALL;

    private int activePage = 1;

    private int pageNumber;

    private boolean isFirstPage;

    private boolean isLastPage;

    private List<Integer> pagination;

    private int totalMatches;

    @Override
    @SuppressWarnings("unchecked")
    public void activate() throws Exception {
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final String lang = LanguageHelper.getLanguage(getCurrentPage());

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
        }

        // Get type from configuration
        final Conf confRes = getResource().adaptTo(Conf.class);
        if (confRes != null) {
            final ValueMap fycConf = confRes.getItem("/findyourcruise/findyourcruise");

            if (fycConf != null) {
                type = fycConf.get("type", String.class);
            }
        }

        // init geolocations informations
        String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;
        String currency = WcmConstants.DEFAULT_CURRENCY;

        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(
                GeolocationTagService.class);

        if (geolocationTagService != null) {
            final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromRequest(
                    getRequest());

            if (geolocationTagModel != null) {
                geomarket = geolocationTagModel.getMarket();
                currency = geolocationTagModel.getCurrency();
            }
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

        // init list of filtered cruises
        final Set<CruiseModel> filteredCruises = new HashSet<>();
        for (final CruiseModel cruise : allCruises) {
            boolean includeCruise = true;

            if (!destinationFilter.equals(FILTER_ALL) && !cruise.getDestination().getName().equals(destinationFilter)) {
                includeCruise = false;
            }

            if (!shipFilter.equals(FILTER_ALL) && !cruise.getShip().getName().equals(shipFilter)) {
                includeCruise = false;
            }

            try {
                final int cruiseDuration = Integer.parseInt(cruise.getDuration());
                if (durationFilterMin != null && durationFilterMax != null
                        && (cruiseDuration < durationFilterMin || cruiseDuration > durationFilterMax)) {
                    includeCruise = false;
                } else if (durationFilterMin != null && durationFilterMax == null
                        && cruiseDuration < durationFilterMin) {
                    includeCruise = false;
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
            }

            if (!cruiseTypeFilter.equals(FILTER_ALL) && !cruise.getCruiseType().equals(cruiseTypeFilter)) {
                includeCruise = false;
            }

            final YearMonth cruiseStartDate = YearMonth.of(cruise.getStartDate().get(Calendar.YEAR),
                    cruise.getStartDate().get(Calendar.MONTH) + 1);
            if (dateFilter != null && !cruiseStartDate.equals(dateFilter)) {
                includeCruise = false;
            }

            if (includeCruise) {
                filteredCruises.add(cruise);

                // TODO extract destinations, ships ... for the filtered cruises
            }

        }

        // build the cruises list for the current page
        int pageSize = PAGE_SIZE; // TODO replace by configuration

        int i = 0;
        for (final CruiseModel cruise : filteredCruises) {
            if (i >= (activePage - 1) * pageSize) {
                cruises.add(new CruiseItem(cruise, geomarket, currency));
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
     * Build Pagination according to the page active
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
     * @return ships available for all cruises for this lang
     */
    public List<ShipModel> getShips() {
        return ships;
    }

    /**
     * @return ports available for all cruises for this lang
     */
    public List<PortModel> getPorts() {
        return ports;
    }

    /**
     * @return dates available for all cruises for this lang
     */
    public Set<YearMonth> getDates() {
        return dates;
    }

    /**
     * @return intersection of features configured in design mode and feature of all cruises for this lang
     */
    public Set<FeatureModel> getFeatures() {
        return features;
    }

    /**
     * @return true if the component is prefiltered by destination (e.g. present in page with resource type
     *         destination)
     */
    public boolean isPrefilteredByDestination() {
        return prefilterByDestination;
    }

    public boolean isPrefilteredByPort() {
        return prefilterByPort;
    }

    public boolean isPrefilteredByShip() {
        return prefilterByShip;
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
     * Represent a cruise item used to display cruise informations (especially geolocated) in find your cruise
     */
    public class CruiseItem {

        private CruiseModel cruiseModel;

        private PriceModel lowestPrice;

        private boolean isWaitList;

        private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();

        public CruiseItem(final CruiseModel cruiseModel, final String market, final String currency) {
            this.cruiseModel = cruiseModel;

            // init lowest price and waitlist based on geolocation
            for (PriceModel priceModel : cruiseModel.getPrices()) {
                if (priceModel.getGeomarket() != null
                        && priceModel.getGeomarket().equals(market.toLowerCase())
                        && priceModel.getCurrency().equals(currency)) {
                    // Init lowest price
                    if (lowestPrice == null) {
                        lowestPrice = priceModel;
                    } else if (lowestPrice.getPrice() > priceModel.getPrice()) {
                        lowestPrice = priceModel;
                    }

                    // Init wait list
                    if (!priceModel.isWaitList()) {
                        isWaitList = false;
                    }
                }
            }

            // init exclusive offers based on geolocation
            for (ExclusiveOfferModel exclusiveOffer : cruiseModel.getExclusiveOffers()) {
                if (exclusiveOffer.getGeomarkets() != null
                        && exclusiveOffer.getGeomarkets().contains(market.toLowerCase())) {
                    exclusiveOffers.add(exclusiveOffer);
                }
            }
        }

        public CruiseModel getCruiseModel() {
            return cruiseModel;
        }

        public PriceModel getLowestPrice() {
            return lowestPrice;
        }

        public boolean isWaitList() {
            return isWaitList;
        }

        public List<ExclusiveOfferModel> getExclusiveOffers() {
            return exclusiveOffers;
        }
    }
}
