package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class)
public class ItineraryExcursionModel {

    @Inject
    @Self
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int excursionId;

    @Inject
    @Optional
    private String excursionReference;

    @Inject
    @Optional
    private Double duration;

    @Inject
    @Optional
    private String plannedDepartureTime;

    @Inject
    @Optional
    private String generalDepartureTime;
    
    @Inject
    @Optional
    private String shorexCategory;
    
    @Inject
    @Optional
    private int price;

    // TODO create custom injector
    private ExcursionModel excursion;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null && StringUtils.isNotEmpty(excursionReference)) {
            final Page excursionPage = pageManager.getPage(excursionReference);

            if (excursionPage != null) {
                excursion = excursionPage.adaptTo(ExcursionModel.class);
                excursion.setPriceAndCategory(this.price,this.shorexCategory);
            }
        }
    }

    public Duration getDuration() {
        int hours = duration.intValue();
        int minutes = (int) ((duration - hours) * 60);

        return new Duration(hours, minutes);
    }

    public String getPlannedDepartureTime() {
        return plannedDepartureTime;
    }

    public String getShorexCategory() {
        return shorexCategory;
    }
    
    public int getPrice() {
        return price;
    }

    public void setGeneralDepartureTime(String generalDepartureTime) {
        this.generalDepartureTime = generalDepartureTime;
    }

    /**
     * @return same as generalDepartureTime, used for existing html compat
     * @see #getGeneralDepartureTime()
     */
    public String schedule() {
        return generalDepartureTime;
    }

    public String getTitle() {
        return excursion != null ? excursion.getTitle() : null;
    }

    public String getDescription() {
        return excursion != null ? excursion.getDescription() : null;
    }

    public String getNote() {
        return excursion != null ? excursion.getNote() : null;
    }

    public String getCodeExcursion() {
        return excursion != null ? excursion.getCodeExcursion() : null;
    }

    public String getApiLongDescription() {
        return excursion != null ? excursion.getApiLongDescription() : null;
    }

    public String getLongDescription() {
        return excursion != null ? excursion.getLongDescription() : null;
    }

    public List<FeatureModel> getFeatures() {
        return excursion != null ? excursion.getFeatures() : new ArrayList<>();
    }

    public Page getPage() {
        return excursion != null ? excursion.getPage() : null;
    }

    public Resource getResource() {
        return resource;
    }

    public String getSchedule() {
        return excursion != null ? excursion.getSchedule() : null;
    }

    public ExcursionModel getExcursion() {
        return excursion;
    }

    public String getAssetSelectionReference() {
        return excursion != null ? CruiseUtils.firstNonNull(excursion.getAssetSelectionReference(),
                excursion.getAssetSelectionReferenceApi()) : null;
    }

    public Long getShorexId() {
        return excursion != null ? excursion.getShorexId() : null;
    }

    public String getCode() {
        return excursion != null ? excursion.getCodeExcursion() : null;
    }

    public class Duration {
        int hours;
        int minutes;

        public Duration(final int hours, final int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        public int getHours() {
            return hours;
        }

        public int getMinutes() {
            return minutes;
        }
    }
}
