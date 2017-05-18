package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Duration;
import com.silversea.aem.components.beans.Feature;

/**
 * Created by aurelienolivier on 12/02/2017.
 */
@Model(adaptables = Page.class)
public class ExcursionModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExcursionModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION)
    @Optional
    private String description;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/codeExcursion")
    @Optional
    private String codeExcursion;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/apiLongDescription")
    @Optional
    private String apiLongDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/pois")
    @Optional
    private String pois;

    private String shortDescription;

    private List<Feature> features;

    private Duration duration;

    private String schedule;

    @PostConstruct
    private void init() {

        String html = description.trim().replaceAll("\\n ", "").replaceAll("<[^>]*>", "");

        Pattern pattern = Pattern.compile("(.{0,400}[.,;\\s\\!\\?])");
        Matcher matcher = pattern.matcher(html);

        matcher.find();
        if (matcher.find()) {
            shortDescription = matcher.group(0);
        }
        features = initFeatures();
    }

    public void initialize(Node node) {
        if (node != null) {
            try {
                schedule = Objects.toString(node.getProperty("plannedDepartureTime").getValue());
                duration = formatDuration(Objects.toString(node.getProperty("duration").getValue()));
            } catch (RepositoryException e) {
                LOGGER.error("Exception while initializing properties", e);
            }
        }
    }
    
    private Duration formatDuration(String durationMinutes){
        
        Duration duration = null;
        if(durationMinutes != null && !durationMinutes.isEmpty()){
            int t = Integer.parseInt(durationMinutes);
            int hours = t / 60;
            int minutes = t % 60;
            duration = new Duration();
            duration.setHours(hours);
            duration.setMinutes(minutes);
        }
        
        return duration;   
    }

    private List<Feature> initFeatures() {
        List<Feature> features = new ArrayList<Feature>();
        Tag[] tags = page.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                if (StringUtils.contains(tag.getTagID(), "features:")) {
                    Resource resource = tag.adaptTo(Resource.class);
                    Feature feature = new Feature();
                    feature.setTitle(tag.getTitle());
                    feature.setIcon(resource.getValueMap().get("icon", String.class));
                    features.add(feature);
                }
            }
        }

        return features;
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

    //
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
