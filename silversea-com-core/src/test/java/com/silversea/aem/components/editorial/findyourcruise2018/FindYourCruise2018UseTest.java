package com.silversea.aem.components.editorial.findyourcruise2018;


import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.components.editorial.findyourcruise2018.filters.*;
import com.silversea.aem.models.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.DISABLED;
import static com.silversea.aem.components.editorial.findyourcruise2018.filters.FilterRowState.ENABLED;
import static org.junit.Assert.*;

public class FindYourCruise2018UseTest {

    private List<CruiseModelLight> cruises;

    private static final String AFRICA_VALUE = "3";
    private static final String ASIA_VALUE = "13";
    private static final String AFRICA_LABEL = "Africa & Indian Ocean";
    private static final String ASIA_LABEL = "Asia";
    private static final String SILVER_GALAPAGOS = "Silver Galapagos";
    private static final String SILVER_MUSE = "Silver Muse";
    private static final String SILVERSEA_EXPEDITION = "silversea-expedition";
    private static final String SILVERSEA_CRUISE = "silversea-cruise";
    private static final String HO_CHI_MINH_CITY = "ho-chi-minh-city";
    private static final String DA_NANG = "da-nang";

    @Before
    public void before() throws IOException {
        cruises = new ArrayList<>();
        File cruisesJson = new File("src/test/resources/cruises2.json");
        JsonParser parser = new JsonParser();
        String cruisesString = String.join("", Files.readAllLines(cruisesJson.toPath()));
        JsonElement parse = parser.parse(cruisesString);
        parse.getAsJsonArray()
                .forEach(jsonElement -> cruises.add(toCruise(jsonElement.getAsJsonObject())));
    }




