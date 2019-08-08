package com.silversea.aem.components.included.lightboxes;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItinerary;
import com.silversea.aem.components.beans.CruisePrePost;
import com.silversea.aem.components.beans.SuitePrice;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.*;
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
    private List<String> shorex;
    private List<ExcursionModel> excursions;
    private List<LandProgramModel> mid;
    private Boolean isEmbark;
    private Boolean isDebark;

    @Override
    public void activate() throws Exception {
        super.activate();
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        portItinerary = retrievePortItineraryModel(selectors);
        if (portItinerary != null) {
            excursions = "silversea-expedition".equals(portItinerary.getCruiseType()) ? Collections.emptyList() :
                    CruiseItinerary.retrieveExcursions(portItinerary);
            excursions.sort(Comparator.comparing(ex -> ex.getTitle().trim()));
            mid = portItinerary.getLandPrograms().stream().map(ItineraryLandProgramModel::getLandProgram)
                    .filter(landProgram -> CruisePrePost.PREPOSTMID.MID
                            .equals(CruisePrePost.PREPOSTMID.retrieve(landProgram.getLandCode())))
                    .collect(toList());
            shorex = Stream.concat(mid.stream().map(LandProgramModel::getTitle),
                    excursions.stream().map(ExcursionModel::getTitle)).collect(toList());
        }
    }

    private ItineraryModel retrievePortItineraryModel(String[] selectors) {
        if (ArrayUtils.isNotEmpty(selectors)) {
            String itineraryId = selectors[2];
            Resource currentResource = getCurrentPage().getContentResource();
            Resource itiResource = currentResource.hasChildren() ? currentResource.getChild("itineraries") : null;
            if (itiResource != null) {
                Resource selectedIti = itiResource.getChild(itineraryId);
                if (selectedIti != null) {
                    isEmbark = StreamSupport.stream(itiResource.getChildren().spliterator(), false).findFirst().get()
                            .getPath().equals(selectedIti.getPath());
                    isDebark = StreamSupport.stream(itiResource.getChildren().spliterator(), false).reduce((a, b) -> b)
                            .get().getPath().equals(selectedIti.getPath());
                    return selectedIti.adaptTo(ItineraryModel.class);
                }
            }
        }
        return null;
    }


    public ItineraryModel getPortItinerary() {
        return portItinerary;
    }

    public List<ExcursionModel> getExcursions() {
        return excursions;
    }

    public List<LandProgramModel> getMid() {
        return mid;
    }

    public Boolean getIsEmbark() {
        return isEmbark;
    }

    public Boolean getIsDebark() {
        return isDebark;
    }

    public List<String> getShorex() {
        return shorex;
    }
}
