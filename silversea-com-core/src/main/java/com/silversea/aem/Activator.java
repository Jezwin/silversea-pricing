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
        String symbolicName = event.getBundle().getSymbolicName();

        if (event.getType() == BundleEvent.STARTED) {
            LOGGER.info("BundleChanged: " + symbolicName + ", event.type: " + event.getType());
        }

        final BundleContext context = event.getBundle().getBundleContext();

        final ServiceReference serviceReference = context.getServiceReference(CruisesCacheService.class.getName());
        if (serviceReference != null) {
            context.getService(serviceReference);
        }
    }
}

