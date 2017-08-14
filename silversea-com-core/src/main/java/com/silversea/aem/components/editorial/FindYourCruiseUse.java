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

    private List<TagModel> featureTags = new ArrayList<>();

    private String type = "v2";

    private List<CruiseModel> allCruises = new ArrayList<>();

    private List<CruiseItem> cruises = new ArrayList<>();

    private List<DestinationModel> destinations = new ArrayList<>();

    private List<ShipModel> ships = new ArrayList<>();

    private List<PortModel> ports = new ArrayList<>();

    private Set<String> durations = new TreeSet<>();

    private String destination;

    private String date;

    private String duration;

    private String ship;

    private String cruiseType;

    private String port;

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
                    // TODO read configuration + lang
                    case "destination":
                        destination = splitSelector[1];
                        break;
                    case "date":
                        break;
                    case "duration":
                        duration = splitSelector[1];
                        break;
                    case "ship":
                        ship = splitSelector[1];
                        break;
                    case "cruisetype":
                        cruiseType = splitSelector[1];
                        break;
                    case "port":
                        port = splitSelector[1];
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
        final List<CruiseModel> filteredCruises = new ArrayList<>();
        for (final CruiseModel cruise : allCruises) {
            // TODO duration
            // TODO port
            if ((destination == null || cruise.getDestination().getName().equals(destination))
                    && (ship == null || cruise.getShip().getName().equals(ship))) {
                filteredCruises.add(cruise);
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
     * @return destinations available for the set of results
     */
    public List<DestinationModel> getDestinations() {
        return destinations;
    }

    /**
     * @return ships available for the set of results
     */
    public List<ShipModel> getShips() {
        return ships;
    }

    public List<PortModel> getPorts() {
        return ports;
    }

    public Set<String> getDurations() {
        return durations;
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