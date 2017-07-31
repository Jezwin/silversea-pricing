package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.silversea.aem.components.beans.Button;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ButtonUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonUse.class);

    private List<Button> callToActionList;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        final String[] callToActions = properties.getInherited("callToActions", String[].class);

        if (callToActions != null) {
            callToActionList = new ArrayList<>();

            try {
                for (String button : callToActions) {
                    final JSONObject json = new JSONObject(button);

                    final String title = json.optString("title");
                    final String titleTablet = json.optString("titleTablet");
                    final String reference = json.optString("reference");
                    final String color = json.optString("color");
                    final String analyticType = json.optString("analyticType");
                    final String size = json.optString("size");

                    callToActionList.add(new Button(title, titleTablet, reference, color, analyticType, size));
                }
            } catch (JSONException e) {
                LOGGER.error("JSONException button json parsing", e);
            }
        }
    }

    /**
     * @return the calltoActionList
     */
    public List<Button> getCalltoActionList() {
        return callToActionList;
    }
}