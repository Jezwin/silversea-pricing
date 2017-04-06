package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;

public class HeroBannerUse extends WCMUsePojo {
    private Button btn1;
    private Button btn2;

    @Override
    public void activate() throws Exception {
        btn1 = getButton("button1");
        btn2 = getButton("button2");
    }

    /**
     * @return Button
     */
    private Button getButton(String node){
        Button button;

        Resource res = getResourceResolver().getResource(getResource().getPath() + "/" + node);
        if(res != null) {
            ValueMap ppties = res.getValueMap();
            button = new Button(
                    ppties.get("titleDesktop", String.class),
                    ppties.get("titleTablet", String.class),
                    ppties.get("reference", String.class),
                    ppties.get("color", String.class),
                    ppties.get("analyticType", String.class),
                    ppties.get("size", String.class));
            return button;
        }

        return null;
    }

    /**
     * @return the btn1
     */
    public Button getBtn1() {
        return btn1;
    }

    /**
     * @return the btn2
     */
    public Button getBtn2() {
        return btn2;
    }
}