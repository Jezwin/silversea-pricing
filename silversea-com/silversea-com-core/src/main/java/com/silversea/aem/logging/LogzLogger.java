package com.silversea.aem.logging;

import io.logz.sender.LogzioSender;
import io.logz.sender.com.google.gson.Gson;
import io.logz.sender.com.google.gson.JsonObject;
import io.vavr.control.Try;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LogzLogger implements SSCLogger {
    Optional<LogzioSender> sender;
    private Gson serialiser = new Gson();
    private String component;
    private Set<String> runModes;
    private String environment = "undefined";

    public LogzLogger(Optional<LogzioSender> sender, String component) {
        this.sender = sender;
        this.component = component;
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        BundleContext context = bundle.getBundleContext();
        SlingSettingsService slingSettingsService = (SlingSettingsService) context.getService(context.getServiceReference(SlingSettingsService.class.getName()));
        if (slingSettingsService != null) {
            this.runModes = slingSettingsService.getRunModes();
            environment = runModes.stream().filter(x -> Environments.all.contains(x.toLowerCase())).findFirst().orElse("unknown");
        }
    }

    private void log(String level, JsonLog message) {
        sender.ifPresent(s -> {
            JsonObject js = message.underlying();
            js.addProperty("level", level);
            js.add("runMode", serialiser.toJsonTree(runModes));
            js.addProperty("component", component);
            js.addProperty("application", "silversea-com");
            js.addProperty("environment", environment);
            js.addProperty("ipAddress", Try.of(InetAddress::getLocalHost).mapTry(InetAddress::getHostAddress).mapTry(Object::toString).getOrElseGet(x -> "error"));
            js.addProperty("hostName", Try.of(InetAddress::getLocalHost).mapTry(InetAddress::getHostName).getOrElseGet(x -> "error"));
            s.send(js);
        });
    }

    @Override
    public void logInfo(JsonLog message) { log("info", message); }
    @Override
    public void logWarning(JsonLog message) {
        log("warning", message);
    }
    @Override
    public void logError(JsonLog message) {
        log("error", message);
    }

    public static class Environments {
        public static List<String> all = Arrays.asList("local", "stage", "prod");
    }
}