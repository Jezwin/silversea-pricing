package com.silversea.aem.components.editorial.findyourcruise2018;

import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.ValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private static final int DEFAULT_PAGE_SIZE = 20;

    private Locale locale;
    private String lang;
    private TagManager tagManager;

    private List<CruiseItem> cruises;

    private FilterBar filterBar;

    private Pagination pagination;

    @Override
    public void activate() throws Exception {
        super.activate();
        locale = getCurrentPage().getLanguage(false);
        lang = LanguageHelper.getLanguage(getCurrentPage());
        tagManager = getResourceResolver().adaptTo(TagManager.class);
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service == null) {
            return;
        }
        init(service);

    }

    public void init(CruisesCacheService service) { //this is here for test purposes
        Map<String, String[]> httpRequest = getFromWebRequest();
        pagination = retrieveResults(httpRequest, service);

    }

    public Pagination retrieveResults(Map<String, String[]> httpRequest, CruisesCacheService service) {
        List<CruiseModelLight> preFilteredCruises = preFiltering(service.getCruises(lang));

        boolean computeFilters = !"true".equals(httpRequest.getOrDefault("onlyResults", new String[]{"false"})[0]);
        boolean computeCruises = !"true".equals(httpRequest.getOrDefault("onlyFilters", new String[]{"false"})[0]);

        filterBar = initFilters(httpRequest, preFilteredCruises);

        List<CruiseModelLight> filteredCruises = applyFilters(preFilteredCruises, filterBar);
        if (computeFilters) {
            filterBar.updateFilters(preFilteredCruises, filteredCruises);
        }

        Pagination pagination = new Pagination(filteredCruises.size(),
                parseInt(httpRequest.getOrDefault("pag", new String[]{"1"})[0]),
                parseInt(httpRequest.getOrDefault("pagSize", new String[]{"" + DEFAULT_PAGE_SIZE})[0]));

        if (computeCruises) {
            cruises = retrievePaginatedCruises(pagination, filteredCruises);
        }
        return pagination;

    }

    List<CruiseModelLight> preFiltering(List<CruiseModelLight> allCruises) {
        Stream<CruiseModelLight> stream = allCruises.stream();

        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        Predicate<CruiseModelLight> hideToday = cruise -> cruise.getStartDate().after(today);


        if (ofNullable(getProperties().get("preFilterWaitlist", Boolean.class)).orElse(false)) {
            stream = stream.filter(cruise -> cruise.getLowestPrices().get(geomarket + currency) != null);
        }

        Optional<List<String>> voyageCodeList =
                ofNullable(getProperties().get("voyageCodeList", String.class)).map(list -> list.split(",")).map(Arrays::asList);
        if (voyageCodeList.isPresent()) {
            stream = stream.filter(cruise -> voyageCodeList.get().contains(cruise.getCruiseCode()));
        }

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

    private List<CruiseModelLight> applyFilters(List<CruiseModelLight> allCruises, FilterBar filterBar) {
        if (!filterBar.anyFilterSelected()) {
            return allCruises;
        }
        return allCruises.parallelStream().filter(filterBar::isCruiseMatching).collect(toCollection(LinkedList::new));
    }


    private List<CruiseItem> retrievePaginatedCruises(Pagination pagination, List<CruiseModelLight> lightCruises) {
        int pagSize = pagination.getPageSize();
        return lightCruises.stream()
                .sorted(Comparator.comparing(CruiseModelLight::getStartDate))
                .skip((pagination.getCurrentPage() - 1) * pagSize)
                .limit(pagSize)
                .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale))
                .collect(toCollection(() -> new ArrayList<>(pagSize)));
    }

    protected ValueMap properties() {
        return getProperties();
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
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }

    public TagManager getTagManager() {
        return tagManager;
    }

}
