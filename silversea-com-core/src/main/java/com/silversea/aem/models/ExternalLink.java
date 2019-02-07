package com.silversea.aem.models;

import com.day.cq.commons.Externalizer;
import org.apache.sling.api.resource.ResourceResolver;

public class ExternalLink {

    private final String link;
    private final String label;

    public ExternalLink(String localLink, String label, Externalizer externalizer, ResourceResolver resourceResolver) {
        this(externalizer.externalLink(resourceResolver, Externalizer.LOCAL, localLink), label);
    }

    public ExternalLink(String link, String label) {
        this.link = link;
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public String getLabel() {
        return label;
    }

}
