package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.CruiseModel;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.TagModel;
import org.apache.sling.api.resource.ValueMap;

/**
 * selectors :
 * destination_all
 * date_all
 * duration_all
 * ship_all
 * cruisetype_all
 * port_all
 * page_2
 */
public class FindYourCruiseUse extends WCMUsePojo {

    private final static int PAGE_SIZE = 15;

    private List<TagModel> featureTags = new ArrayList<>();

    private String type = "v2";

    private List<Page> cruisesPages = new ArrayList<>();

    private List<CruiseModel> cruises = new ArrayList<>();

    private String destination;

    private String date;

    private String duration;

    private String ship;

    private String cruiseType;

    private String port;

    private int activePage = 1;

    private boolean isFirstPage;

    private boolean isLastPage;

    @Override
    public void activate() throws Exception {
        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

        // Get tags to display
        if (tagManager != null) {
            final String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);

            if (tags != null) {
                for (String tagId : tags) {
                    final Tag tag = tagManager.resolve(tagId);

                    if (tag != null) {
                        featureTags.add(tag.adaptTo(TagModel.class));
                    }
                }
            }
        }

        // Parse selectors
        final String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        for (final String selector : selectors) {
            final String[] splitSelector = selector.split("_");

            if (splitSelector.length == 2) {
                switch (splitSelector[0]) {
                    case "destination":
                        destination = splitSelector[1];
                        break;
                    case "date":
                        break;
                    case "duration":
                        break;
                    case "ship":
                        ship = splitSelector[1];
                        break;
                    case "cruisetype":
                        cruiseType = splitSelector[1];
                        break;
                    case "port":
                        port = splitSelector[1];
                        break;
                    case "page":
                        try {
                            activePage = Integer.parseInt(splitSelector[1]);
                        } catch (NumberFormatException ignored) {}
                        break;
                }
            }
        }

        // Get type from configuration
        final Conf confRes = getResource().adaptTo(Conf.class);
        if (confRes != null) {
            final ValueMap fycConf = confRes.getItem("/findyourcruise/findyourcruise");

            if (fycConf != null) {
                type = fycConf.get("type", String.class);
            }
        }

        // Find cruises
        final Page destinations = getPageManager().getPage("/content/silversea-com/en/destinations");
        collectCruisesPages(destinations);

        // Build the cruises list
        int pageSize = PAGE_SIZE; // TODO replace by configuration

        int i = 0;
        for (final Page cruisePage : cruisesPages) {
            if (i > (activePage - 1) * pageSize) {
                final CruiseModel cruise = cruisePage.adaptTo(CruiseModel.class);

                if (cruise != null) {
                    cruises.add(cruise);
                }
            }

            i++;

            if (i == activePage * pageSize) {
                break;
            }
        }

        // Setting convenient booleans for building pagination
        isFirstPage = activePage == 1;
        isLastPage = activePage == getPagesNumber();
    }

    /**
     * @return the featureTags
     */
    public List<TagModel> getFeatureTags() {
        return featureTags;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the list of cruises
     */
    public List<CruiseModel> getCruises() {
        return cruises;
    }

    /**
     * @return the number of pages
     */
    public int getPagesNumber() {
        return (int) Math.ceil((float)cruisesPages.size() / (float)PAGE_SIZE);
    }

    /**
     * @return the index of the active page
     */
    public int getActivePage() {
        return activePage;
    }

    /**
     * @return true if the page is the first in the pagination
     */
    public boolean isFirstPage() {
        return isFirstPage;
    }

    /**
     * @return true if the page is the last in the pagination
     */
    public boolean isLastPage() {
        return isLastPage;
    }

    private void collectCruisesPages(final Page rootPage) {
        if (rootPage.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {
            cruisesPages.add(rootPage);
        } else {
            final Iterator<Page> children = rootPage.listChildren();

            while (children.hasNext()) {
                collectCruisesPages(children.next());
            }
        }
    }
}