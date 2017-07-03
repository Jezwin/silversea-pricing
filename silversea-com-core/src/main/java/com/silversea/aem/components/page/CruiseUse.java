package com.silversea.aem.components.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.DiningModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PublicAreaModel;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.services.GeolocationTagService;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.PathUtils;

public class CruiseUse extends WCMUsePojo {

    // TODO : to change US AND FT by default
    private static final String DEFAULT_GEOLOCATION_COUTRY = "FR";
    private static final String DEFAULT_GEOLOCATION_GEO_MARKET_CODE = "EU";

    private GeolocationTagService geolocationTagService;

    private CruiseModel cruiseModel;
    private String previous;
    private String next;

    private TagManager tagManager;

    @Override
    public void activate() throws Exception {
        tagManager = getResourceResolver().adaptTo(TagManager.class);
        geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
        GeoLocation geoLocation = initGeolocation(geolocationTagService);
        cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
        cruiseModel.initByGeoLocation(geoLocation);
        initPagination();
    }

    private GeoLocation initGeolocation(GeolocationTagService geolocationTagService) {

        String tagId = geolocationTagService.getTagFromRequest(getRequest());
        String country = GeolocationHelper.getCountryCode(getRequest());
        String geoMarketCode = getGeoMarketCode(tagId);
        GeoLocation geoLocation = new GeoLocation();
        if (!StringUtils.isEmpty(country) && !StringUtils.isEmpty(geoMarketCode)) {
            geoLocation.setCountry(country);
            geoLocation.setGeoMarketCode(geoMarketCode.toUpperCase());
        } else {
            geoLocation.setCountry(DEFAULT_GEOLOCATION_COUTRY);
            geoLocation.setGeoMarketCode(DEFAULT_GEOLOCATION_GEO_MARKET_CODE);
        }
        return geoLocation;
    }

    private void initPagination() {
        Page currentPage = getCurrentPage();
        Iterator<Page> children = currentPage.getParent().listChildren();
        if (children != null && children.hasNext()) {
            while (children.hasNext()) {
                Page current = children.next();
                if (StringUtils.equals(current.getPath(), currentPage.getPath())) {
                    if (children.hasNext()) {
                        next = children.next().getPath();
                    }
                    break;
                }
                previous = current.getPath();
            }
        }
    }

    private String getGeoMarketCode(String geolocationTag) {
        String geoMarketCode = null;

        Tag tag = tagManager.resolve(geolocationTag);
        if (tag != null) {
            geoMarketCode = tag.getParent().getParent().getName();
        }
        return geoMarketCode;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    /**
     * @return cruise's model
     */
    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

    public List<Asset> getAllAssetForItinerary() {
        String assetSelectionReference;
        List<Asset> assetList = new ArrayList<Asset>();

        // Add asset from several list inside the same list
        if (cruiseModel.getItineraries() != null) {
            for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
                assetSelectionReference = itinerary.getPage().getProperties().get("assetSelectionReference", String.class);
                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    assetList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
                }
            }

            assetSelectionReference = cruiseModel.getAssetSelectionReference();
            if (StringUtils.isNotBlank(assetSelectionReference)) {
                assetList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
            }
        }

        return assetList;
    }

    public List<Asset> getAllAssetForSuite() {
        String assetSelectionReference;
        List<Asset> assetList = new ArrayList<Asset>();

        // Add asset from several list inside the same list
        if (cruiseModel.getSuites() != null) {
            for (SuiteModel suite : cruiseModel.getSuites()) {
                assetSelectionReference = suite.getPage().getProperties().get("assetSelectionReference", String.class);
                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    assetList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
                }
            }
        }

        return assetList;
    }

    public List<Asset> getAllAssetForDinning() {
        String assetSelectionReference;
        List<Asset> assetList = new ArrayList<Asset>();

        // Add asset from several list inside the same list
        if (cruiseModel.getShip().getDinings() != null) {
            for (DiningModel dinning : cruiseModel.getShip().getDinings()) {
                assetSelectionReference = dinning.getPage().getProperties().get("assetSelectionReference", String.class);
                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    assetList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
                }
            }
        }

        return assetList;
    }

    public List<Asset> getAllAssetForPublicArea() {
        String assetSelectionReference;
        List<Asset> assetList = new ArrayList<Asset>();

        // Add asset from several list inside the same list
        if (cruiseModel.getShip().getPublicAreas() != null) {
            for (PublicAreaModel publicArea : cruiseModel.getShip().getPublicAreas()) {
                assetSelectionReference = publicArea.getPage().getProperties().get("assetSelectionReference", String.class);
                if (StringUtils.isNotBlank(assetSelectionReference)) {
                    assetList.addAll(AssetUtils.buildAssetList(assetSelectionReference, getResourceResolver()));
                }
            }
        }

        return assetList;
    }

    public List<Asset> getAllAssetForDinningNPublicAreas() {
        if(getAllAssetForDinning() != null && getAllAssetForPublicArea() != null) {
            List<Asset> assetList = Stream.concat(getAllAssetForDinning().stream(), getAllAssetForPublicArea().stream()).collect(Collectors.toList());
            return assetList;
        }

        return null;
    }

    public LinkedHashMap<String, List<Asset>> getCruiseGallery() {
        LinkedHashMap<String, List<Asset>> gallery;
        gallery = new LinkedHashMap<String, List<Asset>>();

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
     * Return path for request quote page
     */
    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage().getLanguage(false));
    }
    
    public String getPageLanguage() {
        return getCurrentPage().getLanguage(false).getLanguage();
    }
    
    public String getDescription() {
        return StringUtils.isEmpty(cruiseModel.getDescription()) ? 
                cruiseModel.getImporteddescription() : cruiseModel.getDescription();
    }
}