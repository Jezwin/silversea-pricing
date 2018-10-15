package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private Locale locale;
    private List<CruiseModelLight> cruisesModelLight;
    private List<CruiseItem> cruises;
    private FilterBar filterBar;

    @Override
    public void activate() throws Exception {
        super.activate();
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service != null) {
            locale = getCurrentPage().getLanguage(false);
            cruisesModelLight = retrieveAllCruises(service);
            filterBar = initFilters(cruisesModelLight);
            cruisesModelLight = applyFilters(cruisesModelLight, filterBar);
            updateNonSelectedFilters(cruisesModelLight, filterBar);
        }
    }

    private List<CruiseModelLight> retrieveAllCruises(CruisesCacheService service) {
        final String lang = LanguageHelper.getLanguage(getCurrentPage());
        return service.getCruises(lang);
    }

    private FilterBar initFilters(List<CruiseModelLight> allCruises) {
        FilterBar filterBar = new FilterBar(allCruises);
        fromRequest().forEach(filterBar::addSelectedFilter);
        return filterBar;
    }

    @SuppressWarnings("unchecked")
    private Map<FilterLabel, Set<String>> fromRequest() {
        Map<String, String[]> parameterMap = getRequest().getParameterMap();
        return FilterLabel.VALUES.stream().filter(label -> parameterMap.containsKey(label.getLabel()))
                .collect(toMap(label -> label, label -> new HashSet<>(asList(parameterMap.get(label.getLabel())))));
    }

    private List<CruiseModelLight> applyFilters(List<CruiseModelLight> allCruises, FilterBar filterBar) {
        return allCruises.stream().filter(filterBar::isCruiseMatching).collect(toList());
    }

    private void updateNonSelectedFilters(List<CruiseModelLight> cruises, FilterBar filterBar) {
        filterBar.updateFilters(cruises);
    }


    public List<CruiseItem> getCruises() {
        if (cruises == null) {
            cruises = cruisesModelLight.stream()
                    .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale)).collect(toList());
        }
        return cruises;
    }

    public String getFilters() {
        return filterBar.toString();
    }

}
