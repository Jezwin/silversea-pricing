package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author mjedli
 */
public class DiningUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiningUse.class);

    private List<Page> diningReferenceList = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        Iterator<Resource> resources = getResourceResolver().findResources(
                "//element(*, cq:Page)[" +
                        "jcr:content/@sling:resourceType=\"silversea/silversea-com/components/pages/diningvariation\"" +
                        " and jcr:content/@diningReference=\"" + getCurrentPage().getPath() + "\"]",
                "xpath");

        while (resources.hasNext()) {
            Page page = resources.next().adaptTo(Page.class);

            if (page != null && page.getParent(2) != null) {
                LOGGER.debug("Found page {} on ship {}", page.getPath(), page.getParent(2).getPath());

                diningReferenceList.add(page.getParent(2));
            }
        }
    }

    public List<Page> getDiningReferenceList() {
        return diningReferenceList;
    }
}