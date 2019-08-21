package com.silversea.aem.utils;

import io.logz.sender.LogzioSender;
import io.logz.sender.com.google.gson.Gson;
import io.logz.sender.com.google.gson.JsonObject;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.util.Optional;

public class LogzLogger {
    Optional<LogzioSender> sender;
    private Gson serialiser = new Gson();
    String component;

    public LogzLogger(Optional<LogzioSender> sender, String component) {
        this.sender = sender;
        this.component = component;
    }


    private void log(String logLevel, String event, JsonObject message) {
        sender.ifPresent(s -> {
            message.addProperty("level", logLevel);
            Bundle bundle = FrameworkUtil.getBundle(this.getClass());
            BundleContext context = bundle.getBundleContext();
            SlingSettingsService slingSettingsService = (SlingSettingsService) context.getService(context.getServiceReference(SlingSettingsService.class.getName()));
            if (slingSettingsService != null) {
                message.add("runMode", serialiser.toJsonTree(slingSettingsService.getRunModes()));
            }
            message.addProperty("component", component);
            message.addProperty("event", event);
            s.send(message);
        });
    }


    public void logInfo(String event, JsonObject message) {
        log("info", event, message);
    }
    public void logWarning(String event, JsonObject message) {
        log("warning", event, message);
    }
    public void logError(String event, JsonObject message) {
        log("error", event, message);
    }
}