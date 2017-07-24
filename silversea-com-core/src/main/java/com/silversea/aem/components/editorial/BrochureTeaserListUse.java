package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
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
    public static final String SELECTOR_LANGUAGE_PREFIX = "language_";
    public static final String DEFAULT_BROCHURE_GROUP = "default";

    /**
     * List of brochure paths, based on selected language and geolocation
     */
    private List<BrochureModel> brochures;

    private List<BrochureModel> brochuresNotLanguageFiltered;

    private HashMap<String, String> languages;

    private String currentLanguage;

    private String brochureGroup;

    private List<String> geolocationTags;

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

        final String brochuresPath = getProperties().get("folderReference", 
                WcmConstants.PATH_DAM_SILVERSEA + "/" + WcmConstants.FOLDER_BROCHURES);

        // Building tag list
        buildGeolocationTagList(geolocationTagId);

        // Searching for brochures
        brochuresNotLanguageFiltered = new ArrayList<>();
        brochures = new ArrayList<>();
        languages = new HashMap<String, String>();

        LOGGER.debug("Searching brochures with tags: {}", geolocationTags);

        // get all brochures listed in order
        Resource brochuresRoot = getResourceResolver().getResource(brochuresPath);

        // filter brochures by localization, language, and group
        Iterator<Resource> resources = brochuresRoot.listChildren();
        filterResources(resources);

        // build language list
        buildLanguageList();
    }

    private void buildGeolocationTagList(String geolocationTagId) {
        geolocationTags = new ArrayList<>();

        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        if (geolocationTagId != null) {
            Tag geolocationTag = tagManager.resolve(geolocationTagId);

            if (geolocationTag != null) {
                geolocationTags.addAll(TagHelper.getTagIdsWithParents(geolocationTag));
            }
        }
    }

    private void buildLanguageList() {
        for (BrochureModel brochure : brochuresNotLanguageFiltered) {
            Tag language = brochure.getLanguage();

            LOGGER.debug("Searching language for brochure: {}", brochure.getBrochurePath());

            if (language != null) {
                LOGGER.debug("Adding language {} to language list", language.getTagID());

                languages.put(language.getName(), language.getTitle());
            }
        }
    }

    public void filterResources(Iterator<Resource> resources) {
        while (resources.hasNext()) {
            Resource resource = resources.next();
            String type = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
            if (type.equals(DamConstants.NT_DAM_ASSET)) {
                Asset asset = resource.adaptTo(Asset.class);
                if (asset != null) {
                    BrochureModel brochure = asset.adaptTo(BrochureModel.class);
                    // check localization
                    if (isMatchingLocation(brochure, geolocationTags)) {
                        // check group
                        if (isMatchingGroup(brochure, brochureGroup)) {
                            brochuresNotLanguageFiltered.add(brochure);
                            // check language
                            if (isMatchingLanguage(brochure, currentLanguage)) {
                                // brochure matches all criteria
                                brochures.add(brochure);
                            }
                        }
                    }
                }
            } else if (type.equals(DamConstants.NT_SLING_ORDEREDFOLDER)) {
                filterResources(resource.listChildren());
            }
        }
    }

    public boolean isMatchingLocation(BrochureModel brochure, List<String> localizationTags) {
        ArrayList<Tag> brochureLocations = brochure.getLocalizations();
        for (Tag brochureLocation : brochureLocations) {
            if (localizationTags.contains(brochureLocation.getTagID())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMatchingGroup(BrochureModel brochure, String groupName) {
        return brochure.getGroupNames().contains(groupName);
    }

    public boolean isMatchingLanguage(BrochureModel brochure, String languageName) {
        Tag brochureLang = brochure.getLanguage();
        if (brochureLang != null) {
            return brochureLang.getName().toUpperCase().equals(languageName.toUpperCase());
        }
        return false;
    }

    public List<BrochureModel> getBrochures() {
        return brochures;
    }

    public HashMap<String, String> getLanguages() {
        return languages;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public String getCurrentBrochureGroupSelector() {
        return SELECTOR_BROCHURE_GROUP_PREFIX + brochureGroup;
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

    public String getRequestedLanguage(SlingHttpServletRequest request) {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (String selector : selectors) {
            if (selector.startsWith(SELECTOR_LANGUAGE_PREFIX)) {
                return selector.replace(SELECTOR_LANGUAGE_PREFIX, "");
            }
        }
        return null;
    }
}