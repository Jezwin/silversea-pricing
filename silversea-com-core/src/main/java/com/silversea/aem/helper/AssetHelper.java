package com.silversea.aem.helper;

import com.adobe.cq.dam.dm.delivery.api.ImageDelivery;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.silversea.aem.services.RunModesService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

public class AssetHelper extends WCMUsePojo {

    static final private Logger LOGGER = LoggerFactory.getLogger(AssetHelper.class);

    private ImageDelivery imageDelivery;
    private PublishUtils publishUtils;
    private RunModesService runModesService;

    private String assetPath;

    @Override
    public void activate() throws Exception {
        assetPath = get("assetPath", String.class);

        imageDelivery = getSlingScriptHelper().getService(ImageDelivery.class);
        publishUtils = getSlingScriptHelper().getService(PublishUtils.class);
        runModesService = getSlingScriptHelper().getService(RunModesService.class);
    }

    /**
     * @return the assetType
     */
    public String getAssetType() {
        if (StringUtils.isNotBlank(assetPath)) {
            Resource res = getResourceResolver().getResource(assetPath + "/" + JcrConstants.JCR_CONTENT);
            if (res != null) {
                return res.getValueMap().get(S7damConstants.PN_S7_TYPE, String.class);
            }
        }

        return null;
    }

    /**
     * @return the final image URL, including dynamic media domain if enabled, and if we are on a publish
     */
    public String getImageUrl() {
        final Resource assetResource = getResourceResolver().getResource(assetPath);

        if (runModesService.isPublish() && imageDelivery != null && publishUtils != null && imageDelivery.isEnabled()) {
            try {
                LOGGER.debug("{}", publishUtils.externalizeImageDeliveryAsset(assetResource, assetPath));

                return publishUtils.externalizeImageDeliveryAsset(assetResource, assetPath);
            } catch (RepositoryException e) {
                LOGGER.error("Cannot get Dynamic Media image url", e);
            }
        }

        return "/is/image" + assetPath;
    }
}