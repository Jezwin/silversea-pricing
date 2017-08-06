package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ItineraryExcursionModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int excursionId;

    @Inject
    private String excursionReference;

    // TODO create custom injector
    private ExcursionModel excursion;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            final Page excursionPage = pageManager.getPage(excursionReference);
            excursion = excursionPage.adaptTo(ExcursionModel.class);
        }
    }
}
