package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;
import com.silversea.aem.constants.WcmConstants;

public class HeaderUse extends WCMUsePojo {
    private Navigation navigation;
    private Page link1Page;
    private Page link2Page;
    private Page link3Page;
    private Page link3MobilePage;
    private Page searchPage;
    private Page homePage;
    private List<Page> languagePageList;

    @Override
    public void activate() throws Exception {
        homePage = getCurrentPage().getAbsoluteParent(2);
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        final String rootPath = properties.getInherited(WcmConstants.PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM, homePage.getPath());
        navigation = navigationBuild(rootPath, 2);

        final String link1Reference = properties.getInherited("link1Reference", String.class);
        final String link2Reference = properties.getInherited("link2Reference", String.class);
        final String link3Reference = properties.getInherited("link3Reference", String.class);
        final String link3MobileReference = properties.getInherited("link3MobileReference", String.class);
        final String searchPageReference = properties.getInherited("searchPageReference", String.class);

        link1Page = getPageFromPath(link1Reference);
        link2Page = getPageFromPath(link2Reference);
        link3Page = getPageFromPath(link3Reference);
        link3MobilePage = getPageFromPath(link3MobileReference);
        searchPage = getPageFromPath(searchPageReference);

        languagePageList = new ArrayList<Page>();
        Iterator<Page> homeLangIt = homePage.getParent().listChildren(new PageFilter());
        Page homeLang;
        while(homeLangIt.hasNext()) {
            homeLang = homeLangIt.next();
            if(!homeLang.getPath().equals(homePage.getPath())){
                languagePageList.add(homeLang);
            }
        }
    }

    /**
     * get Page from path
     *
     * @param path
     * @return Page
     */
    private Page getPageFromPath(String path) {
        Resource res = getResourceResolver().getResource(path);
        if (res != null) {
            return res.adaptTo(Page.class);
        }
        return null;
    }

    /**
     * Create navigation content tree
     *
     * @param pathRoot
     * @param maxLevel
     * @return Navigation
     */
    private Navigation navigationBuild(String path, Integer maxLevel) {
        Navigation nav = null;

        Resource resourceRoot = getResourceResolver().getResource(path);

        if (resourceRoot != null) {
            Page pageRoot = resourceRoot.adaptTo(Page.class);

            Page selectPage = PathUtils.isAncestor(pageRoot.getPath(), getCurrentPage().getPath()) ? getCurrentPage() : pageRoot;
            nav = new Navigation(selectPage, PathUtils.getDepth(path) - 1, new PageFilter(), maxLevel);
        }

        return nav;
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
}