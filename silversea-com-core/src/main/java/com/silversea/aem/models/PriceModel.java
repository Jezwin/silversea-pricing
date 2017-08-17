package com.silversea.aem.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Model(adaptables = Resource.class)
public class PriceModel {

    @Inject @Self
    private Resource resource;

    @Inject
    private String availability;

    @Inject @Named("cq:Tags")
    private String[] tagIds;

    private String geomarket;

    @Inject
    private String currency;

    @Inject
    private Long price;

    @Inject
    private String suiteCategory;

    @Inject
    private String suiteReference;

    private SuiteModel suite;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);

        // init suite
        if (pageManager != null) {
            final Page suitePage = pageManager.getPage(suiteReference);

            if (suitePage != null) {
                suite = suitePage.adaptTo(SuiteModel.class);
            }
        }

        // init geotagging
        for (String tagId : tagIds) {
            if (tagId.startsWith("geotagging:")) {
                geomarket = tagId.replace("geotagging:", "");
                break;
            }
        }
    }

    public String getAvailability() {
        return availability;
    }

    public String[] getTagIds() {
        return tagIds;
    }

    public String getGeomarket() {
        return geomarket;
    }

    public String getCurrency() {
        return currency;
    }

    public Long getPrice() {
        return price;
    }

    public String getSuiteCategory() {
        return suiteCategory;
    }

    public SuiteModel getSuite() {
        return suite;
    }

    public boolean isWaitList() {
        return availability.equals("waitlist");
    }
}
