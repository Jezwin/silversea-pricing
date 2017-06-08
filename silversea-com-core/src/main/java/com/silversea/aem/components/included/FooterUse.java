package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationTagService;

/**
 * Model for the footer mbennabi 31/05/2017
 */
public class FooterUse extends WCMUsePojo {
 
    private Iterator<Page> pagesMainColIterator;
    private Iterator<Page> pagesBottomLineIterator;
    
    private Page pageSubCol1;
    private Page pageSubCol2;
    private Page pageSubCol3;
    private Page pageMySilversea;
    private Page pageQuote;
    private Page pageExclusiveOffer;
    private Page pageAaward;
    private Page pageFacebook;
    private Page pageYoutube;
    private Page pageTwitter;
    private Page pageInstagram;
    private Page pagePinterest;
    private Page pageBlog;
    private String phone;

    /**
     * Initialize the component.
     */
    @Override
	public void activate() throws Exception {
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());

        final String[] mainCol = properties.getInherited("reference", String[].class);
        ArrayList<Page> pagesMainCol = new ArrayList<Page>();
        for (int i = 0; i < mainCol.length; i++) {
        	pagesMainCol.add(getPageFromPath(mainCol[i]));
        }
        pagesMainColIterator = pagesMainCol.iterator();
        
        final String subCol1 = properties.getInherited("subCol1", String.class);
        final String subCol2 = properties.getInherited("subCol2", String.class);
        final String subCol3 = properties.getInherited("subCol3", String.class);
        final String mySilverseaReference = properties.getInherited("mySilverseaReference", String.class);
        final String quoteReference = properties.getInherited("quoteReference", String.class);
        final String exclusiveOfferReference = properties.getInherited("exclusiveOfferReference", String.class);
        final String awardReference = properties.getInherited("awardReference", String.class);
        final String facebookReference = properties.getInherited("facebookReference", String.class);
        final String youtubeReference = properties.getInherited("youtubeReference", String.class);
        final String twitterReference = properties.getInherited("twitterReference", String.class);
        final String instagramReference = properties.getInherited("instagramReference", String.class);
        final String pinterestReference = properties.getInherited("pinterestReference", String.class);
        final String blogReference = properties.getInherited("blogReference", String.class);
        
        pageSubCol1 = getPageFromPath(subCol1);
        pageSubCol2 = getPageFromPath(subCol2);
        pageSubCol3 = getPageFromPath(subCol3);
        pageMySilversea = getPageFromPath(mySilverseaReference);
        pageQuote = getPageFromPath(quoteReference);
        pageExclusiveOffer = getPageFromPath(exclusiveOfferReference);
        pageAaward = getPageFromPath(awardReference);
        pageFacebook = getPageFromPath(facebookReference);
        pageYoutube = getPageFromPath(youtubeReference);
        pageTwitter = getPageFromPath(twitterReference);
        pageInstagram = getPageFromPath(instagramReference);
        pagePinterest = getPageFromPath(pinterestReference);
        pageBlog = getPageFromPath(blogReference);
        pageSubCol1.listChildren();
        
        final String[] bottomLine = properties.getInherited("referencelegal", String[].class);
        ArrayList<Page> pagesBottomLine = new ArrayList<Page>();
        for (int i = 0; i < bottomLine.length; i++) {
        	pagesBottomLine.add(getPageFromPath(bottomLine[i]));
        }
        pagesBottomLineIterator = pagesBottomLine.iterator();
        
        // Getting context
    	GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
        final String geolocationTagId = geolocationTagService.getTagFromRequest(getRequest());
        
        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        if (geolocationTagId != null) {
            Tag geolocationTag = tagManager.resolve(geolocationTagId);

            if (geolocationTag != null) {
            	Resource node = geolocationTag.adaptTo(Resource.class);
            	phone = node.getValueMap().get("phone", String.class);
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
        Resource res = getResourceResolver().resolve(path);
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

    public Page getPageQuote() {
        return pageQuote;
    }

    public Page getPageExclusiveOffer() {
        return pageExclusiveOffer;
    }

    public Page getPageAward() {
        return pageAaward;
    }

    public Page getPageFacebook() {
        return pageFacebook;
    }

    public Page getPageYoutube() {
        return pageYoutube;
    }

    public Page getPageTwitter() {
        return pageTwitter;
    }

    public Page getPageInstagram() {
        return pageInstagram;
    }

    public Page getPagePinterest() {
        return pagePinterest;
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
	
    public String getPhone() {
        return phone;
    }
}