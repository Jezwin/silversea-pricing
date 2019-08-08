package com.silversea.ssc.aem.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.servlets.LeadServlet;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

@SuppressWarnings("unused")
@SlingServlet(methods = "GET", paths = "/bin/info", extensions = "json")
public class JsonInfoServlet extends SlingAllMethodsServlet {

    @Reference
    private CruisesCacheService cacheService;

    @Reference
    private PublishUtils publishUtils;

    private Table<String, String, String> codeLangDescription;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        createJson(request, response);
    }

    private void createJson(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        codeLangDescription = retrieveDescriptions(resourceResolver);
        ContentObject[] result =
                cacheService.getCruises("en").stream()
                        .map((CruiseModelLight cruise) -> new ContentObject(cruise, resourceResolver))
                        .toArray(ContentObject[]::new);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String json = LeadServlet.JsonMapper.getJson(result);
        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            log("Error while writing json in the response " + json, e);
        }
    }

    private Table<String, String, String> retrieveDescriptions(ResourceResolver resourceResolver) {
        Iterable<Resource> cruises = () -> resourceResolver
                .findResources(
                        "/jcr:root/content/silversea-com//element(*)[@cruiseCode]",
                        "xpath");
        Table<String, String, String> codeLangDescription = HashBasedTable.create();
        stream(cruises.spliterator(), false)
                .forEach(resource -> {
                    ValueMap valueMap = resource.getValueMap();
                    String lang = valueMap.get("lang", String.class);
                    if (lang == null) {
                        String path = resource.getPath();
                        lang = path.substring(path.indexOf("silversea-com/") + "silversea-com/".length(),
                                path.indexOf("/destinations"));
                    }
                    String description =
                            firstNonNull(valueMap, "jcr:description", "importedDescription", "voyageHighlights");
                    codeLangDescription.put(valueMap.get("cruiseCode", String.class), lang, description);

                });
        return codeLangDescription;
    }

    private static String firstNonNull(ValueMap map, String... keys) {
        for (String key : keys) {
            String value = map.get(key, String.class);
            if (value != null) {
                return value;
            }
        }
        return "Not found";
    }

    private class ContentObject {
        private final String cruiseCode;
        private final String departurePort;
        private final String arrivalPort;

        private final Localized description;
        private final Localized urls;

        private final Detailed destination;
        private final Detailed ship;
        private final LowestPrices lowestPrices;
        private final String commercialName;
        private final DateObject departureDate;
        private final String duration;

        public String getThumbnail() {
            return thumbnail;
        }


        private ContentObject(CruiseModelLight cruise, ResourceResolver resolver) {
            try {
                Externalizer externalizer = resolver.adaptTo(Externalizer.class);
                this.cruiseCode = cruise.getCruiseCode();
                this.destination = new Detailed(cruise.getDestinationId(), cruise.getDestination().getName(),
                        cruise.getDestination().getTitle());
                this.ship =
                        new Detailed(cruise.getShip().getId(), cruise.getShip().getName(), cruise.getShip().getTitle());
                this.lowestPrices = new LowestPrices(cruise);
                this.departureDate = new DateObject(cruise.getStartDate().toInstant());
                this.duration = cruise.getDuration();
                this.commercialName = cruise.getDeparturePortName() + " TO " + cruise.getArrivalPortName();
                this.departurePort = cruise.getDeparturePortName();
                this.arrivalPort = cruise.getArrivalPortName();

                this.description = new Localized(lang -> codeLangDescription.get(cruise.getCruiseCode(), lang));
                this.urls = new Localized(retrieveUrl(this.cruiseCode, externalizer, resolver));

                this.thumbnail = retrieveThumbnail(resolver, cruise);
            } catch (Throwable throwable) {
                log("Error during creation of json object with cruise code " + cruise.getCruiseCode(), throwable);
                throw throwable;
            }
        }

        private String retrieveThumbnail(ResourceResolver resolver, CruiseModelLight cruise) {
            return ofNullable(cruise.getThumbnail())
                    .map(thumb -> "https://silversea-h.assetsadobe2.com/is/image" + thumb).orElse(null);
        }


        public Detailed getDestination() {
            return destination;
        }

        private final String thumbnail;


        public String getDeparturePort() {
            return departurePort;
        }

        public String getArrivalPort() {
            return arrivalPort;
        }

        public String getCruiseCode() {
            return cruiseCode;
        }

        public Localized getUrls() {
            return urls;
        }

        public Detailed getShip() {
            return ship;
        }

        public LowestPrices getLowestPrices() {
            return lowestPrices;
        }

        public String getCommercialName() {
            return commercialName;
        }

        public DateObject getDepartureDate() {
            return departureDate;
        }

        public String getDuration() {
            return duration;
        }

        public Localized getDescription() {
            return description;
        }
    }

    private class Detailed {


        private final String id;

        private final String name;

        private final String title;

        Detailed(String id, String name, String title) {
            this.id = id;
            this.name = name;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

    }

    private class DateObject {
        private final String year;
        private final String month;
        private final String day;

        DateObject(Instant instant) {
            ZonedDateTime zonedInstant = instant.atZone(ZoneId.of("UTC"));
            this.year = zonedInstant.get(ChronoField.YEAR) + "";
            this.month = zonedInstant.get(ChronoField.MONTH_OF_YEAR) + "";
            this.day = zonedInstant.get(ChronoField.DAY_OF_MONTH) + "";
        }

        public String getYear() {
            return year;
        }

        public String getMonth() {
            return month;
        }

        public String getDay() {
            return day;
        }
    }

    private class LowestPrices {
        private final Price WAITING_LIST = new Price("WAITING LIST", "");
        private final Price asAUD;
        private final Price asUSD;
        private final Price euEUR;
        private final Price euGBP;
        private final Price euUSD;
        private final Price ftUSD;
        private final Price ftCAD;
        private final Price ukEUR;
        private final Price ukGBP;

        private final Price ukUSD;

        LowestPrices(CruiseModelLight cruise) {
            Map<String, Price> map = cruise.getLowestPrices().entrySet().stream().collect(Collectors
                    .toMap(Map.Entry::getKey,
                            entry -> new Price(entry.getValue().getComputedPrice() + "", entry.getValue().getCurrency())));
            this.asAUD = map.getOrDefault("asAUD", WAITING_LIST);
            this.asUSD = map.getOrDefault("asUSD", WAITING_LIST);
            this.euEUR = map.getOrDefault("euEUR", WAITING_LIST);
            this.euGBP = map.getOrDefault("euGBP", WAITING_LIST);
            this.euUSD = map.getOrDefault("euUSD", WAITING_LIST);
            this.ftUSD = map.getOrDefault("ftUSD", WAITING_LIST);
            this.ftCAD = map.getOrDefault("ftCAD", WAITING_LIST);
            this.ukEUR = map.getOrDefault("ukEUR", WAITING_LIST);
            this.ukGBP = map.getOrDefault("ukGBP", WAITING_LIST);
            this.ukUSD = map.getOrDefault("ukUSD", WAITING_LIST);
        }

        public Price getAsAUD() {
            return asAUD;
        }

        public Price getAsUSD() {
            return asUSD;
        }

        public Price getEuEUR() {
            return euEUR;
        }

        public Price getEuGBP() {
            return euGBP;
        }

        public Price getEuUSD() {
            return euUSD;
        }

        public Price getFtUSD() {
            return ftUSD;
        }

        public Price getUkEUR() {
            return ukEUR;
        }

        public Price getUkGBP() {
            return ukGBP;
        }

        public Price getUkUSD() {
            return ukUSD;
        }

        public Price getFtCAD() {
            return ftCAD;
        }
    }

    private class Price {
        private final String price;

        private final String currency;

        private Price(String price, String currency) {
            this.price = price;
            this.currency = currency;

        }

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

    }

    private Function<String, String> retrieveUrl(String code, Externalizer externalizer, ResourceResolver resolver) {
        return lang -> ofNullable(cacheService.getCruiseByCruiseCode(lang, code))
                .map(CruiseModelLight::getPath)
                .map(url -> externalizer.publishLink(resolver, url))
                .map(externalUrl -> externalUrl + ".html")
                .orElse(lang + " not found");
    }

    private class Localized {

        private final String en;
        private final String fr;
        private final String es;
        private final String pt;
        private final String de;

        private Localized(Function<String, String> langToValue) {
            this.en = langToValue.apply("en");
            this.fr = langToValue.apply("fr");
            this.es = langToValue.apply("es");
            this.pt = langToValue.apply("pt-br");
            this.de = langToValue.apply("de");
        }


        public String getEn() {
            return en;
        }

        public String getFr() {
            return fr;
        }

        public String getEs() {
            return es;
        }

        public String getPt() {
            return pt;
        }

        public String getDe() {
            return de;
        }

    }

}
