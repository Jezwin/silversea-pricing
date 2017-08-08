package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Page.class)
public class PortModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(PortModel.class);

    @Inject @Optional
    private GeolocationTagService geolocationTagService;

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiTitle") @Optional
    private String apiTitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/jcr:description") @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiDescription") @Optional
    private String apiDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cityId")
    private Integer cityId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/countryIso2")
    private String countryIso2;

    private Tag country;

    private List<ExcursionModel> excursions = new ArrayList<>();

    private List<LandProgramModel> landPrograms = new ArrayList<>();

    private List<HotelModel> hotels = new ArrayList<>();

    @PostConstruct
    private void init() {
        excursions = new ArrayList<>();
        landPrograms = new ArrayList<>();
        hotels = new ArrayList<>();

        final Iterator<Page> childs = page.listChildren();

        while (childs.hasNext()) {
            Page child = childs.next();

            if (child.getName().equals("excursions")) {
                Iterator<Page> excursionsPages = child.listChildren();

                while (excursionsPages.hasNext()) {
                    excursions.add(excursionsPages.next().adaptTo(ExcursionModel.class));
                }
            } else if (child.getName().equals("land-programs")) {
                Iterator<Page> landProgramsPages = child.listChildren();

                while (landProgramsPages.hasNext()) {
                    landPrograms.add(landProgramsPages.next().adaptTo(LandProgramModel.class));
                }
            } else if (child.getName().equals("hotels")) {
                Iterator<Page> hotelsPages = child.listChildren();

                while (hotelsPages.hasNext()) {
                    hotels.add(hotelsPages.next().adaptTo(HotelModel.class));
                }
            }
        }

        if (geolocationTagService != null && StringUtils.isNotEmpty(countryIso2)) {
            final String tagId = geolocationTagService.getTagFromCountryId(countryIso2);
            final TagManager tagManager = page.getContentResource().getResourceResolver().adaptTo(TagManager.class);

            if (tagManager != null && StringUtils.isNotEmpty(tagId)) {
                country = tagManager.resolve(tagId);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getApiTitle() {
        return apiTitle;
    }

    public String getDescription() {
        if (StringUtils.isNotEmpty(description)) {
            return description;
        }

        return apiDescription;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public List<ExcursionModel> getExcursions() {
        return excursions;
    }

    public List<LandProgramModel> getLandPrograms() {
        return landPrograms;
    }

    public List<HotelModel> getHotels() {
        return hotels;
    }

    public String getCountry() {
        return country != null ? country.getTitle() : null;
    }
}
