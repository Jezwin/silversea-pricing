package com.silversea.ssc.aem.components.editorial;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.ExclusiveOfferModel;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocalizedTextUse extends AbstractGeolocationAwareUse {

    static final private Logger LOGGER = LoggerFactory.getLogger(LocalizedTextUse.class);

   String CurrentMarket;
   boolean IsFT;
   boolean IsEU;
   boolean IsUK;
   boolean IsAS;

    @Override
    public void activate() throws Exception {
    	super.activate();
        CurrentMarket = geomarket;
        if(CurrentMarket.equals("ft")){
        	IsFT = true;
        }
        if(CurrentMarket.equals("eu")){
        	IsEU = true;
        }
        if(CurrentMarket.equals("uk")){
        	IsUK = true;
        }
        if(CurrentMarket.equals("as")){
        	IsAS = true;
        }
    }

    public String getCurrentMarket() {
        return CurrentMarket;
    }
    
    public boolean getIsFT() {
        return IsFT;
    }
    public boolean getIsEU() {
        return IsEU;
    }
    public boolean getIsUK() {
        return IsUK;
    }
    public boolean getIsAS() {
        return IsAS;
    }
  
}