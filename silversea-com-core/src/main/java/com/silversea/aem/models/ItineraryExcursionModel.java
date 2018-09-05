package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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

    @Inject @Self
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int excursionId;

    @Inject
    private String excursionReference;

    @Inject @Optional
    private Double duration;

    @Inject @Optional
    private String plannedDepartureTime;

    @Inject @Optional
    private String generalDepartureTime;

    // TODO create custom injector
    private ExcursionModel excursion;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            final Page excursionPage = pageManager.getPage(excursionReference);

            if (excursionPage != null) {
                excursion = excursionPage.adaptTo(ExcursionModel.class);
            }
        }
    }

    public Duration getDuration() {
        int hours = duration.intValue();
        int minutes = (int)((duration - hours) * 60);

        return new Duration(hours, minutes);
    }

    public String getPlannedDepartureTime() {
        return plannedDepartureTime;
    }

    public String getGeneralDepartureTime() {
        return generalDepartureTime;
    }

    public void setGeneralDepartureTime(String generalDepartureTime) {
        this.generalDepartureTime = generalDepartureTime;
    }

    /**
     * @see #getGeneralDepartureTime()
     * @return same as generalDepartureTime, used for existing html compat
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
        return excursion != null ? excursion.getAssetSelectionReference() : null;
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
