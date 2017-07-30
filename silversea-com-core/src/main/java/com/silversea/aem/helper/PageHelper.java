package com.silversea.aem.helper;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.RangeIterator;

import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.BlueprintManager;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

public class PageHelper extends WCMUsePojo {
    static final private Logger LOGGER = LoggerFactory.getLogger(PageHelper.class);
    private Page page;
    private String thumbnail;
    private Map<String, String> languagePages;

    @Override
    public void activate() throws Exception {
        String path = get("path", String.class);
        Resource resource = null;
        if (path != null) {
            resource = getResourceResolver().getResource(path);
        }

        if (resource != null) {
            page = resource.adaptTo(Page.class);

            if (page != null) {
                Resource imageRes = page.getContentResource("image");
                if (imageRes != null) {
                    thumbnail = imageRes.getValueMap().get("fileReference", String.class);
                }
            }
        }

        // Get hrefLang page
        languagePages = fillLanguagePages();
    }

    private Map<String, String> fillLanguagePages() throws WCMException {
        Resource currentRes = getCurrentPage().adaptTo(Resource.class);
        LiveRelationshipManager liveRelationshipManager = getResourceResolver().adaptTo(LiveRelationshipManager.class);
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        Locale locale;

        String bluePrintPath = "";

        if (liveRelationshipManager.hasLiveRelationship(currentRes)) {
            // Current page is a livecopy
            LiveRelationship liveRelationship = liveRelationshipManager.getLiveRelationship(currentRes, false);
            // Set blue print path
            bluePrintPath = liveRelationship.getSourcePath();
        } else if (liveRelationshipManager.isSource(currentRes)) {
            // Current page is blueprint
            bluePrintPath = getCurrentPage().getPath();
        }

        languagePages = new LinkedHashMap<String, String>();
        Resource bluePrintRes = getResourceResolver().getResource(bluePrintPath);

        // Add blueprint
        locale = getPageManager().getPage(bluePrintPath).getLanguage(false);
        languagePages.put(locale.getLanguage(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, bluePrintPath));

        RangeIterator liveRelationships = liveRelationshipManager.getLiveRelationships(bluePrintRes, null, null);

        while (liveRelationships.hasNext()) {
            LiveRelationship liveRelationship = (LiveRelationship) liveRelationships.next();
            Resource targetRes = getResourceResolver().getResource(liveRelationship.getTargetPath());
            locale = targetRes.adaptTo(Page.class).getLanguage(false);

            // Add livecopy
            languagePages.put(locale.getLanguage(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, liveRelationship.getTargetPath()));
        }

        return languagePages;
    }

    /**
     * @return the page for a given path
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return the homePage for the current language web site
     */
    public Page getHomePage() {
        return getCurrentPage().getAbsoluteParent(2);
    }

    /**
     * @return the templateName of the current page
     */
    public String getTemplateName() {
        String path = getCurrentPage().getProperties().get(NameConstants.NN_TEMPLATE, String.class);
        return PathUtils.getName(path);
    }

    /**
     * @return the page for a given path
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return the languagePages
     */
    public Map<String, String> getLanguagePages() {
        return languagePages;
    }
}