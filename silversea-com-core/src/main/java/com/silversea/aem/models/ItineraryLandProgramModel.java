package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ItineraryLandProgramModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int landProgramId;

    @Inject
    private String landProgramReference;

    // TODO create custom injector
    private LandProgramModel landProgram;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            final Page landProgramPage = pageManager.getPage(landProgramReference);

            if (landProgramPage != null) {
                landProgram = landProgramPage.adaptTo(LandProgramModel.class);
            }
        }
    }

    public String getTitle() {
        return landProgram != null ? landProgram.getTitle() : null;
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
        return landProgram != null ? landProgram.getAssetSelectionReference() : null;
    }
}
