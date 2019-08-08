package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Voyage
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Voyage {
  @SerializedName("is_visible")
  private Boolean isVisible = null;

  @SerializedName("load_voyage")
  private Boolean loadVoyage = null;

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("voyage_highlights")
  private String voyageHighlights = null;

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

  public Voyage isVisible(Boolean isVisible) {
    this.isVisible = isVisible;
    return this;
  }

   /**
   * Get isVisible
   * @return isVisible
  **/
  @ApiModelProperty(value = "")
  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean isVisible) {
    this.isVisible = isVisible;
  }

  public Voyage loadVoyage(Boolean loadVoyage) {
    this.loadVoyage = loadVoyage;
    return this;
  }

   /**
   * Get loadVoyage
   * @return loadVoyage
  **/
  @ApiModelProperty(value = "")
  public Boolean getLoadVoyage() {
    return loadVoyage;
  }

  public void setLoadVoyage(Boolean loadVoyage) {
    this.loadVoyage = loadVoyage;
  }

  public Voyage voyageId(Integer voyageId) {
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

  public Voyage voyageUrl(String voyageUrl) {
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

  public Voyage voyageCod(String voyageCod) {
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

  public Voyage voyageHighlights(String voyageHighlights) {
    this.voyageHighlights = voyageHighlights;
    return this;
  }

   /**
   * Get voyageHighlights
   * @return voyageHighlights
  **/
  @ApiModelProperty(value = "")
  public String getVoyageHighlights() {
    return voyageHighlights;
  }

  public void setVoyageHighlights(String voyageHighlights) {
    this.voyageHighlights = voyageHighlights;
  }

  public Voyage voyageName(String voyageName) {
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

  public Voyage voyageMarketName(String voyageMarketName) {
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

  public Voyage voyageDescription(String voyageDescription) {
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

  public Voyage departDate(DateTime departDate) {
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

  public Voyage arriveDate(DateTime arriveDate) {
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

  public Voyage isExpedition(Boolean isExpedition) {
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

  public Voyage itineraryUrl(String itineraryUrl) {
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

  public Voyage days(Integer days) {
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

  public Voyage destinationId(Integer destinationId) {
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

  public Voyage destinationUrl(String destinationUrl) {
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

  public Voyage shipId(Integer shipId) {
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

  public Voyage shipUrl(String shipUrl) {
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

  public Voyage mapUrl(String mapUrl) {
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

  public Voyage features(List<Integer> features) {
    this.features = features;
    return this;
  }

  public Voyage addFeaturesItem(Integer featuresItem) {
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
    Voyage voyage = (Voyage) o;
    return Objects.equals(this.isVisible, voyage.isVisible) &&
        Objects.equals(this.loadVoyage, voyage.loadVoyage) &&
        Objects.equals(this.voyageId, voyage.voyageId) &&
        Objects.equals(this.voyageUrl, voyage.voyageUrl) &&
        Objects.equals(this.voyageCod, voyage.voyageCod) &&
        Objects.equals(this.voyageHighlights, voyage.voyageHighlights) &&
        Objects.equals(this.voyageName, voyage.voyageName) &&
        Objects.equals(this.voyageMarketName, voyage.voyageMarketName) &&
        Objects.equals(this.voyageDescription, voyage.voyageDescription) &&
        Objects.equals(this.departDate, voyage.departDate) &&
        Objects.equals(this.arriveDate, voyage.arriveDate) &&
        Objects.equals(this.isExpedition, voyage.isExpedition) &&
        Objects.equals(this.itineraryUrl, voyage.itineraryUrl) &&
        Objects.equals(this.days, voyage.days) &&
        Objects.equals(this.destinationId, voyage.destinationId) &&
        Objects.equals(this.destinationUrl, voyage.destinationUrl) &&
        Objects.equals(this.shipId, voyage.shipId) &&
        Objects.equals(this.shipUrl, voyage.shipUrl) &&
        Objects.equals(this.mapUrl, voyage.mapUrl) &&
        Objects.equals(this.features, voyage.features);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isVisible, loadVoyage, voyageId, voyageUrl, voyageCod, voyageHighlights, voyageName, voyageMarketName, voyageDescription, departDate, arriveDate, isExpedition, itineraryUrl, days, destinationId, destinationUrl, shipId, shipUrl, mapUrl, features);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Voyage {\n");
    
    sb.append("    isVisible: ").append(toIndentedString(isVisible)).append("\n");
    sb.append("    loadVoyage: ").append(toIndentedString(loadVoyage)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    voyageHighlights: ").append(toIndentedString(voyageHighlights)).append("\n");
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

