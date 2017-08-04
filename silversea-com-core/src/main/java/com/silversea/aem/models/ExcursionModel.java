package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Duration;
import com.silversea.aem.components.beans.Feature;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aurelienolivier on 12/02/2017.
 */
@Model(adaptables = Page.class)
public class ExcursionModel extends AbstractModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExcursionModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/codeExcursion") @Optional
    private String codeExcursion;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiLongDescription") @Optional
    private String apiLongDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/pois") @Optional
    private String pois;

    private String shortDescription;

    private List<Feature> features;

    private Duration duration;

    private String schedule;

    @PostConstruct
    private void init() {
        try {
            String html = description.trim().replaceAll("\\n ", "").replaceAll("<[^>]*>", "");

            Pattern pattern = Pattern.compile("(.{0,400}[.,;\\s\\!\\?])");
            Matcher matcher = pattern.matcher(html);

            matcher.find();
            if (matcher.find()) {
                shortDescription = matcher.group(0);
            }
            features = initFeatures(page);
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public void initialize(Resource resource) {
        if (resource != null) {
            schedule = resource.getValueMap().get("plannedDepartureTime", String.class);
            duration = formatDuration(resource.getValueMap().get("duration", String.class));
        }
    }

    private Duration formatDuration(String durationMinutes) {
        Duration duration = null;
        if (durationMinutes != null && !durationMinutes.isEmpty()) {

            double t = Double.parseDouble(durationMinutes);
            double hours = t / 60;
            double minutes = t % 60;
            duration = new Duration();
            duration.setHours(hours);
            duration.setMinutes(minutes);
        }

        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCodeExcursion() {
        return codeExcursion;
    }

    public String getApiLongDescription() {
        return apiLongDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getPois() {
        return pois;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Page getPage() {
        return page;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getSchedule() {
        return schedule;
    }
}
