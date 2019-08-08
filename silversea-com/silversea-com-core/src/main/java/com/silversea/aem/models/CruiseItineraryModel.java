package com.silversea.aem.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CruiseItineraryModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruiseItineraryModel.class);

    private Calendar date;
    private String arriveTime;
    private String departTime;
    private ItineraryModel itineraryModel;

    public CruiseItineraryModel(ItineraryModel itineraryModel) {
        this.itineraryModel = itineraryModel;
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
            // String amPm =
            // Objects.toString(itineraryNode.getProperty(amPmProperty).getValue());
            // value = time + " " + amPm;
            value = time;
        } catch (RepositoryException e) {
            LOGGER.error("Error while retrieving itinerary time", e);
        }
        return value;
    }

    private String formatTime(String time) {
        String value = "";
        if (!StringUtils.isEmpty(time)) {
            StringBuilder str = new StringBuilder(time);
            value = Objects.toString(str.insert(2, ":"));
        }
        return value;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public ItineraryModel getItineraryModel() {
        return itineraryModel;
    }

    public void setItineraryModel(ItineraryModel itineraryModel) {
        this.itineraryModel = itineraryModel;
    }

}
