package com.silversea.aem.components.editorial;

import java.util.Map;

public class SplittedDynamicTeaserUse extends AbstractSilverUse {

    private Map<String, Object> componentProp;
    private static final String PREFIX_ID_COMPONENT = "#splitteddynamicteaser-";

    @Override
    public void activate() throws Exception {
        componentProp = getComponentProps();
        componentProp.putAll(setComponentPropsTabletValuesFromDesktop(componentProp));
        componentProp.putAll(getCustomCssValuesDesktop(PREFIX_ID_COMPONENT));
        componentProp.putAll(getCustomCssValuesTablet(PREFIX_ID_COMPONENT));
    }


    public Map<String, Object> getComponentProp() {
        return componentProp;
    }
}
