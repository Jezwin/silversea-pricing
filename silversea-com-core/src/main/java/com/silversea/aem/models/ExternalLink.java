package com.silversea.aem.models;

public class ExternalLink {

    private final String link;
    private final String label;

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
