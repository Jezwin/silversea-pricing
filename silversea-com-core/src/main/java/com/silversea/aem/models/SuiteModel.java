package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.SuiteVariation;

@Model(adaptables = Page.class)
public class SuiteModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SuiteModel.class);
    
    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/bedroomsInformation")
    @Optional
    private String bedroomsInformation;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/plan")
    @Optional
    private String plan;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/suiteFeature")
    @Optional
    private String[] features;

    private String lowestPrice;

    private String[] suiteSubTitle;

    private String longDescription;

    private List<SuiteVariation> variations;

    private ResourceResolver resourceResolver;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        String suiteReference = page.getProperties().get("suiteReference", String.class);
        suiteSubTitle = parseSuitesSubtitle();
        longDescription = initLongDescription(suiteReference);
    }

    public void initLowestPrice(Node lowestPriceNode,String geoMarketCode){
        lowestPrice = getPriceByGeoMarketCode(lowestPriceNode,geoMarketCode,"priceMarketCode");
    }

    private String[] parseSuitesSubtitle(){
        String[] subTitle = null;
        String text = page.getProperties().get("suiteSubTitle", String.class);
        if(StringUtils.isNotEmpty(text)){
            subTitle = text.split("\\r?\\n");
        }
        return subTitle;
    }

    private String initLongDescription(String suiteReference){

        String longDescription = page.getProperties().get("longDescription", String.class);
        if(!StringUtils.isNotEmpty(longDescription)){
            Page suiteReferencePage = resourceResolver.resolve(suiteReference).adaptTo(Page.class);
            if(suiteReferencePage!=null){
                longDescription = suiteReferencePage.getProperties().get("longDescription", String.class);
            }
                
        }       
        return longDescription;
    }

    public void initVarirations(Node suiteNode,String geoMarketCode){
        variations = new ArrayList<SuiteVariation>();
        try {
            NodeIterator variationNodes = suiteNode.getNodes();
            if(variationNodes!=null && variationNodes.hasNext()){
                while(variationNodes.hasNext()){
                    Node node = variationNodes.nextNode();
                    if(!StringUtils.equals(node.getName(), "lowest-price")){
                        String price = getPriceByGeoMarketCode(node,geoMarketCode,"");
                        SuiteVariation suiteVariation = new SuiteVariation();
                        suiteVariation.setPrice(price);
                        //TODO: set name
                        suiteVariation.setPrice("variation name");
                        variations.add(suiteVariation);
                    }

                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception while building suites variations",e);
        }
    }

    private String getPriceByGeoMarketCode(Node pricesNode,String geoMarketCode,String property){
        String price = null;
        try {
            NodeIterator nodes = pricesNode.getNodes();
            if(nodes != null && nodes.hasNext()){
                while(nodes.hasNext()){
                    Node node = nodes.nextNode();
                    String priceMarketCode = Objects.toString(node.getProperty(property).getValue());

                    if(StringUtils.equals(geoMarketCode, priceMarketCode)){
                        price = Objects.toString(node.getProperty("price").getValue());
                        break;
                    }
                }
            }

        } catch (RepositoryException e) {
            LOGGER.error("Exception while calculating prices",e);
        }

        return price;
    }

    
    public String getTitle() {
        return title;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public String[] getSuiteSubTitle() {
        return suiteSubTitle;
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getFeatures() {
        return features;
    }

    public List<SuiteVariation> getVariations() {
        return variations;
    } 
    
    
}
