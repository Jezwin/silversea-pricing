package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.beans.MediaDataLayer;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.override.ExternalizerSSC;
import com.silversea.aem.services.GeolocationTagService;
import com.silversea.aem.services.RunModesService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.silversea.aem.constants.WcmConstants.*;

public class DataLayerUse extends WCMUsePojo {

    private String country;

    private String runMode = "";
    private String event = "";

    private String currentPageUrl = "";
    private String userCountry;

    private String userLanguage = "";
    private String pageCategory1 = "";
    private String pageCategory2 = "";
    private String pageCategory3 = "";
    private String destinationId = "";
    private String shipId = "";
    private String destinationName = "";
    private String voyageId = "";
    private String departureDay = "";
    private String voyageDuration = "";
    private String voyageDepartureHarbor = "";
    private String voyageArrivalHarbor = "";
    private String voyageType = "";
    private String shipName = "";
    private String revenue = "";
    private String geomarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;
    private MediaDataLayer media;


    private static final List<String> HOME = Collections.singletonList(RT_LANDING_PAGE);
    private static final List<String> CRUISES = Arrays.asList(RT_CRUISE, RT_COMBO_CRUISE);
    private static final List<String> RESULTS = Arrays.asList(RT_DESTINATION, RT_EXCLUSIVE_OFFER, RT_SHIP, RT_PORT);