    @Test
    public void multiThreads() throws InterruptedException {
        List<Runnable> runners = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            runners.add(this::testMultipleFilters);
            runners.add(this::testOneDestination);
            runners.add(this::testSort);
            runners.add(this::testTwoDestinations);
            runners.add(this::testProperties);
        }
        assertConcurrent("Testing multithreads", runners, 30, 4);
    }

    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds, int numThreads) throws InterruptedException {
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(() -> {
                    allExecutorThreadsReady.countDown();
                    try {
                        afterInitBlocker.await();
                        submittedTestRunnable.run();
                    } catch (final Throwable e) {
                        exceptions.add(e);
                    } finally {
                        allDone.countDown();
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent",
                    allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message + " timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        for (Throwable exception : exceptions) {
            exception.printStackTrace();

        }
        assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
    }

    @Test
    public void performance() throws InterruptedException {
        double nOfRun = 2; //set 2000 to test better
        ExecutorService executor = Executors.newFixedThreadPool(1);//use 2 with caution
        List<UseBuilder> builders = new ArrayList<>();
        for (int i = 0; i < nOfRun; i++) {
            builders.add(new UseBuilder());
            builders.add(new UseBuilder().withDestinations(AFRICA_VALUE, ASIA_VALUE));
            builders.add(new UseBuilder().withType(SILVERSEA_EXPEDITION));
            builders.add(new UseBuilder().withDuration("8"));
            builders.add(new UseBuilder().withDuration("8").withDestinations(ASIA_VALUE));
            builders.add(new UseBuilder().withPorts(HO_CHI_MINH_CITY).withDestinations(ASIA_VALUE));
            builders.add(new UseBuilder().withShip(SILVER_GALAPAGOS).withDestinations(ASIA_VALUE)
                    .withPorts(HO_CHI_MINH_CITY, DA_NANG));
            builders.add(new UseBuilder(
                    "destination=1&ship=10.4.5.6&type=silversea-cruise&port=Singapore.Sydney"));
        }
        Function<UseBuilder, Callable<Long>> test = useBuilder -> () -> {
            long current = System.currentTimeMillis();
            useBuilder.build().init(cruises, "1000");
            return (System.currentTimeMillis() - current);
        };
        double average = executor.invokeAll(builders.stream().map(test).collect(Collectors.toList())).stream()
                .mapToLong(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                    }
                    return 0L;
                }).average().orElse(0);
        System.out.println("******" + average / 1000.0 + "s*****");
        //on my pc ~0.02s, now we are at 0.03
    }


    @Test
    public void testOneDestination() {
        FindYourCruise2018Use use = new UseBuilder().withDestinations(AFRICA_VALUE).build();
        use.init(cruises, "1000");

        //test results
        assertTrue(use.getCruises().stream().allMatch(
                cruise -> AFRICA_LABEL.equals(cruise.getCruiseModel().getDestination().getTitle())));
        long expectedCruises = cruises.stream()
                .filter(cruise -> AFRICA_LABEL.equals(cruise.getDestination().getTitle()))
                .count();
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        AbstractFilter<DestinationItem> destination = use.getFilterBar().getDestination();
        assertTrue(destination.isSelected());
        //only africa is chosen others are enabled
        assertTrue(destination.getRows().stream()
                .allMatch(row -> row.isEnabled() || (AFRICA_LABEL.equals(row.getLabel()) && row.isChosen())));
        assertFalse(use.getFilterBar().getPort().isSelected());
        //silver galapagos doesn't cruise africa
        AbstractFilter<ShipItem> ship = use.getFilterBar().getShip();
        assertEquals(DISABLED, ship.retrieveState(SILVER_GALAPAGOS));
        //silver muse does not cruise africa
        assertEquals(DISABLED, ship.retrieveState((SILVER_MUSE)));
    }

    @Test
    public void testSort() {
        FindYourCruise2018Use use = new UseBuilder().withDestinations(AFRICA_VALUE).sortedBy("price", "desc").build();
        use.init(cruises, "1000");
        assertTrue(use.getCruises().stream().allMatch(cruise -> AFRICA_LABEL.equals(cruise.getCruiseModel().getDestination().getTitle())));
        Long price = Long.MAX_VALUE;
        for (CruiseItem cruiseItem : use.getCruises()) {
            Long computedPrice = cruiseItem.getLowestPrice().getComputedPrice();

            assertTrue(price == null || computedPrice == null || price >= computedPrice);
            price = computedPrice;
        }
    }


    @Test
    public void testTwoDestinations() {
        FindYourCruise2018Use use = new UseBuilder().withDestinations(AFRICA_VALUE, ASIA_VALUE).build();
        Predicate<CruiseModelLight> test = cruise -> AFRICA_LABEL.equals(cruise.getDestination().getTitle()) ||
                ASIA_LABEL.equals(cruise.getDestination().getTitle());
        use.init(cruises, "1000");

        //test results
        assertTrue(use.getCruises().stream().map(CruiseItem::getCruiseModel).allMatch(test));
        long expectedCruises = cruises.stream().filter(test).count();
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        FilterBar filters = use.getFilterBar();
        assertTrue(filters.getDestination().isSelected());
        //only africa and asia is chosen others are enabled
        assertTrue(filters.getDestination().getRows().stream()
                .allMatch(row -> {
                    if (AFRICA_LABEL.equals(row.getLabel()) || ASIA_LABEL.equals(row.getLabel())) {
                        return row.isChosen();
                    }
                    return row.isEnabled();
                }));
        assertFalse(filters.getPort().isSelected());
        //silver galapagos doesn't cruise africa
        assertEquals(DISABLED, filters.getShip().retrieveState(SILVER_GALAPAGOS));
        //silver muse does cruise africa
        assertEquals(ENABLED, filters.getShip().retrieveState(SILVER_MUSE));


    }

    @Test
    public void testProperties() {
        Map<String, String[]> properties = new HashMap<>();
        properties.put("shipId", new String[]{"Silver Discoverer"});
        FindYourCruise2018Use use = new UseBuilder(properties).build();
        Predicate<CruiseModelLight> test = cruise -> "Silver Discoverer".equals(cruise.getShip().getTitle());
        use.init(cruises, "1000");
        long expectedCruises = cruises.stream().filter(test).count();
        assertTrue(use.getCruises().stream().map(CruiseItem::getCruiseModel).allMatch(test));
        assertEquals(expectedCruises, use.getCruises().size());
        assertFalse(use.getFilterBar().getShip().isVisible());

    }

    @Test
    public void testMultipleFilters() {
        FindYourCruise2018Use use =
                new UseBuilder().withDestinations(AFRICA_VALUE, ASIA_VALUE).withPorts(HO_CHI_MINH_CITY, DA_NANG)
                        .build();

        Predicate<CruiseModelLight> test = cruise -> (AFRICA_LABEL.equals(cruise.getDestination().getTitle()) ||
                ASIA_LABEL.equals(cruise.getDestination().getName())) &&
                (cruise.getPorts().stream().map(PortItem::getName)
                        .anyMatch(name -> HO_CHI_MINH_CITY.equals(name) || DA_NANG.equals(name)));
        use.init(cruises, "1000");

        //test results
        long expectedCruises = cruises.stream().filter(test).count();
        assertTrue(use.getCruises().stream().map(CruiseItem::getCruiseModel).allMatch(test));
        assertEquals(expectedCruises, use.getCruises().size());

        //test filters
        FilterBar filterBar = use.getFilterBar();
        AbstractFilter<DestinationItem> destination = filterBar.getDestination();
        assertTrue(destination.isSelected());
        assertEquals(16, destination.getRows().size());//world cruise..
        AbstractFilter<PortItem> portFilter = filterBar.getPort();
        assertTrue(portFilter.isSelected());
        assertEquals(825, portFilter.getRows().size());
        assertEquals(214, portFilter.getRows().stream().filter(port -> !port.isNotVisible()).count());
        assertTrue(portFilter.getRows().stream().noneMatch(row -> row.getState().equals(DISABLED)));
        //only africa and asia is chosen others are enabled
        assertTrue(destination.getRows().stream()
                .allMatch(row -> {
                    if (AFRICA_LABEL.equals(row.getLabel()) || ASIA_LABEL.equals(row.getLabel())) {
                        return row.isChosen();
                    }
                    return row.isDisabled();//both ports are in asia
                }));
        //only ohchiminh and danang is chosen others are enabled
        assertTrue(portFilter.getRows().stream()
                .allMatch(row -> {
                    if (HO_CHI_MINH_CITY.equals(row.getKey()) || DA_NANG.equals(row.getKey())) {
                        return row.isChosen();
                    }
                    return !row.isChosen();//some ports should be enabled some not...
                }));
        AbstractFilter<String> typeFilter = filterBar.getType();
        assertFalse(typeFilter.isSelected());
        assertEquals(DISABLED, typeFilter.retrieveState(SILVERSEA_EXPEDITION));//no expeditions at danang
        //silver galapagos doesn't cruise africa nor asia
        assertEquals(DISABLED, filterBar.getShip().retrieveState(SILVER_GALAPAGOS));

    }

    private CruiseModelLight toCruise(JsonObject json) {
        TestCruiseModel cruiseModel = new TestCruiseModel();
        Function<String, String> stringOrEmpty =
                key -> Optional.ofNullable(json.get(key)).map(JsonElement::getAsString).orElse("");
        cruiseModel.setShip(json.getAsJsonObject("ship").getAsJsonPrimitive("title").getAsString());
        cruiseModel.setDestination(json.getAsJsonObject("destination").getAsJsonPrimitive("title").getAsString());
        cruiseModel.setDestinationId(stringOrEmpty.apply("destinationId"));
        cruiseModel.setCruiseType(json.get("cruiseType").getAsString());
        cruiseModel.setArrivalPortName(stringOrEmpty.apply("arrivalPortName"));
        cruiseModel.setDeparturePortName(stringOrEmpty.apply("departurePortName"));
        cruiseModel.setItineraries(Collections.emptyList());
        cruiseModel.setDuration(json.getAsJsonPrimitive("duration").getAsString());
        cruiseModel.setStartDate(Calendar.getInstance());
        Long price = null;
        try {
            JsonElement priceElement = json.getAsJsonObject("lowestPrices").getAsJsonObject("ftUSD").get("price");
            price = priceElement.getAsLong();//cam be null
        } catch (Throwable e) {
        }
        cruiseModel.setPrices(Arrays.asList(new TestPriceModel(price)));

        List<PortItem> ports = StreamSupport.stream(json.getAsJsonArray("ports").spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(jsonElement -> new PortItem(jsonElement.get("name").getAsString(),
                        jsonElement.get("name").getAsString(), jsonElement.get("countryISO3").getAsString()))
                .collect(Collectors.toList());
        List<FeatureModel> features = StreamSupport.stream(json.getAsJsonArray("features").spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> new FeatureModel() {
                    @Override
                    public String getTitle() {
                        return jsonObject.get("title").getAsString();
                    }

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
        private final Map<String, String[]> properties;


        TestFindYourCruise2018Use(Map<String, String[]> filtersRequest, Map<String, String[]> properties) {
            this.filtersRequest = filtersRequest;
            this.properties = properties;
        }

        @Override
        protected Map<String, String[]> getFromWebRequest() {
            return filtersRequest;
        }

        @Override
        protected Map<String, String[]> filteringSettings() {
            return properties;
        }

        @Override
        List<CruiseModelLight> preFiltering(List<CruiseModelLight> allCruises) {
            return allCruises;//do not prefilter anything
        }

        @Override
        public String getMarketCurrency() {
            return "ftUSD";
        }
    }

    class UseBuilder {

        final Map<String, String[]> filtersRequest;
        final Map<String, String[]> properties;

        UseBuilder() {
            filtersRequest = new HashMap<>();
            properties = new HashMap<>();
            filtersRequest.put("pag", new String[]{"1"});
            filtersRequest.put("pagSize", new String[]{"1000"});//avoid pagination
        }

        UseBuilder(String request) {
            filtersRequest = new HashMap<>();
            properties = new HashMap<>();

            for (String param : request.split("&")) {
                if (!Strings.isNullOrEmpty(param) && param.contains("=")) {
                    String[] split = param.split("=");
                    filtersRequest.put(split[0], split[1].split("\\."));
                }

            }

            filtersRequest.put("pag", new String[]{"1"});
            filtersRequest.put("pagSize", new String[]{"1000"});//avoid pagination
        }

        private UseBuilder with(String label, String... values) {
            filtersRequest.put(label, values);
            return this;
        }

        UseBuilder(Map<String, String[]> properties) {
            this.filtersRequest = new HashMap<>();
            this.properties = properties;
        }

        UseBuilder withPorts(String... ports) {
            return with(PortFilter.KIND, ports);
        }

        UseBuilder withDestinations(String... destinations) {
            return with(DestinationFilter.KIND, destinations);
        }

        UseBuilder withType(String... types) {
            return with("type", types);
        }

        UseBuilder withFeature(String... features) {
            return with(FeatureFilter.KIND, features);
        }

        UseBuilder withShip(String... ships) {
            return with(ShipFilter.KIND, ships);
        }

        UseBuilder withDuration(String... durations) {
            return with(DurationFilter.KIND, durations);
        }


        UseBuilder sortedBy(String name, String type) {
            filtersRequest.put("sortby", new String[]{name + "-" + type});
            return this;
        }

        FindYourCruise2018Use build() {
            return new TestFindYourCruise2018Use(filtersRequest, properties);
        }


    }

    class TestPriceModel extends PriceModel {
        private final Long price;

        public TestPriceModel(Long price) {
            this.price = price;
        }

        @Override
        public Long getPrice() {
            return price;
        }

        @Override
        public String getCurrency() {
            return "USD";
        }

        @Override
        public String getGeomarket() {
            return "ft";
        }

        @Override
        public Long getComputedPrice() {
            return price;
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
        private String duration;
        private String destinationId;
        private List<PriceModel> prices;

        public void setDestinationId(String destinationId) {
            this.destinationId = destinationId;
        }

        public void setDeparturePortName(String departurePortName) {
            this.departurePortName = departurePortName;
        }

        public void setArrivalPortName(String arrivalPortName) {
            this.arrivalPortName = arrivalPortName;
        }

        public void setShip(String shipTitle) {
            this.ship = new ShipModel() {
                @Override
                public String getTitle() {
                    return shipTitle;
                }

                @Override
                public String getShipId() {
                    return shipTitle;
                }
            };
        }

        public void setDuration(String duration) {
            this.duration = duration;
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
                public String getTitle() {
                    return destination;
                }

                @Override
                public String getName() {
                    return destination;
                }

                @Override
                public String getDestinationId() {
                    return destinationId;
                }
            };
        }

        public void setStartDate(Calendar startDate) {
            this.startDate = startDate;
        }

        @Override
        public List<PriceModel> getPrices() {
            return prices;
        }

        public void setPrices(List<PriceModel> prices) {
            this.prices = prices;
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

        @Override
        public String getDuration() {
            return duration;
        }
    }

}
