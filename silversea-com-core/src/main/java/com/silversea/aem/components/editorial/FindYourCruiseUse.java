package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Cruise;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.CruiseModel;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.TagModel;
import org.apache.sling.api.resource.ValueMap;

public class FindYourCruiseUse extends WCMUsePojo {

    private final static int PAGE_SIZE = 15;

    private List<TagModel> featureTags = new ArrayList<>();

    private String type = "v2";

    private List<Page> cruisesPages = new ArrayList<>();

    private List<CruiseModel> cruises = new ArrayList<>();

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
        int page = 1;
        int pageSize = PAGE_SIZE;

        int i = 0;
        for (final Page cruisePage : cruisesPages) {
            if (i > (page - 1) * pageSize) {
                final CruiseModel cruise = cruisePage.adaptTo(CruiseModel.class);

                if (cruise != null) {
                    cruises.add(cruise);
                }
            }

            i++;

            if (i == page * pageSize) {
                break;
            }
        }
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