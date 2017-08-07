package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

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
            excursion = excursionPage.adaptTo(ExcursionModel.class);
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

    /**
     * @see #getGeneralDepartureTime()
     * @return same as generalDepartureTime, used for existing html compat
     */
    public String schedule() {
        return generalDepartureTime;
    }

    public String getTitle() {
        return excursion.getTitle();
    }

    public String getDescription() {
        return excursion.getDescription();
    }

    public String getCodeExcursion() {
        return excursion.getCodeExcursion();
    }

    public String getApiLongDescription() {
        return excursion.getApiLongDescription();
    }

    public String getLongDescription() {
        return excursion.getLongDescription();
    }

    public Page getPage() {
        return excursion.getPage();
    }

    public String getSchedule() {
        return excursion.getSchedule();
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
