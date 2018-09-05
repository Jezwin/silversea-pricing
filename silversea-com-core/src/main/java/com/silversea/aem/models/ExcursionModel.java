package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aurelienolivier on 12/02/2017.
 */
@Model(adaptables = Page.class)
public class ExcursionModel {

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
    
    @Inject @Named(JcrConstants.JCR_CONTENT + "/note") @Optional
    private String note;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/pois") @Optional
    private String pois;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/shorexId") @Optional
    private Long shorexId;
    
    private String shortDescription;

    private List<FeatureModel> features = new ArrayList<>();

    private String schedule;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String  assetSelectionReference;

    @PostConstruct
    private void init() throws Exception {
        String html = description.trim().replaceAll("\\n ", "").replaceAll("<[^>]*>", "");

        Pattern pattern = Pattern.compile("(.{0,400}[.,;\\s\\!\\?])");
        Matcher matcher = pattern.matcher(html);

        matcher.find();
        if (matcher.find()) {
            shortDescription = matcher.group(0);
        }

        // TODO merge with cruise model code
        // init cruise type and features
        final TagManager tagManager = page.getContentResource().getResourceResolver().adaptTo(TagManager.class);
        if (tagManager != null) {
            final Tag[] tags = tagManager.getTags(page.getContentResource());

            for (final Tag tag : tags) {
                if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_FEATURES)) {
                    final FeatureModel featureModel = tag.adaptTo(FeatureModel.class);

                    if (featureModel != null) {
                        features.add(featureModel);
                    }
                }
            }
        }
    }

    @Deprecated
    public void initialize(Resource resource) {
        if (resource != null) {
            schedule = resource.getValueMap().get("plannedDepartureTime", String.class);
        }
    }

    @Deprecated
    private Duration formatDuration(String durationMinutes) {
        Duration duration = null;

        if (durationMinutes != null && !durationMinutes.isEmpty()) {
            double t = Double.parseDouble(durationMinutes);
            double hours = t / 60;
            double minutes = t % 60;
            duration = new Duration();
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

    public String getCode() {
        return getCodeExcursion();
    }

    public String getApiLongDescription() {
        return apiLongDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }
    
    public String getNote() {
        return note;
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

    public List<FeatureModel> getFeatures() {
        return features;
    }

    public String getSchedule() {
        return schedule;
    }

    public Long getShorexId() {
        return shorexId;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    /**
     * TODO replace by {@link java.time.Duration}
     */
    @Deprecated
    public static class Duration {

        double hours;

        double minutes;

        public Duration() {

        }

        public double getHours() {
            return hours;
        }

        public void setHours(double hours) {
            this.hours = hours;
        }

        public double getMinutes() {
            return minutes;
        }

        public void setMinutes(double minutes) {
            this.minutes = minutes;
        }
    }
}
