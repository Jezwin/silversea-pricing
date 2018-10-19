package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.utils.PathUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private Locale locale;
    private List<CruiseModelLight> lightCruises;
    private List<CruiseItem> cruises;
    private FilterBar filterBar;
    private String lang;

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
        lightCruises = retrieveAllCruises(service);
        filterBar = initFilters(lightCruises);
        lightCruises = applyFilters(lightCruises, filterBar);
        updateNonSelectedFilters(lightCruises, filterBar);

    }

    private List<CruiseModelLight> retrieveAllCruises(CruisesCacheService service) {
        return service.getCruises(lang);
    }

    private FilterBar initFilters(List<CruiseModelLight> allCruises) {
        FilterBar filterBar = new FilterBar(allCruises);
        getFromWebRequest().forEach(filterBar::addSelectedFilter);
        return filterBar;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String[]> getFromWebRequest() {
        Map<String, String[]> map = new HashMap(((Map<String, String[]>) getRequest().getParameterMap()));
        map.replaceAll((key, value) -> value[0].split("\\."));
        return map;
    }

    private List<CruiseModelLight> applyFilters(List<CruiseModelLight> allCruises, FilterBar filterBar) {
        return allCruises.stream().filter(filterBar::isCruiseMatching).collect(toList());
    }

    private void updateNonSelectedFilters(List<CruiseModelLight> cruises, FilterBar filterBar) {
        filterBar.updateFilters(cruises);
    }


    public List<CruiseItem> getCruises() {
        if (cruises == null) {
            cruises = lightCruises.stream()
                    .sorted(Comparator.comparing(CruiseModelLight::getStartDate))
                    .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale)).collect(toList());
        }
        return cruises;
    }

    public List<CruiseItem> getCruisesPaginated(int pag, int pagSize) {//pag starts from 1, just an idea, TODO...
        return lightCruises.stream().sorted(Comparator.comparing(CruiseModelLight::getStartDate))
                .skip((pag - 1) * pagSize).limit(pagSize)
                .map(cruise -> new CruiseItem(cruise, geomarket, currency, locale)).collect(toList());
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
