package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.NavPageModel;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.TypeReference;
import org.apache.jackrabbit.oak.commons.PathUtils;

import java.util.*;

public class HeaderUse extends WCMUsePojo {

    private Navigation navigation;
    private Page link1Page;
    private Page link2Page;
    private Page link3Page;
    private Page link4Page;
    private Page link3MobilePage;
    private Page searchPage;
    private Page homePage;
    private List<Page> languagePageList;
    private Map<String, String> languagePages;
    private List<NavPageModel> languagePageListForCurrentPage = new ArrayList<NavPageModel>();
    private GlobalCacheService globalCacheService;
    private HierarchyNodeInheritanceValueMap properties;

    private String currentPath;

    @Override
    public void activate() throws Exception {

        currentPath = getCurrentPage().getPath();
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);

        homePage = getCurrentPage().getAbsoluteParent(2);
        final String rootPath = getProp(WcmConstants.PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM, homePage.getPath());
        navigation = navigationBuild(rootPath, 2);

        final String link1Reference = getProp("link1Reference", "");
        final String link2Reference = getProp("link2Reference", "");
        final String link3Reference = getProp("link3Reference", "");
        final String link4Reference = getProp("link4Reference", "");
        final String link3MobileReference = getProp("link3MobileReference", "");
        final String searchPageReference = getProp("searchPageReference", "");

        link1Page = getPageManager().getPage(link1Reference);
        link2Page = getPageManager().getPage(link2Reference);
        link3Page = getPageManager().getPage(link3Reference);
        link4Page = getPageManager().getPage(link4Reference);
        link3MobilePage = getPageManager().getPage(link3MobileReference);
        searchPage = getPageManager().getPage(searchPageReference);

        languagePages = globalCacheService.getCache("languagePages" + currentPath, new TypeReference<Map<String, String>>() {
        }, this::retrieveLanguagePages);
        languagePageList = globalCacheService.getCache("languagePageList" + currentPath, new TypeReference<List<Page>>() {
        }, () -> {
            List<Page> languagePageList = new ArrayList<>();
            final Iterator<Page> homeLangIt = homePage.getParent().listChildren(new PageFilter());

            Page homeLang;
            while (homeLangIt.hasNext()) {
                homeLang = homeLangIt.next();
                if (!homeLang.getPath().equals(homePage.getPath())) {
                    languagePageList.add(homeLang);
                }
            }

            return languagePageList;
        });

        languagePageListForCurrentPage = globalCacheService.getCache("languagePageListForCurrentPage" + currentPath, new TypeReference<List<NavPageModel>>() {
        }, () -> {
            List<NavPageModel> languagePageListForCurrentPageInner = new ArrayList<>();
      for (Page languagePage : languagePageList) {
          languagePages.forEach((lang, value) -> {
              if (languagePage.getPath().toLowerCase().contains(lang.toLowerCase())) {
                  NavPageModel nPage = new NavPageModel();
                  nPage.setPath(value);
                  nPage.setName(languagePage.getName());
                  nPage.setNavigationTitle(languagePage.getNavigationTitle());
                  languagePageListForCurrentPageInner.add(nPage);
              }
          });
      }

      return languagePageListForCurrentPageInner;
        });


    }

    private String getProp(String key, String defaultValue) {
        return globalCacheService.getCache(key + currentPath, String.class, () -> getHierarchyProperties().getInherited(key, defaultValue));
    }

    private InheritanceValueMap getHierarchyProperties() {
        if (properties == null) {
            properties = new HierarchyNodeInheritanceValueMap(getResource());
        }
        return properties;
    }

    /**
     * @param path
     * @param maxLevel
     * @return navigation content tree
     */
    private Navigation navigationBuild(final String path, final int maxLevel) {
        final Page pageRoot = getPageManager().getPage(path);
        final Page selectPage = PathUtils.isAncestor(pageRoot.getPath(), getCurrentPage().getPath()) ? getCurrentPage() : pageRoot;

        return new Navigation(selectPage, PathUtils.getDepth(path) - 1, new PageFilter(), maxLevel);
    }

    /**
     * @return the navigation
     */
    public Navigation getNavigation() {
        return navigation;
    }

    /**
     * @return the link1Page
     */
    public Page getLink1Page() {
        return link1Page;
    }

    /**
     * @return the link2Page
     */
    public Page getLink2Page() {
        return link2Page;
    }

    /**
     * @return the link3Page
     */
    public Page getLink3Page() {
        return link3Page;
    }

    public Page getLink4Page() {
        return link4Page;
    }

    /**
     * @return the link3MobilePage
     */
    public Page getLink3MobilePage() {
        return link3MobilePage;
    }

    /**
     * @return the searchPage
     */
    public Page getSearchPage() {
        return searchPage;
    }

    /**
     * @return the homePage
     */
    public Page getHomePage() {
        return homePage;
    }

    /**
     * @return the languagePageList
     */
    public List<Page> getLanguagePageList() {
        return languagePageList;
    }

    public List<NavPageModel> getLanguagePageListForCurrentPage() {
        return languagePageListForCurrentPage;
    }

    private HashMap<String, String> retrieveLanguagePages() {
        HashMap<String, String> languagePages = new HashMap<>();
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        String[] langList = {"/en/", "/es/", "/pt-br/", "/de/", "/fr/"};
        String currentPath = getCurrentPage().getPath();
        String currentLng = currentLang(externalizer, langList, currentPath, "", languagePages);

        otherLang(externalizer, langList, currentPath, currentLng, languagePages);

        if ("".equals(currentLng)) {
            String[] langListHome = {"/en", "/es", "/pt-br", "/de", "/fr"};
            currentLng = currentLang(externalizer, langListHome, currentPath, currentLng, languagePages);
            otherLang(externalizer, langListHome, currentPath, currentLng, languagePages);
        }
        return languagePages;

    }

    private void otherLang(Externalizer externalizer, String[] langList, String currentPath, String currentLng, HashMap<String, String> languagePages) {
        Locale locale;
        for (String lng : langList) {
            if (!currentPath.contains(lng)) {
                String newPath = currentPath.replace(currentLng, lng);
                Page page = getPageManager().getPage(newPath);
                if (page != null) {
                    locale = page.getLanguage(false);
                    languagePages
                            .put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, newPath));
                }
            }
        }
    }

    private String currentLang(Externalizer externalizer, String[] langList, String currentPath, String currentLng, HashMap<String, String> languagePages) {
        Locale locale;
        for (String lng : langList) {
            if (currentPath.contains(lng)) {
                Page page = getPageManager().getPage(currentPath);
                if (page != null) {
                    locale = page.getLanguage(false);
                    languagePages
                            .put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, currentPath));
                    currentLng = lng;
                }
            }
        }
        return currentLng;
    }
}