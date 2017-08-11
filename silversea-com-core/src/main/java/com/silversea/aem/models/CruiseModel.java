package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.*;
import com.silversea.aem.constants.WcmConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;

@Model(adaptables = Page.class)
public class CruiseModel extends AbstractModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseModel.class);

    @Inject @Self
    private Page page;

    private PageManager pageManager;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiTitle") @Optional
    private String apititle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/importedDescription") @Optional
    private String importedDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/startDate") @Optional
    private Calendar startDate;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/endDate") @Optional
    private Calendar endDate;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/duration") @Optional
    private String duration;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    private String shipReference;

    private ShipModel ship;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cruiseCode") @Optional
    private String cruiseCode;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/itinerary") @Optional
    private String itinerary;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/keypeople") @Optional
    private String[] keypeople;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cruiseFareAdditions") @Optional
    private String cruiseFareAdditions;

    private String[] splitCruiseFareAdditions;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/itineraries")
    private List<ItineraryModel> itineraries;

    private List<ItineraryModel> compactedItineraries = null;

    private Tag cruiseType;

    private List<PriceModel> prices = new ArrayList<>();

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    private List<FeatureModel> features = new ArrayList<>();

    @PostConstruct
    private void init() {
        pageManager = page.getPageManager();

        final ResourceResolver resourceResolver = page.getContentResource().getResourceResolver();

        // init ship
        final Page shipPage = pageManager.getPage(shipReference);
        if (shipPage != null) {
            ship = shipPage.adaptTo(ShipModel.class);
        }

        // init cruise fare additions
        if (cruiseFareAdditions != null) {
            splitCruiseFareAdditions = cruiseFareAdditions.split("\\r?\\n");
        }

        // init cruise type and features
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (tagManager != null) {
            final Tag[] tags = tagManager.getTags(page.getContentResource());

            for (final Tag tag : tags) {
                if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_CRUISE_TYPES)) {
                    cruiseType = tag;
                } else if (tag.getTagID().startsWith(WcmConstants.TAG_NAMESPACE_FEATURES)) {
                    final FeatureModel featureModel = tag.adaptTo(FeatureModel.class);

                    if (featureModel != null) {
                        features.add(featureModel);
                    }
                }
            }

            if (cruiseType == null) {
                cruiseType = tagManager.resolve(WcmConstants.TAG_CRUISE_TYPE_CRUISE);
            }
        }

        // init prices
        final Resource suitesResource = page.getContentResource().getChild("suites");
        if (suitesResource != null) {
            collectPrices(prices, suitesResource);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImportedDescription() {
        return importedDescription;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getCruiseCode() {
        return cruiseCode;
    }

    public String getItinerary() {
        return itinerary;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public String[] getKeyPeople() {
        return keypeople;
    }

    public String[] getCruiseFareAdditions() {
        return splitCruiseFareAdditions;
    }

    public List<ItineraryModel> getItineraries() {
        return itineraries;
    }

    /**
     * TODO compacted itineraries cause inconsistencies in itineraries
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

    public List<ExclusiveOfferModel> getExclusiveOffers() {
        return exclusiveOffers;
    }

    public Tag getCruiseType() {
        return cruiseType;
    }

    public ShipModel getShip() {
        return ship;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Page getPage() {
        return page;
    }

    public Page getDestination() {
        return page.getParent();
    }

    public String getDeparturePortName() {
        if (itineraries.size() > 0) {
            ItineraryModel itinerary = itineraries.get(0);
            return itinerary.getPort().getApiTitle();
        }

        return null;
    }

    public String getArrivalPortName() {
        if (itineraries.size() > 0) {
            ItineraryModel itinerary = itineraries.get(itineraries.size() - 1);
            return itinerary.getPort().getApiTitle();
        }

        return null;
    }

    public List<PriceModel> getPrices() {
        return prices;
    }

    public List<FeatureModel> getFeatures() {
        return features;
    }

    /**
     * Recursive collection of prices for this cruise
     * @param prices the list of prices
     * @param resource resource from where to get the prices
     */
    private void collectPrices(final List<PriceModel> prices, final Resource resource) {
        if (resource.isResourceType("silversea/silversea-com/components/subpages/prices/pricevariation")) {
            prices.add(resource.adaptTo(PriceModel.class));
        } else {
            final Iterator<Resource> children = resource.listChildren();

            while (children.hasNext()) {
                collectPrices(prices, children.next());
            }
        }
    }

    // ---------------- TODO -------------- //


    private String destinationTitle;

    private String destinationFootNote;

    private String mapOverHead;

    private PriceData lowestPrice;

    private List<SuiteModel> suites;

    private List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<>();

    private List<CruiseFareAddition> exclusiveFareAdditions = new ArrayList<>();

    private ItinerariesData itinerariesData;

    private ResourceResolver resourceResolver;

    /**
     * TODO replace by sling model
     * TODO move display logic to use class
     * Do not display the venetian society feature on the overview tab
     * @return the features list
     */
    public List<Feature> getFeaturesForDisplay() {

        //do not display the venetian society feature on the overview tab
        /*final List<Feature> filteredFeatures = new ArrayList<>();
        final Iterator<Feature> i = features.iterator();
        while (i.hasNext()) {
            Feature feature = i.next();
            if (!feature.getTitle().toUpperCase().equals("VENETIAN SOCIETY")) {
                filteredFeatures.add(feature);
            }
        }
        return filteredFeatures;*/

        return null;
    }

    @Deprecated
    public String getDestinationTitle() {
        return getDestination().getTitle();
    }


    public void initByGeoLocation(GeoLocation geolocation) {
        exclusiveOffers = initExclusiveOffersByGeoLocation(geolocation.getGeoMarketCode(), geolocation.getCountry());
        splitCruiseFareAdditions = parseText(page, "cruiseFareAdditions");
        exclusiveFareAdditions = getAllExclusiveFareAdditions();
        lowestPrice = initLowestPrice(geolocation.getGeoMarketCode(), page);
        mapOverHead = initMapHover();
        suites = initSuites(page, geolocation.getGeoMarketCode(), pageManager);
    }

    private List<ExclusiveOfferModel> initExclusiveOffersByGeoLocation(String geoMarketCode, String country) {

        List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<ExclusiveOfferModel>();
        String[] exclusiveOfferUrls = page.getProperties().get("exclusiveOffers", String[].class);
        String destination = page.getParent().getPath();
        if (exclusiveOfferUrls != null) {
            Arrays.asList(exclusiveOfferUrls).forEach((item) -> {
                if (!StringUtils.isEmpty(item)) {
                    Page page = pageManager.getPage(item);
                    if (page != null) {
                        ExclusiveOfferModel exclusiveOfferModel = page.adaptTo(ExclusiveOfferModel.class);
                        if (exclusiveOfferModel.isValid(geoMarketCode)) {
                            exclusiveOfferModel.initDescription(country, destination);
                            exclusiveOffers.add(exclusiveOfferModel);
                        }
                    } else {
                        LOGGER.warn("Exclusive offer reference {} not found", item);
                    }
                }
            });
        }
        return exclusiveOffers;
    }

    private List<CruiseFareAddition> getAllExclusiveFareAdditions() {
        List<CruiseFareAddition> CruiseFareAddition = new ArrayList<CruiseFareAddition>();
        if (exclusiveOffers != null && !exclusiveOffers.isEmpty()) {
            exclusiveOffers.forEach(item -> {
                CruiseFareAddition.addAll(item.getCruiseFareAdditions());
            });
        }

        return CruiseFareAddition;
    }

    public String initMapHover() {
        String value = null;
        if (exclusiveOffers != null && !exclusiveOffers.isEmpty()) {

            java.util.Optional<String> optValue = exclusiveOffers.stream().filter(e -> !StringUtils.isEmpty(e.getMapOverHead()))
                    .map(ExclusiveOfferModel::getMapOverHead).findFirst();

            if (optValue != null && optValue.isPresent()) {
                value = optValue.get();
            }
        }
        return value;
    }

    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public List<CruiseFareAddition> getExclusiveFareAdditions() {
        return exclusiveFareAdditions;
    }

    public String getDestinationFootNote() {
        return destinationFootNote;
    }

    public List<SuiteModel> getSuites() {
        return suites;
    }

    public String getMapOverHead() {
        return mapOverHead;
    }

    public ItinerariesData getItinerariesData() {
        return itinerariesData;
    }

    public Integer getTotalFareAddictions() {
        if (getExclusiveFareAdditions() != null && getCruiseFareAdditions() != null) {
            return getExclusiveFareAdditions().size() + getCruiseFareAdditions().length;
        }

        return null;
    }
}