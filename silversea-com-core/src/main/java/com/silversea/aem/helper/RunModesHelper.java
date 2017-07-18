package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.settings.SlingSettingsService;

/**
 * TODO review javadoc
 */
public class RunModesHelper extends WCMUsePojo {

    private SlingSettingsService slingSettingsService;

    @Override
    public void activate() throws Exception {
        slingSettingsService = getSlingScriptHelper().getService(SlingSettingsService.class);
    }

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
}