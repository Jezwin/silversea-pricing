package com.silversea.aem.helper;

import org.apache.sling.api.resource.ValueMap;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void activate() throws Exception {
        super.activate();
        ValueMap properties = get("properties", ValueMap.class);
        if (properties != null) {
            Map<String, Object> map = new HashMap<>();
            map.putAll(getComponentProperties());
            map.putAll(properties);
            setComponentProperties(map);
        }
    }

    @Override
    public String generateOutput(String property, String device, String method) {
        String valueWithUnit = getString((property + device).trim());
        if (valueWithUnit == null) {
            return "";
        }
        //special exception for hover and before
        if ("sscFwContent".equalsIgnoreCase(property)) {
            valueWithUnit = "'" + valueWithUnit + "'";
        }
        if (property.contains("Hover")) {
            Optional<Color> color = parseAndDarker(valueWithUnit);
            if(color.isPresent()) {
                valueWithUnit = "rgb(" + color.get().getRed() + "," + color.get().getGreen() + "," + color.get().getBlue() + ")";
            }
        }
        property = property.replace("2", "-2");
        return "--" + LOWER_CAMEL.to(LOWER_HYPHEN, property + device) + ": " + valueWithUnit + ";" + lineSeparator();
    }

    private Optional<Color> parseAndDarker(String input) {
        Pattern c = Pattern.compile("rgba *\\( *([0-9]+), *([0-9]+), *([0-9]+), *([0.1-1]+) *\\)");
        Matcher m = c.matcher(input);

        if (m.matches()) {
            return Optional.of(new Color(Integer.valueOf(m.group(1)),  // r
                    Integer.valueOf(m.group(2)),  // g
                    Integer.valueOf(m.group(3))).darker()); // b
        }

        return Optional.empty();
    }
}
