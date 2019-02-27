package com.silversea.aem.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;


@Model(adaptables = Resource.class)
public class MenuEntry {
    private final Page page;

    private final String label;

    @Inject
    public MenuEntry(@Named("page") Page page, @Named("label") @Optional String label) {
        this.page = page;
        this.label = label;
    }

    public MenuEntry(Page page){
        this.page = page;
        this.label = CruiseUtils.firstNonNull(page.getNavigationTitle(), page.getTitle());
    }

    public Page getPage() {
        return page;
    }

    public String getLabel() {
        return label;
    }

    public ExternalLink toExternalLink(Externalizer externalizer, SlingHttpServletRequest request) {
        return new ExternalLink(page.getPath(), CruiseUtils.firstNonNull(label, page.getNavigationTitle(), page.getTitle()), externalizer,
                request);
    }
}
