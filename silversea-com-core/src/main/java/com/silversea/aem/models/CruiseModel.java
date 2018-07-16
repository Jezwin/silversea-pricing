package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Model(adaptables = Page.class)
public class CruiseModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION)
    @Optional
    private String description;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/apiTitle")
    @Optional
    private String apiTitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/tourBook") @Optional
    private String tourBook;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/importedDescription") @Optional
    private String importedDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/startDate")
    @Optional
    private Calendar startDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/endDate")
    @Optional
    private Calendar endDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/duration")
    @Optional
    private String duration;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customDestination")
    @Optional
    private String customDestination;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    private String shipReference;

    private ShipModel ship;

    private CruiseDestinationModel destination;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cruiseCode")
    @Optional
    private String cruiseCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/itinerary")
    @Optional
    private String itinerary;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/keypeople")
    @Optional
    private String[] keyPeople;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/cruiseFareAdditions")
    @Optional
    private String cruiseFareAdditions;

    private List<String> splitCruiseFareAdditions = new ArrayList<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/itineraries")
    @Optional
    private List<ItineraryModel> itineraries;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/itineraries")
    @Optional
    private List<ItineraryModel> itinerariesStable;

    private List<ItineraryModel> compactedItineraries = null;

    private String cruiseType;

    private List<PriceModel> prices = new ArrayList<>();

    private String thumbnail;

    private List<FeatureModel> features = new ArrayList<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/offer")
    @Optional
    private String[] exclusiveOffersReferences;

    private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/isVisible")
    @Default(booleanValues = true)
    private boolean isVisible;

    private String path;

    private String lang;

    @PostConstruct
    private void init() {
        if (itineraries == null) {
            itineraries = new ArrayList<>();
        }

        final PageManager pageManager = page.getPageManager();
        final ResourceResolver resourceResolver = page.getContentResource().getResourceResolver();

        // init ship
        final Page shipPage = pageManager.getPage(shipReference);
        if (shipPage != null) {
            ship = shipPage.adaptTo(ShipModel.class);
        }

        // init destination
        destination = new CruiseDestinationModel(page.getParent().adaptTo(DestinationModel.class), customDestination);

        // init cruise fare additions
        if (cruiseFareAdditions != null) {
            final String[] split = cruiseFareAdditions.split("\\r?\\n");

            if (split.length > 0) {
                splitCruiseFareAdditions.addAll(Arrays.asList(split));
            }
        }

        // init cruise type and features
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (tagManager != null) {
            final Tag[] tags = tagManager.getTags(page.getContentResource());

            for (final Tag tag : tags) {
                if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_CRUISE_TYPES)) {
                    cruiseType = tag.getName();
                } else if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_FEATURES)) {
                    final FeatureModel featureModel = tag.adaptTo(FeatureModel.class);

                    if (featureModel != null) {
                        features.add(featureModel);
                    }
                }
            }

            if (cruiseType == null) {
                cruiseType = tagManager.resolve(WcmConstants.TAG_CRUISE_TYPE_CRUISE).getName();
            }
        }

        // init exclusive offers
        if (exclusiveOffersReferences != null) {
            for (final String exclusiveOfferReference : exclusiveOffersReferences) {
                final Page exclusiveOfferPage = pageManager.getPage(exclusiveOfferReference);

                if (exclusiveOfferPage != null) {
                    final ExclusiveOfferModel exclusiveOffer = exclusiveOfferPage.adaptTo(ExclusiveOfferModel.class);

                    if (exclusiveOffer != null) {
                        exclusiveOffers.add(exclusiveOffer);
                    }
                }
            }
        }

        // init prices
        final Resource suitesResource = page.getContentResource().getChild("suites");
        if (suitesResource != null) {
            CruiseUtils.collectPrices(prices, suitesResource);
            prices.sort((o1, o2) -> -o1.getPrice().compareTo(o2.getPrice()));
        }

        path = page.getPath();
        lang = LanguageHelper.getLanguage(page);

        final Resource imageResource = page.getContentResource().getChild("image");
        if (imageResource != null) {
            thumbnail = imageResource.getValueMap().get("fileReference", String.class);
        }
    }

    public String getTourBook() {
        return tourBook;
    }

    /**
     * @return cruise title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return cruise api title
     */
    public String getApiTitle() {
        return apiTitle;
    }

    /**
     * @return cruise description
     */
    public String getDescription() {
        return description != null ? description : importedDescription;
    }

    /**
     * @return cruise imported description
     */
    public String getImportedDescription() {
        return importedDescription;
    }

    /**
     * @return start date of the cruise
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * @return end date of  the cruise
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * @return duration of the cruise (in days)
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @return the cruise code
     */
    public String getCruiseCode() {
        return cruiseCode;
    }

    /**
     * @return DAM path of the cruise itinerary image
     */
    public String getItinerary() {
        return itinerary;
    }

    /**
     * @return asset selection reference attached to the cruise
     */
    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    /**
     * @return key people attached to the cruise
     */
    public String[] getKeyPeople() {
        return keyPeople;
    }

    /**
     * @return list of itinerary items
     */
    public List<ItineraryModel> getItineraries() {
        return itineraries;
    }

    public List<ItineraryModel> getItinerariesStable() {
        return itinerariesStable;
    }

    /**
     * TODO compacted itineraries cause inconsistencies in itineraries
     *
     * @return list of compacted itineraries (consecutive days are grouped)
     */
    public List<ItineraryModel> getCompactedItineraries() {
        if (compactedItineraries != null) {
            return compactedItineraries;
        }

        compactedItineraries = new ArrayList<>();

        ItineraryModel lastItinerary = null;
        for (ItineraryModel itineraryModel : itineraries) {
            if (lastItinerary != null && lastItinerary.getPort() != null
                    && itineraryModel != null && itineraryModel.getPort() != null
                    && lastItinerary.getPort().getCityId().equals(itineraryModel.getPort().getCityId())) {
                lastItinerary.setDepartDate(itineraryModel.getDate());
                lastItinerary.setDepartTime(itineraryModel.getDepartTime());

                lastItinerary.getCompactedExcursions().addAll(itineraryModel.getCompactedExcursions());

                lastItinerary.addHotels(itineraryModel.getHotels());
                lastItinerary.addLandPrograms(itineraryModel.getLandPrograms());
            } else {
                // trick to deep clone the itinerary item
                // without implementing java clone method
                lastItinerary = itineraryModel.getResource().adaptTo(ItineraryModel.class);
                compactedItineraries.add(lastItinerary);
            }
        }

        return compactedItineraries;
    }

    /**
     * @return exclusive offers attached to the cruise
     */
    public List<ExclusiveOfferModel> getExclusiveOffers() {
        return exclusiveOffers;
    }

    /**
     * @return the cruise type (cruise or expedition)
     */
    public String getCruiseType() {
        return cruiseType;
    }

    /**
     * @return the ship hosting this cruise
     */
    public ShipModel getShip() {
        return ship;
    }

    /**
     * @return the cruise thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @return cruise page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @return destination page
     */
    public DestinationModel getDestination() {
        return destination;
    }

    /**
     * @return departure port name
     */
    public String getDeparturePortName() {
        if (itineraries.size() > 0) {
            final ItineraryModel itinerary = itineraries.get(0);

            if (itinerary.getPort() != null) {
                return itinerary.getPort().getApiTitle();
            }
        }

        return null;
    }

    /**
     * @return arrival port name
     */
    public String getArrivalPortName() {
        try {
            if (itineraries.size() > 0) {
                final ItineraryModel itinerary = itineraries.get(itineraries.size() - 1);

                if (itinerary.getPort() != null) {
                    return itinerary.getPort().getApiTitle();
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @return prices of each suite variation for this cruise
     */
    public List<PriceModel> getPrices() {
        return prices;
    }

    /**
     * @return features attached to this cruise
     */
    public List<FeatureModel> getFeatures() {
        return features;
    }

    /**
     * @return cruise fare additions
     */
    public List<String> getCruiseFareAdditions() {
        return splitCruiseFareAdditions;
    }

    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @return the path of the cruise page
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the lang of the cruise
     */
    public String getLang() {
        return lang;
    }


}