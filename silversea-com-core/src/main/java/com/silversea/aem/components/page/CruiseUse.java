package com.silversea.aem.components.page;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CruiseUse extends AbstractGeolocationAwareUse {

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

    private List<FeatureModel> enrichmentsFeatures = new ArrayList<>();

    private List<ExclusiveOfferItem> exclusiveOffers = new ArrayList<>();

    private Locale locale;

    @Override
    public void activate() throws Exception {
        super.activate();

        locale = getCurrentPage().getLanguage(false);

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
            Page child = null;
            while (children.hasNext()) {
                previous = child != null ? child.getPath() : null;

                child = children.next();

                if (child.getPath().equals(getCurrentPage().getPath()) && children.hasNext()) {
                    next = children.next().getPath();

                    break;
                }
            }
        }

        // init assets from ship areas
        suitesAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getSuites());
        diningsAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getDinings());
        publicAreasAssetsList = AssetUtils.addAllShipAreaAssets(getResourceResolver(), cruiseModel.getShip().getPublicAreas());

        if (StringUtils.isNotBlank(cruiseModel.getAssetSelectionReference())) {
            itinerariesAssetsList.addAll(AssetUtils.buildAssetList(cruiseModel.getAssetSelectionReference(), getResourceResolver()));
        }

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

        // init number of elements (excursions, hotels, land programs)
        for (ItineraryModel itinerary : cruiseModel.getCompactedItineraries()) {
            excursionsNumber += getItinerariesHasExcursions() ? itinerary.getExcursions().size() : itinerary.getPort().getExcursions().size();
            hotelsNumber += itinerary.getHotels().size();
            landProgramsNumber += itinerary.getLandPrograms().size();
        }

        // init prices based on geolocation
        for (PriceModel priceModel : cruiseModel.getPrices()) {
            if (priceModel.getGeomarket() != null
                    && priceModel.getGeomarket().equals(geomarket)
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
                    prices.add(new SuitePrice(priceModel.getSuite(), priceModel, locale));
                }

                // Init lowest price
                if (!priceModel.isWaitList()) {
                    if (lowestPrice == null) {
                        lowestPrice = priceModel;
                    } else if (lowestPrice.getComputedPrice() > priceModel.getComputedPrice()) {
                        lowestPrice = priceModel;
                    }

                    // Init wait list
                    isWaitList = false;
                }
            }
        }

        // init enrichments features
        for (FeatureModel feature : cruiseModel.getFeatures()) {
            if (feature.getFeatureCode() != null && !feature.getFeatureCode().equals(WcmConstants.FEATURE_CODE_VENETIAN_SOCIETY)) {
                enrichmentsFeatures.add(feature);
            }
        }

        // init exclusive offers based on geolocation
        for (ExclusiveOfferModel exclusiveOffer : cruiseModel.getExclusiveOffers()) {
            if (exclusiveOffer.getGeomarkets() != null
                    && exclusiveOffer.getGeomarkets().contains(geomarket)) {
                exclusiveOffers.add(new ExclusiveOfferItem(exclusiveOffer, countryCode, cruiseModel.getDestination().getPath()));
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
     * @return true if at least on itinerary have an excursion
     */
    public boolean getItinerariesHasExcursions() {
        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            if (itinerary.getExcursions().size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if at least on itinerary have an hotels
     */
    public boolean getItinerariesHasHotels() {
        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            if (itinerary.getHotels().size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if at least on itinerary have a land program
     */
    public boolean getItinerariesHasLandPrograms() {
        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
            if (itinerary.getLandPrograms().size() > 0) {
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
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
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
	 * Put the cruise itinerary map inside voyages gallery.
	 * The cruise itinerary map will be the first element of the list
	 * @return gallery
	 */
	public LinkedHashMap<String, List<Asset>> getCruiseOverviewGallery() {
		 LinkedHashMap<String, List<Asset>> gallery = getCruiseGallery();
		 final Resource members = getResourceResolver().getResource(getCruiseModel().getItinerary());
		 if (members != null) {
			 Asset asset = members.adaptTo(Asset.class);
			 List<Asset> voyages = gallery.get("voyage");
			 if ( asset != null && !voyages.isEmpty()) {
				 voyages.add(0,asset);
				 gallery.put("voyage", voyages);
			 }
		 }
		 return gallery;
	}


    /**
     * @return return prices corresponding to the current geolocation
     */
    public List<SuitePrice> getPrices() {
        return prices;
    }

    /**
     * @return the lowest price for this cruise
     */
    public PriceModel getLowestPrice() {
        return lowestPrice;
    }

    /**
     * @return the computed price, formatted with the locale according to the geolocation
     */
    public String getComputedPriceFormated() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }

    /**
     * @return true is the cruise is on wait list
     */
    public boolean isWaitList() {
        return isWaitList;
    }

    /**
     * @return get the enrichments features (without venitian society)
     */
    public List<FeatureModel> getEnrichmentsFeatures() {
        return enrichmentsFeatures;
    }

    /**
     * @return exclusive offers of this cruise
     */
    public List<ExclusiveOfferItem> getExclusiveOffers() {
        return exclusiveOffers;
    }

    /**
     * @return cruise fare additions of all exclusive offers of this cruise
     */
    public List<String> getExclusiveOffersCruiseFareAdditions() {
        final List<String> cruiseFareAdditions = new ArrayList<>();

        for (final ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
            cruiseFareAdditions.addAll(exclusiveOffer.getCruiseFareAdditions());
        }

        return cruiseFareAdditions;
    }

    /**
     * @return the total number of cruise fares additions
     */
    public int getCruiseFareAdditionsSize() {
        return getExclusiveOffersCruiseFareAdditions().size() + cruiseModel.getCruiseFareAdditions().size();
    }

    /**
     * @return first map overhead
     */
    public String getMapOverHead() {
        for (final ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
            if (exclusiveOffer.getMapOverHead() != null) {
                return exclusiveOffer.getMapOverHead();
            }
        }

        return null;
    }

    /**
     * @return the price prefix from the first exclusive offer
     */
    public String getPricePrefix() {
        for (ExclusiveOfferItem exclusiveOffer : exclusiveOffers) {
            if (exclusiveOffer.getPricePrefix() != null) {
                return exclusiveOffer.getPricePrefix();
            }
        }

        return null;
    }
}