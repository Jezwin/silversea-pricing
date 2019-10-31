package com.silversea.aem.logging;

public interface SSCLoggerFactory {
    LogzLogger getLogger(String component);

    SSCLogger getLogger(Class clazz);
}
