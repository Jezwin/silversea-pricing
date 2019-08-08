package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Page.class)
public class SegmentModel {

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/focusedMapReference") @Optional
    private String focusedMapReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/mapReference") @Optional
    private String mapReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/subtitle") @Optional
    private String subtitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cruiseReference") @Optional
    private String cruiseReference;

    private CruiseModel cruise;

    @PostConstruct
    private void init() {
        final Page cruisePage = this.page.getPageManager().getPage(cruiseReference);
        if (cruisePage != null) {
            cruise = cruisePage.adaptTo(CruiseModel.class);
        }
    }

    public Page getPage() {
        return page;
    }

    public CruiseModel getCruise() {
        return cruise;
    }

    public String getFocusedMapReference() {
        return focusedMapReference;
    }

    public String getMapReference() {
        return mapReference;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
