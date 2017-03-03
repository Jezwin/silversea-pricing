package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.services.GeolocationTagCacheService;
import com.silversea.aem.helper.GeolocationHelper;

public class BrochureTeaserListUse extends WCMUsePojo {

    private static final String COUNTRY_ID_PARAM = "countryIdParam";

    private static final String PATH_TO_BROCHURES_DAM = "/jcr:root/content/dam/siversea-com";

    private List<String> brochureList;

    private GeolocationTagCacheService geolocService;

    private Map<String, String> langMap;

    private String currentCountrySelector;

    private String currentLanguageSelector;

    @Override
    public void activate() throws Exception {

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);

        String tagId = geolocService.getTagIdFromCurrentRequest(getResourceResolver(), getRequest());

        currentCountrySelector = GeolocationHelper.getCountryCodeSelector(getRequest().getRequestPathInfo().getSelectors());

        currentLanguageSelector = GeolocationHelper.getLanguageSelector(getRequest().getRequestPathInfo().getSelectors());

        String langugeCode = geolocService.getLanguageCodeCurrentRequest(getResourceResolver(), getRequest());

        langMap = new HashMap<String, String>();
        for (String currentLang : geolocService.getLangList(getResourceResolver())) {
            langMap.put(GeolocationHelper.LANGUAGE_PREFIX + currentLang, currentLang);
        }

        String langugeCodeQuerie = "";
        if (langugeCode != null && !"".equals(langugeCode)) {
            langugeCodeQuerie = "/" + langugeCode;
        }

        Iterator<Resource> resources = getResourceResolver().findResources(
                PATH_TO_BROCHURES_DAM + langugeCodeQuerie + "/brochures//*[jcr:content/metadata/@cq:tags=\"" + tagId + "\"]",
                "xpath");
        brochureList = new ArrayList<String>();

        while (resources.hasNext()) {
            Node node = resources.next().adaptTo(Node.class);
            brochureList.add(node.getPath());
        }
    }

    public List<String> getBrochureList() {
        return brochureList;
    }

    public Map<String, String> getLangMap() {
        return langMap;
    }

    public String getCurrentCountrySelector() {
        return currentCountrySelector;
    }

    public String getCurrentLanguageSelector() {
        return currentLanguageSelector;
    }
}
