package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.silversea.aem.services.GlobalCacheService;
import org.apache.cxf.common.util.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.utils.PathUtils;

/**
 * Model for the footer mbennabi 31/05/2017
 * <p>
 * TODO review footer management
 */
public class FooterUse extends WCMUsePojo {

    private Iterator<Page> pagesMainColIterator;
    private Iterator<Page> pagesBottomLineIterator;

    private Page pageSubCol1;
    private Page pageSubCol2;
    private Page pageSubCol3;
    private Page pageQuote;
    private Page pageExclusiveOffer;
    private Page pageAaward;
    private Page pageBlog;
    private Page pageMySilversea;
    private Page pageDownlaodBrochure;
    private Page searchPage;

    private String facebookReference;
    private String youtubeReference;
    private String twitterReference;
    private String instagramReference;
    private String pinterestReference;
    private String assetImageBrochure;

    //New Black Brochure Style

    private Iterator<Page> pagesBottomLineIterator2;
    private Iterator<Page> pagesBottomLineIterator3;
    private Iterator<Page> pagesLegalLinkIterator;

    private String titleDesktop;
    private String titleMobile;
    private String descriptionDesktop;
    private String descriptionMobile;
    private String ctaLabelDesktop;
    private String ctaLabelMobile;

    GlobalCacheService globalCacheService;
    private String currentPath;
    private HierarchyNodeInheritanceValueMap inheritanceValueMap;

    /**
     * Initialize the component.
     */
    @Override
    public void activate() throws Exception {
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
        currentPath = getCurrentPage().getPath();
        final String[] references = globalCacheService
                .getCache("prop_reference" + currentPath, String[].class, () -> getInheritanceValueMap().getInherited("reference", String[].class));
        ArrayList<Page> pagesMainCol = new ArrayList<>();
        if (references != null) {
            for (int i = 0; i < references.length; i++) {
                pagesMainCol.add(getPageFromPath(references[i]));
            }
        }
        pagesMainColIterator = pagesMainCol.iterator();
        // internal pages
        final String subCol1 = getFromProp("subCol1");
        final String subCol2 = getFromProp("subCol2");
        final String subCol3 = getFromProp("subCol3");
        final String quoteReference = getFromProp("quoteReference");
        final String exclusiveOfferReference = getFromProp("exclusiveOfferReference");
        final String awardReference = getFromProp("awardReference");
        final String blogReference = getFromProp("blogReference");
        final String mySilverseaReference = getFromProp("mySilverseaReference");
        String linkCtaBrochure = getFromProp("linkCtaBrochure");
        final String searchPageReference =getFromProp("searchPageReference", "");


        pageSubCol1 = getPageFromPath(subCol1);
        pageSubCol2 = getPageFromPath(subCol2);
        pageSubCol3 = getPageFromPath(subCol3);
        pageQuote = getPageFromPath(quoteReference);
        pageDownlaodBrochure = getPageFromPath(linkCtaBrochure);
        pageExclusiveOffer = getPageFromPath(exclusiveOfferReference);
        pageAaward = getPageFromPath(awardReference);
        pageBlog = getPageFromPath(blogReference);
        pageMySilversea = getPageFromPath(mySilverseaReference);
        searchPage = getPageManager().getPage(searchPageReference);


        // external links
        facebookReference = getFromProp("facebookReference");
        youtubeReference = getFromProp("youtubeReference");
        twitterReference = getFromProp("twitterReference");
        instagramReference = getFromProp("instagramReference");
        pinterestReference = getFromProp("pinterestReference");

        titleDesktop = getFromProp("titleDesktop");
        titleMobile = getFromProp("titleMobile");
        descriptionDesktop = getFromProp("descriptionDesktop");
        descriptionMobile = getFromProp("descriptionMobile");
        ctaLabelDesktop = getFromProp("ctaLabelDesktop");
        ctaLabelMobile = getFromProp("ctaLabelMobile");


        final String[] bottomLine = globalCacheService
                .getCache("prop_referencelegal" + currentPath, String[].class, () -> getInheritanceValueMap().getInherited("referencelegal",
                        String[].class));
        ArrayList<Page> pagesBottomLine = new ArrayList<>();
        if (bottomLine != null) {
            for (int i = 0; i < bottomLine.length; i++) {
                pagesBottomLine.add(getPageFromPath(bottomLine[i]));
            }
        }
        pagesBottomLineIterator = pagesBottomLine.iterator();
        pagesBottomLineIterator2 = pagesBottomLine.iterator();
        pagesBottomLineIterator3 = pagesBottomLine.iterator();
        pagesLegalLinkIterator = pagesBottomLine.iterator();

        assetImageBrochure = getFromProp("assetImageBrochure");
    }

