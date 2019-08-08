package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Voyage77
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Voyage77 {
  @SerializedName("is_deleted")
  private Boolean isDeleted = null;

  @SerializedName("is_visible")
  private Boolean isVisible = null;

  @SerializedName("load_voyage")
  private Boolean loadVoyage = null;
  
  @SerializedName("is_combo")
  private Boolean isCombo = null;

  @SerializedName("map_id")
  private Integer mapId = null;

  @SerializedName("map_svg_url")
  private String mapSvgUrl = null;

  @SerializedName("images")
  private List<String> images = new ArrayList<String>();

  @SerializedName("voyage_highlights")
  private String voyageHighlights = null;

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

  @SerializedName("map3_url")
  private String map3Url = null;

  @SerializedName("map4_url")
  private String map4Url = null;

    @SerializedName("MapPos")
  private String mapPos = null;

  @SerializedName("tour_book")
  private String tourBook = null;

  @SerializedName("features")
  private List<Integer> features = new ArrayList<Integer>();

  public Voyage77 isDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
    return this;
  }

    @ApiModelProperty(value = "")
    public String getTourBook() {
        return tourBook;
    }

    public void setTourBook(String tourBook) {
        this.tourBook = tourBook;
    }

    /**
   * Get isDeleted
   * @return isDeleted
  **/
  @ApiModelProperty(value = "")
  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public Voyage77 isVisible(Boolean isVisible) {
    this.isVisible = isVisible;
    return this;
  }

  /**
  * Get isCombo
  * @return isCombo
 **/
 @ApiModelProperty(value = "")
 public Boolean getIsCombo() {
   return isCombo;
 }

 public void setIsCombo(Boolean isCombo) {
   this.isCombo = isCombo;
 }

 public Voyage77 isCombo(Boolean isCombo) {
   this.isCombo = isCombo;
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

  public Voyage77 loadVoyage(Boolean loadVoyage) {
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

  public Voyage77 mapId(Integer mapId) {
    this.mapId = mapId;
    return this;
  }

   /**
   * Get mapId
   * @return mapId
  **/
  @ApiModelProperty(value = "")
  public Integer getMapId() {
    return mapId;
  }

  public void setMapId(Integer mapId) {
    this.mapId = mapId;
  }

  public Voyage77 mapSvgUrl(String mapSvgUrl) {
    this.mapSvgUrl = mapSvgUrl;
    return this;
  }

   /**
   * Get mapSvgUrl
   * @return mapSvgUrl
  **/
  @ApiModelProperty(value = "")
  public String getMapSvgUrl() {
    return mapSvgUrl;
  }

  public void setMapSvgUrl(String mapSvgUrl) {
    this.mapSvgUrl = mapSvgUrl;
  }

  public Voyage77 images(List<String> images) {
    this.images = images;
    return this;
  }

  public Voyage77 addImagesItem(String imagesItem) {
    this.images.add(imagesItem);
    return this;
  }

   /**
   * Get images
   * @return images
  **/
  @ApiModelProperty(value = "")
  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public Voyage77 voyageHighlights(String voyageHighlights) {
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

  public Voyage77 voyageId(Integer voyageId) {
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

  public Voyage77 voyageUrl(String voyageUrl) {
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

  public Voyage77 voyageCod(String voyageCod) {
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

  public Voyage77 voyageName(String voyageName) {
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

  public Voyage77 voyageMarketName(String voyageMarketName) {
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

  public Voyage77 voyageDescription(String voyageDescription) {
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

  public Voyage77 departDate(DateTime departDate) {
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

  public Voyage77 arriveDate(DateTime arriveDate) {
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

  public Voyage77 isExpedition(Boolean isExpedition) {
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

  public Voyage77 itineraryUrl(String itineraryUrl) {
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

  public Voyage77 days(Integer days) {
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

  public Voyage77 destinationId(Integer destinationId) {
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

  public Voyage77 destinationUrl(String destinationUrl) {
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

  public Voyage77 shipId(Integer shipId) {
    this.shipId = shipId;
    return this;
  }

  @ApiModelProperty("")
    public String getMapPos() {
        return mapPos;
    }

    public void setMapPos(String mapPos) {
        this.mapPos = mapPos;
    }

    public Voyage77 mapPos(String mapPos){
        this.mapPos = mapPos;
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

  public Voyage77 shipUrl(String shipUrl) {
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

  public Voyage77 mapUrl(String mapUrl) {
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

  public Voyage77 map3Url(String map3Url) {
      this.map3Url = map3Url;
      return this;
  }

   /**
    * Get map3Url
    * @return map3Url
   **/
   @ApiModelProperty(value = "")
   public String getMap3Url() {
        return map3Url;
    }

   public void setMap3Url(String map3Url) {
        this.map3Url = map3Url;
    }


    public Voyage77 map4Url(String map4Url) {
        this.map4Url = map4Url;
        return this;
    }

    /**
     * Get map4Url
     * @return map4Url
     **/
    @ApiModelProperty(value = "")
    public String getMap4Url() {
        return map4Url;
    }

    public void setMap4Url(String map4Url) {
        this.map4Url = map4Url;
    }

  public Voyage77 features(List<Integer> features) {
    this.features = features;
    return this;
  }

  public Voyage77 addFeaturesItem(Integer featuresItem) {
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
    Voyage77 voyage77 = (Voyage77) o;
      return Objects.equals(this.isDeleted, voyage77.isDeleted) &&
              Objects.equals(this.isVisible, voyage77.isVisible) &&
              Objects.equals(this.loadVoyage, voyage77.loadVoyage) &&
              Objects.equals(this.mapId, voyage77.mapId) &&
              Objects.equals(this.mapSvgUrl, voyage77.mapSvgUrl) &&
              Objects.equals(this.images, voyage77.images) &&
              Objects.equals(this.voyageHighlights, voyage77.voyageHighlights) &&
              Objects.equals(this.voyageId, voyage77.voyageId) &&
              Objects.equals(this.voyageUrl, voyage77.voyageUrl) &&
              Objects.equals(this.voyageCod, voyage77.voyageCod) &&
              Objects.equals(this.voyageName, voyage77.voyageName) &&
              Objects.equals(this.voyageMarketName, voyage77.voyageMarketName) &&
              Objects.equals(this.voyageDescription, voyage77.voyageDescription) &&
              Objects.equals(this.departDate, voyage77.departDate) &&
              Objects.equals(this.arriveDate, voyage77.arriveDate) &&
              Objects.equals(this.isExpedition, voyage77.isExpedition) &&
              Objects.equals(this.itineraryUrl, voyage77.itineraryUrl) &&
              Objects.equals(this.days, voyage77.days) &&
              Objects.equals(this.destinationId, voyage77.destinationId) &&
              Objects.equals(this.destinationUrl, voyage77.destinationUrl) &&
              Objects.equals(this.shipId, voyage77.shipId) &&
              Objects.equals(this.shipUrl, voyage77.shipUrl) &&
              Objects.equals(this.mapUrl, voyage77.mapUrl) &&
              Objects.equals(this.map3Url, voyage77.map3Url) &&
              Objects.equals(this.map4Url, voyage77.map4Url) &&
              Objects.equals(this.isCombo, voyage77.isCombo) &&
              Objects.equals(this.features, voyage77.features) &&
              Objects.equals(this.tourBook, voyage77.tourBook);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDeleted, isVisible, loadVoyage, mapId, mapSvgUrl, images, voyageHighlights, voyageId, voyageUrl, voyageCod, voyageName, voyageMarketName, voyageDescription, departDate, arriveDate, isExpedition, itineraryUrl, days, destinationId, destinationUrl, shipId, shipUrl, mapUrl, features, isCombo, tourBook);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Voyage77 {\n");
    
    sb.append("    isDeleted: ").append(toIndentedString(isDeleted)).append("\n");
    sb.append("    isVisible: ").append(toIndentedString(isVisible)).append("\n");
    sb.append("    loadVoyage: ").append(toIndentedString(loadVoyage)).append("\n");
    sb.append("    mapId: ").append(toIndentedString(mapId)).append("\n");
    sb.append("    mapSvgUrl: ").append(toIndentedString(mapSvgUrl)).append("\n");
    sb.append("    images: ").append(toIndentedString(images)).append("\n");
    sb.append("    voyageHighlights: ").append(toIndentedString(voyageHighlights)).append("\n");
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
    sb.append("    map3Url: ").append(toIndentedString(map3Url)).append("\n");
    sb.append("    map4Url: ").append(toIndentedString(map4Url)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("    tourBook: ").append(toIndentedString(tourBook)).append("\n");
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

