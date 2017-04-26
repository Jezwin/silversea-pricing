package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.CruiseFareAddition;
import com.silversea.aem.technical.json.JsonMapper;

@Model(adaptables = Page.class)
public class ExclusiveOfferModel {

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;
    
    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/mapOverhead")
    @Optional
    private String mapOverHead;
    
    private String description;
    
    private List<CruiseFareAddition> cruiseFareAdditions;

    @PostConstruct
    private void init() {
        cruiseFareAdditions = initFareAdditions();
    }
    public boolean isValid(String geoMarketCode){
        return isTagExists(page,geoMarketCode);
    }

    public void initDescription(String country,String destination){
        //TODO
        /*Page variation = getVariation(page,country);
        //If country exits
        if(variation != null){

        }
        else{

        }*/
        
        description = "Exlusive offer description";

    }

    public Page getVariation(Page page, String country){
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
                if(StringUtils.equals(value, tag.getName())){
                    exist = true; 
                }
            }
        }
        return exist;
    }
    
    private List<CruiseFareAddition> initFareAdditions(){
        
        List<CruiseFareAddition> cruiseFareAdditions = new ArrayList<CruiseFareAddition>();
        String[] fares = page.getProperties().get("cruiseFareAdditions", String[].class);
        if(fares != null){
            Arrays.asList(fares).forEach(item ->{     
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
