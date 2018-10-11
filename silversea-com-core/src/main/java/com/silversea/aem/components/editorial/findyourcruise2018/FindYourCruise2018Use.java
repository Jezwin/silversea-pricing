package com.silversea.aem.components.editorial.findyourcruise2018;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;

import java.util.*;
import java.util.stream.Collectors;

import static com.silversea.aem.components.editorial.findyourcruise2018.Filter.buildSelectedFilter;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {

    private Collection<CruiseItem> cruises;
    private FilterBar filterBar;
    private List<CruiseModelLight> allCruises;
    private Locale locale;

    @Override
    public void activate() throws Exception {
        super.activate();
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service != null) {
            fetchCruises(service);
            setFilters();//set filters with values selected by user
            searchCruises();
            updateFilters();//update filters based on cruises found
        }
    }

    private void fetchCruises(CruisesCacheService service) {
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        locale = getCurrentPage().getLanguage(false);
        allCruises = service.getCruises(lang);
    }

    @SuppressWarnings("unchecked")
    private void setFilters() {
        Map<FilterLabel, Set<String>> allFiltersValues = retrieveAllFiltersValues(FilterLabel.values(), allCruises);
        filterBar = new FilterBar();
        filters = retrieveSetFilters(allFiltersValues, (Map<String, String[]>) getRequest().getParameterMap());
    }


    private void searchCruises() {
        cruises = retrieveCruises(allCruises, filters, locale);
    }

    private Collection<CruiseItem> retrieveCruises(List<CruiseModelLight> allCruises,
                                                   Map<FilterLabel, Filter> filters,
                                                   Locale locale) {
        return allCruises.stream()
                .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale))
                .filter(cruise -> filters.values().stream()
                        .anyMatch(filter -> isCruiseToBeKept(cruise.getCruiseModel(), filter)))
                .collect(Collectors.toList());
    }

    private boolean isCruiseToBeKept(CruiseModelLight cruise, Filter filter) {
        //TODO departure date && features duration...
        return filter.availableValues().contains(filter.getLabel().getMapper().apply(cruise));
    }

    private void updateFilters() {
        filters.putAll(retrieveUnsetFilters(cruises));
    }


    private Map<FilterLabel, Filter> retrieveSetFilters(Map<FilterLabel, Set<String>> allValues, Map<String,
            String[]> paramMap) {
        Map<FilterLabel, Filter> filters = new HashMap<>();
        for (FilterLabel filterLabel : FilterLabel.values()) {
            if (paramMap.containsKey(filterLabel.getLabel())) {
                Set<String> values = retrieveFromParam(filterLabel, paramMap.get(filterLabel.getLabel()));
                filters.put(filterLabel, buildSelectedFilter(filterLabel, allValues.get(filterLabel), values));
            }
        }
        return filters;
    }

    private Set<String> retrieveFromParam(FilterLabel filterLabel, String[] strings) {
        //TODO?
        return Sets.newHashSet(strings);
    }

    private Map<FilterLabel, Filter> retrieveUnsetFilters(Collection<CruiseItem> leftCruises) {
        Map<FilterLabel, Filter> unsetFilters = new HashMap<>();

        retrieveAllFiltersValues(FilterLabel.values(),
                leftCruises.stream().map(CruiseItem::getCruiseModel).collect(Collectors.toList()));
        return unsetFilters;
    }


    public Collection<CruiseItem> getCruises() {
        return cruises;
    }

    public String getFilters() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(filters);
    }
}