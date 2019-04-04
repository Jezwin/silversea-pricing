package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.RunModesService;

/**
 * Helper allowing to deal with run modes
 */
public class RunModesHelper extends WCMUsePojo {

    private GlobalCacheService cache;

    @Override
    public void activate() throws Exception {
        cache = getSlingScriptHelper().getService(GlobalCacheService.class);
    }

    private RunModesService getService() {
        return getSlingScriptHelper().getService(RunModesService.class);
    }

    /**
     * @return true if run modes contains "author"
     */
    public boolean isAuthor() {
        return cache.getCache("IS_AUTHOR", Boolean.class, () -> getService().isAuthor());
    }

    /**
     * @return true if run modes contains "publish"
     */
    public boolean isPublish() {
        return cache.getCache("IS_PUBLISH", Boolean.class, () -> getService().isPublish());
    }

    /**
     * @return class name "runmode-author" if run modes contains "author"
     */
    public String getAuthorRunModeClass() {
        return isAuthor() ? "runmode-author" : "";
    }
}