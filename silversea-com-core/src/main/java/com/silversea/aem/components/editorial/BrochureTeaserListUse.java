package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.RangeIterator;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.TagHelper;
import com.silversea.aem.models.BrochureModel;
import com.silversea.aem.services.GeolocationTagService;

/**
 *
 */
public class BrochureTeaserListUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochureTeaserListUse.class);
    public static final String SELECTOR_BROCHURE_GROUP_PREFIX = "brochure_group_";
    public static final String DEFAULT_BROCHURE_GROUP = "default";

    /**
     * List of brochure paths, based on selected language and geolocation
     */
    private List<BrochureModel> brochures;

    private List<BrochureModel> brochuresNotLanguageFiltered;

    private HashMap<String, String> languages;

    private String currentLanguage;
    
    private String brochureGroup;

    @Override
    public void activate() throws Exception {
        GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);

        // Getting context
        final String geolocationTagId = geolocationTagService.getTagFromRequest(getRequest());

        currentLanguage = LanguageHelper.getLanguage(getRequest());
        if (currentLanguage == null) {
            currentLanguage = LanguageHelper.getLanguage(getCurrentPage());
        }
        
        brochureGroup = getBrochureGroup(getRequest());
        
        final String brochuresPath = getProperties().get("folderReference", "/content/dam/silversea-com/brochures");

        // Building tag list
        List<String> tagList = new ArrayList<>();

        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        if (geolocationTagId != null) {
            Tag geolocationTag = tagManager.resolve(geolocationTagId);

            if (geolocationTag != null) {
                tagList.addAll(TagHelper.getTagIdsWithParents(geolocationTag));
            }
        }

        // Searching for brochures
        brochuresNotLanguageFiltered = new ArrayList<>();
        brochures = new ArrayList<>();
        languages = new HashMap<String, String>();

        LOGGER.debug("Searching brochures with tags: {}", tagList);

        //get brochures with the tagged localization
        RangeIterator<Resource> resources = tagManager.find(brochuresPath,
                tagList.toArray(new String[tagList.size()]), true);

        if (resources != null) {
            while (resources.hasNext()) {
                Resource resource = resources.next();
                Asset asset = resource.getParent().getParent().adaptTo(Asset.class);

                if (asset != null) {
                    BrochureModel brochure = asset.adaptTo(BrochureModel.class);
                    brochuresNotLanguageFiltered.add(brochure);
                }
            }

            // Building brochures list for current selected language
            List<BrochureModel> allGroupBrochures = new ArrayList<>();
            for (BrochureModel brochure : brochuresNotLanguageFiltered) {
                // Checking if found brochure correspond to the current language
                if (brochure.getLanguage().getName().equals(currentLanguage)) {
                    allGroupBrochures.add(brochure);
                }
            }
            
            // filter the list of brochures on the chosen brochure group tag
            for (BrochureModel brochure : allGroupBrochures) {
                // Checking if found brochure correspond to the chosen group
                if (brochure.getGroupNames().contains(brochureGroup)) {
                    brochures.add(brochure);
                }
            }

            // build language list
            for (BrochureModel brochure : brochuresNotLanguageFiltered) {
                Tag language = brochure.getLanguage();

                LOGGER.debug("Searching language for brochure: {}", brochure.getBrochurePath());

                if (language != null) {
                    LOGGER.debug("Adding language {} to language list", language.getTagID());

                    languages.put(language.getName(), language.getTitle());
                }
            }
        }
    }

    public List<BrochureModel> getBrochures() {
        return brochures;
    }

    public HashMap<String,String> getLanguages() {
        return languages;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public String getBrochureGroup(SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_BROCHURE_GROUP_PREFIX)) {
                return selector.replace(SELECTOR_BROCHURE_GROUP_PREFIX, "");
            }
        }
        return DEFAULT_BROCHURE_GROUP;
    }
}