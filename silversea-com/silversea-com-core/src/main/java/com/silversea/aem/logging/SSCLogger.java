package com.silversea.aem.logging;

import java.io.IOException;

public interface SSCLogger {
    void logInfo(JsonLog message);

    void logWarning(JsonLog message);

    void logError(JsonLog message);
}
