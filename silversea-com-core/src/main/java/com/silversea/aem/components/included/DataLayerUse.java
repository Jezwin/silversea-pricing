package com.silversea.aem.components.included;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.services.RunModesService;

/**
 * Created by mbennabi on 20/04/2017.
 */
public class DataLayerUse extends WCMUsePojo {

    private String runMode = "";
    private String event = "";

    private String userEmail = "";
    public String getUserEmail() {
        return userEmail;
    }

    private String userLanguage = "";
    private String pageCategory1 = "";
    private String pageCategory2 = "";
    private String pageCategory3 = "";
    Map<String, String> listCat1;

    @Override
    public void activate() throws Exception {
        /**
         * Environement data
         */
        RunModesService run = getSlingScriptHelper().getService(RunModesService.class);
        runMode = run.getCurrentRunMode();
        if (getCurrentPage().getProperties().get("event").toString().equals("true")
                && getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_PAGE)) {
            event = getCurrentPage().getProperties().get("eventValue").toString();
        }
        /**
         * users data
         */
        Locale locale = getCurrentPage().getLanguage(false);
        userLanguage = locale.getLanguage();
        if (getRequest().getCookie("email").getValue() != null) {
            userEmail = getRequest().getCookie("email").getValue();
        }

        // Cookie[] c = getRequest().getCookies();
        // Cookie c2 = getRequest().getCookie("email");
        /**
         * tree structure data
         */
        listCat1 = new HashMap<>();
        listCat1.put(TemplateConstants.PATH_DESTINATION, "destinations");
        pageCategory1 = getCurrentPage().getProperties().get("pageCategory1").toString();
        pageCategory2 = getCurrentPage().getProperties().get("pageCategory2").toString();
        pageCategory3 = getCurrentPage().getProperties().get("pageCategory3").toString();

        // catégory of distinations
        if (getCurrentPage().getTemplate().getPath().equals(TemplateConstants.PATH_DESTINATION)) {
            if (pageCategory1.isEmpty()) {
                pageCategory1 = "destinations";
                // pageCategory1 = getCurrentPage().getTemplate().getName();
            }
            if (pageCategory2.isEmpty()) {
                pageCategory2 = getCurrentPage().getName();
            }
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

    public String getPageCategorie1() {
        return pageCategory1;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

}