    private String getFromProp(String key) {
        return globalCacheService.getCache("prop_" + key + currentPath, String.class, () -> getInheritanceValueMap().getInherited(key, String.class));
    }

    private String getFromProp(String key, String defaultValue) {
        return globalCacheService.getCache("prop_" + key + currentPath, String.class, () -> getInheritanceValueMap().getInherited(key, defaultValue));
    }

    private InheritanceValueMap getInheritanceValueMap() {
        if (inheritanceValueMap == null) {
            inheritanceValueMap = new HierarchyNodeInheritanceValueMap(getResource());
        }
        return inheritanceValueMap;
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

    public Page getPageSubCol1() {
        return pageSubCol1;
    }

    public Page getPageSubCol2() {
        return pageSubCol2;
    }

    public Page getPageSubCol3() {
        return pageSubCol3;
    }

    public Page getPageMySilversea() {
        return pageMySilversea;
    }

    public String getExternalLinkUrl(String path) {
        if (!StringUtils.isEmpty(path)) {
            if (!path.startsWith("/")) {
                // manually entered external link
                return path;
            } else {
                // internal link
                return path + ".html";
            }
        } else {
            return "";
        }
    }

    public Page getPageQuote() {
        return pageQuote;
    }

    public Page getPageDownlaodBrochure() {
        return pageDownlaodBrochure;
    }

    public Page getPageExclusiveOffer() {
        return pageExclusiveOffer;
    }

    public Page getPageAward() {
        return pageAaward;
    }

    public String getPageFacebook() {
        return getExternalLinkUrl(facebookReference);
    }

    public String getPageYoutube() {
        return getExternalLinkUrl(youtubeReference);
    }

    public String getPageTwitter() {
        return getExternalLinkUrl(twitterReference);
    }

    public String getPageInstagram() {
        return getExternalLinkUrl(instagramReference);
    }

    public String getPagePinterest() {
        return getExternalLinkUrl(pinterestReference);
    }

    public Page getPageBlog() {
        return pageBlog;
    }

    public Iterator<Page> getPagesBottomLineIterator() {
        return pagesBottomLineIterator;
    }

    public Iterator<Page> getPagesMainColIterator() {
        return pagesMainColIterator;
    }

    public int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * @return the searchPage
     */
    public Page getSearchPage() {
        return searchPage;
    }

    /**
     * @return the brochures page
     */


    public String getBrochuresPagePath() {
        return PathUtils.getBrochuresPagePath(getResource(), getCurrentPage());
    }

    public String getAssetImageBrochure() {
        return assetImageBrochure;
    }

    public Iterator<Page> getPagesBottomLineIterator2() {
        return pagesBottomLineIterator2;
    }

    public Iterator<Page> getPagesBottomLineIterator3() {
        return pagesBottomLineIterator3;
    }

    public Iterator<Page> getPagesLegalLinkIterator() {
        return pagesLegalLinkIterator;
    }

    public String getTitleDesktop() {
        return titleDesktop;
    }

    public String getTitleMobile() {
        return titleMobile;
    }

    public String getDescriptionDesktop() {
        return descriptionDesktop;
    }

    public String getDescriptionMobile() {
        return descriptionMobile;
    }

    public String getCtaLabelDesktop() {
        return ctaLabelDesktop;
    }

    public String getCtaLabelMobile() {
        return ctaLabelMobile;
    }
}