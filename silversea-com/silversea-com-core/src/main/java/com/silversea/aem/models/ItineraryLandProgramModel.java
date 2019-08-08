package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ItineraryLandProgramModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int landProgramId;

    @Inject
    @Optional
    private String landProgramReference;

    // TODO create custom injector
    private LandProgramModel landProgram;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null && StringUtils.isNotEmpty(landProgramReference)) {
            final Page landProgramPage = pageManager.getPage(landProgramReference);

            if (landProgramPage != null) {
                landProgram = landProgramPage.adaptTo(LandProgramModel.class);
            }
        }
    }

    public String getTitle() {
        return landProgram != null ? landProgram.getTitle() : null;
    }

    public String getCustomTitle() {
        return landProgram != null ? landProgram.getCustomTitle() : null;
    }

    public String getDescription() {
        return landProgram != null ? landProgram.getDescription() : null;
    }

    public String getShortDescription() {
        return landProgram != null ? landProgram.getShortDescription() : null;
    }

    public Long getLandId() {
        return landProgram != null ? landProgram.getLandId() : null;
    }

    public String getLandCode() {
        return landProgram != null ? landProgram.getLandCode() : null;
    }

    public String getCode() {
        return getLandCode();
    }

    public LandProgramModel getLandProgram() {
        return landProgram;
    }

    public String getAssetSelectionReference() {
        return landProgram != null ? CruiseUtils.firstNonNull(landProgram.getAssetSelectionReference(),
                landProgram.getAssetSelectionReferenceApi()) : null;
    }

    public String getNote() {
        return landProgram != null ? landProgram.getNote() :  null;
    }
}
