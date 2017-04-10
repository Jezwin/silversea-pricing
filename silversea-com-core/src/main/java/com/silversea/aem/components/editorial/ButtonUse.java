package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;

public class ButtonUse extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonUse.class);
    private List<Button> callToActionList;

    @Override
    public void activate() throws Exception {
        final InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(getResource());
        String[] callToActions = properties.getInherited("callToActions", String[].class);
        if (callToActions != null) {

            String title, titleTablet, reference, color, analyticType, size;
            JSONObject json;
            callToActionList = new ArrayList<>();

            try {
                for (String button : callToActions) {
                    json = new JSONObject(button);

                    title = json.getString("title");
                    titleTablet = json.getString("titleTablet");
                    reference = json.getString("reference");
                    color = json.getString("color");
                    analyticType = json.getString("analyticType");
                    size = json.getString("size");
                    Button buttonObj = new Button(title, titleTablet, reference, color, analyticType, size);
                    callToActionList.add(buttonObj);
                }
            } catch (JSONException e) {
                LOGGER.error("JSONException button json parsing : {}", e);
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