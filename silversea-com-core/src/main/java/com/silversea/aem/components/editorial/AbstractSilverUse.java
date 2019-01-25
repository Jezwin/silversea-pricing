package com.silversea.aem.components.editorial;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.silversea.aem.models.CustomCss;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

public abstract class AbstractSilverUse extends WCMUsePojo {

    protected static Set<String> IGNORED_KEYS = Sets.newHashSet("jcr", "sling", "cq", "sscUUID", "cssDesktop", "cssTablet", "cssMobile");
    public static final String TABLET = "Tablet";
    public static final String MOBILE = "Mobile";
    public static final String DESKTOP = "Desktop";

    protected Optional<String> getProp(String key) {
        return getProp(key, String.class);
    }

    protected String getProp(String key, String defaultValue) {
        return getProp(key, String.class, defaultValue);
    }

    protected int getInt(String key, int defaultValue) {
        return getProp(key).map(Integer::parseInt).orElse(defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getProp(key).map("true"::equals).orElse(defaultValue);
    }


    protected <T> Optional<T> getProp(String key, Class<T> type) {
        return ofNullable(getProperties()).map(props -> props.get(key, type));
    }

    protected <T> T getProp(String key, Class<T> type, T defaultValue) {
        return getProp(key, type).orElse(defaultValue);

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
    protected <T> Map setComponentPropsTabletValuesFromDesktop(Map properties) {
        return setComponentPropsValuesDevices(properties, "Tablet", "Devices");
    }

    /*
     Set mobile values from desktop if value is not present
    */
    protected <T> Map setComponentPropsMobileValuesFromDesktop(Map properties) {
        return setComponentPropsValuesDevices(properties, "Mobile", "Devices");
    }

    private <T> Map setComponentPropsValuesDevices(Map<String, Object> properties, String device, String defaultDevice) {
        Map componentProp = new HashMap<>();
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
    protected <T> Map getCustomCssValuesDesktop(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssDesktop");
    }

    /*
     Retrieve all css custom styles for tablet inside the multifield putting the prefixID
     * */
    protected <T> Map getCustomCssValuesTablet(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssTablet");
    }

    /*
     Retrieve all css custom styles for mobile inside the multifield putting the prefixID
     * */
    protected <T> Map getCustomCssValuesMobile(String prefixID) {
        return getCustomCssValueDevice(prefixID, "cssMobile");
    }

    /*
     Retrieve all css custom style by device inside the multifield
     Example:#splitteddynamicteaser--892277276 .title { font-size: 20px; }  #splitteddynamicteaser--892277276 .description{ font-size: 20px; }
     * */
    private <T> Map getCustomCssValueDevice(String prefixID, String device) {
        Map componentProp = new HashMap<>();
        getProp("sscUUID").ifPresent(id -> {
            String cssDevice = "";
            List<CustomCss> cssList = retrieveMultiField(device, CustomCss.class);
            for (CustomCss customCss : cssList) {
                cssDevice += " " + prefixID + id + " " + customCss.getCss() + " ";
            }
            componentProp.put(device, cssDevice);
        });
        return componentProp;
    }


    private <T> T fromVarArgs(T[] args, int index) {
        if (args == null || args.length == 0) {
            return null;
        }
        int length = args.length;
        if (length <= index) {
            return args[length - 1];
        }
        return args[index];
    }


    protected DeviceProperty<String> getDeviceProp(String key, String... defaultValue) {
        return new DeviceProperty<>(
                getProp(key + DESKTOP).filter(Strings::isNullOrEmpty).orElse(fromVarArgs(defaultValue, 0)),
                getProp(key + TABLET).filter(Strings::isNullOrEmpty).orElse(fromVarArgs(defaultValue, 0)),
                getProp(key + MOBILE).filter(Strings::isNullOrEmpty).orElse(fromVarArgs(defaultValue, 0))
        );
    }

    protected DeviceProperty<Integer> getDeviceProp(String key, Integer... defaultValue) {
        return new DeviceProperty<>(
                getInt(key + DESKTOP, fromVarArgs(defaultValue, 0)),
                getInt(key + TABLET, fromVarArgs(defaultValue, 1)),
                getInt(key + MOBILE, fromVarArgs(defaultValue, 2))
        );
    }

    protected DeviceProperty<Boolean> getDeviceProp(String key, Boolean... defaultValue) {
        return new DeviceProperty<>(
                getBoolean(key + DESKTOP, fromVarArgs(defaultValue, 0)),
                getBoolean(key + TABLET, fromVarArgs(defaultValue, 1)),
                getBoolean(key + MOBILE, fromVarArgs(defaultValue, 2))
        );
    }

    protected <T> List<T> retrieveMultiField(String child, Class<T> adaptable) {
        return retrieveMultiField(child, element -> element.adaptTo(adaptable)).collect(Collectors.toList());
    }


    protected <T> Stream<T> retrieveMultiField(String child, Function<Resource, T> map) {
        return ofNullable(getResource())
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(map).filter(Objects::nonNull))
                .orElse(Stream.empty());
    }


}
