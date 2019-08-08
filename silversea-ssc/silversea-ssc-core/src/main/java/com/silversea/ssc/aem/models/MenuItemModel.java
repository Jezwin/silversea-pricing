package com.silversea.ssc.aem.models;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import java.util.HashMap;

@Model(adaptables = Resource.class)
public class MenuItemModel {
    @Inject
    private String text;
    @Inject
    @Optional
    private String anchor;
    @Inject
    @Optional
    private String pageLink;
    @Inject
    @Optional
    private boolean mobile;
    @Inject
    @Optional
    private boolean desktop;
    @Inject
    @Optional
    private boolean tablet;
    @Inject
    @Optional
    private int offsetScrollDesktop;
    @Inject
    @Optional
    private int offsetScrollTablet;
    @Inject
    @Optional
    private int offsetScrollMobile;
    @Inject
    @Optional
    private String sscFwBackgroundColorDesktop;
    @Inject
    @Optional
    private String sscFwColorDesktop;
    @Inject
    @Optional
    private String sscFwBackgroundColorTablet;
    @Inject
    @Optional
    private String sscFwColorTablet;
    @Inject
    @Optional
    private String sscFwBackgroundColorMobile;
    @Inject
    @Optional
    private String sscFwColorMobile;

    @Inject
    @Optional
    private String[] geoTag;

    private ValueMap properties;

    public MenuItemModel() {
    }

    public String getText() {
        return text;
    }

    public String getAnchor() {
        return anchor;
    }

    public boolean isMobile() {
        return mobile;
    }

    public boolean isDesktop() {
        return desktop;
    }

    public boolean isTablet() {
        return tablet;
    }

    public String[] getGeoTag() {
        return geoTag;
    }

    public int getOffsetScrollMobile() {
        return offsetScrollMobile;
    }

    public int getOffsetScrollTablet() {
        return offsetScrollTablet;
    }

    public int getOffsetScrollDesktop() {
        return offsetScrollDesktop;
    }

    public void setOffsetScrollDesktop(int offsetScrollDesktop) {
        this.offsetScrollDesktop = offsetScrollDesktop;
    }

    public String getSscFwBackgroundColorDesktop() {
        return sscFwBackgroundColorDesktop;
    }

    public String getSscFwColorDesktop() {
        return sscFwColorDesktop;
    }

    public String getSscFwBackgroundColorTablet() {
        return sscFwBackgroundColorTablet;
    }

    public String getSscFwColorTablet() {
        return sscFwColorTablet;
    }

    public String getSscFwBackgroundColorMobile() {
        return sscFwBackgroundColorMobile;
    }

    public String getSscFwColorMobile() {
        return sscFwColorMobile;
    }

    public ValueMap getProperties() {
        properties = new ValueMapDecorator(new HashMap<>());
        properties.put("sscFwBackgroundColorDesktop", this.sscFwBackgroundColorDesktop);
        properties.put("sscFwBackgroundColorTablet", this.sscFwBackgroundColorTablet);
        properties.put("sscFwBackgroundColorMobile", this.sscFwBackgroundColorMobile);
        properties.put("sscFwColorDesktop", this.sscFwColorDesktop);
        properties.put("sscFwColorTablet", this.sscFwColorTablet);
        properties.put("sscFwColorMobile", this.sscFwColorMobile);
        return properties;
    }

    public String getPageLink() {
        return pageLink;
    }

    public boolean isExternal() {
        return java.util.Optional.ofNullable(getAnchor()).map(s -> s.startsWith("#") || s.startsWith(".")).orElse(getPageLink() != null);
    }
}
