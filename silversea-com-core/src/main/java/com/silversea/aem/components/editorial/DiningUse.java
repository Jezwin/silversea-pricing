/**
 * 
 */
package com.silversea.aem.components.editorial;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.social.journal.client.api.Journal;

/**
 * @author mjedli
 *
 */
public class DiningUse extends WCMUsePojo {

    private static final String DINING_REFERENCE_PROPERTIE = "diningReference";

    private static final String SHIPS_PATH = "/jcr:root/content/silversea-com/en/ships";

    private Map<String, String> diningReferenceList;

    @Override
    public void activate() throws Exception {

        String diningReference = getCurrentPage().getPath();
        diningReferenceList = new HashMap<String, String>();

        Iterator<Resource> resources = getResourceResolver().findResources(
                SHIPS_PATH + "//*[jcr:content/@diningReference=\"" + diningReference + "\"]", "xpath");

        while (resources.hasNext()) {
            Node node = resources.next().adaptTo(Node.class);
            diningReferenceList.put(node.getName(), node.getPath() + Journal.URL_SUFFIX);
        }
    }

    public Map<String, String> getDiningReferenceList() {
        return diningReferenceList;
    }

}
