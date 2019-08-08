package com.silversea.aem.helper;

import com.silversea.aem.components.editorial.DeviceProperty;
import org.apache.sling.api.resource.ValueMap;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static java.lang.Integer.valueOf;
import static java.lang.System.in;
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

    private static final Pattern COLOR_PATTERN_RGBA =
            Pattern.compile("rgba *\\( *([0-9]+), *([0-9]+), *([0-9]+), *([0.1-1]+) *\\)");

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

        /*
         * With this lines you can use the helper like this ...Helper @ property='x,y,z', x=devicePropertyX, y=devicePropertyY
         */
        for (String attribute : getCssAttributes()) {
            DeviceProperty deviceProperty = get(attribute, DeviceProperty.class);
            if (deviceProperty != null) {
                for (String device : DEVICES) {
                    getComponentProperties().put(attribute + device, deviceProperty.get(device));
                }
            }
        }


    }

    @Override
    public String generateOutput(String property, String device, String method) {
        String valueWithUnit = getString((property + device).trim());
        if (valueWithUnit == null) {
            return "";
        }
        valueWithUnit = specialCases(property, valueWithUnit);
        return "--" + LOWER_CAMEL.to(LOWER_HYPHEN, property + device) + ": " + valueWithUnit + ";" + lineSeparator();
    }

    private String specialCases(String property, String valueWithUnit) {
        //special exception for hover and before
        if ("sscFwContent".equalsIgnoreCase(property)) {
            valueWithUnit = "'" + valueWithUnit + "'";
        }
        if (property.contains("Hover")) {
            valueWithUnit = hoverColor(valueWithUnit).orElse(valueWithUnit);
        }
        return valueWithUnit;
    }

    private Optional<String> hoverColor(String input) {
        Matcher m = COLOR_PATTERN_RGBA.matcher(input);
        if (m.matches()) {
            return Optional.of(
                    new Color(valueOf(m.group(1)), valueOf(m.group(2)), valueOf(m.group(3))).darker())
                    .map(color -> "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
        } else if (input.startsWith("#")) {
            return Optional.of(
                    Color.decode(input).darker())
                    .map(color -> "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
        }
        return Optional.empty();
    }
}
