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
            landProgram = landProgramPage.adaptTo(LandProgramModel.class);
        }
    }
}
