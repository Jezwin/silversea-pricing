package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.CruiseModel;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.TagModel;

public class FindYourCruiseUse extends WCMUsePojo {

    private List<TagModel> featureTags;

    private String type = "v2";

    int limit = 0;

    private List<CruiseModel> cruises = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);
        if (tags != null) {
            featureTags = new ArrayList<>();
            TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

            for (String tagId : tags) {
                featureTags.add(tagManager.resolve(tagId).adaptTo(TagModel.class));
            }
        }

        // Get type from configuration
        Resource confRes = getResource().adaptTo(Conf.class).getItemResource("/findyourcruise/findyourcruise");

        if (confRes != null) {
            type = confRes.getValueMap().get("type", String.class);
        }

        // Find cruises
        final Page destinations = getPageManager().getPage("/content/silversea-com/en/destinations");

        collectCruises(destinations);
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

    private void collectCruises(final Page rootPage) {
        if (rootPage.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {
            cruises.add(rootPage.adaptTo(CruiseModel.class));
            limit++;
        } else if (limit < 10) {
            final Iterator<Page> children = rootPage.listChildren();

            while (children.hasNext()) {
                collectCruises(children.next());
            }
        }
    }
}