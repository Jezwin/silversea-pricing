package com.silversea.aem.components.editorial.findyourcruise2018;

import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.FeatureModel;
import com.silversea.aem.models.FeatureModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.silversea.aem.components.editorial.findyourcruise2018.FilterRowState.ENABLED;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private static final int PAG_SIZE = 20;

    private Locale locale;
    private List<CruiseItem> cruises;
    private FilterBar filterBar;
    private String lang;
    private int pagNumber = 1;
    private int pagSize = 20;
    private int totalResults;
    private boolean onlyResults = false;
    private boolean onlyFilters = false;
    private TagManager tagManager;
    private String requestQuotePagePath;

    @Override
    public void activate() throws Exception {
        super.activate();
        locale = getCurrentPage().getLanguage(false);
        lang = LanguageHelper.getLanguage(getCurrentPage());
        tagManager = getResourceResolver().adaptTo(TagManager.class);
        requestQuotePagePath = PathUtils.getRequestQuotePagePath(getResource(), getCurrentPage());
        CruisesCacheService service = getSlingScriptHelper().getService(CruisesCacheService.class);
        if (service != null) {
            init(service);
        }
    }

    public void init(CruisesCacheService service) {
        List<CruiseModelLight> allCruises = service.getCruises(lang);
        allCruises = preFiltering(allCruises);
        filterBar = initFilters(allCruises);
        if (filterBar.anyFilterSelected()) {
            List<CruiseModelLight> filteredCruises = applyFilters(allCruises, filterBar);
            if (!onlyResults) {
                totalResults = filteredCruises.size();
                filterBar.updateFilters(allCruises, filteredCruises);
            }
            if (!onlyFilters) {
                totalResults = filteredCruises.size();
                cruises = retrievePaginatedCruises(filteredCruises);
            }
        } else {
            totalResults = allCruises.size();
            cruises = retrievePaginatedCruises(allCruises);
        }

    }

    List<CruiseModelLight> preFiltering(List<CruiseModelLight> allCruises) {
        /*
        Calendar today = Calendar.getInstance();
        return allCruises.stream().filter(cruise -> !cruise.getStartDate().before(today)).collect(Collectors.toList());
        */
        return allCruises;
    }

    private FilterBar initFilters(List<CruiseModelLight> allCruises) {
        FilterBar filterBar = new FilterBar();
        Map<String, String[]> map = getFromWebRequest();
        filterBar.init(this, map, allCruises);
        pagNumber = parseInt(map.getOrDefault("pag", new String[]{"1"})[0]);
        pagSize = parseInt(map.getOrDefault("pagSize", new String[]{"" + PAG_SIZE})[0]);
        onlyResults = "true".equals(map.getOrDefault("onlyResults", new String[]{"false"})[0]);
        onlyFilters = "true".equals(map.getOrDefault("onlyFilters", new String[]{"false"})[0]);
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


    private List<CruiseItem> retrievePaginatedCruises(List<CruiseModelLight> lightCruises) {
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

    public FilterBar getFilterBar() {
        return filterBar;
    }

    public String getRequestQuotePagePath() {
        return requestQuotePagePath;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

}
