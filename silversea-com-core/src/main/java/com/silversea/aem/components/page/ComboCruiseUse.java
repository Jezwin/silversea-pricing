package com.silversea.aem.components.page;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.helper.PriceHelper;
import com.silversea.aem.models.ComboCruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.SegmentModel;
import com.silversea.aem.utils.PathUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ComboCruiseUse extends AbstractGeolocationAwareUse {

    private ComboCruiseModel comboCruiseModel;

    private List<SuitePrice> prices = new ArrayList<>();

    private PriceModel lowestPrice = null;

    private boolean isWaitList = true;

    private Locale locale;

    private int excursionsNumber = 0;

    private int landProgramsNumber = 0;

    @Override
    public void activate() throws Exception {
        super.activate();

        // init cruise model from current page
        if (getRequest().getAttribute("cruiseModel") != null) {
            comboCruiseModel = (ComboCruiseModel) getRequest().getAttribute("comboCruiseModel");
        } else {
            comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
            getRequest().setAttribute("comboCruiseModel", comboCruiseModel);
        }

        if (comboCruiseModel == null) {
            throw new Exception("Cannot get combo cruise model");
        }

        locale = getCurrentPage().getLanguage(false);

        // init number of elements (excursions, hotels, land programs)
        for (final SegmentModel segment : comboCruiseModel.getSegments()) {
            if (segment.getCruise() != null) {
                for (ItineraryModel itinerary : segment.getCruise().getCompactedItineraries()) {
                    if (itinerary.getPort() != null) {
                        excursionsNumber += itinerary.getHasDedicatedShorex() ? itinerary.getExcursions().size() : itinerary.getPort().getExcursions().size();
                        landProgramsNumber += itinerary.getLandPrograms().size();
                    }
                    
                    if (itinerary.getDepartDate() != null && itinerary.getDate() != null) {
        				int numberDays = itinerary.getDepartDate().get(Calendar.DATE) - itinerary.getDate().get(Calendar.DATE);
        				itinerary.setNumberDays(numberDays);
        			}

                }
            }
        }

        // init prices based on geolocation
        // TODO duplicated with com.silversea.aem.components.page.CruiseUse
        for (final PriceModel priceModel : comboCruiseModel.getPrices()) {
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
                    prices.add(new SuitePrice(priceModel.getSuite(), priceModel, locale, priceModel.getSuiteCategory()));
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
    }

    /**
     * @return combo cruise model
     */
    public ComboCruiseModel getComboCruiseModel() {
        return comboCruiseModel;
    }

    /**
     * @return the number of excursions, of the itineraries or the attached ports
     */
    public int getExcursionsNumber() {
        return excursionsNumber;
    }

    /**
     * @return the number of land programs, of the itineraries or the attached ports
     */
    public int getLandProgramsNumber() {
        return landProgramsNumber;
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
}