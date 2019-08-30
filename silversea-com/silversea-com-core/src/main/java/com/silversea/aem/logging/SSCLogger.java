package com.silversea.aem.logging;

public interface SSCLogger {
    void logInfo(JsonLog message);

    void logWarning(JsonLog message);

    void logError(JsonLog message);
}
