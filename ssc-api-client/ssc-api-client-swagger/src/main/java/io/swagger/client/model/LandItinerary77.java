package io.swagger.client.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.joda.time.DateTime;

/**
 * LandItinerary
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class LandItinerary77 {
  @SerializedName("is_deleted")
  private Boolean isDeleted = null;
	  
  @SerializedName("land_itinerary_id")
  private Integer landItineraryId = null;

  @SerializedName("land_id")
  private Integer landId = null;

  @SerializedName("land_cod")
  private String landCod = null;

  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("date")
  private DateTime date = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  @SerializedName("land_url")
  private String landUrl = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  public LandItinerary77 landItineraryId(Integer landItineraryId) {
    this.landItineraryId = landItineraryId;
    return this;
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
  

   /**
   * Get landItineraryId
   * @return landItineraryId
  **/
  @ApiModelProperty(value = "")
  public Integer getLandItineraryId() {
    return landItineraryId;
  }

  public void setLandItineraryId(Integer landItineraryId) {
    this.landItineraryId = landItineraryId;
  }

  public LandItinerary77 landId(Integer landId) {
    this.landId = landId;
    return this;
  }

   /**
   * Get landId
   * @return landId
  **/
  @ApiModelProperty(value = "")
  public Integer getLandId() {
    return landId;
  }

  public void setLandId(Integer landId) {
    this.landId = landId;
  }

  public LandItinerary77 landCod(String landCod) {
    this.landCod = landCod;
    return this;
  }

   /**
   * Get landCod
   * @return landCod
  **/
  @ApiModelProperty(value = "")
  public String getLandCod() {
    return landCod;
  }

  public void setLandCod(String landCod) {
    this.landCod = landCod;
  }

  public LandItinerary77 cityId(Integer cityId) {
    this.cityId = cityId;
    return this;
  }

   /**
   * Get cityId
   * @return cityId
  **/
  @ApiModelProperty(value = "")
  public Integer getCityId() {
    return cityId;
  }

  public void setCityId(Integer cityId) {
    this.cityId = cityId;
  }

  public LandItinerary77 voyageId(Integer voyageId) {
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

  public LandItinerary77 date(DateTime date) {
    this.date = date;
    return this;
  }

   /**
   * Get date
   * @return date
  **/
  @ApiModelProperty(value = "")
  public DateTime getDate() {
    return date;
  }

  public void setDate(DateTime date) {
    this.date = date;
  }

  public LandItinerary77 cityUrl(String cityUrl) {
    this.cityUrl = cityUrl;
    return this;
  }

   /**
   * Get cityUrl
   * @return cityUrl
  **/
  @ApiModelProperty(value = "")
  public String getCityUrl() {
    return cityUrl;
  }

  public void setCityUrl(String cityUrl) {
    this.cityUrl = cityUrl;
  }

  public LandItinerary77 landUrl(String landUrl) {
    this.landUrl = landUrl;
    return this;
  }

   /**
   * Get landUrl
   * @return landUrl
  **/
  @ApiModelProperty(value = "")
  public String getLandUrl() {
    return landUrl;
  }

  public void setLandUrl(String landUrl) {
    this.landUrl = landUrl;
  }

  public LandItinerary77 voyageUrl(String voyageUrl) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LandItinerary77 landItinerary = (LandItinerary77) o;
    return Objects.equals(this.landItineraryId, landItinerary.landItineraryId) &&
        Objects.equals(this.landId, landItinerary.landId) &&
        Objects.equals(this.landCod, landItinerary.landCod) &&
        Objects.equals(this.cityId, landItinerary.cityId) &&
        Objects.equals(this.voyageId, landItinerary.voyageId) &&
        Objects.equals(this.date, landItinerary.date) &&
        Objects.equals(this.cityUrl, landItinerary.cityUrl) &&
        Objects.equals(this.landUrl, landItinerary.landUrl) &&
        Objects.equals(this.voyageUrl, landItinerary.voyageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(landItineraryId, landId, landCod, cityId, voyageId, date, cityUrl, landUrl, voyageUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LandItinerary {\n");
    
    sb.append("    landItineraryId: ").append(toIndentedString(landItineraryId)).append("\n");
    sb.append("    landId: ").append(toIndentedString(landId)).append("\n");
    sb.append("    landCod: ").append(toIndentedString(landCod)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
    sb.append("    landUrl: ").append(toIndentedString(landUrl)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
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

