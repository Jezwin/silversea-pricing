package com.silversea.aem.services.impl;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.silversea.aem.services.BestPriceOfferConfigurationService;


@Component(immediate = true, label = "Silversea - Best Price/AI Offer Configuration", metatype = true)
@Service(value = BestPriceOfferConfigurationService.class)
public class BestPriceOfferConfigurationServiceImpl implements BestPriceOfferConfigurationService{
	
	private static final String DEFAULT_AI_OFFER = "/content/silversea-com/en/exclusive-offers/best-price-avaialable-test1";
    private static final String DEFAULT_BP_OFFER = "/content/silversea-com/en/exclusive-offers/best-price-avaialable-test2";

    @Property(value = DEFAULT_AI_OFFER, label = "All Inclusive Offer Path", description = "Path to all inclusive offer page")
    private static final String AI_OFFER = "allInclusiveOffer";
    private String allInclusiveOffer;

    @Property(value = DEFAULT_BP_OFFER, label = "Best Price Offer Path", description = "Path to best price offer page")
    private static final String BP_OFFER = "bestPriceOffer";
    private String bestPriceOffer;
    
    @Activate
    @Modified
    protected void activate(final ComponentContext context) {
        Dictionary<?, ?> properties = context.getProperties();

        allInclusiveOffer = PropertiesUtil.toString(properties.get(AI_OFFER), DEFAULT_AI_OFFER);
        bestPriceOffer = PropertiesUtil.toString(properties.get(BP_OFFER), DEFAULT_BP_OFFER);
    }
    
    @Override
	public String getAllInclusiveOffer() {
		return allInclusiveOffer;
	}
    
    @Override
	public String getBestPriceOffer() {
		return bestPriceOffer;
	}
    
}