    // TODO review exception
    @Override
    public void activate() throws Exception {
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final RunModesService runModesService = getSlingScriptHelper().getService(RunModesService.class);

        // TODO create geo sensitive use pojo
        // init geolocations informations
        String currency = WcmConstants.DEFAULT_CURRENCY;

        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(
                GeolocationTagService.class);

        if (geolocationTagService != null) {
            final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromRequest(
                    getRequest());

            if (geolocationTagModel != null) {
                geomarket = geolocationTagModel.getMarket();
                currency = geolocationTagModel.getCurrency();
            }
        }

        // media
        country = GeolocationHelper.getCountryCode(getRequest());
        if (country != null) {
            geomarket = GeolocationHelper.getGeoMarket(tagManager, country);
            geomarket = geomarket != null ? geomarket.toUpperCase() : "FT";
        } else {
            country = "US";
            geomarket = "FT";
        }

        // Environment data
        if (runModesService != null) {
            runMode = runModesService.getCurrentRunMode();
        }

        final Resource contentResource = getCurrentPage().getContentResource();
        ValueMap valueMap = contentResource.getValueMap();
        final String type = (String) valueMap.getOrDefault("sling:resourceType", "");

        if (getPageProperties().get("pageEvent") != null) {
            event = getPageProperties().get("pageEvent", String.class);
        } else if (RESULTS.contains(type)) {
            event = "searchresults";
        } else if (CRUISES.contains(type)) {
            event = "offerdetail";
        } else if (HOME.contains(type)) {
            event = "home";
        } else {
            event = "other";
        }

        // users data
        final Locale locale = getCurrentPage().getLanguage(false);
        userLanguage = locale.getLanguage();

        userCountry = GeolocationHelper.getCountryCode(getRequest());
        if (userCountry == null) {
            userCountry = "US";
        }


        currentPageUrl = ExternalizerSSC.publishLink(getResourceResolver(),
                    getCurrentPage().getPath()) + ".html";


        // tree structure data
        if (getPageProperties().get("pageCategory1") != null) {
            pageCategory1 = getPageProperties().get("pageCategory1", String.class);
        }

        if (getPageProperties().get("pageCategory2") != null) {
            pageCategory2 = getPageProperties().get("pageCategory2", String.class);
        }

        if (getPageProperties().get("pageCategory3") != null) {
            pageCategory3 = getPageProperties().get("pageCategory3", String.class);
        }

        // TODO move to constants interface
        final Map<String, String> listCat1 = new HashMap<>();
        listCat1.put(WcmConstants.RT_VOYAGE, "voyages");
        listCat1.put(RT_EXCLUSIVE_OFFER, "single exclusive offer");
        listCat1.put(RT_DESTINATION, "destinations");
        listCat1.put(WcmConstants.RT_SUITE, "single accommodation");
        listCat1.put(WcmConstants.RT_SUITE_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_EXCURSIONS, "single excursion");
        listCat1.put(RT_SHIP, "single ship");
        listCat1.put(WcmConstants.RT_DINING, "single onboard");
        listCat1.put(WcmConstants.RT_PUBLIC_AREA, "single public areas");
        listCat1.put(WcmConstants.RT_VOYAGE_JOURNAL, "voyage journals");
        listCat1.put(WcmConstants.RT_PRESS_RELEASE, "press releases");
        listCat1.put(WcmConstants.RT_FEATURE, "single onboard");
        listCat1.put(RT_PORT, "single port");
        listCat1.put(WcmConstants.RT_BLOG_POST, "blog");
        listCat1.put(WcmConstants.RT_KEY_PEOPLE, "single onboard");
        listCat1.put(WcmConstants.RT_VOYAGE_JOURNAL_DAY, "voyage journals");
        listCat1.put(WcmConstants.RT_PUBLIC_AREA_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_LAND_PROGRAMS, "single land programmes");
        listCat1.put(WcmConstants.RT_DINING_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_PAGE, "editorial pages");

        String comboTag = null;
        if (RT_COMBO_CRUISE.equals(type)) {
            if (tagManager != null) {
                final Tag[] listTag = tagManager.getTags(contentResource);

                for (Tag tag : listTag) {
                    if (tag != null && tag.getNamespace().getPath().equals("/etc/tags/combo-cruise-types")) {
                        comboTag = tag.getName();
                    }
                }

                if (comboTag != null) {
                    listCat1.put(RT_COMBO_CRUISE, comboTag);
                } else {
                    listCat1.put(RT_COMBO_CRUISE, "");
                }
            }
        }

        if (pageCategory1.equals("")) {
            final String value = listCat1.get(contentResource.getResourceType());

            if (value != null) {
                pageCategory1 = value;
            } else {
                pageCategory1 = StringUtils
                        .substringAfterLast(getPageProperties().get("cq:template", String.class), "/");
            }
        }

        // CAT2
        if (WcmConstants.RT_KEY_PEOPLE.equals(type)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = "enrichments";
            }

            if (pageCategory3.equals("")) {
                if (tagManager != null) {
                    final Tag[] tags = tagManager.getTags(contentResource);

                    for (Tag tag : tags) {
                        if (tag.getNamespace().getPath().equals("/etc/tags/key-people")) {
                            pageCategory3 = tag.getName();
                        }
                    }
                }
            }
        } else if (WcmConstants.RT_SUITE_VARIATION.equals(type)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "suites";
            }
        } else if (WcmConstants.RT_PUBLIC_AREA_VARIATION.equals(type)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "public areas";
            }
        } else if (WcmConstants.RT_DINING_VARIATION.equals(type)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "dining";
            }
        }

        String name = getCurrentPage().getName();
        if (pageCategory2.equals("") && name != null) {
            pageCategory2 = name;
        }

        if (WcmConstants.RT_PAGE.equals(type)) {
            if (pageCategory3.equals("")) {
                pageCategory3 = getCurrentPage().getParent().getName();
            }
        }

        //Destination - fill track_destination
        if (RT_DESTINATION.equals(type)) {
            destinationId = (String) valueMap.get("destinationId");
            destinationName = name;
        }

        //Ship Fill Ship
        if (RT_SHIP.equals(type)) {
            shipName = name;
            shipId = (String) valueMap.get("shipId");
        }

        // Cruise details
        if (WcmConstants.RT_VOYAGE.equals(type)) {
            final CruiseModel cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
            if (cruiseModel != null) {
                destinationId = cruiseModel.getDestination().getDestinationId().toString();
                destinationName = cruiseModel.getDestination().getName();

                // TODO check value, cruise code != cruise id
                voyageId = cruiseModel.getCruiseCode();

                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                departureDay = dt1.format(cruiseModel.getStartDate().getTime());
                voyageDuration = cruiseModel.getDuration();

                voyageDepartureHarbor = cruiseModel.getDeparturePortName();
                voyageArrivalHarbor = cruiseModel.getArrivalPortName();
                voyageType = cruiseModel.getCruiseType();

                shipName = cruiseModel.getShip().getName();
                shipId = cruiseModel.getShip().getShipId();

                // init lowest price and waitlist based on geolocation
                PriceModel lowestPrice = null;
                for (PriceModel priceModel : cruiseModel.getPrices()) {
                    if (priceModel.getGeomarket() != null
                            && priceModel.getGeomarket().equals(geomarket)
                            && priceModel.getCurrency().equals(currency)) {
                        // Init lowest price
                        if (lowestPrice == null) {
                            lowestPrice = priceModel;
                        } else if (lowestPrice.getPrice() > priceModel.getPrice()) {
                            lowestPrice = priceModel;
                        }
                    }
                }

                if (lowestPrice != null) {
                    Long price = lowestPrice.getPrice();
                    revenue = currency + price;
                }
            }
        }

        String adwordsConversionLabel = "";
        String adwordsValue = "1.00";
        String adwordsFormat = "";
        if (geomarket.equals("US") || (geomarket.equals("FT") && (country.equals("US") || country.equals("CA")))) {
            switch (pageCategory2) {
                case "RAQ TY":
                    adwordsConversionLabel = "XXW_CPmImQQQl5v74wM";
                    adwordsFormat = "2";
                    break;
                case "RAC TY":
                    adwordsConversionLabel = "4ekHCIGahAQQl5v74wM";
                    adwordsFormat = "2";
                    break;
                case "RAB TY":
                    adwordsConversionLabel = "kakUCIDVllcQl5v74wM";
                    adwordsFormat = "2";
                    break;
                case "SFO TY":
                    adwordsConversionLabel = "kdt4CPGbhAQQl5v74wM";
                    adwordsFormat = "2";
                    break;
                case "Send E-mail TY":
                    adwordsConversionLabel = "tgx1COGdhAQQl5v74wM";
                    adwordsFormat = "2";
                    break;
            }

            media = new MediaDataLayer("US", "US", "337dc751", "1014943127", adwordsConversionLabel, adwordsFormat,
                    adwordsValue, "1014943127", "6sX6CLfmsFwQl5v74wM", "1014943127", "GSvQCJnls1wQl5v74wM",
                    "1000698659832", "39634");
        }
        if (geomarket.equals("LAM") || (geomarket.equals("FT") && (!country.equals("US") && !country.equals("CA")))) {
            switch (pageCategory2) {
                case "RAQ TY":
                    adwordsConversionLabel = "2ro7CO-klggQ2cHp1QM";
                    adwordsFormat = "3";
                    break;
                case "RAC TY":
                    adwordsConversionLabel = "LUfyCNenlggQ2cHp1QM";
                    adwordsFormat = "3";
                    break;
                case "RAB TY":
                    adwordsConversionLabel = "eYA8CO-pllcQ2cHp1QM";
                    adwordsFormat = "2";
                    break;
                case "SFO TY":
                    adwordsConversionLabel = "LKy0CP-ilggQ2cHp1QM";
                    adwordsFormat = "3";
                    break;
                case "Send E-mail TY":
                    adwordsConversionLabel = "-YN5COellggQ2cHp1QM";
                    adwordsFormat = "3";
                    break;
            }

            media = new MediaDataLayer("LAM", "LAM", "337dc751", "985293017", adwordsConversionLabel, adwordsFormat,
                    adwordsValue, "985293017", "19tjCL3os1wQ2cHp1QM", "985293017", "c5paCPzos1wQ2cHp1QM",
                    "1000698659832", "39634");
        }

        if (geomarket.equals("AP") || geomarket.equals("AS")) {
            switch (pageCategory2) {
                case "RAQ TY":
                    adwordsConversionLabel = "7HNzCNzvkQgQ1MDT0AM";
                    adwordsFormat = "3";
                    break;
                case "RAC TY":
                    adwordsConversionLabel = "OW6VCMTykQgQ1MDT0AM";
                    adwordsFormat = "3";
                    break;
                case "RAB TY":
                    adwordsConversionLabel = "GiVYCNv4lVcQ1MDT0AM";
                    adwordsFormat = "2";
                    break;
                case "SFO TY":
                    adwordsConversionLabel = "htQZCOztkQgQ1MDT0AM";
                    adwordsFormat = "3";
                    break;
                case "Send E-mail TY":
                    adwordsConversionLabel = "8OO5CMzxkQgQ1MDT0AM";
                    adwordsFormat = "3";
                    break;
            }

            media = new MediaDataLayer("AP", "AP", "337dc751", "974446676", adwordsConversionLabel, adwordsFormat,
                    adwordsValue, "974446676", "4DSFCNLmsFwQ1MDT0AM", "974446676", "2n1oCOrpsFwQ1MDT0AM",
                    "1000698659832", "39634");
        }

        if (geomarket.equals("UK")) {
            switch (pageCategory2) {
                case "RAQ TY":
                    adwordsConversionLabel = "htdlCLDd2iMQgL_7yAM";
                    adwordsFormat = "3";
                    break;
                case "RAC TY":
                    adwordsConversionLabel = "EcqGCIjAxSEQgL_7yAM";
                    adwordsFormat = "3";
                    break;
                case "RAB TY":
                    adwordsConversionLabel = "f4dGCLz6lVcQgL_7yAM";
                    adwordsFormat = "2";
                    break;
                case "SFO TY":
                    adwordsConversionLabel = "CkrCCPjBxSEQgL_7yAM";
                    adwordsFormat = "3";
                    break;
                case "Send E-mail TY":
                    adwordsConversionLabel = "80vfCIDBxSEQgL_7yAM";
                    adwordsFormat = "3";
                    break;
            }

            media = new MediaDataLayer("UK", "UK", "337dc751", "958324608", adwordsConversionLabel, adwordsFormat,
                    adwordsValue, "958324608", "I_N0CIiOsFwQgL_7yAM", "958324608", "RzaaCPWMsFwQgL_7yAM",
                    "1000698659832", "39634");
        }

        if (geomarket.equals("EMEA") || geomarket.equals("EU")) {
            switch (pageCategory2.toUpperCase()) {
                case "RAQ TY":
                    adwordsConversionLabel = "81HGCJzThggQzILD0AM";
                    adwordsFormat = "3";
                    break;
                case "RAC TY":
                    adwordsConversionLabel = "Fl7fCPTXhggQzILD0AM";
                    adwordsFormat = "3";
                    break;
                case "RAB TY":
                    adwordsConversionLabel = "Pr99CPn0llcQzILD0AM";
                    adwordsFormat = "2";
                    break;
                case "SFO TY":
                    adwordsConversionLabel = "Id1aCKzRhggQzILD0AM";
                    adwordsFormat = "3";
                    break;
                case "Send E-mail TY":
                    adwordsConversionLabel = "We5mCITWhggQzILD0AM";
                    adwordsFormat = "3";
                    break;
            }

            media = new MediaDataLayer("EMEA", "EMEA", "337dc751", "974176588", adwordsConversionLabel,
                    adwordsFormat, adwordsValue, "974176588", "uPZUCPPpsFwQzILD0AM", "974176588",
                    "Z58tCPSRsFwQzILD0AM", "1000698659832", "39634");
        }

    }

    public String getContry() {
        return country;
    }

    public String getGeoLoc() {
        return geomarket;
    }

    public String getUserContry() {
        return userCountry;
    }

    public String getRunMode() {
        return runMode;
    }

    public String getEvent() {
        return event;
    }

    public String getPageCategory1() {
        return pageCategory1;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    /**
     * TODO check
     *
     * @return
     */
    public String getUserEmail() {
        return "";
    }

    public String getPageCategory2() {
        return pageCategory2;
    }

    public String getPageCategory3() {
        return pageCategory3;
    }

    public MediaDataLayer getMedia() {
        return media;
    }

    public String getCurrentPageUrl() {
        return currentPageUrl;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getShipId() {
        return shipId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getVoyageId() {
        return voyageId;
    }

    public String getDepartureDay() {
        return departureDay;
    }

    public String getVoyageDuration() {
        return voyageDuration;
    }

    public String getVoyageDepartureHarbor() {
        return voyageDepartureHarbor;
    }

    public String getVoyageArrivalHarbor() {
        return voyageArrivalHarbor;
    }

    public String getVoyageType() {
        return voyageType;
    }

    public String getShipName() {
        return shipName;
    }

    public String getRevenue() {
        return revenue;
    }

}