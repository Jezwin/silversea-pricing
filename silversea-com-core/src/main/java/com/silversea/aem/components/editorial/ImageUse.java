package com.silversea.aem.components.editorial;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.confmgr.Conf;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;

public class ImageUse extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUse.class);

    private Integer desktopWidth = WcmConstants.DEFAULT_WIDTH_DESKTOP;
    private Integer mobileWidth = WcmConstants.DEFAULT_WIDTH_MOBILE;

    @Override
    public void activate() {
        Integer widthResponsiveDesktop = 12;
        Integer widthResponsiveMobile = 12;

        // Get value from the cq:responsive sub node
        Resource cqResponsiveRes = getResource().getChild(NameConstants.NN_RESPONSIVE_CONFIG);
        if (cqResponsiveRes != null) {
            Resource defaultRes = cqResponsiveRes.getChild(WcmConstants.NN_DEFAULT);
            if (defaultRes != null) {
                widthResponsiveDesktop = defaultRes.getValueMap().get("width", 12);
            }

            Resource phoneRes = cqResponsiveRes.getChild(WcmConstants.NN_PHONE);
            if (phoneRes != null) {
                widthResponsiveMobile = phoneRes.getValueMap().get("width", 12);
            } else {
                widthResponsiveMobile = widthResponsiveDesktop;
            }
        }

        // Get width from configuration
        Resource confRes = getResource().adaptTo(Conf.class).getItemResource("/responsive/image/");

        if (confRes != null) {
            Resource desktopConfRes = confRes.getChild(getResource().getResourceType() + "/desktop-" + widthResponsiveDesktop);

            if (desktopConfRes == null) {
                // If configuration is not define for the component, use the image component configuration.
                desktopConfRes = confRes.getChild(WcmConstants.RT_IMAGE_CPT + "/desktop-" + widthResponsiveDesktop);
            }
            desktopWidth = desktopConfRes.getValueMap().get("width", Integer.class);

            Resource mobileConfRes = confRes.getChild(getResource().getResourceType() + "/mobile-" + widthResponsiveMobile);
            if (mobileConfRes == null) {
                // If configuration is not define for the component, use the image component configuration.
                mobileConfRes = confRes.getChild(WcmConstants.RT_IMAGE_CPT + "/mobile-" + widthResponsiveMobile);
            }
            mobileWidth = mobileConfRes.getValueMap().get("width", Integer.class);
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