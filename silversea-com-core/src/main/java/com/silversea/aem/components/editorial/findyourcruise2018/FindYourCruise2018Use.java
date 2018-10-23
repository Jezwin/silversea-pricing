package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private static final int PAG_SIZE = 20;

    private Locale locale;
    private List<CruiseItem> cruises;
    private FilterBar filterBar;
    private String lang;
    private int pagNumber = 1;
    private int pagSize = 20;
    private int totalResults;

    @Override
    public void activate() throws Exception {
        super.activate();
        locale = getCurrentPage().getLanguage(false);
        lang = LanguageHelper.getLanguage(getCurrentPage());
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service != null) {
            init(service);
        }
    }

    public void init(CruisesCacheService service) {
        List<CruiseModelLight> lightCruises;
        lightCruises = retrieveAllCruises(service);
        filterBar = initFilters(lightCruises);
        lightCruises = applyFilters(lightCruises, filterBar); //if non filter is selected...
        totalResults = lightCruises.size();
        updateNonSelectedFilters(lightCruises, filterBar);
        cruises = retrieveCruises(lightCruises);

    }

    private List<CruiseModelLight> retrieveAllCruises(CruisesCacheService service) {
        return service.getCruises(lang);
    }

    private FilterBar initFilters(List<CruiseModelLight> allCruises) {
        FilterBar filterBar = new FilterBar(allCruises);
        Map<String, String[]> map = getFromWebRequest();
        map.forEach(filterBar::addSelectedFilter);
        pagNumber = parseInt(map.getOrDefault("pag", new String[]{"1"})[0]);
        pagSize = parseInt(map.getOrDefault("pagSize", new String[]{"" + PAG_SIZE})[0]);
        return filterBar;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String[]> getFromWebRequest() {
        Map<String, String[]> map = new HashMap(((Map<String, String[]>) getRequest().getParameterMap()));
        map.replaceAll((key, value) -> value[0].split("\\."));
        return map;
    }

    private List<CruiseModelLight> applyFilters(List<CruiseModelLight> allCruises, FilterBar filterBar) {
        return allCruises.parallelStream().filter(filterBar::isCruiseMatching).collect(toCollection(LinkedList::new));
    }

    private void updateNonSelectedFilters(List<CruiseModelLight> cruises, FilterBar filterBar) {
        filterBar.updateFilters(cruises);
    }


    private List<CruiseItem> retrieveCruises(List<CruiseModelLight> lightCruises) {
        return lightCruises.stream()
                .sorted(Comparator.comparing(CruiseModelLight::getStartDate))
                .skip((pagNumber - 1) * pagSize)
                .limit(pagSize)
                .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale))
                .collect(toCollection(() -> new ArrayList<>(pagSize)));
    }

    public List<CruiseItem> getCruises() {
        return cruises;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public String getFilters() {
        return filterBar.toString();
    }

    public FilterBar getFilterBar() {
        return filterBar;
    }

    public String getRequestQuotePagePath() {
        return PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
    }
}
