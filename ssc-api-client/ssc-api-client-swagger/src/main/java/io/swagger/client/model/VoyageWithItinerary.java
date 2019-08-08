package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.Itinerary;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * VoyageWithItinerary
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class VoyageWithItinerary {
  @SerializedName("itinerary")
  private List<Itinerary> itinerary = new ArrayList<Itinerary>();

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("voyage_name")
  private String voyageName = null;

  @SerializedName("voyage_market_name")
  private String voyageMarketName = null;

  @SerializedName("voyage_description")
  private String voyageDescription = null;

  @SerializedName("depart_date")
  private DateTime departDate = null;

  @SerializedName("arrive_date")
  private DateTime arriveDate = null;

  @SerializedName("is_expedition")
  private Boolean isExpedition = null;

  @SerializedName("itinerary_url")
  private String itineraryUrl = null;

  @SerializedName("days")
  private Integer days = null;

  @SerializedName("destination_id")
  private Integer destinationId = null;

  @SerializedName("destination_url")
  private String destinationUrl = null;

  @SerializedName("ship_id")
  private Integer shipId = null;

  @SerializedName("ship_url")
  private String shipUrl = null;

  @SerializedName("map_url")
  private String mapUrl = null;

  @SerializedName("features")
  private List<Integer> features = new ArrayList<Integer>();

  public VoyageWithItinerary itinerary(List<Itinerary> itinerary) {
    this.itinerary = itinerary;
    return this;
  }

  public VoyageWithItinerary addItineraryItem(Itinerary itineraryItem) {
    this.itinerary.add(itineraryItem);
    return this;
  }

   /**
   * Get itinerary
   * @return itinerary
  **/
  @ApiModelProperty(value = "")
  public List<Itinerary> getItinerary() {
    return itinerary;
  }

  public void setItinerary(List<Itinerary> itinerary) {
    this.itinerary = itinerary;
  }

  public VoyageWithItinerary voyageId(Integer voyageId) {
    this.voyageId = voyageId;
    return this;
  }

   /**
   * Get voyageId
   * @return voyageId
  **/
  @ApiModelProperty(value = "")
  public Integer getVoyageId() {
    return voyageId;
  }

  public void setVoyageId(Integer voyageId) {
    this.voyageId = voyageId;
  }

  public VoyageWithItinerary voyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
    return this;
  }

   /**
   * Get voyageUrl
   * @return voyageUrl
  **/
  @ApiModelProperty(value = "")
  public String getVoyageUrl() {
    return voyageUrl;
  }

  public void setVoyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
  }

  public VoyageWithItinerary voyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
    return this;
  }

   /**
   * Get voyageCod
   * @return voyageCod
  **/
  @ApiModelProperty(value = "")
  public String getVoyageCod() {
    return voyageCod;
  }

  public void setVoyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
  }

  public VoyageWithItinerary voyageName(String voyageName) {
    this.voyageName = voyageName;
    return this;
  }

   /**
   * Get voyageName
   * @return voyageName
  **/
  @ApiModelProperty(value = "")
  public String getVoyageName() {
    return voyageName;
  }

  public void setVoyageName(String voyageName) {
    this.voyageName = voyageName;
  }

  public VoyageWithItinerary voyageMarketName(String voyageMarketName) {
    this.voyageMarketName = voyageMarketName;
    return this;
  }

   /**
   * Get voyageMarketName
   * @return voyageMarketName
  **/
  @ApiModelProperty(value = "")
  public String getVoyageMarketName() {
    return voyageMarketName;
  }

  public void setVoyageMarketName(String voyageMarketName) {
    this.voyageMarketName = voyageMarketName;
  }

  public VoyageWithItinerary voyageDescription(String voyageDescription) {
    this.voyageDescription = voyageDescription;
    return this;
  }

   /**
   * Get voyageDescription
   * @return voyageDescription
  **/
  @ApiModelProperty(value = "")
  public String getVoyageDescription() {
    return voyageDescription;
  }

  public void setVoyageDescription(String voyageDescription) {
    this.voyageDescription = voyageDescription;
  }

  public VoyageWithItinerary departDate(DateTime departDate) {
    this.departDate = departDate;
    return this;
  }

   /**
   * Get departDate
   * @return departDate
  **/
  @ApiModelProperty(value = "")
  public DateTime getDepartDate() {
    return departDate;
  }

  public void setDepartDate(DateTime departDate) {
    this.departDate = departDate;
  }

  public VoyageWithItinerary arriveDate(DateTime arriveDate) {
    this.arriveDate = arriveDate;
    return this;
  }

   /**
   * Get arriveDate
   * @return arriveDate
  **/
  @ApiModelProperty(value = "")
  public DateTime getArriveDate() {
    return arriveDate;
  }

  public void setArriveDate(DateTime arriveDate) {
    this.arriveDate = arriveDate;
  }

  public VoyageWithItinerary isExpedition(Boolean isExpedition) {
    this.isExpedition = isExpedition;
    return this;
  }

   /**
   * Get isExpedition
   * @return isExpedition
  **/
  @ApiModelProperty(value = "")
  public Boolean getIsExpedition() {
    return isExpedition;
  }

  public void setIsExpedition(Boolean isExpedition) {
    this.isExpedition = isExpedition;
  }

  public VoyageWithItinerary itineraryUrl(String itineraryUrl) {
    this.itineraryUrl = itineraryUrl;
    return this;
  }

   /**
   * Get itineraryUrl
   * @return itineraryUrl
  **/
  @ApiModelProperty(value = "")
  public String getItineraryUrl() {
    return itineraryUrl;
  }

  public void setItineraryUrl(String itineraryUrl) {
    this.itineraryUrl = itineraryUrl;
  }

  public VoyageWithItinerary days(Integer days) {
    this.days = days;
    return this;
  }

   /**
   * Get days
   * @return days
  **/
  @ApiModelProperty(value = "")
  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }

  public VoyageWithItinerary destinationId(Integer destinationId) {
    this.destinationId = destinationId;
    return this;
  }

   /**
   * Get destinationId
   * @return destinationId
  **/
  @ApiModelProperty(value = "")
  public Integer getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(Integer destinationId) {
    this.destinationId = destinationId;
  }

  public VoyageWithItinerary destinationUrl(String destinationUrl) {
    this.destinationUrl = destinationUrl;
    return this;
  }

   /**
   * Get destinationUrl
   * @return destinationUrl
  **/
  @ApiModelProperty(value = "")
  public String getDestinationUrl() {
    return destinationUrl;
  }

  public void setDestinationUrl(String destinationUrl) {
    this.destinationUrl = destinationUrl;
  }

  public VoyageWithItinerary shipId(Integer shipId) {
    this.shipId = shipId;
    return this;
  }

   /**
   * Get shipId
   * @return shipId
  **/
  @ApiModelProperty(value = "")
  public Integer getShipId() {
    return shipId;
  }

  public void setShipId(Integer shipId) {
    this.shipId = shipId;
  }

  public VoyageWithItinerary shipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
    return this;
  }

   /**
   * Get shipUrl
   * @return shipUrl
  **/
  @ApiModelProperty(value = "")
  public String getShipUrl() {
    return shipUrl;
  }

  public void setShipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
  }

  public VoyageWithItinerary mapUrl(String mapUrl) {
    this.mapUrl = mapUrl;
    return this;
  }

   /**
   * Get mapUrl
   * @return mapUrl
  **/
  @ApiModelProperty(value = "")
  public String getMapUrl() {
    return mapUrl;
  }

  public void setMapUrl(String mapUrl) {
    this.mapUrl = mapUrl;
  }

  public VoyageWithItinerary features(List<Integer> features) {
    this.features = features;
    return this;
  }

  public VoyageWithItinerary addFeaturesItem(Integer featuresItem) {
    this.features.add(featuresItem);
    return this;
  }

   /**
   * Get features
   * @return features
  **/
  @ApiModelProperty(value = "")
  public List<Integer> getFeatures() {
    return features;
  }

  public void setFeatures(List<Integer> features) {
    this.features = features;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyageWithItinerary voyageWithItinerary = (VoyageWithItinerary) o;
    return Objects.equals(this.itinerary, voyageWithItinerary.itinerary) &&
        Objects.equals(this.voyageId, voyageWithItinerary.voyageId) &&
        Objects.equals(this.voyageUrl, voyageWithItinerary.voyageUrl) &&
        Objects.equals(this.voyageCod, voyageWithItinerary.voyageCod) &&
        Objects.equals(this.voyageName, voyageWithItinerary.voyageName) &&
        Objects.equals(this.voyageMarketName, voyageWithItinerary.voyageMarketName) &&
        Objects.equals(this.voyageDescription, voyageWithItinerary.voyageDescription) &&
        Objects.equals(this.departDate, voyageWithItinerary.departDate) &&
        Objects.equals(this.arriveDate, voyageWithItinerary.arriveDate) &&
        Objects.equals(this.isExpedition, voyageWithItinerary.isExpedition) &&
        Objects.equals(this.itineraryUrl, voyageWithItinerary.itineraryUrl) &&
        Objects.equals(this.days, voyageWithItinerary.days) &&
        Objects.equals(this.destinationId, voyageWithItinerary.destinationId) &&
        Objects.equals(this.destinationUrl, voyageWithItinerary.destinationUrl) &&
        Objects.equals(this.shipId, voyageWithItinerary.shipId) &&
        Objects.equals(this.shipUrl, voyageWithItinerary.shipUrl) &&
        Objects.equals(this.mapUrl, voyageWithItinerary.mapUrl) &&
        Objects.equals(this.features, voyageWithItinerary.features);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itinerary, voyageId, voyageUrl, voyageCod, voyageName, voyageMarketName, voyageDescription, departDate, arriveDate, isExpedition, itineraryUrl, days, destinationId, destinationUrl, shipId, shipUrl, mapUrl, features);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyageWithItinerary {\n");
    
    sb.append("    itinerary: ").append(toIndentedString(itinerary)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    voyageName: ").append(toIndentedString(voyageName)).append("\n");
    sb.append("    voyageMarketName: ").append(toIndentedString(voyageMarketName)).append("\n");
    sb.append("    voyageDescription: ").append(toIndentedString(voyageDescription)).append("\n");
    sb.append("    departDate: ").append(toIndentedString(departDate)).append("\n");
    sb.append("    arriveDate: ").append(toIndentedString(arriveDate)).append("\n");
    sb.append("    isExpedition: ").append(toIndentedString(isExpedition)).append("\n");
    sb.append("    itineraryUrl: ").append(toIndentedString(itineraryUrl)).append("\n");
    sb.append("    days: ").append(toIndentedString(days)).append("\n");
    sb.append("    destinationId: ").append(toIndentedString(destinationId)).append("\n");
    sb.append("    destinationUrl: ").append(toIndentedString(destinationUrl)).append("\n");
    sb.append("    shipId: ").append(toIndentedString(shipId)).append("\n");
    sb.append("    shipUrl: ").append(toIndentedString(shipUrl)).append("\n");
    sb.append("    mapUrl: ").append(toIndentedString(mapUrl)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

