package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.services.RunModesService;

/**
 * Helper allowing to deal with run modes
 */
public class RunModesHelper extends WCMUsePojo {

    private RunModesService runModesService;

    @Override
    public void activate() throws Exception {
        runModesService = getSlingScriptHelper().getService(RunModesService.class);
    }

    /**
     * @return true if run modes contains "author"
     */
    public boolean isAuthor() {
        return runModesService.isAuthor();
    }

    /**
     * @return true if run modes contains "publish"
     */
    public boolean isPublish() {
        return runModesService.isPublish();
    }

    /**
     * @return class name "runmode-author" if run modes contains "author"
     */
    public String getAuthorRunModeClass() {
        return isAuthor() ? "runmode-author" : "";
    }
}