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
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.TagManager.FindResults;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.CruiseFareAddition;
import com.silversea.aem.components.beans.Feature;
import com.silversea.aem.components.beans.Price;
import com.silversea.aem.enums.Currency;

/**
 * Created by mbennabi on 17/02/2017.
 */
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

    private String[] cruiseFareAdditions;

    private String destinationFootNote;

    private String cruiseType;

    private String shipName;

    private Price lowestPrice;

    private ResourceResolver resourceResolver;

    private TagManager tagManager;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        tagManager = resourceResolver.adaptTo(TagManager.class);
        String geoMarketCode = getGeoMarketCode("FR");
        exclusiveOffers = initExclusiveOffersByGeoLocation(geoMarketCode,"FR");
        features = initFeatures();
        cruiseType = initCruiseType();
        shipName = getPagereferenceTitle(shipReference);
        destinationTitle = getPagereferenceTitle(page.getParent().getPath());
        cruiseFareAdditions = parseCruiseFareAdditions();
        lowestPrice = initLowestPrice("EU");
        exclusiveFareAdditions = getAllExclusiveFareAdditions();
        destinationFootNote = page.getParent().getProperties().get("footnote", String.class);
        suites= initSuites(geoMarketCode);
    }

    private List<ExclusiveOfferModel> initExclusiveOffersByGeoLocation(String geoMarketCode,String country){

        List<ExclusiveOfferModel> exclusiveOffers = new ArrayList<ExclusiveOfferModel>();
        String[] exclusiveOfferUrls = page.getProperties().get("exclusiveOffers",String[].class);
        String destination = page.getParent().getPath();
        if(exclusiveOfferUrls != null){
            Arrays.asList(exclusiveOfferUrls).forEach((item)->{
                Page page = resourceResolver.resolve(item).adaptTo(Page.class);
                ExclusiveOfferModel exclusiveOfferModel = page.adaptTo(ExclusiveOfferModel.class);
                if(exclusiveOfferModel.isValid(geoMarketCode)){
                    exclusiveOfferModel.initDescription(country, destination);
                    exclusiveOffers.add(exclusiveOfferModel);
                }
            });
        }
        return exclusiveOffers;
    }

    //TODO: changes implementation
    private String getGeoMarketCode(String country){
        String geoMarketCode = null;

        FindResults findResults = tagManager.findByTitle(country);
        if(findResults != null && findResults.tags != null && findResults.tags.length > 0){
            geoMarketCode=  findResults.tags[0].getParent()
                    .getParent()
                    .getName();
        }
        //TODO: to delete
        geoMarketCode = "EU";
        return geoMarketCode;
    }

    private String[] parseCruiseFareAdditions(){
        String[] cruiseFareAdditions = null;
        String text = page.getProperties().get("cruiseFareAdditions", String.class);
        if(StringUtils.isNotEmpty(text)){
            cruiseFareAdditions = text.split("\\r?\\n");
        }
        return cruiseFareAdditions;
    }

    private List<Feature> initFeatures(){
        List<Feature> features = new ArrayList<Feature>();
        Tag[] tags = page.getTags();
        if(tags != null){
            for(Tag tag : tags){
                if(StringUtils.contains(tag.getTagID(),"features:")){
                    Resource resource = tag.adaptTo(Resource.class);
                    Feature feature = new Feature();
                    feature.setTitle(tag.getTitle());
                    feature.setIcon(resource.getValueMap().get("icon",String.class));
                    features.add(feature);
                }
            }
        }

        return features;
    }

    private String initCruiseType(){
        String cruiseType = null;
        Tag[] tags = page.getTags();
        if(tags != null){
            for(Tag tag : tags){
                if(StringUtils.contains(tag.getTagID(),"cruise-type:")){
                    cruiseType = tag.getName();
                    break;
                }
            }
        }
        return cruiseType;
    }

    private String getPagereferenceTitle(String pagePath){
        Page page = resourceResolver.resolve(pagePath).adaptTo(Page.class);
        String title = page.getProperties().get(JcrConstants.JCR_TITLE,String.class);

        return title;
    }

    private Price initLowestPrice(String geoMarketCode){
        Price lowestPrice = null;
        try {

            Node node = page.adaptTo(Node.class);
            Node lowestPricesNode = node.getNode("lowest-prices");
            NodeIterator iterator =  lowestPricesNode.getNodes();

            if(iterator != null){
                while(iterator.hasNext()){
                    Node next = iterator.nextNode();
                    if(StringUtils.contains(next.getName(),geoMarketCode)){
                        String priceValue = Objects.toString(next.getProperty("price").getValue());
                        lowestPrice = initPrice(geoMarketCode,priceValue);
                    }
                }
            }        

        } catch (RepositoryException e) {
            LOGGER.error("Exception while calculating cruise lowest price",e);
        }

        return lowestPrice;
    }

    private List<CruiseFareAddition> getAllExclusiveFareAdditions(){
        List<CruiseFareAddition> CruiseFareAddition = new ArrayList<CruiseFareAddition>();
        if(exclusiveOffers!=null && !exclusiveOffers.isEmpty()){
            exclusiveOffers.forEach(item ->{
                CruiseFareAddition.addAll(item.getCruiseFareAdditions());
            });
        }

        return CruiseFareAddition;
    }

    private List<SuiteModel> initSuites(String geoMarketCode){
        List<SuiteModel> suiteList = new ArrayList<SuiteModel>();
        Node cruiseNode = page.adaptTo(Node.class);
        Node suitesNode;
        try {
            suitesNode = cruiseNode.getNode("suites");

            NodeIterator suites = suitesNode.getNodes();
            if(suites !=null && suites.hasNext()){
                while (suites.hasNext()) {
                    Node node = suites.nextNode();
                    String path = Objects.toString(node.getProperty("suiteReference").getValue());
                    Page resource = resourceResolver.resolve(path).adaptTo(Page.class);
                    SuiteModel suiteModel = resource.adaptTo(SuiteModel.class);
                    Node lowestPriceNode = node.getNode("lowest-prices");
                    suiteModel.initLowestPrice(lowestPriceNode,geoMarketCode);
                    suiteModel.initVarirations(node,geoMarketCode);
                    suiteList.add(suiteModel);
                }
            }
        }catch (RepositoryException e) {
            LOGGER.error("Exception while building suites",e);
        }

        return suiteList;
    }

    //TODO: duplicated code
    Price initPrice(String geoMarketCode ,String value){      
        Price price = new Price();
        Currency currency = getCurrencyByMarKetCode(geoMarketCode);
        price.setCurrency(currency.getLabel());
        price.setValue(value);
        if(StringUtils.isNumeric(value)){
            price.setWaitList(false);
        }
        else{
            price.setWaitList(true);
        }
        return price;
    }

    //TODO: duplicated code
    private Currency getCurrencyByMarKetCode(String marKetCode){
        return Arrays.stream(Currency.values())
                .filter(e -> e.name().equals(marKetCode)).findFirst()
                .orElseThrow(() -> new IllegalStateException());
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

    public String getShipName() {
        return shipName;
    }

    public Price getLowestPrice() {
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
}