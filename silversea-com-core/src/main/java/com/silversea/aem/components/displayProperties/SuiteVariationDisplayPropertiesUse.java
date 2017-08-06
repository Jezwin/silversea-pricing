package com.silversea.aem.components.displayProperties;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by mbennabi on 13/03/2017.
 */
public class SuiteVariationDisplayPropertiesUse extends WCMUsePojo {

    private Page suite;

    public void activate() throws Exception {
        final String suiteReference = getPageProperties().get("suiteReference", String.class);

        if (StringUtils.isNotEmpty(suiteReference)) {
            suite = getPageManager().getPage(suiteReference);
        }
    }

    public Page getSuitePage() {
        return suite;
    }
}
