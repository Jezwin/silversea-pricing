package com.silversea.aem.components.included.lightboxes;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ExcursionModel;
import com.silversea.aem.models.ItineraryExcursionModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.utils.CruiseUtils;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class LightboxPortUse extends AbstractGeolocationAwareUse {

    private ItineraryModel portItinerary;
    private List<ExcursionModel> excursions;
    private Boolean isEmbark;
    private Boolean isDebark;

    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        portItinerary = retrievePortItineraryModel(selectors);
        excursions = retrieveExcursions(portItinerary);
        excursions.sort(Comparator.comparing(ex -> ex.getTitle().trim()));
    }

    private ItineraryModel retrievePortItineraryModel(String[] selectors) {
        if (ArrayUtils.isNotEmpty(selectors)) {
            String itineraryId = selectors[2];
            Resource currentResource = getCurrentPage().getContentResource();
            Resource itiResource = currentResource.hasChildren() ? currentResource.getChild("itineraries") : null;
            if (itiResource != null) {
                Resource selectedIti = itiResource.getChild(itineraryId);
                if (selectedIti != null) {
                    isEmbark = StreamSupport.stream(itiResource.getChildren().spliterator(), false).findFirst().get().getPath().equals(selectedIti.getPath());
                    isDebark = StreamSupport.stream(itiResource.getChildren().spliterator(), false).reduce((a, b) -> b).get().getPath().equals(selectedIti.getPath());

                    ItineraryModel itiModel = selectedIti.adaptTo(ItineraryModel.class);

                    return itiModel;
                }
            }
        }
        return null;
    }

    private List<ExcursionModel> retrieveExcursions(ItineraryModel itinerary){
        if (itinerary.getHasDedicatedShorex()) {
            return ofNullable(itinerary.getExcursions())
                    .map(Collection::stream).orElseGet(Stream::empty)
                    .map(ItineraryExcursionModel::getExcursion)
                    .collect(toList());
        } else {
            return ofNullable(itinerary.getPort().getExcursions())
                    .map(Collection::stream).orElseGet(Stream::empty)
                    .filter(ex -> !isEmbark || ex.isOkForEmbark())
                    .filter(ex -> !isDebark || ex.isOkForDebarks())
                    .collect(toList());
        }
    }

    public ItineraryModel getPortItinerary() {
        return portItinerary;
    }

    public List<ExcursionModel> getExcursions() {
        return excursions;
    }

    public Boolean getIsEmbark() {
        return isEmbark;
    }

    public Boolean getIsDebark() {
        return isDebark;
    }

}
