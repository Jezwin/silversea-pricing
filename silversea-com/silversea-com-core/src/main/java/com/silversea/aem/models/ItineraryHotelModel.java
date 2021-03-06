package com.silversea.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ItineraryHotelModel {

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private int hotelId;

    @Inject
    @Optional
    private String hotelReference;

    // TODO create custom injector
    private HotelModel hotel;

    @PostConstruct
    private void init() {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null && StringUtils.isNotEmpty(hotelReference)) {
            final Page hotelPage = pageManager.getPage(hotelReference);

            if (hotelPage != null) {
                hotel = hotelPage.adaptTo(HotelModel.class);
            }
        }
    }

    public String getName() {
        return hotel != null ? hotel.getName() : null;
    }

    public String getCustomTitle() {
        return hotel != null ? hotel.getCustomTitle() : null;
    }
    public String getDescription() {
        return hotel != null ? hotel.getDescription() : null;
    }

    public String getShortDescription() {
        return hotel != null ? hotel.getShortDescription() : null;
    }

    public String getCode() {
        return hotel != null ? hotel.getCode() : null;
    }

    public String getAssetSelectionReference() {
        return hotel != null ? CruiseUtils.firstNonNull(hotel.getAssetSelectionReference(),
                hotel.getAssetSelectionReferenceApi()) : null;
    }

    public Long getHotelId() {
        return hotel != null ? hotel.getHotelId() : null;
    }

    public HotelModel getHotel() {
		return hotel;
	}

    public String getNote() {
       return hotel != null ? hotel.getNote() : null;
    }
}
