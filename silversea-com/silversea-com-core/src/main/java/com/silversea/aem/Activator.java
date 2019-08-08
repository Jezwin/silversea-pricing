package com.silversea.aem;

import com.silversea.aem.services.CruisesCacheService;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        context.addBundleListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTED
                && event.getBundle().getSymbolicName().equals("com.silversea.aem.silversea-com-core")) {

            try {
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                LOGGER.error("Cannot wait before init of CruiseCacheService");
            }
            while(!buildCacheInit(event)){
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    LOGGER.error("Cannot wait before init of CruiseCacheService");
                }
            }
        }
    }

    private boolean buildCacheInit(BundleEvent event) {
        try {
            final BundleContext context = event.getBundle().getBundleContext();

            final ServiceReference serviceReference = context.getServiceReference(CruisesCacheService.class.getName());
            if (serviceReference != null) {
                final CruisesCacheService cruisesCacheService = (CruisesCacheService) context.getService(serviceReference);
                cruisesCacheService.buildCruiseCache();
                return true;
            }
            return false;
        }catch(Exception e){
            LOGGER.error("Failed to generate Init Cruise Cache -- will try again after");
            return false;
        }
    }

}

