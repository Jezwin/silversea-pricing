package com.silversea.aem.components.editorial.findyourcruise2018;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.beans.CruiseItem;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class FindYourCruise2018Use extends AbstractGeolocationAwareUse {


    private Locale locale;
    private List<CruiseModelLight> cruisesModelLight;
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
        cruisesModelLight = retrieveAllCruises(service);
        filterBar = initFilters(cruisesModelLight);
        cruisesModelLight = applyFilters(cruisesModelLight, filterBar);
        updateNonSelectedFilters(cruisesModelLight, filterBar);

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
    protected Map<String, String[]> getFromWebRequest(){
        return getRequest().getParameterMap();
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

    FilterBar getFilterBar() {
        return filterBar;
    }
}
