package com.silversea.aem.logging;

import io.logz.sender.com.google.gson.JsonArray;
import io.logz.sender.com.google.gson.JsonObject;
import io.logz.sender.com.google.gson.JsonPrimitive;
import scala.collection.immutable.List;

public class JsonLog {

    private final JsonObject json;

    public static JsonLog jsonLog(String event) {
        return new JsonLog(event);
    }

    public static JsonLog nested() { return new JsonLog(); }

    private JsonLog(String event) {
        json = new JsonObject();
        json.addProperty("event", event);
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

    public JsonObject underlying() {
        return json;
    }
}
