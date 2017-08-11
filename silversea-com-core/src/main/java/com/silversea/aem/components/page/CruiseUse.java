package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.*;
import com.silversea.aem.services.GeolocationTagService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CruiseUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseUse.class);

    private CruiseModel cruiseModel;

    private String previous;

    private String next;

    private int excursionsNumber = 0;

    private int hotelsNumber = 0;

    private int landProgramsNumber = 0;

    private List<Asset> suitesAssetsList = new ArrayList<>();

    private List<Asset> diningsAssetsList = new ArrayList<>();

    private List<Asset> publicAreasAssetsList = new ArrayList<>();

    private List<Asset> itinerariesAssetsList = new ArrayList<>();

    private List<SuitePrice> prices = new ArrayList<>();

    private PriceModel lowestPrice = null;

    private boolean isWaitList = true;

    @Override
    public void activate() throws Exception {
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        // init cruise model from current page
        if (getRequest().getAttribute("cruiseModel") != null) {
            cruiseModel = (CruiseModel) getRequest().getAttribute("cruiseModel");
        } else {
            cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
            getRequest().setAttribute("cruiseModel", cruiseModel);
        }

        if (cruiseModel == null) {
            throw new Exception("Cannot get cruise model");
        }

        // init pagination
        final Iterator<Page> children = getCurrentPage().getParent().listChildren();
        if (children != null && children.hasNext()) {
            while (children.hasNext()) {
                final Page child = children.next();

                if (child.getPath().equals(getCurrentPage().getPath()) && children.hasNext()) {
                    next = children.next().getPath();
                    previous = child.getPath();

                    break;
                }
            }
        }

        // init assets from ship areas
        suitesAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getSuites());
        diningsAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getDinings());
        publicAreasAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getPublicAreas());

        // init assets from itinerary and cruise itself
        for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
            final PortModel portModel = itinerary.getPort();

            if (portModel != null) {
                final String assetSelectionReference = portModel.getAssetSelectionReference();

                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    itinerariesAssetsList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
                }
            }
        }

        if (StringUtils.isNotBlank(cruiseModel.getAssetSelectionReference())) {
            itinerariesAssetsList.addAll(AssetUtils.buildAssetList(cruiseModel.getAssetSelectionReference(), getResourceResolver()));
        }

        // init number of elements (excursions, hotels, land programs)
        if (getItinerariesHasElements()) {
            for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
                excursionsNumber += itinerary.getExcursions().size();
                hotelsNumber += itinerary.getHotels().size();
                landProgramsNumber += itinerary.getLandPrograms().size();
            }
        } else {
            for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
                if (itinerary.getPort() != null) {
                    excursionsNumber += itinerary.getPort().getExcursions().size();
                    hotelsNumber += itinerary.getPort().getHotels().size();
                    landProgramsNumber += itinerary.getPort().getLandPrograms().size();
                }
            }
        }

        // init prices based on geolocation
        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);

        if (geolocationTagService != null) {
            final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromRequest(getRequest());

            String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;
            String currency = WcmConstants.DEFAULT_CURRENCY;
            if (geolocationTagModel != null) {
                geomarket = geolocationTagModel.getMarket();
                currency = geolocationTagModel.getCurrency();
            }

            for (PriceModel priceModel : cruiseModel.getPrices()) {
                if (priceModel.getGeomarket() != null
                        && priceModel.getGeomarket().equals(geomarket.toLowerCase())
                        && priceModel.getCurrency().equals(currency)) {
                    // Adding price to suites/prices mapping
                    boolean added = false;

                    for (SuitePrice price : prices) {
                        if (price.getSuite().equals(priceModel.getSuite())) {
                            price.add(priceModel);

                            added = true;
                        }
                    }

                    if (!added) {
                        prices.add(new SuitePrice(priceModel.getSuite(), priceModel));
                    }

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
        }
    }

    /**
     * @return cruise model
     */
    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

    /**
     * @return the destination title
     */
    public String getDestinationTitle() {
        return getCurrentPage().getParent().getTitle();
    }

    /**
     * TODO move to cruise model
     *
     * @return the cruise description
     */
    public String getDescription() {
        return StringUtils.isEmpty(cruiseModel.getDescription()) ? cruiseModel.getImportedDescription() : cruiseModel.getDescription();
    }

    /**
     * @return true if at least on itinerary have an excursion, land program or hotels
     */
    public boolean getItinerariesHasElements() {
        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            if (itinerary.getExcursions().size() > 0 || itinerary.getLandPrograms().size() > 0 || itinerary.getHotels().size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the number of excursions, of the itineraries or the attached ports
     */
    public int getExcursionsNumber() {
        return excursionsNumber;
    }

    /**
     * @return the number of hotels, of the itineraries or the attached ports
     */
    public int getHotelsNumber() {
        return hotelsNumber;
    }

    /**
     * @return the number of land programs, of the itineraries or the attached ports
     */
    public int getLandProgramsNumber() {
        return landProgramsNumber;
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
     * @return assets from suites of the ship
     */
    public List<Asset> getAllAssetForSuite() {
        return suitesAssetsList;
    }

    /**
     * @return assets from dinings of the ship
     */
    public List<Asset> getAllAssetForDinning() {
        return diningsAssetsList;
    }

    /**
     * @return assets from public areas of the ship
     */
    public List<Asset> getAllAssetForPublicArea() {
        return publicAreasAssetsList;
    }

    /**
     * @return assets form itinerary (cities) and the cruise itself
     */
    public List<Asset> getAllAssetForItinerary() {
        return itinerariesAssetsList;
    }

    /**
     * @return the language of the current page
     */
    public String getPageLanguage() {
        return getCurrentPage().getLanguage(false).getLanguage();
    }

    /**
     * @return the request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }

    /**
     * @return common list for dinings and public areas
     */
    public List<Asset> getAllAssetForDinningNPublicAreas() {
        return Stream.concat(getAllAssetForDinning().stream(),
                getAllAssetForPublicArea().stream()).collect(Collectors.toList());
    }

    /**
     * @return all the assets of the gallery
     */
    public LinkedHashMap<String, List<Asset>> getCruiseGallery() {
        LinkedHashMap<String, List<Asset>> gallery;
        gallery = new LinkedHashMap<>();

        if (getAllAssetForItinerary() != null) {
            gallery.put("voyage", getAllAssetForItinerary());
        }
        if (getAllAssetForSuite() != null) {
            gallery.put("suites", getAllAssetForSuite());
        }
        if (getAllAssetForDinning() != null) {
            gallery.put("dinings", getAllAssetForDinning());
        }
        if (getAllAssetForPublicArea() != null) {
            gallery.put("public-areas", getAllAssetForPublicArea());
        }
        // TODO : gallery.put("virtual-tours", value);
        // TODO : gallery.put("ship-exteriors", value);

        return gallery;
    }

    /**
     * @return return prices corresponding to the current geolocation
     */
    public List<SuitePrice> getPrices() {
        return prices;
    }

    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    public boolean isWaitList() {
        return isWaitList;
    }

    /**
     * Inner class used to store mapping between one suite and price variations Lowest price is updated when a
     * <code>PriceModel</code> is added to the price variations list
     */
    public class SuitePrice {

        private SuiteModel suiteModel;

        private List<PriceModel> pricesVariations = new ArrayList<>();

        private PriceModel lowestPrice;

        public SuitePrice(final SuiteModel suiteModel, final PriceModel price) {
            this.suiteModel = suiteModel;
            pricesVariations.add(price);

            lowestPrice = price;
        }

        public SuiteModel getSuite() {
            return suiteModel;
        }

        public List<PriceModel> getPricesVariations() {
            return pricesVariations;
        }

        public PriceModel getLowestPrice() {
            return lowestPrice;
        }

        public void add(final PriceModel priceModel) {
            pricesVariations.add(priceModel);

            if (priceModel.getPrice() < lowestPrice.getPrice()) {
                lowestPrice = priceModel;
            }
        }
    }
}