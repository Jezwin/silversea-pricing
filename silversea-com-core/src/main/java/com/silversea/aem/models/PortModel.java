package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
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

/**
 * TODO review how to get childs
 * Created by aurelienolivier on 12/02/2017.
 */
@Model(adaptables = Page.class)
public class PortModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(PortModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiDescription") @Optional
    private String apiDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/cityId")
    private Integer cityId;

    private List<ExcursionModel> excursions;

    private List<LandProgramModel> landprograms;

    private List<HotelModel> hotels;

    @PostConstruct
    private void init() {
        excursions = new ArrayList<>();
        landprograms = new ArrayList<>();
        hotels = new ArrayList<>();

        try {
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
                        landprograms.add(landProgramsPages.next().adaptTo(LandProgramModel.class));
                    }
                } else if (child.getName().equals("hotels")) {
                    Iterator<Page> hotelsPages = child.listChildren();

                    while (hotelsPages.hasNext()) {
                        hotels.add(hotelsPages.next().adaptTo(HotelModel.class));
                    }
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error while initializing model {}", e);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public Integer getCityId() {
        return cityId;
    }

    public List<ExcursionModel> getExcursions() {
        return excursions;
    }

    public List<LandProgramModel> getLandPrograms() {
        return landprograms;
    }

    public List<HotelModel> getHotels() {
        return hotels;
    }
}
