package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.models.TagModel;

public class FindYourCruiseUse extends WCMUsePojo {
    private List<TagModel> featureTags;

    @Override
    public void activate() throws Exception {
        String[] tags = getCurrentStyle().get(TagConstants.PN_TAGS, String[].class);
        if (tags != null) {
            featureTags = new ArrayList<TagModel>();
            TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

            for (String tagId : tags) {
                featureTags.add(tagManager.resolve(tagId).adaptTo(TagModel.class));
            }
        }
    }

    /**
     * @return the featureTags
     */
    public List<TagModel> getFeatureTags() {
        return featureTags;
    }
}