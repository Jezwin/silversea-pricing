package com.silversea.aem.importers;

import java.util.HashMap;

/**
 * @author aurelienolivier
 */
public interface ImportersConstants {

    String CITIES_BASE_PATH = "/content/silversea-com/en/ports";

    HashMap CITY_PROPERTIES_MAPPING = new HashMap() {{
        put("city_cod", "cityCode");
        put("city_name", "jcr:title");
        put("description", "jcr:description");
    }};
}
