package com.silversea.aem.importers;

import java.util.HashMap;

/**
 * @author aurelienolivier
 */
public interface ImportersConstants {

    String BASEPATH_PORTS = "/content/silversea-com/en/other-resources/find-a-port";

    HashMap PROPERTIES_MAPPING_PORTS = new HashMap() {{
        put("city_cod", "cityCode");
        put("city_name", "jcr:title");
        put("description", "jcr:description");
    }};
}
