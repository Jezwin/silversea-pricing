package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUpUse extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(SignUpUse.class);

    private String pageReference;

    @Override
    public void activate() throws Exception {

        InheritanceValueMap allInheritedProperties = new HierarchyNodeInheritanceValueMap(getResource());
        pageReference = allInheritedProperties.getInherited("pageReference", String.class);
    }
    public String getPageReference () {
        return pageReference;
    }
}
