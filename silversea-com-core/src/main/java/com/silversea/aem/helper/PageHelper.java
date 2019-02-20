package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.override.ExternalizerSSC;
import com.silversea.aem.services.GlobalCacheService;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class PageHelper extends WCMUsePojo {
    
    final static private Logger LOGGER = LoggerFactory.getLogger(PageHelper.class);
    
    private Page page;
    private String thumbnail;
    private String thumbnailInherited;
    private Map<String, String> languagePages;
    private String templateName;

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
                final InheritanceValueMap propertiesInherited = new HierarchyNodeInheritanceValueMap(resource);
                thumbnailInherited = propertiesInherited.getInherited("image/fileReference", String.class);

                /*final GlobalCacheService globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
                Resource finalResource = resource;
                thumbnailInherited = globalCacheService.getCache("thumbnailInherited-" + page.getParent().getPath(), String.class, () -> {
                    final InheritanceValueMap propertiesInherited = new HierarchyNodeInheritanceValueMap(finalResource);
                    return propertiesInherited.getInherited("image/fileReference", String.class);
                });*/

            }
        }

    }

    private void fillLanguagePages() {
        languagePages = new LinkedHashMap<>();
        String[] langList = {"/en/", "/es/", "/pt-br/", "/de/", "/fr/"};
        String currentPath = getCurrentPage().getPath();
        String currentLng = currentLang(langList, currentPath);
        otherLang(langList, currentPath, currentLng);
        if (currentLng.equals("")) {
            String[] langListHome = {"/en", "/es", "/pt-br", "/de", "/fr"};
            currentLng = currentLang(langListHome, currentPath);
            otherLang(langListHome, currentPath, currentLng);
        }
    }

    private void otherLang(String[] langList, String currentPath, String currentLang) {
        for (String lang : langList) {
            if (!currentPath.contains(lang)) {
                String newPath = currentPath.replace(currentLang, lang);
                Page page = getPageManager().getPage(newPath);
                if (page != null) {
                    Locale locale = page.getLanguage(false);
                    languagePages.put(locale.toLanguageTag(), ExternalizerSSC.externalLink(getResourceResolver(), Externalizer.LOCAL, newPath));
                }
            }
        }
    }

    private String currentLang( String[] langList, String currentPath) {
        for (String lang : langList) {
            if (currentPath.contains(lang)) {
                Page page = getPageManager().getPage(currentPath);
                if (page != null) {
                    Locale locale = page.getLanguage(false);
                    languagePages.put(locale.toLanguageTag(), ExternalizerSSC.externalLink(getResourceResolver(), Externalizer.LOCAL, currentPath));
                    return lang;
                }
            }
        }
        return "";
    }

    /**
     * @return the page for a given path
     */
    public Page getPage() {
        return page;
    }

    public Boolean isIndexable() {
        final String resourceType = page.getContentResource().getResourceType();
        return !(page.getProperties().get("notIndexed", false)
                || resourceType.endsWith(WcmConstants.RT_SUB_REDIRECT_PAGE)
                || WcmConstants.RT_HOTEL.equals(resourceType)
                || WcmConstants.RT_LAND_PROGRAMS.equals(resourceType)
                || WcmConstants.RT_EXCURSIONS.equals(resourceType)
                || WcmConstants.RT_TRAVEL_AGENT.equals(resourceType)
                || WcmConstants.RT_COMBO_SEGMENT.equals(resourceType)
                || WcmConstants.RT_EXCLUSIVE_OFFER.equals(resourceType)
                || WcmConstants.RT_EXCLUSIVE_OFFER_VARIATION.equals(resourceType)
                || WcmConstants.RT_LANDING_PAGE.equals(resourceType)
                || WcmConstants.RT_LIGHTBOX.equals(resourceType));
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
        templateName = PathUtils.getName(path);
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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
        if (languagePages == null) {
            fillLanguagePages();
        }
        return languagePages;
    }

    /**
     * @return the thumbnailInherited
     */
    public String getThumbnailInherited() {
        return thumbnailInherited;
    }
}