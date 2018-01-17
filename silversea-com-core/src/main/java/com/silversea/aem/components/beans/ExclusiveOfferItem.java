\package com.silversea.aem.components.beans;

import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.models.ExclusiveOfferModel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Class used to store informations about exclusive offer and the best
 * matching variation, and display informations according to it
 */
public class ExclusiveOfferItem {

    private ExclusiveOfferModel exclusiveOffer;

    private ExclusiveOfferModel exclusiveOfferVariation;

    private String destinationPath;

    public ExclusiveOfferItem(final ExclusiveOfferModel exclusiveOffer, final String countryCodeIso2, final String destinationPath) {
        this.exclusiveOffer = exclusiveOffer;

        if (this.exclusiveOffer.getVariations() != null) {
            for (final ExclusiveOfferModel variation : this.exclusiveOffer.getVariations()) {
            	if(variation.getTagIds() != null){
	                for (final String tagId : variation.getTagIds()) {
	                    // TODO add method to compare geolocation
	                    if (tagId.startsWith(WcmConstants.GEOLOCATION_TAGS_PREFIX) && tagId.endsWith("/" + countryCodeIso2)) {
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

        this.destinationPath = destinationPath;
    }

    public String getTitle() {
        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultIfBlank(exclusiveOfferVariation.getTitle(), exclusiveOffer.getTitle());
        }

        return exclusiveOffer.getTitle();
    }

    public String getDescription() {
        if (destinationPath != null && exclusiveOffer.getDestinationsTexts().containsKey(destinationPath)) {
            return exclusiveOffer.getDestinationsTexts().get(destinationPath);
        }

        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultIfBlank(exclusiveOfferVariation.getDescription(), exclusiveOffer.getDescription());
        }

        return exclusiveOffer.getDescription();
    }

    public String getLongDescription() {
        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultIfBlank(exclusiveOfferVariation.getLongDescription(), exclusiveOffer.getLongDescription());
        }

        return exclusiveOffer.getLongDescription();
    }

    public List<String> getCruiseFareAdditions() {
        if (exclusiveOfferVariation != null && exclusiveOfferVariation.getCruiseFareAdditions() != null && !exclusiveOfferVariation.getCruiseFareAdditions().isEmpty()) {
            return exclusiveOfferVariation.getCruiseFareAdditions();
        }

        return exclusiveOffer.getCruiseFareAdditions();
    }

    public List<String> getFootNotes() {
        if (exclusiveOfferVariation != null && exclusiveOfferVariation.getFootNotes() != null && !exclusiveOfferVariation.getFootNotes().isEmpty()) {
            return exclusiveOfferVariation.getFootNotes();
        }

        return exclusiveOffer.getFootNotes();
    }

    public String getMapOverHead() {
        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultString(exclusiveOfferVariation.getMapOverHead(), exclusiveOffer.getMapOverHead());
        }

        return exclusiveOffer.getMapOverHead();
    }

    public String getLightboxReference() {
        if (exclusiveOfferVariation != null) {
            return StringUtils.defaultString(exclusiveOfferVariation.getLightboxReference(), exclusiveOffer.getLightboxReference());
        }

        return exclusiveOffer.getLightboxReference();
    }

    public String getPricePrefix() {
        return exclusiveOffer.getPricePrefix();
    }

    public String getPath() {
        return exclusiveOffer.getPath();
    }
}