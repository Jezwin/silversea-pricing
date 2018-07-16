package com.silversea.aem.importers.polling;

import com.silversea.aem.services.CruisesCacheService;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Silversea - Cruise Cache  Updater", metatype = true, immediate = true)
@Service(value = Runnable.class)
@Properties({
        @Property(name = "scheduler.expression", value = "0 0 2 * * ?"),
        @Property(name = "scheduler.concurrent", boolValue = false)}
)
public class CruiseCacheUpdater implements Runnable {

    final static private Logger LOGGER = LoggerFactory.getLogger(CruiseCacheUpdater.class);

    @Reference
    private SlingSettingsService slingSettingsService;
    
    @Reference
    private CruisesCacheService cruisesCacheService;

   

    @Override
    public void run() {
        if (!slingSettingsService.getRunModes().contains("author")) {
            LOGGER.info("Running ...");
            cruisesCacheService.buildCruiseCache();
            LOGGER.debug("Cruise Cache update service end");
        } else {
        	LOGGER.debug("Cruise Cache updater service run only on publish instance");
        }
    }
}
