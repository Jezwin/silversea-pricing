package com.silversea.aem.components.editorial.findyourcruise2018;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FindYourCruise2018UseTest {

    private List<CruiseModelLight> cruises;

    @Before
    public void before() throws IOException {
        cruises = new ArrayList<>();
        File cruisesJson = new File("src/test/resources/cruises.json");
        JsonParser parser = new JsonParser();
        String cruisesString = String.join("", Files.readAllLines(cruisesJson.toPath()));
        JsonElement parse = parser.parse(cruisesString);
        parse.getAsJsonArray()
                .forEach(jsonElement -> cruises.add(toCruise(jsonElement.getAsJsonObject())));
    }

    @Test
    public void testOneDestination() {
        FindYourCruise2018Use use = new FindYourCruise2018Use() {
            @Override
            protected Map<FilterLabel, Set<String>> fromRequest() {
                Map<FilterLabel, Set<String>> map = new HashMap<>();
                map.put(FilterLabel.DESTINATION, Collections.singleton("africa-indian-ocean-cruise"));
                return map;
            }
        };
        long expectedCruises = cruises.stream()
                .filter(cruise -> "africa-indian-ocean-cruise".equals(cruise.getDestination().getName()))
                .count();
        CruisesCacheService cacheService = new TestCacheService();
        use.init(cacheService);
        Assert.assertTrue(use.getCruises().stream().allMatch(
                cruise -> "africa-indian-ocean-cruise".equals(cruise.getCruiseModel().getDestination().getName())));
        Assert.assertEquals(expectedCruises, use.getCruises().size());

    }

    private CruiseModelLight toCruise(JsonObject json) {
        TestCruiseModel cruiseModel = new TestCruiseModel();
        Function<String, String> stringOrEmpty =
                key -> Optional.ofNullable(json.get(key)).map(JsonElement::getAsString).orElse("");
        cruiseModel.setShip(json.getAsJsonObject("ship").getAsJsonPrimitive("name").getAsString());
        cruiseModel.setDestination(json.getAsJsonObject("destination").getAsJsonPrimitive("name").getAsString());
        cruiseModel.setCruiseType(json.get("cruiseType").getAsString());
        cruiseModel.setArrivalPortName(stringOrEmpty.apply("arrivalPortName"));
        cruiseModel.setDeparturePortName(stringOrEmpty.apply("departurePortName"));
        cruiseModel.setItineraries(Collections.emptyList());
        List<PortItem> ports = StreamSupport.stream(json.getAsJsonArray("ports").spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(jsonElement -> new PortItem(jsonElement.get("name").getAsString(),
                        jsonElement.get("title").getAsString()))
                .collect(Collectors.toList());
        List<FeatureModel> features = StreamSupport.stream(json.getAsJsonArray("features").spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> new FeatureModel() {
                    @Override
                    public String getName() {
                        return jsonObject.get("name").getAsString();
                    }
                }).collect(Collectors.toList());
        cruiseModel.setFeatures(features);
        return new CruiseModelLight(cruiseModel) {
            @Override
            public List<PortItem> getPorts() {
                return ports;
            }
        };
    }

    class TestCruiseModel extends CruiseModel {
        private ShipModel ship;
        private String cruiseType;
        private List<FeatureModel> features;
        private DestinationModel destination;
        private Calendar startDate;
        private List<ItineraryModel> itineraries;
        private String departurePortName;
        private String arrivalPortName;

        public void setDeparturePortName(String departurePortName) {
            this.departurePortName = departurePortName;
        }

        public void setArrivalPortName(String arrivalPortName) {
            this.arrivalPortName = arrivalPortName;
        }

        public void setShip(String shipName) {
            this.ship = new ShipModel() {
                @Override
                public String getName() {
                    return shipName;
                }
            };
        }

        public void setCruiseType(String cruiseType) {
            this.cruiseType = cruiseType;
        }

        public void setFeatures(List<FeatureModel> features) {
            this.features = features;
        }

        public void setDestination(String destination) {
            this.destination = new DestinationModel() {
                @Override
                public String getName() {
                    return destination;
                }
            };
        }

        public void setStartDate(Calendar startDate) {
            this.startDate = startDate;
        }

        public void setItineraries(List<ItineraryModel> itineraries) {
            this.itineraries = itineraries;
        }

        @Override
        public ShipModel getShip() {
            return ship;
        }

        @Override
        public String getCruiseType() {
            return cruiseType;
        }

        @Override
        public List<FeatureModel> getFeatures() {
            return features;
        }

        @Override
        public DestinationModel getDestination() {
            return destination;
        }

        @Override
        public Calendar getStartDate() {
            return startDate;
        }

        @Override
        public List<ItineraryModel> getItineraries() {
            return itineraries;
        }

        @Override
        public String getDeparturePortName() {
            return departurePortName;
        }

        @Override
        public String getArrivalPortName() {
            return arrivalPortName;
        }
    }

    class TestCacheService implements CruisesCacheService {

        @Override
        public List<CruiseModelLight> getCruises(String lang) {
            return cruises;
        }

        @Override
        public CruiseModelLight getCruiseByCruiseCode(String lang, String cruiseCode) {
            return null;
        }

        @Override
        public List<DestinationModelLight> getDestinations(String lang) {
            return null;
        }

        @Override
        public List<ShipModelLight> getShips(String lang) {
            return null;
        }

        @Override
        public List<PortModelLight> getPorts(String lang) {
            return null;
        }

        @Override
        public Set<Integer> getDurations(String lang) {
            return null;
        }

        @Override
        public Set<YearMonth> getDepartureDates(String lang) {
            return null;
        }

        @Override
        public Set<FeatureModelLight> getFeatures(String lang) {
            return null;
        }

        @Override
        public void addOrUpdateCruise(CruiseModelLight cruiseModel, String langIn) {

        }

        @Override
        public void removeCruise(String lang, String cruiseCode) {

        }

        @Override
        public void buildCruiseCache() {

        }
    }

}