package com.silversea.aem.helper;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static java.lang.System.lineSeparator;

/*
 * INPUT
 * data-sly-test.fwHelper=${FrameworkVarHelper @ vars='titleHeight, descriptionHeight'} style=${fwHelper.allDevices}
 *
 *
 *
 * OUTPUT
 * --title-height-mobile: `properties.titleHeightMobile`;
 * --title-height-tablet: `properties.titleHeightTablet`;
 * --title-height-desktop: `properties.titleHeightDesktop`;
 * --description-height-mobile: `properties.descriptionHeightMobile`;
 * ...etc
 *
 *
 * note that the property value must contain the unit type. */
public class FrameworkVarHelper extends AbstractFrameworkHelper {
    @Override
    public String generateOutput(String property, String device, String method) {
        String valueWithUnit = getString(property + device);
        if (valueWithUnit == null) {
            return "";
        }
        property = property.replace("2", "-2");
        return "--" + LOWER_CAMEL.to(LOWER_HYPHEN, property + device) + ": " + valueWithUnit + ";" + lineSeparator();
    }

}
