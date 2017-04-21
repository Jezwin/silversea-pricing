package com.silversea.aem.components.included;

import java.util.Locale;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagConstants;
import com.silversea.aem.services.GeolocationTagService;
import com.silversea.aem.services.RunModesService;

/**
 * Created by mbennabi on 20/04/2017.
 */
public class DataLayerUse extends WCMUsePojo {

    private String runMode = "";
    private String userLanguage = "";
    private String pageCategorie1 = "";
    private String pageCategory2 = "";
    private String pageCategory3 = "";


    @Override
    public void activate() throws Exception {
        /**
         * Environement data
         */
        RunModesService run = getSlingScriptHelper().getService(RunModesService.class);
        runMode = run.getCurrentRunMode();
        /**
         * users data
         */
        Locale locale = getCurrentPage().getLanguage(false);
        userLanguage = locale.getLanguage();
        /**
         * tree structure data
         */
        pageCategorie1 = getCurrentPage().getTemplate().getName();
        pageCategory2 = getCurrentPage().getName();
    }

    /**
     * @return the current Run Mode
     */
       
    public String getRunMode() {
        return runMode;
    }

    public String getPageCategorie1() {
        return pageCategorie1;
    }

    public String getUserLanguage() {
        return userLanguage;
    }
    


}