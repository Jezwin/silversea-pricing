package com.silversea.aem.components.included;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.cxf.common.util.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.utils.PathUtils;

/**
 * Model for the footer mbennabi 31/05/2017
 *
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
    private String brochureimage;
    private String assetImageBrochure;

    //New Black Brochure Style
    //TOOD: clean all not use iterators
    private Iterator<Page> pagesCol1Iterator;
    private Iterator<Page> pagesCol2Iterator;
    private Iterator<Page> pagesCol3Iterator;
    private Iterator<Page> pagesCol4Iterator;
    private Iterator<Page> pagesBottomLineIterator2;
    private Iterator<Page> pagesBottomLineIterator3;
    private Iterator<Page> pagesLegalLinkIterator;
    
    private String Col2title;
    private String Col3title;
    private String Col4title;

    private String titleDesktop;
    private String titleMobile;
    private String descriptionDesktop;
    private String descriptionMobile;
    private String ctaLabelDesktop;
    private String ctaLabelMobile;

    /**
     * Initialize the component.
     */
    @Override
    public void activate() throws Exception {
        InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        final String[] mainCol = properties.getInherited("reference", String[].class);
        ArrayList<Page> pagesMainCol = new ArrayList<Page>();
        if (mainCol != null) {
            for (int i = 0; i < mainCol.length; i++) {
                pagesMainCol.add(getPageFromPath(mainCol[i]));
            }
        }
        pagesMainColIterator = pagesMainCol.iterator();
        // internal pages
        final String subCol1 = properties.getInherited("subCol1", String.class);
        final String subCol2 = properties.getInherited("subCol2", String.class);
        final String subCol3 = properties.getInherited("subCol3", String.class);
        final String quoteReference = properties.getInherited("quoteReference", String.class);
        final String exclusiveOfferReference = properties.getInherited("exclusiveOfferReference", String.class);
        final String awardReference = properties.getInherited("awardReference", String.class);
        final String blogReference = properties.getInherited("blogReference", String.class);
        final String mySilverseaReference = properties.getInherited("mySilverseaReference", String.class);
        String linkCtaBrochure = properties.getInherited("linkCtaBrochure", String.class);
        final String searchPageReference = properties.getInherited("searchPageReference", "");



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
        facebookReference = properties.getInherited("facebookReference", String.class);
        youtubeReference = properties.getInherited("youtubeReference", String.class);
        twitterReference = properties.getInherited("twitterReference", String.class);
        instagramReference = properties.getInherited("instagramReference", String.class);
        pinterestReference = properties.getInherited("pinterestReference", String.class);

        titleDesktop = properties.getInherited("titleDesktop", String.class);
        titleMobile = properties.getInherited("titleMobile", String.class);
        descriptionDesktop = properties.getInherited("descriptionDesktop", String.class);
        descriptionMobile = properties.getInherited("descriptionMobile", String.class);
        ctaLabelDesktop = properties.getInherited("ctaLabelDesktop", String.class);
        ctaLabelMobile = properties.getInherited("ctaLabelMobile", String.class);


        final String[] bottomLine = properties.getInherited("referencelegal", String[].class);
        ArrayList<Page> pagesBottomLine = new ArrayList<Page>();
        if (bottomLine != null) {
            for (int i = 0; i < bottomLine.length; i++) {
                pagesBottomLine.add(getPageFromPath(bottomLine[i]));
            }
        }
        pagesBottomLineIterator = pagesBottomLine.iterator();
        pagesBottomLineIterator2 = pagesBottomLine.iterator();
        pagesBottomLineIterator3 = pagesBottomLine.iterator();
        pagesLegalLinkIterator =pagesBottomLine.iterator();
        
        //brochure style properties
        final String[] Col1 = properties.getInherited("col1", String[].class);
        ArrayList<Page> pagesCol1 = new ArrayList<Page>();
        if (Col1 != null) {
            for (int i = 0; i < Col1.length; i++) {
            	pagesCol1.add(getPageFromPath(Col1[i]));
            }
        }
        
        final String[] Col2 = properties.getInherited("col2", String[].class);
        ArrayList<Page> pagesCol2 = new ArrayList<Page>();
        if (Col2 != null) {
            for (int i = 0; i < Col2.length; i++) {
            	pagesCol2.add(getPageFromPath(Col2[i]));
            }
        }
        
        final String[] Col3 = properties.getInherited("col3", String[].class);
        ArrayList<Page> pagesCol3 = new ArrayList<Page>();
        if (Col3 != null) {
            for (int i = 0; i < Col3.length; i++) {
            	pagesCol3.add(getPageFromPath(Col3[i]));
            }
        }
        
        final String[] Col4 = properties.getInherited("col4", String[].class);
        ArrayList<Page> pagesCol4 = new ArrayList<Page>();
        if (Col4 != null) {
            for (int i = 0; i < Col4.length; i++) {
            	pagesCol4.add(getPageFromPath(Col4[i]));
            }
        }
        
        pagesCol1Iterator = pagesCol1.iterator();
        pagesCol2Iterator = pagesCol2.iterator();
        pagesCol3Iterator = pagesCol3.iterator();
        pagesCol4Iterator = pagesCol4.iterator();
        brochureimage = properties.getInherited("brochureimage", String.class);
        assetImageBrochure = properties.getInherited("assetImageBrochure", String.class);
        Col2title = properties.getInherited("Col2title", String.class);
        Col3title = properties.getInherited("Col3title", String.class);
        Col4title = properties.getInherited("Col4title", String.class);
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

	public Iterator<Page> getPagesCol1Iterator() {
		return pagesCol1Iterator;
	}

	public Iterator<Page> getPagesCol2Iterator() {
		return pagesCol2Iterator;
	}

	public Iterator<Page> getPagesCol3Iterator() {
		return pagesCol3Iterator;
	}

	public Iterator<Page> getPagesCol4Iterator() {
		return pagesCol4Iterator;
	}

	public String getBrochureimage() {
		return brochureimage;
	}

    public String getAssetImageBrochure() {
        return assetImageBrochure;
    }

	public String getCol2title() {
		return Col2title;
	}

	public String getCol3title() {
		return Col3title;
	}

	public String getCol4title() {
		return Col4title;
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