package com.silversea.aem.logging;

import io.logz.sender.com.google.gson.JsonArray;
import io.logz.sender.com.google.gson.JsonObject;
import io.logz.sender.com.google.gson.JsonPrimitive;

import java.io.StringWriter;

public class JsonLog {

    private final JsonObject json;

    public static JsonLog jsonLog(String event) {
        return new JsonLog(event);
    }

    public static JsonLog jsonLog(String event, String message){
        return new JsonLog(event,message);
    }

    public static JsonLog nested() { return new JsonLog(); }

    private JsonLog(String event) {
        json = new JsonObject();
        json.addProperty("event", event);
    }

    private JsonLog(String event, String message) {
        json = new JsonObject();
        json.addProperty("event", event);
        json.addProperty("message",message);
    }

    private JsonLog() {
        json = new JsonObject();
    }

    public JsonLog with(String key, String value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonLog with(String key, Number value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonLog with(String key, boolean value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonLog with(String key, String[] values) {
        JsonArray array = new JsonArray();
        for (String v: values) {
            array.add(new JsonPrimitive(v));
        }
        json.add(key, array);
        return this;
    }

    public JsonLog with(String key, JsonLog nested) {
        json.add(key, nested.underlying());
        return this;
    }

    public JsonLog with(Throwable e) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", e.getClass().getCanonicalName());
        obj.addProperty("stackTrace", GetStackTraceString(e));
        if (e.getCause() != null) obj.addProperty("cause", e.getCause().getMessage());
        underlying().add("exception", obj);

        this.json.addProperty("message", e.getMessage());

        return this;
    }

    private String GetStackTraceString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    JsonObject underlying() {
        return json;
    }

    @Override
    public String toString() {
        return underlying().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JsonLog other = (JsonLog)obj;
        return other.underlying().toString().equals(this.underlying().toString());
    }
}
