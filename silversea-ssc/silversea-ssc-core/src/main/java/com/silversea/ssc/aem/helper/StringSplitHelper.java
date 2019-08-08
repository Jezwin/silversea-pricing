package com.silversea.ssc.aem.helper;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

public class StringSplitHelper extends WCMUsePojo {

    private String lastSplit;

    @Override
    public void activate() throws Exception {
        String text = get("text", String.class);
        String split = get("split", String.class);
        lastSplit = text;
        if (StringUtils.isNotEmpty(text) && split != null) {
            
            lastSplit = text.split(split)[text.split(split).length -1];

        }
    }

    /**
     * @return the lastSplit
     */
    public String getLastSplit() {
        return lastSplit;
    }
}