package com.silversea.aem.importers.polling;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.polling.importer.ImportException;
import com.day.cq.polling.importer.Importer;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.importers.services.FeaturesImporter;
import com.silversea.aem.importers.services.ShipsImporter;

@Component(metatype = false, enabled = true, label = "General Polling importer")
@Service(value = Importer.class)
@Property(name = Importer.SCHEME_PROPERTY, value = "general-poll-importer", propertyPrivate = true)
public class GeneralPollImporter implements Runnable, Importer {

    static final private Logger LOGGER = LoggerFactory.getLogger(GeneralPollImporter.class);

    @Reference
    ShipsImporter shipsImporter;

    @Reference
    CountriesImporter countriesImporter;

    @Reference
    FeaturesImporter featuresImporter;

    @Override
    public void importData(String scheme, String datasource, Resource target) throws ImportException {
        initService();
    }

    @Override
    public void importData(String scheme, String datasource, Resource target, String login, String password)
            throws ImportException {
        initService();
    }

    @Override
    public void run() {
        LOGGER.error("General Polling Importer Running ...");
    }

    private void initService() {
        try {
            shipsImporter.importShips();
            countriesImporter.importCountries();
            //featuresImporter.importFeatures();
            LOGGER.debug("Service was executed.");
        } catch (Exception e) {
            String errorMessage = "Some issues are happened ()";
            LOGGER.error(errorMessage, e);
        }
    }

}
