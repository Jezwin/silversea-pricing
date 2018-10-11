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

import static com.silversea.aem.components.editorial.findyourcruise2018.Filter.selectedFilter;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {

    private Collection<CruiseItem> cruises;
    private Map<FilterLabel, Filter> filters;
    private List<CruiseModelLight> allCruises;
    private Locale locale;

    @Override
    public void activate() throws Exception {
        super.activate();
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service != null) {
            setFilters(service);
            search();
            updateFilters();
        }
    }

    @SuppressWarnings("unchecked")
    private void setFilters(CruisesCacheService cruisesCacheService) {
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        locale = getCurrentPage().getLanguage(false);
        allCruises = cruisesCacheService.getCruises(lang);
        Map<FilterLabel, Set<String>> allFiltersValues = retrieveAllFiltersValues(allCruises);
        filters = retrieveSetFilters(allFiltersValues, (Map<String, String[]>) getRequest().getParameterMap());
    }

    private Map<FilterLabel, Set<String>> retrieveAllFiltersValues(List<CruiseModelLight> allCruises) {
        Map<FilterLabel, Set<String>> result = new HashMap<>();
        for (FilterLabel label : FilterLabel.values()) {
            Set<String> values = new HashSet<>();
            result.put(label, values);
        }
        for (CruiseModelLight cruise : allCruises) {
            for (FilterLabel filter : FilterLabel.values()) {
                switch (filter) {
                    case DESTINATION:
                    case DEPARTURE:
                    case DURATION:
                    case SHIP:
                    case TYPE:
                        result.get(filter).add(filter.getMapper().apply(cruise));
                        break;
                    case PORT:
                    case FEATURES:
                        result.get(filter).addAll(Arrays.asList(filter.getMapper().apply(cruise).split(":")));
                        break;
                }
            }
        }
        return result;
    }

    private void search() {
        cruises = retrieveCruises(allCruises, filters, locale);
    }

    private Collection<CruiseItem> retrieveCruises(List<CruiseModelLight> allCruises,
                                                   Map<FilterLabel, Filter> filters,
                                                   Locale locale) {
        //TODO
        return allCruises.stream().map(cruise -> new CruiseItem(cruise, geomarket, currency, locale))
                .collect(Collectors.toList());
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
                filters.put(filterLabel, selectedFilter(filterLabel, allValues.get(filterLabel), values));
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
        //TODO
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