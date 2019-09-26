package com.silversea.aem.reporting.models.write;

import com.day.cq.wcm.api.Page;
import com.google.common.collect.Iterators;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

public class CruiseDetailsBean {


    private final Integer cruiseId;
    private final String cruiseCode;
    private final String offer;
    private final Integer itineraryCount;
    private final String destinationId;
    private final String language;
    private final String bigItineraryMap;
    private final String bigThumbnailItineraryMap;
    private final String thumbnail;
    private final String importedDescription;
    private final String jcrDescription;
    private final String itineraries;
    private final Boolean isVisible;

    public CruiseDetailsBean(Resource resource) {
        ValueMap valueMap = resource.getValueMap();
        cruiseId = valueMap.get("cruiseId", Integer.class);
        cruiseCode = valueMap.get("cruiseCode", String.class);

        String[] offers = valueMap.get("offer", String[].class);
        if(offers != null) {
            offer = String.join(" ", offers);
        }
        else{
            offer = null;
        }
        jcrDescription = valueMap.get("jcr:description", String.class);
        importedDescription = valueMap.get("importedDescription", String.class);
        thumbnail = valueMap.get("thumbnail", String.class);
        bigItineraryMap = valueMap.get("bigItineraryMap", String.class);
        bigThumbnailItineraryMap = valueMap.get("bigThumbnailItineraryMap", String.class);
        itineraries = valueMap.get("itineraries", String.class);
        isVisible = valueMap.get("isVisible", Boolean.class);
        Resource child = resource.getChild("itineraries");
        if(child != null) {
            itineraryCount = Iterators.size(child.listChildren());
        }
        else {
            itineraryCount = 0;
        }
        Resource parent = resource.getParent().getParent();
        Page page = parent.adaptTo(Page.class);
        destinationId = page.getProperties().get("destinationId", String.class);
        Page languageAncestor = page.getAbsoluteParent(2);

        if (languageAncestor != null) {
            language = languageAncestor.getName();
        }
        else{
            language = "";
        }
    }

    public Integer getCruiseId() {
        return cruiseId;
    }

    public String getCruiseCode() {
        return cruiseCode;
    }

    public String getOffer() {
        return offer;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Integer getItineraryCount() {
        return itineraryCount;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getLanguage() {
        return language;
    }

    public String getItineraries() {
        return itineraries;
    }

    public String getJcrDescription() {
        return jcrDescription;
    }

    public String getImportedDescription() {
        return importedDescription;
    }

    public String getBigItineraryMap() {
        return bigItineraryMap;
    }

    public String getBigThumbnailItineraryMap() {
        return bigThumbnailItineraryMap;
    }

    public Boolean getVisible() {
        return isVisible;
    }
}
