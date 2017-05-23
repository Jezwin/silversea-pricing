package com.silversea.aem.components.page;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO merge with {@link DiningUse}
 *
 * @author mbennabi 14/03/2017
 */
public class SuiteUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuiteUse.class);

    private List<Page> suiteReferenceList = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        Iterator<Resource> resources = getResourceResolver().findResources(
                "//element(*, cq:Page)[" +
                        "jcr:content/@sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"" +
                        " and jcr:content/@suiteReference=\"" + getCurrentPage().getPath() + "\"]",
                "xpath");

        while (resources.hasNext()) {
            Page page = resources.next().adaptTo(Page.class);

            if (page != null && page.getParent(2) != null) {
                LOGGER.debug("Found page {} on ship {}", page.getPath(), page.getParent(2).getPath());

                suiteReferenceList.add(page.getParent(2));
            }
        }
    }

    public List<Page> getSuiteReferenceList() {
        return suiteReferenceList;
    }
}