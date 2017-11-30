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

    private boolean isParentPage = true;

    public SitemapModel() {
    }

    public List<SitemapEntryModel> getEntries() {
        final List<SitemapEntryModel> entries = new ArrayList<>();

        final Page parentPage = page.getParent();

        LOGGER.debug("Parent page path : {}", parentPage.getPath());

        if (parentPage.getContentResource() != null && parentPage.getContentResource().adaptTo(SitemapEntryModel.class) != null) {
            entries.add(parentPage.getContentResource().adaptTo(SitemapEntryModel.class));
        }

        entries.addAll(getEntries(parentPage));

        return entries;
    }

    private List<SitemapEntryModel> getEntries(Page page) {
        final List<SitemapEntryModel> entries = new ArrayList<>();

        // TODO should be test the parent page ?
        if (page.getContentResource() != null && page.adaptTo(SitemapEntryModel.class) != null && isParentPage) {
            if (page.getProperties() != null && !page.getProperties().get(WcmConstants.PN_NOT_IN_SITEMAP, false))
                entries.add(page.adaptTo(SitemapEntryModel.class));
            isParentPage = false;
        }

        Iterator<Page> pages = page.listChildren(new SitemapFilter());

        while (pages.hasNext()) {
            final Page currentPage = pages.next();

            LOGGER.trace("Reading page for generating sitemap : {}", currentPage.getPath());

            if (currentPage.getContentResource() != null && currentPage.adaptTo(SitemapEntryModel.class) != null) {
                LOGGER.trace("Adding in sitemap : {}", currentPage.getPath());

                entries.add(currentPage.adaptTo(SitemapEntryModel.class));
            }
        }

        pages = page.listChildren();

        while (pages.hasNext()) {
            entries.addAll(getEntries(pages.next()));
        }

        return entries;
    }

    private static class SitemapFilter implements Filter<Page> {

        @Override
        public boolean includes(Page page) {
            if (page.getContentResource() == null) {
                return false;
            }

            if (page.getProperties() != null) {
                final String resourceType = page.getContentResource().getResourceType();

                if (page.getParent() != null && page.getParent().getContentResource() != null) {
                    final String parentResourceType = page.getParent().getContentResource().getResourceType();

                    if (WcmConstants.RT_PORT.equals(parentResourceType)) {
                        return false;
                    }
                }

                return !(page.getProperties().get(WcmConstants.PN_NOT_IN_SITEMAP, false)
                        || resourceType.endsWith(WcmConstants.RT_SUB_REDIRECT_PAGE)
                        || WcmConstants.RT_HOTEL.equals(resourceType)
                        || WcmConstants.RT_LAND_PROGRAMS.equals(resourceType)
                        || WcmConstants.RT_EXCURSIONS.equals(resourceType)
                        || WcmConstants.RT_TRAVEL_AGENT.equals(resourceType)
                        || WcmConstants.RT_COMBO_SEGMENT.equals(resourceType)
                        || WcmConstants.RT_EXCLUSIVE_OFFER.equals(resourceType)
                        || WcmConstants.RT_EXCLUSIVE_OFFER_VARIATION.equals(resourceType)
                        || WcmConstants.RT_LANDING_PAGE.equals(resourceType));
            }

            return true;
        }
    }
}
