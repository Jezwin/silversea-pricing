package com.silversea.aem;

import com.silversea.aem.logging.*;
import com.silversea.aem.services.CruisesCacheService;
import io.vavr.Lazy;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.silversea.aem.logging.JsonLog.jsonLog;

public class Activator implements BundleActivator, BundleListener {


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
        SSCLogger sscLogger = getLog(event.getBundle().getBundleContext());
        sscLogger.logInfo(jsonLog("BundleChanged").with("bundleEvent", event.getType()));
        if (event.getType() == BundleEvent.STARTED
                && event.getBundle().getSymbolicName().equals("com.silversea.aem.silversea-com-core")) {

            try {
                sscLogger.logInfo(jsonLog("SleepingBeforeCruiseCacheInit"));
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                sscLogger.logError(jsonLog("SleepBeforeCruiseCacheInitFailed").with(e));
            }
            while(!buildCacheInit(event, sscLogger)){
                sscLogger.logWarning(jsonLog("CruiseInitFailed"));
                try {
                    sscLogger.logInfo(jsonLog("SleepingBeforeNextCruiseCacheInitAttempt"));
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    sscLogger.logError(jsonLog("SleepBeforeNextCruiseCacheInitInterrupted").with(e));
                }
            }
        }
    }

    private boolean buildCacheInit(BundleEvent event, SSCLogger sscLogger) {
        try {
            sscLogger.logInfo(jsonLog("PreparingCruiseCacheInit"));
            final BundleContext context = event.getBundle().getBundleContext();

            final ServiceReference serviceReference = context.getServiceReference(CruisesCacheService.class.getName());
            if (serviceReference != null) {
                final CruisesCacheService cruisesCacheService = (CruisesCacheService) context.getService(serviceReference);
                sscLogger.logInfo(jsonLog("StartingCruiseCacheInit"));
                cruisesCacheService.buildCruiseCache();
                sscLogger.logInfo(jsonLog("CruiseCacheInitComplete"));
                return true;
            }
            sscLogger.logWarning(jsonLog("CruiseCacheServiceNotFound"));
            return false;
        }catch(Exception e){
            sscLogger.logError(jsonLog("FailedToInitCruiseCache").with(e));
            return false;
        }
    }

    private SSCLogger getLog(BundleContext context) {
        final ServiceReference sRef = context.getServiceReference(SSCLoggerFactory.class.getName());
        SSCLoggerFactory factory = (SSCLoggerFactory) context.getService(sRef);
        return factory.getLogger(Activator.class);
    }

}

