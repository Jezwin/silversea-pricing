package com.silversea.aem.components.included;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.beans.MediaDataLayer;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.services.RunModesService;

/**
 * Created by mbennabi on 20/04/2017.
 */
public class DataLayerUse extends WCMUsePojo {

    private String runMode = "";
    private String event = "";

    private String userEmail = "";

    private String userLanguage = "";
    private String pageCategory1 = "";
    private String pageCategory2 = "";
    private String pageCategory3 = "";
    Map<String, String> listCat1;
    Map<String, String> listCat2;

    private String geoLoc;
    private MediaDataLayer media;

    @Override
    public void activate() throws Exception {
        /**
         * Environement data
         */
        RunModesService run = getSlingScriptHelper().getService(RunModesService.class);
        runMode = run.getCurrentRunMode();
        if (getCurrentPage().getProperties().get("event") != null
                && getCurrentPage().getProperties().get("event").toString().equals("true")
                && getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_PAGE)) {
            event = getCurrentPage().getProperties().get("eventValue").toString();
        }

        /**
         * users data
         */
        Locale locale = getCurrentPage().getLanguage(false);
        userLanguage = locale.getLanguage();
        // if (getRequest().getCookie("email").getValue() != null) {
        // userEmail = getRequest().getCookie("email").getValue();
        // }

        /**
         * tree structure data
         */
        if (getCurrentPage().getProperties().get("pageCategory1") != null)
            pageCategory1 = getCurrentPage().getProperties().get("pageCategory1").toString();
        if (getCurrentPage().getProperties().get("pageCategory2") != null)
            pageCategory2 = getCurrentPage().getProperties().get("pageCategory2").toString();
        if (getCurrentPage().getProperties().get("pageCategory3") != null)
            pageCategory3 = getCurrentPage().getProperties().get("pageCategory3").toString();

        listCat1 = new HashMap<>();
        listCat1.put(TemplateConstants.PATH_VOYAGE, "voyages");
        listCat1.put(TemplateConstants.PATH_EXCLUSIVE_OFFERT, "single exclusive offer");
        listCat1.put(TemplateConstants.PATH_DESTINATION, "destinations");
        listCat1.put(TemplateConstants.PATH_SUITE, "single accommodation");
        listCat1.put(TemplateConstants.PATH_SUITE_VARIATION, "single ship");
        listCat1.put(TemplateConstants.PATH_EXCURSION, "single excursion");
        listCat1.put(TemplateConstants.PATH_SHIP, "single ship");
        listCat1.put(TemplateConstants.PATH_DINING, "single onboard");
        listCat1.put(TemplateConstants.PATH_PUBLIC_AREA, "single public areas");
        listCat1.put(TemplateConstants.PATH_VOYAGE_JOURNAL, "voyage journals");
        listCat1.put(TemplateConstants.PATH_PRESS_RELEASE, "press releases");
        listCat1.put(TemplateConstants.PATH_FEATURE, "single onboard");
        listCat1.put(TemplateConstants.PATH_PORT, "single port");
        listCat1.put(TemplateConstants.PATH_BLOG_POST, "blog");
        listCat1.put(TemplateConstants.PATH_KEY_PEPOLE, "single onboard");
        listCat1.put(TemplateConstants.PATH_VOYAGE_JOURNAL_DAY, "voyage journals");
        listCat1.put(TemplateConstants.PATH_PUBLIC_AREA_VARIATION, "single ship");
        listCat1.put(TemplateConstants.PATH_LANDPROGRAM, "single land programmes");
        listCat1.put(TemplateConstants.PATH_DINING_VARIATION, "single ship");
        listCat1.put(TemplateConstants.PATH_PAGE, "editorial pages");

        if (getCurrentPage().getTemplate().getPath() != null && pageCategory1.equals("")) {
            String value = listCat1.get(getCurrentPage().getTemplate().getPath());
            if (value != null) {
                pageCategory1 = value;
            }
        }
        if (pageCategory1.equals("")) {
            pageCategory1 = getCurrentPage().getTemplate().getName();
        }

        // CAT2
        if (getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_KEY_PEPOLE)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = "enrichments";
            }
        }
        if (getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_SUITE_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getProperties().get("jcr:title", String.class);
            }
            if (pageCategory3.equals("")) {
                pageCategory3 = "suites";
            }
        }
        if (getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_PUBLIC_AREA_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getProperties().get("jcr:title", String.class);
            }
            if (pageCategory3.equals("")) {
                pageCategory3 = "public areas";
            }
        }
        if (getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_DINING_VARIATION)) {
            if (pageCategory2.equals("")) {
                pageCategory2 = getCurrentPage().getParent(2).getProperties().get("jcr:title", String.class);
            }
            if (pageCategory3.equals("")) {
                pageCategory3 = "dining";
            }
        }
        if (pageCategory2.equals("") && getCurrentPage().getName() != null) {
            pageCategory2 = getCurrentPage().getName();
        }
        /**
         * media
         */
        // TODO récuperer la bonne market
        geoLoc = "UK";
        if (geoLoc.equals("US")) {
            media = new MediaDataLayer("US", "US", "337dc751", "1014943127", "adwords_conversion_label", 
                    "adwords_format", "adwords_value", "1014943127", "6sX6CLfmsFwQl5v74wM", "1014943127", "GSvQCJnls1wQl5v74wM", "1000698659832", "39634");
        }
        if (geoLoc.equals("LAM")) {
            media = new MediaDataLayer("LAM", "LAM", "337dc751", "985293017", "adwords_conversion_label", 
                    "adwords_format", "adwords_value", "985293017", "19tjCL3os1wQ2cHp1QM", "985293017", "c5paCPzos1wQ2cHp1QM", "1000698659832", "39634");
        }
        if (geoLoc.equals("AP")) {
            media = new MediaDataLayer("AP","AP", "337dc751", "974446676", "adwords_conversion_label", 
                    "adwords_format", "adwords_value", "974446676", "4DSFCNLmsFwQ1MDT0AM", "974446676", "2n1oCOrpsFwQ1MDT0AM", "1000698659832", "39634");
        }
        if (geoLoc.equals("UK")) {
            media = new MediaDataLayer("UK","UK", "337dc751","958324608" , "adwords_conversion_label", 
                    "adwords_format", "adwords_value", "958324608", "I_N0CIiOsFwQgL_7yAM", "958324608", "RzaaCPWMsFwQgL_7yAM", "1000698659832", "39634");
        }
        if (geoLoc.equals("EMEA")) {
            media = new MediaDataLayer("EMEA","EMEA", "337dc751", "974176588", "adwords_conversion_label", 
                    "adwords_format", "adwords_value", "974176588", "uPZUCPPpsFwQzILD0AM", "974176588", "Z58tCPSRsFwQzILD0AM", "1000698659832", "39634");
        }

    }

    /**
     * @return the current Run Mode
     */

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

}