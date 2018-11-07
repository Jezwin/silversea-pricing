package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.jackrabbit.oak.commons.PathUtils;


import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.foundation.Navigation;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.NavPageModel;

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

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());

        homePage = getCurrentPage().getAbsoluteParent(2);
        final String rootPath = properties.getInherited(WcmConstants.PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM, homePage.getPath());
        navigation = navigationBuild(rootPath, 2);

        final String link1Reference = properties.getInherited("link1Reference", "");
        final String link2Reference = properties.getInherited("link2Reference", "");
        final String link3Reference = properties.getInherited("link3Reference", "");
        final String link4Reference = properties.getInherited("link4Reference", "");
        final String link3MobileReference = properties.getInherited("link3MobileReference", "");
        final String searchPageReference = properties.getInherited("searchPageReference", "");

        link1Page = getPageManager().getPage(link1Reference);
        link2Page = getPageManager().getPage(link2Reference);
        link3Page = getPageManager().getPage(link3Reference);
        link4Page = getPageManager().getPage(link4Reference);
        link3MobilePage = getPageManager().getPage(link3MobileReference);
        searchPage = getPageManager().getPage(searchPageReference);

        languagePageList = new ArrayList<>();

        final Iterator<Page> homeLangIt = homePage.getParent().listChildren(new PageFilter());

        Page homeLang;
        while (homeLangIt.hasNext()) {
            homeLang = homeLangIt.next();
            if (!homeLang.getPath().equals(homePage.getPath())) {
                languagePageList.add(homeLang);
            }
        }
        
        fillLanguagePages();
        
        for (Page pageL : languagePageList) {
        	languagePages.entrySet().forEach(entry -> {
        	    if(pageL.getPath().toLowerCase().contains(entry.getKey().toLowerCase())){
        	    	NavPageModel nPage = new NavPageModel();
        	    	nPage.setPath(entry.getValue());
        	    	nPage.setName(pageL.getName());
        	    	nPage.setNavigationTitle(pageL.getNavigationTitle());
        	    	languagePageListForCurrentPage.add(nPage);
        	    }
        	}); 
		}
        
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
    
    private Map<String, String> fillLanguagePages() throws WCMException {
        languagePages = new LinkedHashMap<>();
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        Locale locale;
        String[] langList = {"/en/","/es/", "/pt-br/", "/de/", "/fr/"};
        String currentPath = getCurrentPage().getPath();
        String currentLng = "";
        for (String lng : langList) {
			if(currentPath.contains(lng)){
				 Page page = getPageManager().getPage(currentPath);
				 if(page != null){
					 locale = page.getLanguage(false);
		             languagePages.put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, currentPath));
		             currentLng = lng;
				 }
			}
		}
        
        for (String lng : langList) {
			if(!currentPath.contains(lng)){
				 String newPath = currentPath.replace(currentLng, lng);
				 Page page = getPageManager().getPage(newPath);
				 if(page != null){
					 locale = page.getLanguage(false);
		             languagePages.put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, newPath));
				 }
			}
		}
        
        if(currentLng == ""){
        	 String[] langListHome = {"/en","/es", "/pt-br", "/de", "/fr"};
        	 for (String lng : langListHome) {
     			if(currentPath.contains(lng)){
     				 Page page = getPageManager().getPage(currentPath);
     				 if(page != null){
     					 locale = page.getLanguage(false);
     		             languagePages.put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, currentPath));
     		             currentLng = lng;
     				 }
     			}
     		}
             
             for (String lng : langListHome) {
     			if(!currentPath.contains(lng)){
     				 String newPath = currentPath.replace(currentLng, lng);
     				 Page page = getPageManager().getPage(newPath);
     				 if(page != null){
     					 locale = page.getLanguage(false);
     		             languagePages.put("-com/" + locale.toLanguageTag(), externalizer.externalLink(getResourceResolver(), Externalizer.LOCAL, newPath));
     				 }
     			}
     		}
        }

        return languagePages;
    }
}