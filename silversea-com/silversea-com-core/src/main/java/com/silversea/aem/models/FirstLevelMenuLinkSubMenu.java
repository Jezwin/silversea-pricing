package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class)
public class FirstLevelMenuLinkSubMenu {

    private final String label;
    private final boolean menu;
    private final List<SubMenuEntry> subMenu;

    public FirstLevelMenuLinkSubMenu(ExternalLink label) {
        this.label = label.getLabel();
        this.subMenu = Collections.singletonList(new SubMenuEntry(label));
        this.menu = false;
    }

    @Inject
    public FirstLevelMenuLinkSubMenu(@Named("label") String label, @Named("subMenu") @Optional List<SubMenuEntry> subMenu) {
        this.label = label;
        if (subMenu == null || subMenu.isEmpty()) {//this is a case of misconfiguration
            subMenu = Collections.singletonList(new SubMenuEntry(new ExternalLink("", label)));//TODO
        }
        this.subMenu = subMenu;
        this.menu = subMenu.size() > 1 || subMenu.get(0).getEntries().size() > 1;
    }

    public List<SubMenuEntry> getSubMenu() {
        return subMenu;
    }

    public String getLabel() {
        return label;
    }

    public String getLink() {//used when has not a menu
        return subMenu.get(0).getEntries().get(0).getLink();
    }

    public boolean hasMenu() {
        return menu;
    }
}
