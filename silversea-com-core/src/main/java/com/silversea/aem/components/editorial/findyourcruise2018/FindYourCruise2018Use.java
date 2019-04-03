package com.silversea.aem.components.editorial.findyourcruise2018;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.components.editorial.findyourcruise2018.filters.*;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.OfferPriorityModel;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.time.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private static final String DEFAULT_PAGE_SIZE = "12";

    private Locale locale;
    private String lang;
    private TagManager tagManager;

    private String worldCruisePath;
    private String grandVoyagePath;

    private List<CruiseItem> cruises;

    private FilterBar filterBar;

    private Pagination pagination;
    private String requestQuotePagePath;
    private List<OfferPriorityModel> priorityOffer;


    @Override
    public void activate() throws Exception {
        super.activate();

        Page currentPage = getCurrentPage();
        Resource resource = getResource();
        SlingHttpServletRequest request = getRequest();
        ResourceResolver resourceResolver = getResourceResolver();
        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        lang = LanguageHelper.getLanguage(currentPage);
        locale = currentPage.getLanguage(false);
        tagManager = resourceResolver.adaptTo(TagManager.class);
        priorityOffer = retrievePriorityOffer();

        worldCruisePath = externalizer.relativeLink(request, PathUtils.getWorldCruisesPagePath(resource, currentPage));
        grandVoyagePath = externalizer.relativeLink(request, PathUtils.getGrandVoyagesPagePath(resource, currentPage));
        requestQuotePagePath = retrieveRequestQuotePath(resource);
        String paginationLimit = null;

        if (getProp("paginationLimit", String.class).isPresent()) {
            paginationLimit = getProp("paginationLimit", String.class).get();
        } else {
            paginationLimit = ofNullable(getCurrentStyle()).map(style -> style.get("paginationLimit", String.class)).orElse(DEFAULT_PAGE_SIZE);
        }

        Optional<List<CruiseModelLight>> allCruises =
                ofNullable(getSlingScriptHelper().getService(CruisesCacheService.class)).map(service -> {
                    try {
                        return service.getCruises(lang);
                    } catch (Throwable throwable) {
                        return null;
                    }
                });
        if (allCruises.isPresent()) {
            init(allCruises.get(), paginationLimit);
        } else {
            //to avoid displaying blank pages when error occurs
            dullInit();
        }

    }

    public List<OfferPriorityModel> retrievePriorityOffer() {
        return retrieveMultiField("priorityOffer", OfferPriorityModel.class);
    }

    private String retrieveRequestQuotePath(Resource resource) {
        return ofNullable(resource.adaptTo(Conf.class))
                .map(conf -> conf.getItemResource("requestquotepage/page"))
                .map(conf -> conf.getValueMap().get("reference", String.class))
                .map(reference -> "/content/silversea-com/" + lang + reference)
                .orElse("");
    }

    private void dullInit() {
        pagination = Pagination.EMPTY;
        cruises = Collections.emptyList();
        filterBar = new FilterBar();
        filterBar.init(this, Collections.emptyMap(), Collections.emptyList());

    }

    public void init(List<CruiseModelLight> allCruises, String paginationLimit) { //this is here for test purposes
        Map<String, String[]> httpRequest = getFromWebRequest();
        pagination = retrieveResults(httpRequest, allCruises, paginationLimit);

    }

    private Pagination retrieveResults(Map<String, String[]> httpRequest, List<CruiseModelLight> allCruises, String paginationLimit) {
        List<CruiseModelLight> preFilteredCruises = preFiltering(allCruises);

        boolean computeFilters = !"true".equals(httpRequest.getOrDefault("onlyResults", new String[]{"false"})[0]);
        boolean computeCruises = !"true".equals(httpRequest.getOrDefault("onlyFilters", new String[]{"false"})[0]);

        filterBar = initFilters(httpRequest, allCruises);

        List<CruiseModelLight> filteredCruises = applyFilters(preFilteredCruises, filterBar);
        if (computeFilters) {
            filterBar.updateFilters(preFilteredCruises, filteredCruises);
        }

        Pagination pagination = new Pagination(filteredCruises.size(),
                parseInt(httpRequest.getOrDefault("pag", new String[]{"1"})[0]),
                parseInt(httpRequest.getOrDefault("pagSize", new String[]{paginationLimit})[0]));


        if (computeCruises) {
            this.cruises = retrievePaginatedCruises(pagination, filteredCruises);
        }
        return pagination;
    }

    private Map<String, String[]> addFilterByPage() {
        final String currentPageResourceType = getCurrentPage().getContentResource().getResourceType();
        Map<String, String[]> map = new HashMap<>();
        String[] value = new String[]{getCurrentPage().getName()};
        if (getProperties().get("noPageContent") == null || getProperties().get("noPageContent", String.class).isEmpty() || getProperties().get("noPageContent", String.class).equals("false")) {
            switch (currentPageResourceType) {
                case WcmConstants.RT_DESTINATION:
                    map.put(DestinationFilter.KIND + "Id", new String[]{getCurrentPage().getProperties().get("destinationId", String.class)});
                    break;
                case WcmConstants.RT_PORT:
                    map.put(PortFilter.KIND + "Id", value);
                    break;
                case WcmConstants.RT_SHIP:
                    map.put(ShipFilter.KIND + "Id", new String[]{getCurrentPage().getProperties().get("shipId", String.class)});
                    break;
                case WcmConstants.RT_EXCLUSIVE_OFFER:
                    map.put(OffersFilter.KIND + "Id", new String[]{getCurrentPage().getPath()});
                case WcmConstants.RT_FEATURE:
                    final Tag[] pageTags = getCurrentPage().getTags();
                    if (pageTags != null) {
                        String[] tags =
                                Arrays.stream(pageTags).filter(tag -> tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_FEATURES))
                                        .map(tag -> tag.adaptTo(FeatureModel.class)).filter(Objects::nonNull).map(FeatureModel::getFeatureId)
                                        .toArray(String[]::new);
                        map.put(FeatureFilter.KIND + "Id", tags);
                    }
                    break;
            }
        }
        return map;
    }

    List<CruiseModelLight> preFiltering(List<CruiseModelLight> allCruises) {
        //prefilter for single filters is not done here, it's in the filter initialization, in the selectedKeys (when this comment was written!)
        Stream<CruiseModelLight> stream = allCruises.stream();

        Instant today = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC);
        Predicate<CruiseModelLight> hideToday = cruise -> cruise.getStartDate().toInstant().isAfter(today);

        Optional<List<String>> voyageCodeList =
                ofNullable(getProperties().get("voyagecodelist", String.class)).map(list -> list.split(",")).map(Arrays::asList);
        if (voyageCodeList.isPresent()) {
            stream = stream.filter(cruise -> voyageCodeList.get().contains(cruise.getCruiseCode()));
        }

        stream = stream.filter(getProp("periodslist").map(DepartureFilter::prefilterPeriods).orElse(cruise -> true));

        stream = stream.filter(hideToday);

        return stream.collect(Collectors.toList());
    }

    private FilterBar initFilters(Map<String, String[]> httpRequest, List<CruiseModelLight> allCruises) {
        FilterBar filterBar = new FilterBar();
        filterBar.init(this, httpRequest, allCruises);
        return filterBar;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String[]> getFromWebRequest() {
        Map<String, String[]> map = new HashMap(((Map<String, String[]>) getRequest().getParameterMap()));
        map.replaceAll((key, value) -> value[0].split("\\."));
        return map;
    }

    private List<CruiseModelLight> applyFilters(List<CruiseModelLight> prefilteredCruises, FilterBar filterBar) {
        if (!filterBar.anyFilterSelected()) {
            return prefilteredCruises;
        }
        return prefilteredCruises.parallelStream().filter(filterBar::isCruiseMatching).collect(toCollection(LinkedList::new));
    }


    private List<CruiseItem> retrievePaginatedCruises(Pagination pagination, List<CruiseModelLight> lightCruises) {
        int pagSize = pagination.getPageSize();
        return lightCruises.stream()
                .sorted(filterBar.comparator(this))
                .skip((pagination.getCurrent() - 1) * pagSize)
                .limit(pagSize)
                .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale))
                .collect(toCollection(() -> new ArrayList<>(pagSize)));
    }

    protected Map<String, String[]> filteringSettings() {
        ValueMap properties = getProperties();
        Map<String, String[]> map = new HashMap<>(addFilterByPage());
        properties.forEach((key, value) -> {
            if (value instanceof String[]) {
                map.put(key, (String[]) value);
            } else if (value instanceof String) {
                map.put(key, ((String) value).split(","));
            } else if (value instanceof Boolean) {
                map.put(key, new String[]{value.toString()});
            }
        });
        return map;
    }

    public List<CruiseItem> getCruises() {
        return cruises;
    }

    public int getTotalResults() {
        return pagination.getTotalResults();
    }

    public FilterBar getFilterBar() {
        return filterBar;
    }

    public String getRequestQuotePagePath() {
        return requestQuotePagePath;
    }

    public TagManager getTagManager() {
        return tagManager;
    }


    public String getWorldCruisePath() {
        return worldCruisePath;
    }

    public String getGrandVoyagePath() {
        return grandVoyagePath;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public String getMarketCurrency() {
        return super.geomarket + super.currency;
    }

    public String toJson() {
        JsonArray array = new JsonArray();
        Gson gson = new Gson();
        cruises.forEach(cruise -> array.add(gson.toJsonTree(cruise)));
        return array.toString();
    }

    public List<OfferPriorityModel> getPriorityOffer() {
        return priorityOffer;
    }
}
