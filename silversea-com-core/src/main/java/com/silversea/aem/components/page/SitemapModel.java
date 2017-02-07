package com.silversea.aem.components.page;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author aurelienolivier
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class SitemapModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapModel.class);

    @Inject @Named("currentPage")
    private Page page;

    public SitemapModel() {
    }

    /**
     *
     * @return
     */
    public List<SitemapEntryModel> getEntries() {
        List<SitemapEntryModel> entries = new ArrayList<>();

        Page parentPage = page.getParent();

        LOGGER.debug("Parent page path : {}", parentPage.getPath());

        if (parentPage != null) {
            if (parentPage.getContentResource() != null && parentPage.getContentResource().adaptTo(SitemapEntryModel.class) != null) {
                entries.add(parentPage.getContentResource().adaptTo(SitemapEntryModel.class));
            }

            entries.addAll(getEntries(parentPage));
        }

        return entries;
    }

    /**
     *
     * @param page
     * @return
     */
    private List<SitemapEntryModel> getEntries(Page page) {
        List<SitemapEntryModel> entries = new ArrayList<>();

        /* TODO should be test the parent page ?
        if (page.getContentResource() != null && page.adaptTo(SitemapEntryModel.class) != null) {
            //entries.add(page.adaptTo(SitemapEntryModel.class));
        }
        */

        Iterator<Page> pages = page.listChildren(new SitemapFilter());

        while (pages.hasNext()) {
            Page currentPage = pages.next();

            LOGGER.debug("Reading page for generating sitemap : {}", currentPage.getPath());

            if (currentPage.getContentResource() != null && currentPage.adaptTo(SitemapEntryModel.class) != null) {
                LOGGER.debug("Adding in sitemap : {}", currentPage.getPath());

                entries.add(currentPage.adaptTo(SitemapEntryModel.class));
            }
        }

        pages = page.listChildren();

        while (pages.hasNext()) {
            entries.addAll(getEntries(pages.next()));
        }

        return entries;
    }

    /**
     *
     */
    private static class SitemapFilter implements Filter<Page> {

        @Override
        public boolean includes(Page page) {
            if (page.getContentResource() == null) {
                return false;
            }

            if (page.getProperties() != null) {
                return !(page.getProperties().get(WcmConstants.PN_NOT_IN_SITEMAP, false)
                        || page.getContentResource().getResourceType().endsWith(WcmConstants.RT_SUB_REDIRECT_PAGE));
            }

            return true;
        }
    }
}
