package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by asiba on 17/07/2017.
 */
public class TabContainerUse extends WCMUsePojo {

    private Iterator<Resource> itParTab;
    private List<Resource> listParTab;

    @Override
    public void activate() throws Exception {
        Resource parTabcontainer = getResource().getChild("parTabcontainer");
        if (parTabcontainer != null) {
            Resource childNode = null;
            itParTab = parTabcontainer.listChildren();
            listParTab = new ArrayList<Resource>();

            while(itParTab.hasNext()) {
                childNode = itParTab.next();

                if (childNode.isResourceType("silversea/silversea-com/components/editorial/tab")) {
                    listParTab.add(childNode);
                }
            }

        }
    }

    public List<Resource> getListParTab () { return listParTab; }
}
