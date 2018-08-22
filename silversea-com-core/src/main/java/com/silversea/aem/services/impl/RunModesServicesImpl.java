package com.silversea.aem.services.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.settings.SlingSettingsService;

import com.silversea.aem.services.RunModesService;

@Service
@Component(immediate = true)
public class RunModesServicesImpl implements RunModesService {

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public boolean isAuthor() {
        return slingSettingsService.getRunModes().contains("author");
    }

    @Override
    public boolean isPublish() {
        return slingSettingsService.getRunModes().contains("publish");
    }

    /**
     * TODO rename method TODO check the validity of run modes TODO add missing
     * run modes
     */
    @Override
    public String getCurrentRunMode() {
        String runMode = null;
        if (slingSettingsService.getRunModes().contains("prod")) {
            runMode = "prod";
        }
        if (slingSettingsService.getRunModes().contains("preprod")) {
            runMode = "preprod";
        }
        if (slingSettingsService.getRunModes().contains("dev")) {
            runMode = "dev";
        }
        if (slingSettingsService.getRunModes().contains("local")) {
            runMode = "local";
        }

        return runMode;
    }

    public String getCurrentRunModeFull() {
        return slingSettingsService.getRunModes().toString();
    }
}