package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;

@Model(adaptables = Page.class)
public class ExclusiveOfferModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOfferModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/jcr:title")
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/pageTitle")
    @Optional
    private String pageTitle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/jcr:description")
    @Optional
    private String description;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cq:tags")
    @Optional
    private String[] tagIds;

    private List<String> geomarkets = new ArrayList<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cruiseFareAdditions")
    @Optional
    private String[] cruiseFareAdditionsJson;

    private List<String> cruiseFareAdditions = new ArrayList<>();

    private List<String> footNotes = new ArrayList<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/destinations")
    @Optional
    private String[] destinationsTextsJson;

    private Map<String, String> destinationsTexts = new HashMap<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/mapOverhead")
    @Optional
    private String mapOverHead;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/lightboxReference")
    @Optional
    private String lightboxReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/pricePrefix")
    @Optional
    private String pricePrefix;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/activeSystem")
    @Optional
    private String activeSystem;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/ghostOffer")
    @Optional
    private String ghostOffer;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultTitle")
    @Optional
    private String defaultTitle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultShortTitle")
    @Optional
    private String defaultShortTitle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultDescription")
    @Optional
    private String defaultDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultDescriptionTnC")
    @Optional
    private String defaultDescriptionTnC;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultShortDescription")
    @Optional
    private String defaultShortDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultMapOverhead")
    @Optional
    private String defaultMapOverhead;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultFootnote")
    @Optional
    private String defaultFootnote;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultEoFootnotes")
    @Optional
    private String defaultEoFootnotes;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/defaultVoyageIcon")
    @Optional
    private String defaultVoyageIcon;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/priorityWeight")
    @Optional
    private Integer priorityWeight;


    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customMainSettings")
    @Optional
    private String[] customMainSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/expirationDate")
    @Optional
    private Date expirationDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customTokenSettings")
    @Optional
    private String[] customTokenSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customTokenValuesSettings")
    @Optional
    private String[] customTokenValuesSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/pathImageLB")
    @Optional
    private String pathImageLB;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/positionDescriptionDesktopLB")
    @Optional
    private String positionDescriptionDesktopLB;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/positionDescriptionMobileLB")
    @Optional
    private String positionDescriptionMobileLB;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/activeGreysBoxes")
    @Optional
    private String activeGreysBoxes;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customLBSettings")
    @Optional
    private String[] customLBSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customVoyageSettings")
    @Optional
    private String[] customVoyageSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customVoyagesFaresSettings")
    @Optional
    private String[] customVoyageFaresSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customTnCSettings")
    @Optional
    private String[] customTnCSettings;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/categoryEO")
    @Optional
    private String[] categoryEO;

    private String path;

    private List<ExclusiveOfferModel> variations = new ArrayList<>();

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

                    final String footNote = jsonObject.optString("notes");
                    if (StringUtils.isNotEmpty(footNote)) {
                        footNotes.add(footNote);
                    }
                } catch (JSONException ignored) {
                }
            }
        }

        // init destinations texts
        if (destinationsTextsJson != null) {
            for (final String destinationTextJson : destinationsTextsJson) {

                try {
                    final JSONObject jsonObject = new JSONObject(destinationTextJson);

                    final String reference = jsonObject.optString("reference");
                    final String text = jsonObject.optString("text");
                    if (StringUtils.isNotEmpty(reference) && StringUtils.isNotEmpty(text)) {
                        destinationsTexts.put(reference, text);
                    }
                } catch (JSONException ignored) {
                }
            }
        }

        // init variations
//        final Iterator<Page> children = page.listChildren();
//        while (children.hasNext()) {
//            final Page child = children.next();
//
//            if (child.getContentResource().isResourceType(WcmConstants.RT_EXCLUSIVE_OFFER_VARIATION)) {
//                final ExclusiveOfferModel variation = child.adaptTo(ExclusiveOfferModel.class);
//
//                if (variation != null) {
//                    variations.add(variation);
//                }
//            }
//        }

        path = page.getPath();
    }

    public String getTitle() {
        return pageTitle != null ? pageTitle : title;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String[] getTagIds() {
        return tagIds;
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

    public Map<String, String> getDestinationsTexts() {
        return destinationsTexts;
    }

    public String getMapOverHead() {
        return mapOverHead;
    }

    public String getLightboxReference() {
        return lightboxReference;
    }

    public String getPricePrefix() {
        return pricePrefix;
    }

    public List<ExclusiveOfferModel> getVariations() {
        return variations;
    }

    public String getPath() {
        return path;
    }

    public boolean getActiveSystem() {
        if (activeSystem != null) {
            return Boolean.valueOf(activeSystem);
        } else {
            return false;
        }
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public String getDefaultDescriptionTnC() {
        return defaultDescriptionTnC;
    }

    public String getDefaultShortDescription() {
        return defaultShortDescription;
    }

    public String getDefaultMapOverhead() {
        return defaultMapOverhead;
    }

    public String getDefaultFootnote() {
        return defaultFootnote;
    }

    public String getDefaultEoFootnotes() {
        return defaultEoFootnotes;
    }

    public String[] getCustomMainSettings() {
        return customMainSettings;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String[] getCustomTokenSettings() {
        return customTokenSettings;
    }

    public String[] getCustomTokenValuesSettings() {
        return customTokenValuesSettings;
    }

    public String getPathImageLB() {
        return pathImageLB;
    }

    public boolean getActiveGreysBoxes() {
        return Boolean.valueOf(activeGreysBoxes);
    }

    public String[] getCustomLBSettings() {
        return customLBSettings;
    }

    public String[] getCustomVoyageSettings() {
        return customVoyageSettings;
    }

    public String[] getCustomVoyageFaresSettings() {
        return customVoyageFaresSettings;
    }

    public String[] getCustomTnCSettings() {
        return customTnCSettings;
    }

    public String getDefaultShortTitle() {
        return defaultShortTitle;
    }

    public String[] getCategoryEO() {
        return categoryEO;
    }

    public String getPositionDescriptionDesktopLB() {
        return positionDescriptionDesktopLB;
    }

    public String getPositionDescriptionMobileLB() {
        return positionDescriptionMobileLB;
    }

    public String getDefaultVoyageIcon() {
        return defaultVoyageIcon;
    }

    public Integer getDefaultPriorityWeight() {
        return priorityWeight;
    }

    public boolean isGhostOffer() {
        return StringUtils.isNotEmpty(ghostOffer) ? Boolean.valueOf(ghostOffer) : false;
    }
}