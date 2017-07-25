package com.silversea.aem.components.beans;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;
@SuppressWarnings("unchecked")
public class VoyageWrapper {

    static final private Logger LOGGER = LoggerFactory.getLogger(VoyageWrapper.class);

    private Voyage voyage;
    private Voyage77 voyage77;

    public VoyageWrapper(){

    }

    public <T> void init(T obj){

        if(obj instanceof Voyage){
            voyage = (Voyage) obj;
        }
        else if(obj instanceof Voyage77){
            voyage77 = (Voyage77) obj;
        }
        else{
            new IllegalArgumentException("Instance of object is not a voayage");
        }
    }

    private <T> T getInstance(){
        T object;
        if(voyage != null){
            object = (T) voyage;
        }
        else{
            object = (T) voyage77;
        }

        return object;
    }

    public <T> T callMethod(String methodName) {
        T result = null;

        try {
            T object = getInstance();       
            if (object != null && !StringUtils.isEmpty(methodName)) {
                Method method = object.getClass().getDeclaredMethod(methodName);
                if (method != null) {
                    result = (T) method.invoke(object);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while call method {}",methodName,e);
        }

        return result;
    }

    public Integer getVoyageId() {
        Integer voyageId = (Integer) callMethod("getVoyageId");
        return voyageId;
    }
    public String getVoyageUrl() {
        String voyageUrl = (String) callMethod("getVoyageUrl"); 
        return voyageUrl;
    }

    public String getVoyageCod() {
        String voyageCod = (String) callMethod("getVoyageCod"); 
        return voyageCod;
    }

    public String getVoyageHighlights() {
        String voyageHighlights = (String) callMethod("getVoyageHighlights");
        return voyageHighlights;
    }

    public String getVoyageName() {
        String voyageName = (String) callMethod("getVoyageName");
        return voyageName;
    }

    public String getVoyageMarketName() {
        String voyageMarketName = (String) callMethod("voyageMarketName");
        return voyageMarketName;
    }

    public String getVoyageDescription() {
        String voyageDescription = (String) callMethod("getVoyageDescription");
        return voyageDescription;
    }

    public DateTime getDepartDate() {
        DateTime departDate =  (DateTime) callMethod("getDepartDate");
        return departDate;
    }

    public DateTime getArriveDate() {
        DateTime arriveDate = (DateTime) callMethod("getArriveDate");
        return arriveDate;
    }

    public Boolean getIsExpedition() {
        boolean isExpedition = (Boolean) callMethod("getIsExpedition");
        return isExpedition;
    }

    public String getItineraryUrl() {
        String itineraryUrl = (String) callMethod("getItineraryUrl");
        return itineraryUrl;
    }

    public Integer getDays() {
        Integer days = (Integer) callMethod("getDays");
        return days;
    }

    public Integer getDestinationId() {
        Integer destinationId = (Integer) callMethod("getDestinationId");
        return destinationId;
    }

    public String getDestinationUrl() {
        String destinationUrl = (String) callMethod("getDestinationUrl");
        return destinationUrl;
    }

    public Integer getShipId() {
        Integer shipId = (Integer) callMethod("getShipId");
        return shipId;
    }

    public String getShipUrl() {
        String shipUrl = (String) callMethod("getShipUrl");
        return shipUrl;
    }

    public String getMapUrl() {
        String mapUrl = (String) callMethod("getMapUrl");
        return mapUrl;
    }

    public List<Integer> getFeatures() {
        List<Integer> features= (List<Integer>)callMethod("getFeatures");
        return features;
    }
    
    public Boolean getIsDeleted(){
        Boolean isDeleted = false;
        if(voyage77 != null && voyage77.getIsDeleted() != null)
            isDeleted = voyage77.getIsDeleted();
        return isDeleted;
    }
    
    public Boolean getIsVisible(){
        Boolean isVisible = false;
        if(voyage77 != null && voyage77.getIsVisible() != null)
            isVisible = voyage77.getIsVisible();
        return isVisible;
    }
}
