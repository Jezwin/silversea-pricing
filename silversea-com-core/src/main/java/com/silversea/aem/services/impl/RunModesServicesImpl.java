package com.silversea.aem.services.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.settings.SlingSettingsService;

import com.silversea.aem.services.RunModesService;

@Component(immediate = true)
@Service(value = RunModesService.class)
public class RunModesServicesImpl implements RunModesService {


    @Reference
    private SlingSettingsService slingSettingsService;
    /**
     * Check runmode if author.
     *
     * @return
     */
    public boolean isAuthor() {
        return slingSettingsService.getRunModes().contains("author");
    }

    /**
     * Check runmode if publish
     *
     * @return
     */
    public boolean isPublish() {
        return slingSettingsService.getRunModes().contains("publish");
    }

    public String getAuthorRunModeClass() {
        return isAuthor() ? "runmode-author" : "";
    }
    /**
     * mbennabi 
     * get current run mode
     */
    public String getCurrentRunMode() {
        String runMode = null;
        if(slingSettingsService.getRunModes().contains("prod")){
            runMode= "prod";
        }
        if(slingSettingsService.getRunModes().contains("preprod")){
            runMode = "preprod";
        }
        if(slingSettingsService.getRunModes().contains("dev")){
            runMode = "dev";
        }
           
        return runMode;
    }
}