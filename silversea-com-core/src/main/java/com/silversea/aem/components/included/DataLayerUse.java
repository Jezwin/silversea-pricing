package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.components.beans.MediaDataLayer;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.services.GeolocationService;
import com.silversea.aem.services.RunModesService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by mbennabi on 20/04/2017.
 */
public class DataLayerUse extends WCMUsePojo {

    private Map<String, String> listCat1;

    // TODO typo
    private String contry;

    private String runMode = "";
    private String event = "";
    private String userEmail = "";
    private String currentPageUrl = "";

    // TODO typo
    private String userContry;

    private String userLanguage = "";
    private String pageCategory1 = "";
    private String pageCategory2 = "";
    private String pageCategory3 = "";
    private String destinationId = "";
    private String destinationName = "";
    private String voyageId = "";
    private String departureDay = "";
    private String voyageDuration = "";
    private String voyageDepartureHarbor = "";
    private String voyageArrivalHarbor = "";
    private String voyageType = "";
    private String shipName = "";
    private String revenue = "";
    private String geoLoc;
    private MediaDataLayer media;

    // TODO review exception
    @Override
    public void activate() throws Exception {
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);

        // Environment data
        RunModesService run = getSlingScriptHelper().getService(RunModesService.class);
        runMode = run.getCurrentRunMode();

        final Resource contentResource = getCurrentPage().getContentResource();
        final ValueMap pageProperties = getCurrentPage().getProperties();

        if (pageProperties.get("pageEvent") != null) {
            event = pageProperties.get("pageEvent", String.class);
        }

        if (contentResource.isResourceType(WcmConstants.RT_EXCLUSIVE_OFFER) && event.equals("")) {
            event = "searchresults";
        }

        if (contentResource.isResourceType(WcmConstants.RT_DESTINATION) && event.equals("")) {
            event = "offerdetail";
        }

        // users data
        final Locale locale = getCurrentPage().getLanguage(false);
        userLanguage = locale.getLanguage();

        userContry = GeolocationHelper.getCountryCode(getRequest());
        if (userContry == null) {
            userContry = "US";
        }

        currentPageUrl = externalizer.publishLink(getResourceResolver(), "http", getCurrentPage().getPath()) + ".html";

        // tree structure data
        if (pageProperties.get("pageCategory1") != null) {
            pageCategory1 = pageProperties.get("pageCategory1", String.class);
        }

        if (pageProperties.get("pageCategory2") != null) {
            pageCategory2 = pageProperties.get("pageCategory2", String.class);
        }

        if (pageProperties.get("pageCategory3") != null) {
            pageCategory3 = pageProperties.get("pageCategory3", String.class);
        }

        listCat1 = new HashMap<>();
        listCat1.put(WcmConstants.RT_VOYAGE, "voyages");
        listCat1.put(WcmConstants.RT_EXCLUSIVE_OFFER, "single exclusive offer");
        listCat1.put(WcmConstants.RT_DESTINATION, "destinations");
        listCat1.put(WcmConstants.RT_SUITE, "single accommodation");
        listCat1.put(WcmConstants.RT_SUITE_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_EXCURSIONS, "single excursion");
        listCat1.put(WcmConstants.RT_SHIP, "single ship");
        listCat1.put(WcmConstants.RT_DINING, "single onboard");
        listCat1.put(WcmConstants.RT_PUBLIC_AREA, "single public areas");
        listCat1.put(WcmConstants.RT_VOYAGE_JOURNAL, "voyage journals");
        listCat1.put(WcmConstants.RT_PRESS_RELEASE, "press releases");
        listCat1.put(WcmConstants.RT_FEATURE, "single onboard");
        listCat1.put(WcmConstants.RT_PORT, "single port");
        listCat1.put(WcmConstants.RT_BLOG_POST, "blog");
        listCat1.put(WcmConstants.RT_KEY_PEPOLE, "single onboard");
        listCat1.put(WcmConstants.RT_VOYAGE_JOURNAL_DAY, "voyage journals");
        listCat1.put(WcmConstants.RT_PUBLIC_AREA_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_LAND_PROGRAMS, "single land programmes");
        listCat1.put(WcmConstants.RT_DINING_VARIATION, "single ship");
        listCat1.put(WcmConstants.RT_PAGE, "editorial pages");

