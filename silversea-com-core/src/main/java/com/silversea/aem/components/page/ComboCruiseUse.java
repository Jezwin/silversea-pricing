package com.silversea.aem.components.page;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItinerary;
import com.silversea.aem.components.beans.CruisePrePost;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.components.included.combo.AssetGalleryCruiseUse;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.utils.PathUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class ComboCruiseUse extends AbstractGeolocationAwareUse {

    private ComboCruiseModel comboCruiseModel;

    private List<SuitePrice> prices = new ArrayList<>();

    private List<KeyPerson> keyPeople;

    private String keyPeopleTitle;

    private List<CruisePrePost> globalPrePost;

    private PriceModel lowestPrice = null;

    private boolean isWaitList = true;

    private Locale locale;

    private List<SilverseaAsset> shipAssetGallery;

    private SegmentModel segmentModel;

    private List<CruiseItinerary> itinerary;

    private List<SilverseaAsset> assetsGallery;

    @Override
    public void activate() throws Exception {
        super.activate();


        // init cruise model from current page
        comboCruiseModel = retrieveComboCruise().orElseThrow(() -> new Exception("Cannot get combo cruise model"));

        keyPeople = retrieveMultiField( "keyPeople", resource -> resource.getChild("path"))
                .map(path -> path.adaptTo(String.class))
                .map(getPageManager()::getPage)
                .filter(Objects::nonNull)
                .map((Page page) -> new KeyPerson(page, getResourceResolver()))
                .collect(toList());
        keyPeopleTitle = getResource().getValueMap().get("keyPeopleTitle", String.class);
        globalPrePost = retrievePrePost(comboCruiseModel);

        locale = getCurrentPage().getLanguage(false);
        segmentModel = retrieveSelectedSegment(comboCruiseModel, null);
        assetsGallery = AssetGalleryCruiseUse.retrieveAssetsGallery(getResource(), getResourceResolver(), getCurrentPage(), true);
        itinerary = Cruise2018Use.retrieveItinerary(segmentModel.getCruise(), getResourceResolver());
        shipAssetGallery = Cruise2018Use.retrieveShipAssetsGallery(segmentModel.getCruise(), getResourceResolver());

        prices = Cruise2018Use.retrievePrices(getCurrentPage(), geomarket, currency, comboCruiseModel.getPrices());
        lowestPrice = Cruise2018Use.retrieveLowestPrice(prices);
        isWaitList = lowestPrice == null;
    }

    private Optional<ComboCruiseModel> retrieveComboCruise() {
        if (getRequest().getAttribute("cruiseModel") != null) {
            return Optional.ofNullable((ComboCruiseModel) getRequest().getAttribute("comboCruiseModel"));
        }
        return Optional.ofNullable(getCurrentPage().adaptTo(ComboCruiseModel.class));
    }

    private List<CruisePrePost> retrievePrePost(ComboCruiseModel comboCruiseModel) {
        return comboCruiseModel.getSegments().stream()
                .map(SegmentModel::getCruise)
                .map(cruise -> Cruise2018Use.retrieveItinerary(cruise, getResourceResolver()))
                .flatMap(itineraries -> Cruise2018Use.retrievePrePosts(itineraries).stream())
                .collect(toList());
    }

    private SegmentModel retrieveSelectedSegment(ComboCruiseModel comboCruiseModel, String selector) {
        return comboCruiseModel != null && !comboCruiseModel.getSegments().isEmpty() ? comboCruiseModel.getSegments().get(0) : null;
    }


    /**
     * @return the total number of cruise fares additions
     */
    public int getCruiseFareAdditionsSize() {
        return comboCruiseModel.getComboCruiseFareAdditions().size();
    }


    public List<? extends CardLightbox> getKeyPeople() {
        return keyPeople;
    }

    /**
     * @return combo cruise model
     */
    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }

    public Calendar getStartDate() {
        return comboCruiseModel.getSegments().get(0).getCruise().getStartDate();
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

    public String getComputedPriceFormatted() {
        return PriceHelper.getValue(locale, getLowestPrice().getComputedPrice());
    }

    /**
     * @return true is the cruise is on wait list
     */
    public boolean isWaitList() {
        return isWaitList;
    }

    /**
     * Return path for request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    public Calendar getEndDate() {
        int lastIndex = comboCruiseModel.getSegments().size();
        return comboCruiseModel.getSegments().get(lastIndex - 1).getCruise().getEndDate();
    }

    public String getDeparturePortName() {
        int lastIndex = comboCruiseModel.getSegments().size();
        return comboCruiseModel.getSegments().get(lastIndex - 1).getCruise().getDeparturePortName();
    }

    public String getArrivalPortName() {
        return comboCruiseModel.getSegments().get(0).getCruise().getArrivalPortName();
    }

    public Integer getDuration() {
        return comboCruiseModel.getDuration();
    }

    public boolean isEarlyBookingBonus() {
        return lowestPrice.getEarlyBookingBonus() != null;
    }

    public String getKeyPeopleTitle() {
        return keyPeopleTitle;
    }

    public boolean isFeetSquare() {
        return "US".equals(countryCode);
    }

    public List<SilverseaAsset> getShipAssetGallery() {
        return shipAssetGallery;
    }

    public List<SilverseaAsset> getAssetsGallery() {
        return assetsGallery;
    }

    public List<CruisePrePost> getGlobalPrePost() {
        return globalPrePost;
    }


    public List<CruiseItinerary> getItinerary() {
        return itinerary;
    }

    public SegmentModel getSegmentModel() {
        return segmentModel;
    }
}