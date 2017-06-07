package com.silversea.aem.components.included;

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
 * Model for the footer mbennabi 31/05/2017
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class FooterModel {

    /**
     * Resource
     */
    @Inject
    protected Resource resource;

    /**
     * Resource Resolver
     */

    private String[] mainCol;
    private String subCol1;
    private String subCol2;
    private String subCol3;
    private String mySilverseaReference;

    private String quoteReference;
    private String exclusiveOfferReference;
    private String awardReference;
    private String facebookReference;
    private String youtubeReference;
    private String twitterReference;
    private String instagramReference;
    private String pinterestReference;
    private String blogReference;

    private String[] bottomLine;

    /**
     * Constructor NavigationModel
     */
    public FooterModel(SlingHttpServletRequest request) {
        // Empty
    }

    /**
     * Initialize the component.
     */
    @PostConstruct
    public void init() {
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(resource);

        mainCol = properties.getInherited("mainCol", String[].class);
        subCol1 = properties.getInherited("subCol1", String.class);
        subCol2 = properties.getInherited("subCol2", String.class);
        subCol3 = properties.getInherited("subCol3", String.class);
        mySilverseaReference = properties.getInherited("mySilverseaReference", String.class);

        quoteReference = properties.getInherited("quoteReference", String.class);
        exclusiveOfferReference = properties.getInherited("exclusiveOfferReference", String.class);
        awardReference = properties.getInherited("awardReference", String.class);
        facebookReference = properties.getInherited("facebookReference", String.class);
        youtubeReference = properties.getInherited("youtubeReference", String.class);
        twitterReference = properties.getInherited("twitterReference", String.class);
        instagramReference = properties.getInherited("instagramReference", String.class);
        pinterestReference = properties.getInherited("pinterestReference", String.class);
        blogReference = properties.getInherited("blogReference", String.class);

        bottomLine = properties.getInherited("bottomLine", String[].class);

    }

    public Resource getResource() {
        return resource;
    }

    public String[] getMainCol() {
        return mainCol;
    }

    public String getSubCol1() {
        return subCol1;
    }

    public String getSubCol2() {
        return subCol2;
    }

    public String getSubCol3() {
        return subCol3;
    }

    public String getMySilverseaReference() {
        return mySilverseaReference;
    }

    public String getQuoteReference() {
        return quoteReference;
    }

    public String getExclusiveOfferReference() {
        return exclusiveOfferReference;
    }

    public String getAwardReference() {
        return awardReference;
    }

    public String getFacebookReference() {
        return facebookReference;
    }

    public String getYoutubeReference() {
        return youtubeReference;
    }

    public String getTwitterReference() {
        return twitterReference;
    }

    public String getInstagramReference() {
        return instagramReference;
    }

    public String getPinterestReference() {
        return pinterestReference;
    }

    public String getBlogReference() {
        return blogReference;
    }

    public String[] getBottomLine() {
        return bottomLine;
    }

}