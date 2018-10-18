package com.silversea.aem.components.editorial.findyourcruise2018;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterBar.*;
import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.*;
import static org.junit.Assert.*;

public class FindYourCruise2018UseTest {

    private List<CruiseModelLight> cruises;
    CruisesCacheService cacheService;

    @Before
    public void before() throws IOException {
        cruises = new ArrayList<>();
        File cruisesJson = new File("src/test/resources/cruises.json");
        JsonParser parser = new JsonParser();
        String cruisesString = String.join("", Files.readAllLines(cruisesJson.toPath()));
        JsonElement parse = parser.parse(cruisesString);
        parse.getAsJsonArray()
                .forEach(jsonElement -> cruises.add(toCruise(jsonElement.getAsJsonObject())));
        cacheService = new TestCacheService();
    }

    @Test
    public void testOneDestination() {
        final String africa = "africa-indian-ocean-cruise";
        FindYourCruise2018Use use = new UseBuilder().withDestinations(africa).build();
        use.init(cacheService);

        //test results
        assertTrue(use.getCruises().stream().allMatch(
                cruise -> africa.equals(cruise.getCruiseModel().getDestination().getName())));
        long expectedCruises = cruises.stream()
                .filter(cruise -> africa.equals(cruise.getDestination().getName()))
                .count();
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        FilterBar filters = use.getFilterBar();
        assertTrue(DESTINATION.isSelected());
        //only africa is chosen others are enabled
        assertTrue(DESTINATION.getRows().stream()
                .allMatch(row -> row.isEnabled() || (africa.equals(row.getLabel()) && row.isChosen())));
        assertFalse(PORT.isSelected());
        //silver galapagos doesn't cruise africa
        assertEquals(DISABLED, SHIP.retrieveState("silver-galapagos"));
        //silver muse does cruise africa
        assertEquals(ENABLED, SHIP.retrieveState(("silver-muse")));
    }

    @Test
    public void testTwoDestinations() {
        String africa = "africa-indian-ocean-cruise";
        String asia = "asia-cruise";
        FindYourCruise2018Use use = new UseBuilder().withDestinations(africa, asia).build();
        Predicate<CruiseModelLight> test = cruise -> africa.equals(cruise.getDestination().getName()) ||
                asia.equals(cruise.getDestination().getName());
        use.init(cacheService);

        //test results
        assertTrue(use.getCruises().stream().map(CruiseItem::getCruiseModel).allMatch(test));
        long expectedCruises = cruises.stream().filter(test).count();
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        FilterBar filters = use.getFilterBar();
        assertTrue(DESTINATION.isSelected());
        //only africa and asia is chosen others are enabled
        assertTrue(DESTINATION.getRows().stream()
                .allMatch(row -> {
                    if (africa.equals(row.getLabel()) || asia.equals(row.getLabel())) {
                        return row.isChosen();
                    }
                    return row.isEnabled();
                }));
        assertFalse(PORT.isSelected());
        //silver galapagos doesn't cruise africa
        assertEquals(DISABLED, SHIP.retrieveState("silver-galapagos"));
        //silver muse does cruise africa
        assertEquals(ENABLED, SHIP.retrieveState("silver-muse"));

    }

    @Test
    public void testMultipleFilters() {
        String africa = "africa-indian-ocean-cruise";
        String asia = "asia-cruise";
        String ohchiminh = "ho-chi-minh-city";
        String danang = "da-nang";
        FindYourCruise2018Use use =
                new UseBuilder().withDestinations(africa, asia).withPorts(ohchiminh, danang)
                        .build();

        Predicate<CruiseModelLight> test = cruise -> (africa.equals(cruise.getDestination().getName()) ||
                asia.equals(cruise.getDestination().getName())) && (cruise.getPorts().stream().map(PortItem::getName)
                .anyMatch(name -> ohchiminh.equals(name) || danang.equals(name)));
        use.init(cacheService);

        //test results
        long expectedCruises = cruises.stream().filter(test).count();
        assertTrue(use.getCruises().stream().map(CruiseItem::getCruiseModel).allMatch(test));
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        FilterBar filters = use.getFilterBar();
        assertTrue(DESTINATION.isSelected());
        assertTrue(PORT.isSelected());
        //only africa and asia is chosen others are enabled
        assertTrue(DESTINATION.getRows().stream()
                .allMatch(row -> {
                    if (africa.equals(row.getLabel()) || asia.equals(row.getLabel())) {
                        return row.isChosen();
                    }
                    return row.isEnabled();
                }));
        //only ohchiminh and danang is chosen others are enabled
        assertTrue(PORT.getRows().stream()
                .allMatch(row -> {
                    if (ohchiminh.equals(row.getLabel()) || danang.equals(row.getLabel())) {
                        return row.isChosen();
                    }
                    return row.isEnabled();
                }));
        assertFalse(TYPE.isSelected());
        assertEquals(DISABLED, TYPE.retrieveState("silversea-expedition"));//no expeditions at danang
        //silver galapagos doesn't cruise africa nor asia
        assertEquals(DISABLED, SHIP.retrieveState("silver-galapagos"));

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

    class TestFindYourCruise2018Use extends FindYourCruise2018Use {
        final Map<String, String[]> filtersRequest;

        TestFindYourCruise2018Use(Map<String, String[]> filtersRequest) {
            this.filtersRequest = filtersRequest;
        }

        @Override
        protected Map<String, String[]> getFromWebRequest() {
            return filtersRequest;
        }
    }

    class UseBuilder {

        final Map<String, String[]> filtersRequest;

        UseBuilder() {
            filtersRequest = new HashMap<>();
        }

        private UseBuilder with(String label, String... values) {
            filtersRequest.put(label, values);
            return this;
        }

        UseBuilder withPorts(String... ports) {
            return with(PORT.getKind(), ports);
        }

        UseBuilder withDestinations(String... destinations) {
            return with(DESTINATION.getKind(), destinations);
        }

        UseBuilder withType(String... types) {
            return with(TYPE.getKind(), types);
        }

        UseBuilder withFeature(String... features) {
            return with(FEATURES.getKind(), features);
        }

        UseBuilder withShip(String... ships) {
            return with(SHIP.getKind(), ships);
        }

        UseBuilder withDuration(String... durations) {
            return with(DURATION.getKind(), durations);
        }

        FindYourCruise2018Use build() {
            return new TestFindYourCruise2018Use(filtersRequest);
        }


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
