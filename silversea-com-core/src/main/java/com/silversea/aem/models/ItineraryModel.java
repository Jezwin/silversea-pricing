package com.silversea.aem.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

@Model(adaptables = Page.class)
public class ItineraryModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ItineraryModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String country;
    private String city;
    private Calendar date;
    private String arriveTime;
    private String departTime;
    private String thumbnail;
    private String description;

    private List<ExcursionModel> excursions;
    private List<LandprogramModel> landprograms;
    private List<HotelModel> hotels;

    private ResourceResolver resourceResolver;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        thumbnail = page.getProperties().get("image/fileReference", String.class);
        description = initDescription();
    }

    public void initDate(Node itineraryNode) {

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
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
            String amPm = Objects.toString(itineraryNode.getProperty(amPmProperty).getValue());
            value = time + " " + amPm;
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

    private List<ExcursionModel> initExcursions(Node itineraryNode) {
        List<ExcursionModel> excursions = new ArrayList<ExcursionModel>();
        try {
            Node excursionsNode = itineraryNode.getNode("excursions");
            NodeIterator excursionsNodes = excursionsNode.getNodes();
            if (excursionsNodes != null && excursionsNodes.hasNext()) {
                while (excursionsNodes.hasNext()) {
                    Node node = excursionsNodes.nextNode();
                    String path = Objects.toString(node.getProperty("excursionReference").getValue());
                    Page resource = resourceResolver.resolve(path).adaptTo(Page.class);
                    ExcursionModel excursionModel = resource.adaptTo(ExcursionModel.class);
                    excursions.add(excursionModel);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error while initializing excursions", e);
        }
        return excursions;
    }
    
    private List<ExcursionModel> initHotels(Node itineraryNode) {
        List<HotelModel> hotels = new ArrayList<HotelModel>();
        try {
            Node hotelsNode = itineraryNode.getNode("hotels");
            NodeIterator hotelsNodes = hotelsNode.getNodes();
            if (hotelsNodes != null && hotelsNodes.hasNext()) {
                while (hotelsNodes.hasNext()) {
                    Node node = hotelsNodes.nextNode();
                    String path = Objects.toString(node.getProperty("hotelReference").getValue());
                    Page resource = resourceResolver.resolve(path).adaptTo(Page.class);
                    HotelModel hotelModel = resource.adaptTo(HotelModel.class);
                    hotels.add(hotelModel);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error while initializing hotels", e);
        }
        return excursions;
    }
    
    private List<LandprogramModel> initLandPrograms(Node itineraryNode) {
        List<LandprogramModel> landprograms = new ArrayList<LandprogramModel>();
        try {
            Node landprogramsNode = itineraryNode.getNode("land-programs");
            NodeIterator landprogramsNodes = landprogramsNode.getNodes();
            if (landprogramsNodes != null && landprogramsNodes.hasNext()) {
                while (landprogramsNodes.hasNext()) {
                    Node node = landprogramsNodes.nextNode();
                    String path = Objects.toString(node.getProperty("landProgramReference").getValue());
                    Page resource = resourceResolver.resolve(path).adaptTo(Page.class);
                    HotelModel hotelModel = resource.adaptTo(HotelModel.class);
                    hotels.add(hotelModel);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error while initializing land programs", e);
        }
        return landprograms;
    }

    private String formatTime(String time) {
        String value = "";
        if (!StringUtils.isEmpty(time)) {
            StringBuilder str = new StringBuilder(time);
            value = Objects.toString(str.insert(2, ":"));
        }
        return value;
    }

    public Page getPage() {
        return page;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
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
}
