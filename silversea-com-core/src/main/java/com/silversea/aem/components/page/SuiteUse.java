/**
 * 
 */
package com.silversea.aem.components.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.social.journal.client.api.Journal;
import com.day.cq.wcm.api.Page;

/**
 * @author mbennabi 14/03/2017
 *
 */
public class SuiteUse extends WCMUsePojo {
    

    private Map<String, String> suiteReferenceList;

    @Override
    public void activate() throws Exception {
        final String SHIPS_PATH = "/jcr:root/content/silversea-com/"+getCurrentPage().getAbsoluteParent(2).getName()+"/ships";
        String suiteReferenc = getCurrentPage().getPath();
        suiteReferenceList = new HashMap<String, String>();

        Iterator<Resource> resources = getResourceResolver()
                .findResources(SHIPS_PATH + "//*[jcr:content/@suiteReference=\"" + suiteReferenc + "\"]", "xpath");

        while (resources.hasNext()) {
            Page node = resources.next().adaptTo(Page.class);
            if (node.getParent() != null)
                suiteReferenceList.put(node.getParent(2).getTitle(), node.getParent(2).getPath() + Journal.URL_SUFFIX);
        }
    }

    public Map<String, String> getSuiteReferenceList() {
        return suiteReferenceList;
    }
}