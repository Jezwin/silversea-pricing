package com.silversea.aem.components.displayProperties;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ilazaar.
 */
public class DiningVariationDisplayPropertiesUse extends WCMUsePojo {

    private Page dining;

    public void activate() throws Exception {
        final String pathDining = getCurrentPage().getProperties().get("diningReference", String.class);

        if (StringUtils.isNotEmpty(pathDining)) {
            dining = getPageManager().getPage(pathDining);
        }
    }

    public Page getDiningPage() {
        return dining;
    }
}
