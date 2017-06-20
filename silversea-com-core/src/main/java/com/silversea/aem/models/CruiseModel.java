package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.CruiseFareAddition;
import com.silversea.aem.components.beans.Feature;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.components.beans.ItinerariesData;
import com.silversea.aem.components.beans.PriceData;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = { Page.class })
public class CruiseModel extends AbstractModel {

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
    private String apititle;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/importedDescription")
    @Optional
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
    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    @Optional
    private String shipReference;

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
    private String[] keypeople;

    private String destinationTitle;

    private List<Feature> features;

    private List<SuiteModel> suites;

    private List<ExclusiveOfferModel> exclusiveOffers;

    private List<CruiseFareAddition> exclusiveFareAdditions;

    private List<ItineraryModel> itineraries;

    private ItinerariesData itinerariesData;

    private ShipModel ship;

    private String[] cruiseFareAdditions;

    private String destinationFootNote;

    private String cruiseType;

    private String mapOverHead;

    private PriceData lowestPrice;

    private ResourceResolver resourceResolver;

    private boolean hasLandPrograms = false;
    
    private PageManager pageManager;

    @PostConstruct
    private void init() {
        try {
            resourceResolver = page.getContentResource().getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            features = initFeatures(page);
            cruiseType = initCruiseType();
            destinationTitle = getPagereferenceTitle(page.getParent().getPath());
            destinationFootNote = page.getParent().getProperties().get("footnote", String.class);
            itineraries = initIteniraries();
            ship = initShip(shipReference,pageManager);
            itinerariesData = initItinerariesData();
            hasLandPrograms = hasLandProgram();
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public void initByGeoLocation(GeoLocation geolocation) {
        exclusiveOffers = initExclusiveOffersByGeoLocation(geolocation.getGeoMarketCode(), geolocation.getCountry());
        cruiseFareAdditions = parseText(page, "cruiseFareAdditions");
        exclusiveFareAdditions = getAllExclusiveFareAdditions();
        lowestPrice = initLowestPrice(geolocation.getGeoMarketCode(),page);
        mapOverHead = initMapHover();
        suites = initSuites(page,geolocation.getGeoMarketCode(),pageManager);
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

    private List<ItineraryModel> initIteniraries() {
        List<ItineraryModel> iteniraries = new ArrayList<ItineraryModel>();
        try {

            Node cruiseNode = page.adaptTo(Node.class);
            Node itinerariesNode = cruiseNode.getNode("itineraries");
            NodeIterator itinerariesNodes = itinerariesNode.getNodes();
            if (itinerariesNodes != null && itinerariesNodes.hasNext()) {
                while (itinerariesNodes.hasNext()) {
                    Node node = itinerariesNodes.nextNode();
                    String path = Objects.toString(node.getProperty("portReference").getValue());
                    if (!StringUtils.isEmpty(path)) {
                        Page pageReference = pageManager.getPage(path);
                        if (pageReference != null) {
                            ItineraryModel itineraryModel = pageReference.adaptTo(ItineraryModel.class);
                            itineraryModel.init(node);
                            iteniraries.add(itineraryModel);
                        } else {
                            LOGGER.warn("Port reference {} not found", path);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception while initilizing itineraries", e);
        }

        return iteniraries;
    }

    private String initCruiseType() {
        String cruiseType = null;
        Tag[] tags = page.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                if (StringUtils.contains(tag.getTagID(), "cruise-type:")) {
                    cruiseType = tag.getName();
                    break;
                }
            }
        }
        return cruiseType;
    }

    private String getPagereferenceTitle(String pagePath) {
        String title = null;
        Resource resource = resourceResolver.resolve(pagePath);
        if (resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)) {
            Page pa = resource.adaptTo(Page.class);
            title = pa.getProperties().get(JcrConstants.JCR_TITLE, String.class);
        } else {
            LOGGER.warn("Page reference {} not found", pagePath);
        }

        return title;
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
            value = exclusiveOffers.stream().filter(e -> !StringUtils.isEmpty(e.getMapOverHead()))
                    .map(ExclusiveOfferModel::getMapOverHead).findFirst()
                    .orElseThrow(() -> new IllegalStateException());
        }
        return value;
    }

    public ItinerariesData initItinerariesData() {

        int nbHotels = 0;
        int nbExcursions = 0;
        int nbLandPrograms = 0;

        for (ItineraryModel itinerary : itineraries) {
            if (itinerary != null 
                    && itinerary.getHotels() != null 
                    && itinerary.getExcursions() != null
                    && itinerary.getLandprograms() != null) {
                
                nbHotels += itinerary.getHotels().size();
                nbExcursions += itinerary.getExcursions().size();
                nbLandPrograms += itinerary.getLandprograms().size();
            } 
        }

        return new ItinerariesData(nbHotels, nbExcursions, nbLandPrograms);
    }
    
    private boolean hasLandProgram(){
        boolean hasLandProgram = false; 
        if(itineraries != null && !itineraries.isEmpty()){
            hasLandProgram = itineraries.stream().filter(e -> !e.getLandprograms().isEmpty()).findFirst().isPresent();
        }
        return hasLandProgram;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getApititle() {
        return apititle;
    }

    public String getImporteddescription() {
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

    public String getShipReference() {
        return shipReference;
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

    public String[] getKeypeople() {
        return keypeople;
    }

    public String[] getCruiseFareAdditions() {
        return cruiseFareAdditions;
    }

    public String getDestinationTitle() {
        return destinationTitle;
    }

    public List<ExclusiveOfferModel> getExclusiveOffers() {
        return exclusiveOffers;
    }

    public String getCruiseType() {
        return cruiseType;
    }

    public List<Feature> getFeatures() {
        return features;
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

    public List<ItineraryModel> getItineraries() {
        return itineraries;
    }

    public ShipModel getShip() {
        return ship;
    }

    public boolean hasLandPrograms() {
        return hasLandPrograms;
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