        String comboTag = null;
        if (contentResource.isResourceType(WcmConstants.RT_COMBO_CRUISE)) {
            final Tag[] listTag = tagManager.getTags(contentResource);

            for (Tag tag : listTag) {
                if (tag != null && tag.getNamespace().getPath().equals("/etc/tags/combo-cruise-types")) {
                    comboTag = tag.getName();
                }
            }

            if (comboTag != null) {
                listCat1.put(WcmConstants.RT_COMBO_CRUISE, comboTag);
            } else {
                listCat1.put(WcmConstants.RT_COMBO_CRUISE, "");
            }

        }

        if (pageCategory1.equals("")) {
            String value = listCat1.get(contentResource.getResourceType());

            if (value != null) {
                pageCategory1 = value;
            } else {
                pageCategory1 = StringUtils
                        .substringAfterLast(pageProperties.get("cq:template", String.class), "/");
            }
        }

        // CAT2
        if (contentResource.isResourceType(WcmConstants.RT_KEY_PEPOLE)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = "enrichments";
            }

            if (pageCategory3.equals("")) {
                Resource res = contentResource;
                Tag[] tags = tagManager.getTags(res);
                for (Tag tag : tags) {
                    if (tag != null && "/etc/tags/key-people".equals(Objects.toString(tag.getNamespace()))) {
                        pageCategory3 = tag.getName();
                    }
                }
            }
        }

        if (contentResource.isResourceType(WcmConstants.RT_SUITE_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "suites";
            }
        }

        if (contentResource.isResourceType(WcmConstants.RT_PUBLIC_AREA_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "public areas";
            }
        }

        if (contentResource.isResourceType(WcmConstants.RT_DINING_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getName();
            }

            if (pageCategory3.equals("")) {
                pageCategory3 = "dining";
            }
        }

        if (pageCategory2.equals("") && getCurrentPage().getName() != null) {
            pageCategory2 = getCurrentPage().getName();
        }

        if (contentResource.isResourceType(WcmConstants.RT_PAGE)) {
            if (pageCategory3.equals("")) {
                pageCategory3 = getCurrentPage().getParent().getName();
            }
        }

        // Cruise details
        if (contentResource.isResourceType(WcmConstants.RT_VOYAGE)) {
            CruiseModel cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
            GeolocationService geolocationService = getSlingScriptHelper().getService(GeolocationService.class);

            // TODO replace by com.silversea.aem.services.GeolocationTagService
            GeoLocation geoLocation = geolocationService.initGeolocation(getRequest());
            //cruiseModel.initByGeoLocation(geoLocation);

            destinationId = pageProperties.get("cmp-destinationId", String.class);
            destinationName = getCurrentPage().getParent().getName();

            voyageId = pageProperties.get("cruiseCode", String.class);

            departureDay = cruiseModel.getStartDate().getTime().toString();
            voyageDuration = cruiseModel.getDuration();

            // TODO review
            //voyageDepartureHarbor = cruiseModel.getDeparturePortName();
            //voyageArrivalHarbor = cruiseModel.getArrivalPortName();
            voyageType = cruiseModel.getCruiseType().getName();

            shipName = StringUtils
                    .substringAfterLast(pageProperties.get("shipReference", String.class), "/");

            // TODO review
            /*String price = cruiseModel.getLowestPrice().getValue();
            String currency = cruiseModel.getLowestPrice().getCurrency();

            if (price != null && currency != null) {
                revenue = currency + price;
            }*/

        }

        // media
        contry = GeolocationHelper.getCountryCode(getRequest());
        if (contry != null) {
            geoLoc = GeolocationHelper.getGeoMarket(tagManager, contry);
        } else {
            contry = "US";
        }

        if (geoLoc != null) {
            geoLoc = geoLoc.toUpperCase();
        } else {
            geoLoc = "FT";
        }

        // TODO apply java naming conventions
        String adwords_conversion_label = "";
        String adwords_value = "1.00";
        String adwords_format = "";
        if (geoLoc.equals("US") || (geoLoc.equals("FT") && (contry.equals("US") || contry.equals("CA")))) {
            if (pageCategory2.equals("RAQ TY")) {
                adwords_conversion_label = "XXW_CPmImQQQl5v74wM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("RAC TY")) {
                adwords_conversion_label = "4ekHCIGahAQQl5v74wM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("RAB TY")) {
                adwords_conversion_label = "kakUCIDVllcQl5v74wM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("SFO TY")) {
                adwords_conversion_label = "tgx1COGdhAQQl5v74wM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("Send E-mail TY")) {
                adwords_conversion_label = "kdt4CPGbhAQQl5v74wM";
                adwords_format = "2";
            }

            media = new MediaDataLayer("US", "US", "337dc751", "1014943127", adwords_conversion_label, adwords_format,
                    adwords_value, "1014943127", "6sX6CLfmsFwQl5v74wM", "1014943127", "GSvQCJnls1wQl5v74wM",
                    "1000698659832", "39634");
        }

        if (geoLoc.equals("LAM") || (geoLoc.equals("FT") && (!contry.equals("US") && !contry.equals("CA")))) {
            if (pageCategory2.equals("RAQ TY")) {
                adwords_conversion_label = "2ro7CO-klggQ2cHp1QM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAC TY")) {
                adwords_conversion_label = "LUfyCNenlggQ2cHp1QM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAB TY")) {
                adwords_conversion_label = "eYA8CO-pllcQ2cHp1QM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("SFO TY")) {
                adwords_conversion_label = "-YN5COellggQ2cHp1QM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("Send E-mail TY")) {
                adwords_conversion_label = "LKy0CP-ilggQ2cHp1QM";
                adwords_format = "3";
            }

            media = new MediaDataLayer("LAM", "LAM", "337dc751", "985293017", adwords_conversion_label, adwords_format,
                    adwords_value, "985293017", "19tjCL3os1wQ2cHp1QM", "985293017", "c5paCPzos1wQ2cHp1QM",
                    "1000698659832", "39634");
        }

        if (geoLoc.equals("AP") || geoLoc.equals("AS")) {
            if (pageCategory2.equals("RAQ TY")) {
                adwords_conversion_label = "7HNzCNzvkQgQ1MDT0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAC TY")) {
                adwords_conversion_label = "OW6VCMTykQgQ1MDT0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAB TY")) {
                adwords_conversion_label = "GiVYCNv4lVcQ1MDT0AM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("SFO TY")) {
                adwords_conversion_label = "8OO5CMzxkQgQ1MDT0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("Send E-mail TY")) {
                adwords_conversion_label = "htQZCOztkQgQ1MDT0AM";
                adwords_format = "3";
            }

            media = new MediaDataLayer("AP", "AP", "337dc751", "974446676", adwords_conversion_label, adwords_format,
                    adwords_value, "974446676", "4DSFCNLmsFwQ1MDT0AM", "974446676", "2n1oCOrpsFwQ1MDT0AM",
                    "1000698659832", "39634");
        }

        if (geoLoc.equals("UK")) {
            if (pageCategory2.equals("RAQ TY")) {
                adwords_conversion_label = "htdlCLDd2iMQgL_7yAM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAC TY")) {
                adwords_conversion_label = "EcqGCIjAxSEQgL_7yAM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAB TY")) {
                adwords_conversion_label = "f4dGCLz6lVcQgL_7yAM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("SFO TY")) {
                adwords_conversion_label = "80vfCIDBxSEQgL_7yAM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("Send E-mail TY")) {
                adwords_conversion_label = "CkrCCPjBxSEQgL_7yAM";
                adwords_format = "3";
            }

            media = new MediaDataLayer("UK", "UK", "337dc751", "958324608", adwords_conversion_label, adwords_format,
                    adwords_value, "958324608", "I_N0CIiOsFwQgL_7yAM", "958324608", "RzaaCPWMsFwQgL_7yAM",
                    "1000698659832", "39634");
        }

        if (geoLoc.equals("EMEA") || geoLoc.equals("EU")) {
            if (pageCategory2.toUpperCase().equals("RAQ TY")) {
                adwords_conversion_label = "81HGCJzThggQzILD0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAC TY")) {
                adwords_conversion_label = "Fl7fCPTXhggQzILD0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("RAB TY")) {
                adwords_conversion_label = "Pr99CPn0llcQzILD0AM";
                adwords_format = "2";
            }

            if (pageCategory2.equals("SFO TY")) {
                adwords_conversion_label = "We5mCITWhggQzILD0AM";
                adwords_format = "3";
            }

            if (pageCategory2.equals("Send E-mail TY")) {
                adwords_conversion_label = "Id1aCKzRhggQzILD0AM";
                adwords_format = "3";
            }

            media = new MediaDataLayer("EMEA", "EMEA", "337dc751", "974176588", adwords_conversion_label,
                    adwords_format, adwords_value, "974176588", "uPZUCPPpsFwQzILD0AM", "974176588",
                    "Z58tCPSRsFwQzILD0AM", "1000698659832", "39634");
        }

    }

    public String getContry() {
        return contry;
    }

    public String getGeoLoc() {
        return geoLoc;
    }

    public String getUserContry() {
        return userContry;
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

    public String getUserEmail() {
        return userEmail;
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