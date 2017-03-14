package com.silversea.aem.components.page;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;
import com.silversea.aem.constants.WcmConstants;

/**
 * Model for the Navigation Menu
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class HeaderModel {

    /**
     * Current Page
     */
    @Inject
    protected Page currentPage;

    /**
     * Resource
     */
    @Inject
    protected Resource resource;

    /**
     * Resource Resolver
     */
    @Inject
    protected ResourceResolver resourceResolver;

    /**
     * SlingHttpServletRequest
     */
    @Inject
    protected SlingHttpServletRequest request;

    /**
     * navigation
     */
    public Navigation navigation;

    public Page homePage;

    /**
     * link 1 page (Request a quote page)
     */
    public Page link1Page;

    /**
     * link 2 page (brochure page)
     */
    public Page link2Page;

    /**
     * link 3 page (My Silversea page)
     */
    public Page link3Page;

    /**
     * Search Page
     */
    public Page searchPage;

    /**
     * Constructor NavigationModel
     */
    public HeaderModel(SlingHttpServletRequest request) {
        // Empty
    }

    /**
     * Initialize the component.
     */
    @PostConstruct
    public void init() {
        homePage = currentPage.getAbsoluteParent(2);
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(resource);
        final String rootPath = properties.getInherited(WcmConstants.PN_REFERENCE_PAGE_MAIN_NAVIGATION_BOTTOM, homePage.getPath());
        navigation = navigationBuild(rootPath, 2);

        final String link1Reference = properties.getInherited("link1Reference", String.class);
        final String link2Reference = properties.getInherited("link2Reference", String.class);
        final String link3Reference = properties.getInherited("link3Reference", String.class);
        final String searchPageReference = properties.getInherited("searchPageReference", String.class);

        link1Page = getPageFromPath(link1Reference);
        link2Page = getPageFromPath(link2Reference);
        link3Page = getPageFromPath(link3Reference);
        searchPage = getPageFromPath(searchPageReference);
    }

    /**
     * get Page from path
     *
     * @param path
     * @return Page
     */
    private Page getPageFromPath(String path) {
        Resource res = resourceResolver.resolve(path);
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

        Resource resourceRoot = resourceResolver.getResource(path);

        if (resourceRoot != null) {
            Page pageRoot = resourceRoot.adaptTo(Page.class);

            Page selectPage = PathUtils.isAncestor(pageRoot.getPath(), currentPage.getPath()) ? currentPage : pageRoot;
            nav = new Navigation(selectPage, PathUtils.getDepth(path) - 1, new PageFilter(), maxLevel);
        }

        return nav;
    }
}