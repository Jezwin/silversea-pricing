package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ItineraryHotelModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int hotelId;

    @Inject
    private String hotelReference;

    // TODO create custom injector
    private HotelModel hotel;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            final Page hotelPage = pageManager.getPage(hotelReference);
            hotel = hotelPage.adaptTo(HotelModel.class);
        }
    }

    public String getName() {
        return hotel.getName();
    }

    public String getDescription() {
        return hotel.getDescription();
    }

    public String getShortDescription() {
        return hotel.getShortDescription();
    }

    public String getCode() {
        return hotel.getCode();
    }
}
