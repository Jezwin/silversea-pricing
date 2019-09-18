package com.silversea.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.sling.api.resource.Resource;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NewCruiseDisclaimerHelper extends WCMUsePojo {

    private String cruiseCode;

    public Boolean getShowDisclaimer() {
        return showDisclaimer;
    }

    private Boolean showDisclaimer;

    @Override
    public void activate() throws Exception {
        // activated
        cruiseCode = get("cruiseCode", String.class);
        List<String> cruiseCodes = Arrays.asList(getNewCruiseCodesFromCrx());
        showDisclaimer = cruiseCodes.contains(cruiseCode);
    }

    public String[] getNewCruiseCodesFromCrx() {
        Resource pageContentResource = getResourceResolver().getResource("/content/silversea-com/en/destinations/jcr:content/newItinerariesDisclaimer/cruiseCodes");

        if (pageContentResource != null) {
            String cruiseCodes = pageContentResource.adaptTo(String.class);
            return parseCodes(cruiseCodes);
        }

        return new String[0];
    }

    public String[] parseCodes(String cruiseCodes) {
        return cruiseCodes.split("\\s*,\\s*");
    }
}
