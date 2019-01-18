package com.silversea.aem.models;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    private String lang;

    private String path;

    private boolean isVisible;

    private List<PortItem> ports = new ArrayList<>();

    private Map<String, PriceModelLight> lowestPrices = new HashMap<String, PriceModelLight>();

    private List<FeatureModelLight> features = new ArrayList<>();

    private List<ExclusiveOfferModelLight> exclusiveOffers = new ArrayList<>();
    
    private List<String> portPaths = new ArrayList<>();

    public CruiseModelLight(CruiseModel cruiseModel) {

        title = cruiseModel.getTitle();

        departurePortName = cruiseModel.getDeparturePortName();
        try{
        arrivalPortName = cruiseModel.getArrivalPortName();
        }catch(Exception e){
        	arrivalPortName = "missing";
        }
        destination = new DestinationItem(cruiseModel.getDestination().getName(), cruiseModel.getDestination().getTitle());

        destinationId = cruiseModel.getDestination().getDestinationId();

        ship = new ShipItem(cruiseModel.getShip().getName(), cruiseModel.getShip().getTitle(),cruiseModel.getShip().getShipId());

        startDate = cruiseModel.getStartDate();

        duration = cruiseModel.getDuration();

        cruiseCode = cruiseModel.getCruiseCode();

        cruiseType = cruiseModel.getCruiseType();

        thumbnail = cruiseModel.getThumbnail();

        path = cruiseModel.getPath();

        lang = cruiseModel.getLang();

        List<FeatureModel> tmpFeat = cruiseModel.getFeatures();
        List<FeatureModelLight> tmpFeatLight = new ArrayList<>();
        for (FeatureModel featureModel : tmpFeat) {
			tmpFeatLight.add(new FeatureModelLight(featureModel));
		}
        features = tmpFeatLight;
        tmpFeat = null;

        for(ExclusiveOfferModel eoModel : cruiseModel.getExclusiveOffers()) {
            ExclusiveOfferModelLight eoModelLight =  new ExclusiveOfferModelLight(eoModel);
            exclusiveOffers.add(eoModelLight);
        }

        isVisible = cruiseModel.isVisible();

        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            PortModel port = itinerary.getPort();
            ports.add(new PortItem(port.getName(), port.getApiTitle(), port.getCountryIso3()));
            if (port.getPath() != null){
            	portPaths.add(port.getPath());
            }
        }

        initLowestPrices(cruiseModel);

        cruiseModel = null;
    }

    private void initLowestPrices(CruiseModel cruiseModel) {
        // init lowest price based on geolocation and currency
        for (PriceModel priceModel : cruiseModel.getPrices()) {
            if (priceModel.getGeomarket() != null && !priceModel.isWaitList()) {
                // Init lowest price
                String priceKey = priceModel.getGeomarket() + priceModel.getCurrency();
                if (lowestPrices.containsKey(priceKey)) {
                    // check if price is lower than the one in the table
                    PriceModelLight lowestPrice = lowestPrices.get(priceKey);
                    if (priceModel.getComputedPrice() < lowestPrice.getComputedPrice()) {
                        lowestPrices.put(priceKey, new PriceModelLight(priceModel));
                    }
                } else {
                    lowestPrices.put(priceKey, new PriceModelLight(priceModel));
                }
            }
        }
    }

    public String getTitle() {
        if(!StringUtils.isEmpty(departurePortName) && !StringUtils.isEmpty(arrivalPortName) && !StringUtils.isEmpty(cruiseCode)){
            String delimiter = "to";
            return cruiseCode + " - " + getDeparturePortName() + " "+ delimiter + " " + getArrivalPortName();
        }
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
    public List<ExclusiveOfferModelLight> getExclusiveOffers() {
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
    public Map<String, PriceModelLight> getLowestPrices() {
        return lowestPrices;
    }

    /**
     * @return features attached to this cruise
     */
    public List<FeatureModelLight> getFeatures() {
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

	public List<String> getPortPaths() {
		return portPaths;
	}


    public String getLang() {
        return lang;
    }
}