package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationTagService;

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

    @Inject @Named(JcrConstants.JCR_CONTENT + "/countryIso3")
    private String countryIso3;
    private Tag country;

    private List<ExcursionModel> excursions = new ArrayList<>();

    private List<LandProgramModel> landPrograms = new ArrayList<>();

    private List<HotelModel> hotels = new ArrayList<>();

    private String path;

    private String name;

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
                
                ExcursionModel excursionModel = null;
                while (excursionsPages.hasNext()) {
                	excursionModel = excursionsPages.next().adaptTo(ExcursionModel.class);
                	final ExcursionModel excursionModelFinal = excursionModel;
                	if (excursionModel != null && StringUtils.isNotEmpty(excursionModel.getCodeExcursion()) && StringUtils.isNotEmpty(excursionModel.getApiLongDescription())) {
                		if(!excursions.stream().anyMatch(dto -> dto.getCodeExcursion() == excursionModelFinal.getCodeExcursion())){
                			excursions.add(excursionModel);
                		}
                	}
                }
            } else if (child.getName().equals("land-programs")) {
                Iterator<Page> landProgramsPages = child.listChildren();
                LandProgramModel landProgramModel = null;
                while (landProgramsPages.hasNext()) {
                	landProgramModel = landProgramsPages.next().adaptTo(LandProgramModel.class);
                	if (landProgramModel != null && StringUtils.isNotEmpty(landProgramModel.getLandCode())) {
                		landPrograms.add(landProgramModel);
                	}
                }
            } else if (child.getName().equals("hotels")) {
                Iterator<Page> hotelsPages = child.listChildren();
                HotelModel hotelModel = null;
                while (hotelsPages.hasNext()) {
                	hotelModel = hotelsPages.next().adaptTo(HotelModel.class);
                	if (hotelModel != null && StringUtils.isNotEmpty(hotelModel.getName())) {
                		hotels.add(hotelModel);
                	}
                }
            }
        }

        if (geolocationTagService != null && StringUtils.isNotEmpty(countryIso2)) {
            final String tagId = geolocationTagService.getTagIdFromCountryId(countryIso2);
            final TagManager tagManager = page.getContentResource().getResourceResolver().adaptTo(TagManager.class);

            if (tagManager != null && StringUtils.isNotEmpty(tagId)) {
                country = tagManager.resolve(tagId);
            }
        }
        
        path = page.getPath();
        name = page.getName();
        
        Collections.sort(excursions, new Comparator<ExcursionModel>(){
        	@Override
        	  public int compare(ExcursionModel o1, ExcursionModel o2)
        	  {
        	     return o1.getTitle().compareTo(o2.getTitle());
        	  }
        	});
    }

    public String getTitle() {
        return title;
    }

    public String getApiTitle() {
    	if(StringUtils.isNotEmpty(title)) {
    		return title;
    	}else {
    		return apiTitle;
    	}
    		
        
    }

    public String getDescription() {
        if (StringUtils.isNotEmpty(description)) {
            return description;
        }

        return apiDescription;
    }
    
    public String getRealDescription(){
    	return description;
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

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getCountryIso3() {
        return countryIso3;
    }

    public String getCountryIso2() {
        return countryIso2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PortModel)) {
            return false;
        }

        final PortModel objShipModel = (PortModel)obj;

        return objShipModel.getPath().equals(getPath());
    }
}
