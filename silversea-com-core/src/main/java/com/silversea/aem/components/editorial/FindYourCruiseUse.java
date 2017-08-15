package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;

/**
 * selectors : destination_all date_all duration_all ship_all cruisetype_all port_all page_2
 */
public class FindYourCruiseUse extends WCMUsePojo {

    private final static int PAGE_SIZE = 15;

    private final static String FILTER_ALL = "all";

    private List<TagModel> featureTags = new ArrayList<>();

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

    // durations available for the cruises
    private Set<Integer> durations = new TreeSet<>();

    // destination filter
    private String destinationFilter = FILTER_ALL;

    // true if find your cruise is prefiltered by destination
    private boolean prefilterByDestination;

    // date filter
    private String dateFilter = FILTER_ALL;

    // duration filter
    private String durationFilter = FILTER_ALL;

    private Integer durationFilterMin;

    private Integer durationFilterMax;

    // ship filter
    private String shipFilter = FILTER_ALL;

    // cruise type filter
    private String cruiseTypeFilter = FILTER_ALL;

    // port filter
    private String portFilter = FILTER_ALL;

    private int activePage = 1;

    private boolean isFirstPage;

    private boolean isLastPage;

    @Override
    public void activate() throws Exception {
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        // Get tags to display
        if (tagManager != null) {
            final String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);

            if (tags != null) {
                for (String tagId : tags) {
                    final Tag tag = tagManager.resolve(tagId);

                    if (tag != null) {
                        featureTags.add(tag.adaptTo(TagModel.class));
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
                        break;
                    case "duration":
                        durationFilter = splitSelector[1];

                        final String[] splitDuration = durationFilter.split("-");

                        if (splitDuration.length == 2) {
                            try {
                                durationFilterMin = Integer.parseInt(splitDuration[0]);
                                durationFilterMax = Integer.parseInt(splitDuration[1]);
                            } catch (NumberFormatException ignored) {}
                        } else {
                            try {
                                durationFilterMin = Integer.parseInt(durationFilter);
                            } catch (NumberFormatException ignored) {}
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
                        } catch (NumberFormatException ignored) {}
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
            allCruises = cruisesCacheService.getCruises("en");
            destinations = cruisesCacheService.getDestinations("en");
            ships = cruisesCacheService.getShips("en");
            ports = cruisesCacheService.getPorts("en");
            durations = cruisesCacheService.getDurations("en");
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
            } catch (NumberFormatException ignored) {}

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

            if (i >= activePage * pageSize) {
                break;
            }

            i++;
        }

        // Setting convenient booleans for building pagination
        isFirstPage = activePage == 1;
        isLastPage = activePage == getPagesNumber();
    }

    /**
     * @return the featureTags
     */
    public List<TagModel> getFeatureTags() {
        return featureTags;
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
     * @return true if the component is prefiltered by destination (e.g. present in page with resource type destination)
     */
    public boolean isPrefilteredByDestination() {
        return prefilterByDestination;
    }

    /**
     * @return the number of pages
     */
    public int getPagesNumber() {
        return (int) Math.ceil((float) allCruises.size() / (float) PAGE_SIZE);
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