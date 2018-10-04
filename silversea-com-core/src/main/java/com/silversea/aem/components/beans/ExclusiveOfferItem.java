package com.silversea.aem.components.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ExclusiveOfferModel;

/**
 * Class used to store informations about exclusive offer and the best
 * matching variation, and display informations according to it
 */
public class ExclusiveOfferItem {

    private ExclusiveOfferModel exclusiveOffer;

    private ExclusiveOfferModel exclusiveOfferVariation;

    private String title;

    private String description;

    private String shortDescription;

    private String mapOverhead;

    private String eoFootnotes;

    private String exclusiveOfferPath;

    private List<String> cruiseFareAdditions;

    private List<String> footnotes;

    private String voyageIcon;

    private Boolean newSystem;

    private String destinationPath;

    private String imageLBPath;

    private String footnote;

    private Boolean isLBGreyBoxActivated;

    private String positionDescriptionDesktopLB;

    private String positionDescriptionMobileLB;

    private Integer priorityWeight;

    public ExclusiveOfferItem(final ExclusiveOfferModel exclusiveOffer, final String countryCodeIso2,
                              final String destinationPath, EoBean result) {

        this.exclusiveOffer = exclusiveOffer;
        newSystem = false;
        if (result == null) { //Rely on the old system
            if (this.exclusiveOffer.getVariations() != null) {
                for (final ExclusiveOfferModel variation : this.exclusiveOffer.getVariations()) {
                    if (variation.getTagIds() != null) {
                        for (final String tagId : variation.getTagIds()) {
                            // TODO add method to compare geolocation
                            if (tagId.startsWith(WcmConstants.GEOLOCATION_TAGS_PREFIX) &&
                                    tagId.endsWith("/" + countryCodeIso2)) {
                                exclusiveOfferVariation = variation;
                                break;
                            }
                        }
                    }
                    if (exclusiveOfferVariation != null) {
                        break;
                    }
                }
            }
        } else { //Rely on the new EO System
            newSystem = true;

            title = result.getTitle();
            description = result.getDescription();
            shortDescription = result.getShortDescription();
            mapOverhead = result.getMapOverhead();
            footnotes = new ArrayList<String>();
            cruiseFareAdditions = new ArrayList<String>();
            exclusiveOfferPath = exclusiveOffer.getPath();
            imageLBPath = exclusiveOffer.getPathImageLB();
            isLBGreyBoxActivated = exclusiveOffer.getActiveGreysBoxes();
            eoFootnotes = result.getEoFootnotes();
            positionDescriptionDesktopLB = exclusiveOffer.getPositionDescriptionDesktopLB();
            positionDescriptionMobileLB = exclusiveOffer.getPositionDescriptionMobileLB();
            voyageIcon = result.getIcon();
            priorityWeight = result.getPriorityWeight();
            footnote =  result.getFootnote();
            if (StringUtils.isNotEmpty(result.getFootnote())) {
                footnotes.add(result.getFootnote());
            }
            if (result.getCruiseFares() != null && result.getCruiseFares().length > 0) {
                for (int i = 0; i < result.getCruiseFares().length; i++) {
                    if (StringUtils.isNotEmpty(result.getCruiseFares()[i].additionalFare)) {
                        cruiseFareAdditions.add(result.getCruiseFares()[i].additionalFare);
                    }
                    if (StringUtils.isNotEmpty(result.getCruiseFares()[i].footnote)) {
                        footnotes.add(result.getCruiseFares()[i].footnote);
                    }
                }
            }

        }

        this.destinationPath = destinationPath;
    }

    public Boolean getNewSystem() {
        return newSystem;
    }

    public String getTitle() {
        if (!newSystem) {
            if (exclusiveOfferVariation != null) {
                return StringUtils.defaultIfBlank(exclusiveOfferVariation.getTitle(), exclusiveOffer.getTitle());
            }

            return exclusiveOffer.getTitle();
        } else {
            return title;
        }
    }

    public String getDescription() {
        if (!newSystem) {
            if (destinationPath != null && exclusiveOffer.getDestinationsTexts().containsKey(destinationPath)) {
                return exclusiveOffer.getDestinationsTexts().get(destinationPath);
            }

            if (exclusiveOfferVariation != null) {
                return StringUtils
                        .defaultIfBlank(exclusiveOfferVariation.getDescription(), exclusiveOffer.getDescription());
            }

            return exclusiveOffer.getDescription();
        } else {
            return shortDescription;
        }
    }

    public String getLongDescription() {
        if (!newSystem) {
            if (exclusiveOfferVariation != null) {
                return StringUtils.defaultIfBlank(exclusiveOfferVariation.getLongDescription(),
                        exclusiveOffer.getLongDescription());
            }

            return exclusiveOffer.getLongDescription();
        } else {
            return description;
        }
    }

    public List<String> getCruiseFareAdditions() {
        if (!newSystem) {
            if (exclusiveOfferVariation != null && exclusiveOfferVariation.getCruiseFareAdditions() != null &&
                    !exclusiveOfferVariation.getCruiseFareAdditions().isEmpty()) {
                return exclusiveOfferVariation.getCruiseFareAdditions();
            }

            return exclusiveOffer.getCruiseFareAdditions();
        } else {
            return cruiseFareAdditions;
        }
    }

    public List<String> getFootNotes() {
        if (!newSystem) {
            if (exclusiveOfferVariation != null && exclusiveOfferVariation.getFootNotes() != null &&
                    !exclusiveOfferVariation.getFootNotes().isEmpty()) {
                return exclusiveOfferVariation.getFootNotes();
            }

            return exclusiveOffer.getFootNotes();
        } else {
            return footnotes;
        }
    }

    public String getMapOverHead() {
        if (!newSystem) {
            if (exclusiveOfferVariation != null) {
                return StringUtils
                        .defaultString(exclusiveOfferVariation.getMapOverHead(), exclusiveOffer.getMapOverHead());
            }

            return exclusiveOffer.getMapOverHead();
        } else {
            return mapOverhead;
        }
    }

    public String getLightboxReference() {
        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultString(exclusiveOfferVariation.getLightboxReference(),
                    exclusiveOffer.getLightboxReference());
        }

        return exclusiveOffer.getLightboxReference();
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }

    public String getExclusiveOfferPath() {
        return exclusiveOfferPath;
    }

    public String getImageLBPath() {
        return imageLBPath;
    }

    public Boolean getIsLBGreyBoxActivated() {
        return isLBGreyBoxActivated;
    }

    public String getPricePrefix() {
        return exclusiveOffer.getPricePrefix();
    }

    public String getPath() {
        return exclusiveOffer.getPath();
    }

    public String getEoFootnotes() {
        return eoFootnotes;
    }

    public String getPositionDescriptionDesktopLB() {
        return positionDescriptionDesktopLB;
    }

    public String getPositionDescriptionMobileLB() {
        return positionDescriptionMobileLB;
    }

    public String getVoyageIcon() {
        return voyageIcon;
    }

    public String getFootnote() {
        return footnote;
    }
}