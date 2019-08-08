package com.silversea.ssc.aem.models;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class StickyMenuUse extends AbstractGeolocationAwareUse {
    private List<MenuItemModel> items;
    private boolean replaceHeader;
    private boolean showOnDesktop;
    private boolean showOnTablet;
    private boolean showOnMobile;


    @Override
    public void activate() throws Exception {
        super.activate();
        items = retrieveMultiField("menuItems", MenuItemModel.class).stream().filter(this::isMenuItemMatchingGeo)
                .collect(toList());
        replaceHeader = "true".equals(getProperties().get("replaceHeader", String.class));
        showOnDesktop = items.stream().anyMatch(MenuItemModel::isDesktop);
        showOnTablet = items.stream().anyMatch(MenuItemModel::isTablet);
        showOnMobile = items.stream().anyMatch(MenuItemModel::isMobile);
    }

    public List<MenuItemModel> getItems() {
        return items;
    }

    private boolean isMenuItemMatchingGeo(MenuItemModel item) {
        String[] tags = item.getGeoTag();
        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                String t = tag.replaceAll(WcmConstants.GEOLOCATION_TAGS_PREFIX, "");
                if (super.isBestMatch(t)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }


    public boolean isReplaceHeader() {
        return replaceHeader;
    }

    public boolean isShowOnDesktop() {
        return showOnDesktop;
    }

    public boolean isShowOnTablet() {
        return showOnTablet;
    }

    public boolean isShowOnMobile() {
        return showOnMobile;
    }
}
