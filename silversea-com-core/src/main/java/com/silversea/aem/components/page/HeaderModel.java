package com.silversea.aem.components.page;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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
    }

    /**
     * Create navigation content tree
     *
     * @param pathRoot
     * @param maxLevel
     * @return
     */
    private Navigation navigationBuild(String path, Integer maxLevel) {
        Navigation nav = null;

        Resource resourceRoot = resourceResolver.getResource(path);

        if (resourceRoot != null) {
            Page pageRoot = resourceRoot.adaptTo(Page.class);

            Page selectPage = PathUtils.isAncestor(pageRoot.getPath(), currentPage.getPath()) ? currentPage : pageRoot;
            nav = new Navigation(selectPage, PathUtils.getDepth(path) - 1, new NavigationPageFilter(), maxLevel);
        }

        return nav;
    }

    /**
     * Navigation page filter
     */
    public class NavigationPageFilter extends PageFilter {

        /**
         * Filter the type of page based on resource
         */
        public NavigationPageFilter() {
            super();
        }

        /**
         * Override the default filter.
         *
         * @param page
         * @return
         */
        @Override
        public boolean includes(Page page) {
            ValueMap pageProperties = page.getProperties();

            if (page.getContentResource() == null) {
                return false;
            }

            if (pageProperties != null && (page.isHideInNav() || !page.isValid())) {
                return false;
            }

            return true;
        }
    }
}