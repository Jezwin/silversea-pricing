package com.silversea.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

public class TagHelperUse extends WCMUsePojo {
    private String languageTitle;

    @Override
    public void activate() throws Exception {
        String tagID = get("tagID", String.class);
        if (StringUtils.isNotBlank(tagID)) {
            TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
            Tag tag = tagManager.resolve(tagID);
            languageTitle = tag.getTitle(getCurrentPage().getLanguage(false));
        }
    }

    /**
     * @return the languageTitle
     */
    public String getLanguageTitle() {
        return languageTitle;
    }
}