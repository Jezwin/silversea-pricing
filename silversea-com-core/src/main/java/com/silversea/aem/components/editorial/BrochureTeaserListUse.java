package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.components.services.GeolocationTagCacheService;

public class BrochureTeaserListUse extends WCMUsePojo {

    private static final String CONTENT_DAM_SIVERSEA_COM = "/content/dam/siversea-com";

    private static final String COUNTRY_ID_PARAM = "countryIdParam";

    private static final String PATH_TO_BROCHURES_DAM = "/jcr:root/content/dam/siversea-com/brochures";

    private List<String> brochureList;

    private GeolocationTagCacheService geolocService;

    private List<String> langList;

    @Override
    public void activate() throws Exception {

        Resource resourceNode = getResourceResolver().getResource(CONTENT_DAM_SIVERSEA_COM);
        Node silverseaDamNode = resourceNode.adaptTo(Node.class);
        NodeIterator langNodeIterator = silverseaDamNode.getNodes();

        langList = new ArrayList<String>();

        while (langNodeIterator.hasNext()) {
            Node currentLangNode = (Node) langNodeIterator.next();
            if (currentLangNode.getName() != null && !"jcr:content".equals(currentLangNode.getName()))
                langList.add(currentLangNode.getName());
        }

        geolocService = getSlingScriptHelper().getService(GeolocationTagCacheService.class);

        String tagId = geolocService.getTagIdFromCurrentRequest(getResourceResolver(), getRequest());

        Iterator<Resource> resources = getResourceResolver()
                .findResources(PATH_TO_BROCHURES_DAM + "//*[jcr:content/metadata/@cq:tags=\"" + tagId + "\"]", "xpath");
        brochureList = new ArrayList<String>();

        while (resources.hasNext()) {
            Node node = resources.next().adaptTo(Node.class);
            brochureList.add(node.getPath());
        }
    }

    public List<String> getBrochureList() {
        return brochureList;
    }

    public List<String> getLangList() {
        return langList;
    }
}
