package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CruiseModelLight {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseModelLight.class);

    private String title;

    private String departurePortName;

    private String arrivalPortName;

    private DestinationItem destination;

    private String destinationId;

    private ShipItem ship;

    private Calendar startDate;

    private String duration;

    private String cruiseCode;

    private String cruiseType;

    private String thumbnail;

    private String path;

    private boolean isVisible;

    private List<PortItem> ports = new ArrayList<>();

    private Map<String, PriceModel> lowestPrices = new HashMap<String, PriceModel>();

    private List<FeatureModel> features = new ArrayList<>();

    private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();
    
    private List<ItineraryModel> itineraries;
    
    public CruiseModelLight(CruiseModel cruiseModel) {

        title = cruiseModel.getTitle();

        departurePortName = cruiseModel.getDeparturePortName();

        arrivalPortName = cruiseModel.getArrivalPortName();

        destination = new DestinationItem(cruiseModel.getDestination().getName(), cruiseModel.getDestination().getTitle());

        destinationId = cruiseModel.getDestination().getDestinationId();

        ship = new ShipItem(cruiseModel.getShip().getName(), cruiseModel.getShip().getTitle());

        startDate = cruiseModel.getStartDate();

        duration = cruiseModel.getDuration();

        cruiseCode = cruiseModel.getCruiseCode();

        cruiseType = cruiseModel.getCruiseType();

        thumbnail = cruiseModel.getThumbnail();

        path = cruiseModel.getPath();

        features = cruiseModel.getFeatures();

        exclusiveOffers = cruiseModel.getExclusiveOffers();

        isVisible = cruiseModel.isVisible();
        itineraries = cruiseModel.getItineraries();
        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            ports.add(new PortItem(itinerary.getPort().getName(), itinerary.getPort().getApiTitle()));
        }

        initLowestPrices(cruiseModel);
    }

    private void initLowestPrices(CruiseModel cruiseModel) {
        // init lowest price based on geolocation and currency
        for (PriceModel priceModel : cruiseModel.getPrices()) {
            if (priceModel.getGeomarket() != null && !priceModel.isWaitList()) {
                // Init lowest price
                String priceKey = priceModel.getGeomarket() + priceModel.getCurrency();
                if (lowestPrices.containsKey(priceKey)) {
                    // check if price is lower than the one in the table
                    PriceModel lowestPrice = lowestPrices.get(priceKey);
                    if (priceModel.getComputedPrice() < lowestPrice.getComputedPrice()) {
                        lowestPrices.put(priceKey, priceModel);
                    }
                } else {
                    lowestPrices.put(priceKey, priceModel);
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public DestinationItem getDestination() {
        return destination;
    }

    public ShipItem getShip() {
        return ship;
    }

    /**
     * @return start date of the cruise
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * @return duration of the cruise (in days)
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @return the cruise code
     */
    public String getCruiseCode() {
        return cruiseCode;
    }

    /**
     * @return list of ports of the itineraries
     */
    public List<PortItem> getPorts() {
        return ports;
    }

    /**
     * @return exclusive offers attached to the cruise
     */
    public List<ExclusiveOfferModel> getExclusiveOffers() {
        return exclusiveOffers;
    }

    /**
     * @return the cruise type (cruise or expedition)
     */
    public String getCruiseType() {
        return cruiseType;
    }

    /**
     * @return the cruise thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return departure port name
     */
    public String getDeparturePortName() {
        return departurePortName;
    }

    /**
     * @return arrival port name
     */
    public String getArrivalPortName() {
        return arrivalPortName;
    }

    /**
     * @return the lowest price for this cruise per market and currency
     */
    public Map<String, PriceModel> getLowestPrices() {
        return lowestPrices;
    }

    /**
     * @return features attached to this cruise
     */
    public List<FeatureModel> getFeatures() {
        return features;
    }

    /**
     * @return the path of the cruise page
     */
    public String getPath() {
        return path;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getDestinationId() {
        return destinationId;
    }

	public List<ItineraryModel> getItineraries() {
		return itineraries;
	}

}