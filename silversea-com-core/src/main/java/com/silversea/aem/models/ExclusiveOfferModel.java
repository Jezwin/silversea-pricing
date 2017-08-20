package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
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

@Model(adaptables = Page.class)
public class ExclusiveOfferModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cq:tags") @Optional
    private String[] tagIds;

    private List<String> geomarkets = new ArrayList<>();

    @Inject @Named(JcrConstants.JCR_CONTENT + "/jcr:title")
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/jcr:description") @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cruiseFareAdditions") @Optional
    private String[] cruiseFareAdditionsJson;

    private List<String> cruiseFareAdditions = new ArrayList<>();

    private List<String> footNotes = new ArrayList<>();

    @Inject @Named(JcrConstants.JCR_CONTENT + "/mapOverhead") @Optional
    private String mapOverHead;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/lightboxReference") @Optional
    private String lightboxReference;

    private String path;

    @PostConstruct
    private void init() {
        // init geotagging
        if (tagIds != null) {
            for (String tagId : tagIds) {
                if (tagId.startsWith("geotagging:")) {
                    geomarkets.add(tagId.replace("geotagging:", ""));
                }
            }
        }

        // init cruise fare additions and footnotes
        if (cruiseFareAdditionsJson != null) {
            for (final String cruiseFareAdditionJson : cruiseFareAdditionsJson) {
                try {
                    final JSONObject jsonObject = new JSONObject(cruiseFareAdditionJson);

                    final String addition = jsonObject.optString("addition");
                    if (StringUtils.isNotEmpty(addition)) {
                        cruiseFareAdditions.add(addition);
                    }

                    final String footNote = jsonObject.optString("note");
                    if (StringUtils.isNotEmpty(footNote)) {
                        footNotes.add(footNote);
                    }
                } catch (JSONException ignored) {
                }
            }
        }

        path = page.getPath();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public List<String> getGeomarkets() {
        return geomarkets;
    }

    public List<String> getCruiseFareAdditions() {
        return cruiseFareAdditions;
    }

    public List<String> getFootNotes() {
        return footNotes;
    }

    public String getMapOverHead() {
        return mapOverHead;
    }

    public String getLightboxReference() {
        return lightboxReference;
    }

    public String getPath() {
        return path;
    }
}
