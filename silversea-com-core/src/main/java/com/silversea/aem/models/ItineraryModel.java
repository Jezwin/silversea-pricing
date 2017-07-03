package com.silversea.aem.models;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.services.GeolocationTagService;

@Model(adaptables = Page.class)
public class ItineraryModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ItineraryModel.class);

    @Inject
    @Self
    private Page page;

    private Calendar date;
    private String arriveTime;
    private String departTime;
    private String thumbnail;
    private String description;
    private String country;

    private List<ExcursionModel> excursions;
    private List<LandprogramModel> landprograms;
    private List<HotelModel> hotels;

    private ResourceResolver resourceResolver;
    private TagManager tagManager;
    private PageManager pageManager;
    
    @Inject
    private GeolocationTagService geolocationTagService;

    @PostConstruct
    private void init() {
        try{
            resourceResolver = page.getContentResource().getResourceResolver();
            tagManager = resourceResolver.adaptTo(TagManager.class);
            pageManager= resourceResolver.adaptTo(PageManager.class);
            thumbnail = page.getProperties().get("image/fileReference", String.class);
            description = initDescription();
            country = initCountry();
        }catch(RuntimeException e){
            LOGGER.error("Error while initializing model {}",e);
        }
    }

    public void initDate(Node itineraryNode) {

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
            date = Calendar.getInstance();
            arriveTime = getTime(itineraryNode, "arriveTime", "arriveAmPm");
            departTime = getTime(itineraryNode, "departTime", "departAmPm");
            date.setTime(dateFormat.parse(Objects.toString(itineraryNode.getProperty("date").getValue())));
        } catch (RepositoryException | ParseException e) {
            LOGGER.error("Error while initializing itinerary date", e);
        }
    }

    private String getTime(Node itineraryNode, String timeProperty, String amPmProperty) {
        String value = null;
        try {
            String time = Objects.toString(itineraryNode.getProperty(timeProperty).getValue());
            time = formatTime(time);
            //String amPm = Objects.toString(itineraryNode.getProperty(amPmProperty).getValue());
            //value = time + " " + amPm;
            value = time;
        } catch (RepositoryException e) {
            LOGGER.error("Error while retrieving itinerary time", e);
        }
        return value;
    }

    private String initDescription() {
        String value = null;
        value = page.getProperties().get(JcrConstants.JCR_DESCRIPTION, String.class);
        if (StringUtils.isEmpty(value)) {
            value = page.getProperties().get("apiDescription", String.class);
        }
        return value;
    }

    public <T> List<T> initModels(Node itineraryNode, Class<T> modelClass, String rootNode, String reference,
            String callBack) {
        List<T> list = new ArrayList<T>();
        try {
            Resource r = resourceResolver.resolve(itineraryNode.getPath()+"/"+rootNode);
            Iterator<Resource> children = r.listChildren();
            if (children != null && children.hasNext()) {
                while (children.hasNext()) {
                    Resource node = children.next();
                    String path = Objects.toString(node.getValueMap().get(reference));
                    if (!StringUtils.isEmpty(path)) {
                        Page page = pageManager.getPage(path);
                        if(page != null){
                            T model = page.adaptTo(modelClass);
                            callback(model, node, callBack, Resource.class);
                            list.add(model);
                        }
                        else{
                            LOGGER.debug("Page reference {} not found",path);
                        }
                    }
                    else{
                        LOGGER.debug("Property  {} is empty",reference);
                    }
                }
            }
        } 
        catch (RepositoryException e) {
            LOGGER.error("Error while initializing models", e);
        }
        return list;
    }

    public <T> void callback(T model, T paramValue, String methodName, Class<?>... paramType) {
        try {
            if (model != null && !StringUtils.isEmpty(methodName)) {
                Method method = model.getClass().getDeclaredMethod(methodName, paramType);
                if (method != null) {
                    method.invoke(model, paramValue);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while invoking callbakc", e);
        }
    }

    public void init(Node itineraryNode) {
        initDate(itineraryNode);
        excursions = initModels(itineraryNode, ExcursionModel.class, "excursions", "excursionReference", "initialize");
        hotels = initModels(itineraryNode, HotelModel.class, "hotels", "hotelReference", null);
        landprograms = initModels(itineraryNode, LandprogramModel.class, "land-programs", "landProgramReference", null);
    }

    private String formatTime(String time) {
        String value = "";
        if (!StringUtils.isEmpty(time)) {
            StringBuilder str = new StringBuilder(time);
            value = Objects.toString(str.insert(2, ":"));
        }
        return value;
    }

    private String initCountry(){
        String country = null;
        String countryId = page.getProperties().get("countryIso2", String.class);
        String tagId = geolocationTagService.getTagFromCountryId(countryId);
        Tag tag = tagManager.resolve(tagId);
        if(tag != null){
            country = tag.getTitle();
        }
        return country;
    }

    public Page getPage() {
        return page;
    }

    public String getCountry() {
        return country ;
    }

    public Calendar getDate() {
        return date;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getDepartTime() {
        return departTime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public List<ExcursionModel> getExcursions() {
        return excursions;
    }

    public List<LandprogramModel> getLandprograms() {
        return landprograms;
    }

    public List<HotelModel> getHotels() {
        return hotels;
    }

    public String getTitle() {
        return page.getProperties().get("apiTitle", page.getTitle());
    }
}
