package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;

public class ImageUse extends WCMUsePojo {
    private Integer desktopWidth = WcmConstants.DEFAULT_WIDTH_DESKTOP;
    private Integer mobileWidth = WcmConstants.DEFAULT_WIDTH_MOBILE;
    private static final String SLASH_DESKTOP_DASH = "/desktop-";
    private static final String SLASH_MOBILE_DASH = "/mobile-";

    @Override
    public void activate() {
        Integer widthResponsiveDesktop = 12;
        Integer widthResponsiveMobile = 12;

        // Get value from the cq:responsive sub node
        Resource cqResponsiveRes = getResource().getChild(NameConstants.NN_RESPONSIVE_CONFIG);
        Resource parentSlider = getResource().getParent().getParent();
        Integer sliderWidth = null;
        if(parentSlider != null){
        	//We are in a slider 
        	if(parentSlider.getValueMap().get("sling:resourceType", String.class).equalsIgnoreCase("silversea/silversea-com/components/editorial/slider")){
        		Integer itemToDisplay = parentSlider.getValueMap().get("itemNumber", Integer.class);
        		if(itemToDisplay != null){
        			sliderWidth = 12 / itemToDisplay;
        			if(itemToDisplay > 1){
        				sliderWidth = sliderWidth + 1;
        			}
        		}
        	}
        }
        
        if (cqResponsiveRes != null) {
            Resource defaultRes = cqResponsiveRes.getChild(WcmConstants.NN_DEFAULT);
            if (defaultRes != null) {
                widthResponsiveDesktop = defaultRes.getValueMap().get(WcmConstants.PN_WIDTH, 12);
                if(sliderWidth != null){
                	widthResponsiveDesktop = sliderWidth;
                }
            }

            Resource phoneRes = cqResponsiveRes.getChild(WcmConstants.NN_PHONE);
            if (phoneRes != null) {
                widthResponsiveMobile = phoneRes.getValueMap().get(WcmConstants.PN_WIDTH, 12);
            } else {
                widthResponsiveMobile = widthResponsiveDesktop;
            }
        }

        // Get width from configuration
        Resource confRes = getResource().adaptTo(Conf.class).getItemResource("/responsive/image/");

        if (confRes != null) {
            Resource desktopConfRes = confRes.getChild(getResource().getResourceType() + SLASH_DESKTOP_DASH + widthResponsiveDesktop);

            if (desktopConfRes == null) {
                // If configuration is not define for the component, use the image component configuration.
                desktopConfRes = confRes.getChild(WcmConstants.RT_IMAGE_CPT + SLASH_DESKTOP_DASH + widthResponsiveDesktop);
            }
            desktopWidth = desktopConfRes.getValueMap().get(WcmConstants.PN_WIDTH, Integer.class);

            Resource mobileConfRes = confRes.getChild(getResource().getResourceType() + SLASH_MOBILE_DASH + widthResponsiveMobile);
            if (mobileConfRes == null) {
                // If configuration is not define for the component, use the image component configuration.
                mobileConfRes = confRes.getChild(WcmConstants.RT_IMAGE_CPT + SLASH_MOBILE_DASH + widthResponsiveMobile);
            }
            mobileWidth = mobileConfRes.getValueMap().get(WcmConstants.PN_WIDTH, Integer.class);
        }
    }

    /**
     * @return the desktopWidth
     */
    public Integer getDesktopWidth() {
        return desktopWidth;
    }

    /**
     * @return the mobileWidth
     */
    public Integer getMobileWidth() {
        return mobileWidth;
    }

    /**
     * @return the desktopRetinaWidth
     */
    public Integer getDesktopRetinaWidth() {
        return desktopWidth * 2;
    }

    /**
     * @return the mobileRetinaWidth
     */
    public Integer getMobileRetinaWidth() {
        return mobileWidth * 2;
    }
}