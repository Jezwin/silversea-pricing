package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class)
public class HeaderSecondRowMenu {

    private final ExternalLink link;
    private final List<SubMenuEntry> subMenu;

    public HeaderSecondRowMenu(ExternalLink link, List<SubMenuEntry> subMenu) {
        this.link = link;
        this.subMenu = subMenu;
    }

    public List<SubMenuEntry> getSubMenu() {
        return subMenu;
    }

    public String getLabel() {
        return link.getLabel();
    }

    public String getLink() {//used when has not a menu
        return link.getLink();
    }

    public boolean hasMenu() {
        return !subMenu.isEmpty();
    }
}
