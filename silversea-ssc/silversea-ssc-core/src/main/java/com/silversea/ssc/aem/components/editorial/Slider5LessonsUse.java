package com.silversea.ssc.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;

public class Slider5LessonsUse extends WCMUsePojo {

    private String assetSelectionReferenceDesktop;
    private String assetSelectionReferenceMobile;

    private String assetSelectionReferenceGalleryDesktop;
    private String assetSelectionReferenceGalleryMobile;


    @Override
    public void activate() throws Exception {
        String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        if (selectors != null && selectors.length > 1) {
            assetSelectionReferenceGalleryDesktop = getProperties().get("assetSelectionReferenceDesktopLesson" + selectors[1], String.class);
            assetSelectionReferenceGalleryMobile = getProperties().get("assetSelectionReferenceMobileLesson" + selectors[1], String.class);
        }
    }

    public String getAssetSelectionReferenceDesktop() {
        return assetSelectionReferenceDesktop;
    }

    public void setAssetSelectionReferenceDesktop(String assetSelectionReferenceDesktop) {
        this.assetSelectionReferenceDesktop = assetSelectionReferenceDesktop;
    }

    public String getAssetSelectionReferenceMobile() {
        return assetSelectionReferenceMobile;
    }

    public String getAssetSelectionReferenceGalleryDesktop() {
        return assetSelectionReferenceGalleryDesktop;
    }

    public String getAssetSelectionReferenceGalleryMobile() {
        return assetSelectionReferenceGalleryMobile;
    }
}
