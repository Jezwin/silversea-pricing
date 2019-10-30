package com.silversea.aem.proxies;

import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;

public class NullLogger implements SSCLogger {
    @Override
    public void logInfo(JsonLog message) {

    }

    @Override
    public void logWarning(JsonLog message) {

    }

    @Override
    public void logError(JsonLog message) {

    }

    @Override
    public void logDebug(JsonLog message) {

    }

    @Override
    public void logTrace(JsonLog message) {

    }
}
