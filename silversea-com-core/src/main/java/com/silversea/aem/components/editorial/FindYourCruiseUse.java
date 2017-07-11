package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;

public class FindYourCruiseUse extends WCMUsePojo {
    private List<Resource> featureTags;

    @Override
    public void activate() throws Exception {
        String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);
        if (tags != null) {
            featureTags = new ArrayList<Resource>();
            TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
            for (String tagId : tags) {
                featureTags.add(tagManager.resolve(tagId).adaptTo(Resource.class));
            }
        }
    }

    /**
     * @return the featureTags
     */
    public List<Resource> getFeatureTags() {
        return featureTags;
    }
}