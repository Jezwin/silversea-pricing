package com.silversea.aem.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.WcmConstants;

@Model(adaptables = Resource.class)
public class PriceModel {

    @Inject @Self
    private Resource resource;

    @Inject @Optional
    private String availability;

    private String[] tagIds;

    private String geomarket;

    @Inject
    private String currency;

    @Inject
    private Long price;

    @Inject @Optional
    private Long earlyBookingBonus;

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

        // init tag ids
        tagIds = resource.getValueMap().get("cq:tags", String[].class);

        // init geotagging
        if (tagIds != null) {
            for (String tagId : tagIds) {
                if (tagId.startsWith("geotagging:")) {
                    geomarket = tagId.replace("geotagging:", "");
                    break;
                }
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

    public Long getEarlyBookingBonus() {
        return earlyBookingBonus;
    }

    public Long getComputedPrice() {
        return earlyBookingBonus != null ? earlyBookingBonus : price;
    }

    public String getSuiteCategory() {
        return suiteCategory;
    }

    public SuiteModel getSuite() {
        return suite;
    }

    public boolean isWaitList() {
        return WcmConstants.PV_AVAILABILITY_WAITLIST.equals(availability);
    }
}
