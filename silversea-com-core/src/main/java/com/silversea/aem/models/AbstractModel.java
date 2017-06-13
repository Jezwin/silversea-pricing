package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.Feature;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.enums.Currency;

public abstract class AbstractModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(AbstractModel.class);

    protected Page getPageReference(Page page, String reference, ResourceResolver resourceResolver) {
        Page pageReference = null;
        String path = page.getProperties().get(reference, String.class);
        Resource resource = resourceResolver.resolve(path);
        if(resource!= null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)  ){
            pageReference = resource.adaptTo(Page.class);
        }
        else{
            LOGGER.warn("Page reference {} not found",path);
        } 

        return pageReference;
    }

    protected String initPropertyWithFallBack(Page page, String reference, String property, String referenceProperty,ResourceResolver resourceResolver) {
        String value = property;
        if (StringUtils.isEmpty(property)) {
            Page pageRef = getPageReference(page,reference,resourceResolver);
            if (pageRef != null) {
                value = pageRef.getProperties().get(referenceProperty, String.class);
            }

        }
        return value;
    }

    protected List<Feature> initFeatures(Page page) {
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

    protected String[] parseText(Page page, String property) {
        String[] parsedText = null;
        String text = page.getProperties().get(property, String.class);
        if (StringUtils.isNotEmpty(text)) {
            parsedText = text.split("\\r?\\n");
        }
        return parsedText;
    }

    protected PriceData initPrice(String geoMarketCode, String value) {
        PriceData price = new PriceData();
        Currency currency = getCurrencyByMarKetCode(geoMarketCode);
        price.setCurrency(currency.getLabel());
        price.setValue(value);
        if (StringUtils.isNumeric(value)) {
            price.setWaitList(false);
        } else {
            price.setWaitList(true);
        }
        return price;
    }

    protected PriceData initLowestPrice(String geoMarketCode,Page page) {
        PriceData lowestPrice = null;
        try {
            Node node = page.adaptTo(Node.class);
            Node lowestPricesNode = node.getNode("lowest-prices");
            NodeIterator iterator = lowestPricesNode.getNodes();

            if (iterator != null) {
                while (iterator.hasNext()) {
                    Node next = iterator.nextNode();
                    if (StringUtils.contains(next.getName(), geoMarketCode)) {
                        String priceValue = Objects.toString(next.getProperty("price").getValue());
                        lowestPrice = initPrice(geoMarketCode, priceValue);
                    }
                }
            }

        } catch (RepositoryException e) {
            LOGGER.error("Exception while calculating cruise lowest price", e);
        }

        return lowestPrice;
    }

    protected Currency getCurrencyByMarKetCode(String marKetCode) {
        return Arrays.stream(Currency.values()).filter(e -> e.name().equals(marKetCode)).findFirst()
                .orElseThrow(() -> new IllegalStateException());
    }
    
    protected Page getPage(String path,PageManager pageManager){
        Page page = null;
        if(!StringUtils.isEmpty(path)){
            page = pageManager.getPage(path);
        }
        return page;
    }
}
