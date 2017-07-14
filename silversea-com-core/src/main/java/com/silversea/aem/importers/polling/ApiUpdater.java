package com.silversea.aem.importers.polling;

import com.silversea.aem.importers.services.CitiesImporter;
import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.polling.importer.ImportException;
import com.day.cq.polling.importer.Importer;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.importers.services.FeaturesImporter;

@Component
@Service(value = Runnable.class)
@Properties({
    @Property( name = "scheduler.expression", value = "0 * * * * ?"),
    @Property(name="scheduler.concurrent", boolValue=false)
})
public class ApiUpdater implements Runnable {

    final static private Logger LOGGER = LoggerFactory.getLogger(ApiUpdater.class);

    @Reference
    private CitiesImporter citiesImporter;

    @Override
    public void run() {
        LOGGER.info("Running ...");

        // update cities
        final ImportResult importResult = citiesImporter.updateCities();
        LOGGER.info("City import : {} success, {} errors",
                importResult.getSuccessNumber(), importResult.getErrorNumber());

        // TODO update hotels
        // TODO update land programs
        // TODO update excursions

        // TODO activate all modifications
    }
}
