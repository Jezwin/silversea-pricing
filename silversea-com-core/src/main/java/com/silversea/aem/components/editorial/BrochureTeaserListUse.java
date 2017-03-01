package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.services.GeolocationTagCacheService;

public class BrochureTeaserListUse extends WCMUsePojo {

    private static final String COUNTRY_ID_PARAM = "countryIdParam";

    private static final String PATH_TO_BROCHURES_DAM = "/jcr:root/content/dam/siversea-com/brochures";

    private List<String> brochureList;

    private GeolocationTagCacheService geolocService;

    private List<String> langList;

    private String currentCountrySelector;

    @Override
    public void activate() throws Exception {

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);

        String tagId = geolocService.getTagIdFromCurrentRequest(getResourceResolver(), getRequest());

        currentCountrySelector = getRequest().getRequestPathInfo().getSelectors()[0];

        String langugeCode = geolocService.getLanguageCodeCurrentRequest(getResourceResolver(), getRequest());

        langList = geolocService.getLangList(getResourceResolver());

        String langugeCodeQuerie = "";
        if (langugeCode != null && !"".equals(langugeCode)) {
            langugeCodeQuerie = "/" + langugeCode;
        }

        Iterator<Resource> resources = getResourceResolver().findResources(PATH_TO_BROCHURES_DAM + langugeCodeQuerie + "//*[jcr:content/metadata/@cq:tags=\"" + tagId + "\"]", "xpath");
        brochureList = new ArrayList<String>();

        while (resources.hasNext()) {
            Node node = resources.next().adaptTo(Node.class);
            brochureList.add(node.getPath());
        }
    }

    public List<String> getBrochureList() {
        return brochureList;
    }

    public List<String> getLangList() {
        return langList;
    }

    public String getCurrentCountrySelector() {
        return currentCountrySelector;
    }
}
