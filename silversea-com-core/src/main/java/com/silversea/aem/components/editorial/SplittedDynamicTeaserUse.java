package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;


public class SplittedDynamicTeaserUse extends AbstractSilverUse {

    private ValueMap componentProp;
    private static final String PREFIX_ID_COMPONENT = "#splitteddynamicteaser-";

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

}
