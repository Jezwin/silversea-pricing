package com.silversea.aem.components.included.combo;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.CruiseItinerary;
import com.silversea.aem.components.page.Cruise2018Use;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.SegmentModel;
import com.silversea.aem.utils.StringsUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Handle route change from Combo Cruise Page
 * Triggered from onClickChangeRoute class.
 */
public class RouteUse extends WCMUsePojo {

    private String itineraryMap;
    private List<CruiseItinerary> itinerary;
    private CruiseModel cruise;
    private final Boolean showIntro = true;
    private static final List<String> SELECTORS_ITINERARY_DETAILS = Arrays.asList("templates","itineraryexcursions", "itinerarydetail");

    @Override
    public void activate() throws Exception {
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();

        StringsUtils.filterDinamicSelector(selectors, SELECTORS_ITINERARY_DETAILS).flatMap(this::getSegmentSelected)
                .ifPresent(segment -> {
                    setCruise(segment.getCruise());
                    setItineraryMap(segment.getFocusedMapReference());
                    setItinerary(segment.getCruise());
                });
    }

    private Optional<SegmentModel> getSegmentSelected(String routeName) {
        String pathSegmentResource = getCurrentPage().getPath() + "/" + routeName;
        Resource segmentResource = getResourceResolver().getResource(pathSegmentResource);
        return Optional.ofNullable(segmentResource).map(resource -> resource.adaptTo(Page.class)).map(page -> page.adaptTo(SegmentModel.class));
    }


    public CruiseModel getCruise() {
        return cruise;
    }

    private void setCruise(CruiseModel cruise) {
        this.cruise = cruise;
    }

    public List<CruiseItinerary> getItinerary() {
        return itinerary;
    }

    private void setItinerary(CruiseModel cruiseModel) {
        this.itinerary = Cruise2018Use.retrieveItinerary(cruiseModel, getResourceResolver());
    }

    public String getItineraryMap() {
        return itineraryMap;
    }

    private void setItineraryMap(String itineraryMap) {
        this.itineraryMap = itineraryMap;
    }

    public Boolean getShowIntro() {
        return showIntro;
    }
}
