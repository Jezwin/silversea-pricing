package com.silversea.aem.logging;

import com.silversea.aem.logging.JsonLog;
import io.logz.sender.LogzioSender;
import io.logz.sender.com.google.gson.Gson;
import io.logz.sender.com.google.gson.JsonObject;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.util.Optional;
import java.util.Set;

public class LogzLogger {
    Optional<LogzioSender> sender;
    private Gson serialiser = new Gson();
    String component;
    Set runModes;

    public LogzLogger(Optional<LogzioSender> sender, String component) {
        this.sender = sender;
        this.component = component;
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        BundleContext context = bundle.getBundleContext();
        SlingSettingsService slingSettingsService = (SlingSettingsService) context.getService(context.getServiceReference(SlingSettingsService.class.getName()));
        if (slingSettingsService != null) {
            this.runModes = slingSettingsService.getRunModes();
        }
    }

    private void log(String level, JsonLog message) {
        sender.ifPresent(s -> {
            JsonObject js = message.underlying();
            js.addProperty("level", level);
            js.add("runMode", serialiser.toJsonTree(runModes));
            js.addProperty("component", component);
            s.send(js);
        });
    }

    public void logInfo(JsonLog message) { log("info", message); }
    public void logWarning(JsonLog message) {
        log("warning", message);
    }
    public void logError(JsonLog message) {
        log("error", message);
    }
}