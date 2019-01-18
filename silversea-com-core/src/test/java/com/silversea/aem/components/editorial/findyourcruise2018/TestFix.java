package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TestFix {

    @Test
    public void myTest() throws IOException {
        File cruisesJson = new File("src/test/resources/cruises.json");
        JsonParser parser = new JsonParser();
        String cruisesString = String.join("", Files.readAllLines(cruisesJson.toPath()));
        JsonElement cruises = parser.parse(cruisesString);
        File citiesJson = new File("src/test/resources/cities.json");
        String citiesString = String.join("", Files.readAllLines(citiesJson.toPath()));
        JsonElement cities = parser.parse(citiesString);
        Map<String, String> portMap = new HashMap<>();
        int x = 0;
        for (JsonElement jsonElement : cities.getAsJsonArray()) {
            portMap.put(jsonElement.getAsJsonObject().get("city_name").getAsString(), jsonElement.getAsJsonObject().get("country_iso3").getAsString());
        }
        for (JsonElement jsonElement : cruises.getAsJsonArray()) {
            for (JsonElement port : jsonElement.getAsJsonObject().get("ports").getAsJsonArray()) {
                JsonObject portObject = port.getAsJsonObject();
                if (portMap.containsKey(portObject.get("name").getAsString())) {
                    portObject.addProperty("countryISO3", portMap.get(portObject.get("name").getAsString()));
                    x++;
                }
            }
        }
        if (x > 0) {
            JsonWriter writer = new JsonWriter(new FileWriter("src/test/resources/cruise2.json"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(cruises, writer);
            writer.close();
        } else {
            //trovare il modo di metterci tutti i porti...piuttosto fare un python e vaffanculo
            Assert.fail();

        }

    }
}
