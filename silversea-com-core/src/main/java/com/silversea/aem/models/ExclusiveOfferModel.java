package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.CruiseFareAddition;
import com.silversea.aem.components.beans.Destination;
import com.silversea.aem.technical.json.JsonMapper;

@Model(adaptables = Page.class)
public class ExclusiveOfferModel {

    @Inject
    @Self
    private Page page;

    private String title;
    
    private String mapOverHead;
    
    private String description;
    
    private List<CruiseFareAddition> cruiseFareAdditions;
    
    @PostConstruct
    private void init() {       
        mapOverHead = page.getProperties().get("mapOverhead", String.class);
        title = page.getProperties().get(JcrConstants.JCR_TITLE, String.class);
        String[] fareAdditons = page.getProperties().get("cruiseFareAdditions", String[].class);
        cruiseFareAdditions = initFareAdditions(fareAdditons);
    }
    public boolean isValid(String geoMarketCode){
        return isTagExists(page,geoMarketCode);
    }

    public void initDescription(String country,String destination){
      
        if(StringUtils.isEmpty(getDestinationText(destination))){
            Page variation = getVariationByCountry(page,country);
            if(variation != null){
                InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(variation.getContentResource());
                title = properties.getInherited(JcrConstants.JCR_TITLE, String.class);
                mapOverHead = properties.getInherited("mapOverhead", String.class);
                description = properties.getInherited("longDescription", String.class);
                String[] fareAdditons = properties.getInherited("cruiseFareAdditions", String[].class);
                initFareAdditions(fareAdditons);
            }
            else{
                description = page.getProperties().get("longDescription",String.class);
            }
        }
        else{
            description = getDestinationText(destination);
        }
    }

    private String getDestinationText(String destinationReference){
        String description = null;
        String[] destinations = page.getProperties().get("destinations",String[].class);
        if(destinations != null){
            for(String item : destinations){  
                Destination destination = JsonMapper.getDomainObject(item, Destination.class);
                if(destination != null && 
                        StringUtils.equals(destination.getReference(), destinationReference)){
                    description = destination.getText();
                }
            }
        }
        return description;
    }
    public Page getVariationByCountry(Page page, String country){
        Iterator<Page> children = page.listChildren();
        if(children != null && children.hasNext() ){
            while(children.hasNext()){
                Page current = children.next();
                if(isTagExists(current,country)){
                    return current;
                }
            }
        }

        return null;
    }
    public boolean isTagExists(Page page,String value){
        boolean exist = false;
        Tag[] tags = page.getTags();
        if(tags != null){
            for(Tag tag:tags){
                if(StringUtils.equals(value, tag.getName().toUpperCase())){
                    exist = true; 
                }
            }
        }
        return exist;
    }
    
    private List<CruiseFareAddition> initFareAdditions(String[] fareAdditions){
        
        List<CruiseFareAddition> cruiseFareAdditions = new ArrayList<CruiseFareAddition>();
        if(fareAdditions != null){
            Arrays.asList(fareAdditions).forEach(item ->{     
                CruiseFareAddition cruiseFareAddition = JsonMapper.getDomainObject(item, CruiseFareAddition.class);
                cruiseFareAdditions.add(cruiseFareAddition);
            });
        }
        
        return cruiseFareAdditions; 
    }
    
    public String getTitle() {
        return title;
    }
    public String getMapOverHead() {
        return mapOverHead;
    }
    public String getDescription() {
        return description;
    }
    public List<CruiseFareAddition> getCruiseFareAdditions() {
        return cruiseFareAdditions;
    }
}
