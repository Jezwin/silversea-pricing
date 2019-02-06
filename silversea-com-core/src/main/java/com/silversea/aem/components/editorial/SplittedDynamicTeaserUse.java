package com.silversea.aem.components.editorial;

import com.google.common.collect.Sets;
import com.silversea.aem.models.CustomCss;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SplittedDynamicTeaserUse extends AbstractSilverUse {

    private ValueMap componentProp;
    private static final String PREFIX_ID_COMPONENT = "#splitteddynamicteaser-";
    protected static Set<String> IGNORED_KEYS = Sets.newHashSet("jcr", "sling", "cq", "sscUUID", "cssDesktop", "cssTablet", "cssMobile");

    @Override
    public void activate() throws Exception {
        componentProp = getComponentPropsValueMap();
        componentProp.putAll(setComponentPropsTabletValuesFromDesktop(componentProp));
        componentProp.putAll(getCustomCssValuesDesktop(PREFIX_ID_COMPONENT));
        componentProp.putAll(getCustomCssValuesTablet(PREFIX_ID_COMPONENT));
    }


    public ValueMap getComponentProp() {
        return componentProp;
    }

    protected ValueMap getComponentPropsValueMap() {
        return new ValueMapDecorator(getComponentProps());
    }

    protected Map<String, Object> getComponentProps() {
        Map<String, Object> componentProp = new HashMap<>();
        getProperties().forEach((key, value) -> {
            if (!IGNORED_KEYS.contains(key)) {
                componentProp.put(key, value);
            }
        });
        return componentProp;
    }

    /*
      Set tablet values from desktop if value is not present
    */
    protected Map<String, String> setComponentPropsTabletValuesFromDesktop(Map<String, Object> properties) {
        return setComponentPropsValuesDevices(properties, "Tablet", "Devices");
    }

    /*
     Set mobile values from desktop if value is not present
    */
    protected Map<String, String> setComponentPropsMobileValuesFromDesktop(Map<String, Object> properties) {
        return setComponentPropsValuesDevices(properties, "Mobile", "Devices");
    }

    private Map<String, String> setComponentPropsValuesDevices(Map<String, Object> properties, String device, String defaultDevice) {
        Map<String, String> componentProp = new HashMap<>();
        properties.forEach((key, value) -> {
            if (key.contains(defaultDevice)) {
                String keyOfDevice = key.replace(defaultDevice, device);
                if (!properties.containsKey(keyOfDevice)) {
                    properties.put(keyOfDevice, value);
                }
            }
        });
        return componentProp;
    }

    /*
     Retrieve all css custom styles for desktop inside the multifield putting the prefixID
     * */
    protected Map<String, String> getCustomCssValuesDesktop(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssDesktop");
    }

    protected Map<String, String> getCustomCssValuesTablet(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssTablet");
    }

    protected Map<String, String> getCustomCssValuesMobile(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssMobile");
    }

    /*
     Retrieve all css custom style by device inside the multifield
     Example:#splitteddynamicteaser--892277276 .title { font-size: 20px; }  #splitteddynamicteaser--892277276 .description{ font-size: 20px; }
     */
    private Map<String, String> getCustomCssValueDevice(String prefixID, String device) {
        Map<String, String> componentProp = new HashMap<>();
        getProp("sscUUID").ifPresent(id -> {
            StringBuilder cssDevice = new StringBuilder();
            List<CustomCss> cssList = retrieveMultiField(device, CustomCss.class);
            for (CustomCss customCss : cssList) {
                cssDevice.append(" ").append(prefixID).append(id).append(" ").append(customCss.getCss()).append(" ");
            }
            componentProp.put(device, cssDevice.toString());
        });
        return componentProp;
    }

}
