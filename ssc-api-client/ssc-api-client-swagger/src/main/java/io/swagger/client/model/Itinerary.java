package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

/**
 * Itinerary
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Itinerary {
  @SerializedName("itinerary_id")
  private Integer itineraryId = null;

  @SerializedName("itinerary_url")
  private String itineraryUrl = null;

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  @SerializedName("itinerary_date")
  private DateTime itineraryDate = null;

  @SerializedName("arrive_time")
  private String arriveTime = null;

  @SerializedName("arrive_time_ampm")
  private String arriveTimeAmpm = null;

  @SerializedName("depart_time")
  private String departTime = null;

  @SerializedName("depart_time_ampm")
  private String departTimeAmpm = null;

  @SerializedName("is_overnight")
  private Boolean isOvernight = null;

  public Itinerary itineraryId(Integer itineraryId) {
    this.itineraryId = itineraryId;
    return this;
  }

   /**
   * Get itineraryId
   * @return itineraryId
  **/
  @ApiModelProperty(value = "")
  public Integer getItineraryId() {
    return itineraryId;
  }

  public void setItineraryId(Integer itineraryId) {
    this.itineraryId = itineraryId;
  }

  public Itinerary itineraryUrl(String itineraryUrl) {
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

  public Itinerary voyageId(Integer voyageId) {
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

  public Itinerary voyageUrl(String voyageUrl) {
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

  public Itinerary cityId(Integer cityId) {
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

  public Itinerary cityUrl(String cityUrl) {
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

  public Itinerary itineraryDate(DateTime itineraryDate) {
    this.itineraryDate = itineraryDate;
    return this;
  }

   /**
   * Get itineraryDate
   * @return itineraryDate
  **/
  @ApiModelProperty(value = "")
  public DateTime getItineraryDate() {
    return itineraryDate;
  }

  public void setItineraryDate(DateTime itineraryDate) {
    this.itineraryDate = itineraryDate;
  }

  public Itinerary arriveTime(String arriveTime) {
    this.arriveTime = arriveTime;
    return this;
  }

   /**
   * Get arriveTime
   * @return arriveTime
  **/
  @ApiModelProperty(value = "")
  public String getArriveTime() {
    return arriveTime;
  }

  public void setArriveTime(String arriveTime) {
    this.arriveTime = arriveTime;
  }

  public Itinerary arriveTimeAmpm(String arriveTimeAmpm) {
    this.arriveTimeAmpm = arriveTimeAmpm;
    return this;
  }

   /**
   * Get arriveTimeAmpm
   * @return arriveTimeAmpm
  **/
  @ApiModelProperty(value = "")
  public String getArriveTimeAmpm() {
    return arriveTimeAmpm;
  }

  public void setArriveTimeAmpm(String arriveTimeAmpm) {
    this.arriveTimeAmpm = arriveTimeAmpm;
  }

  public Itinerary departTime(String departTime) {
    this.departTime = departTime;
    return this;
  }

   /**
   * Get departTime
   * @return departTime
  **/
  @ApiModelProperty(value = "")
  public String getDepartTime() {
    return departTime;
  }

  public void setDepartTime(String departTime) {
    this.departTime = departTime;
  }

  public Itinerary departTimeAmpm(String departTimeAmpm) {
    this.departTimeAmpm = departTimeAmpm;
    return this;
  }

   /**
   * Get departTimeAmpm
   * @return departTimeAmpm
  **/
  @ApiModelProperty(value = "")
  public String getDepartTimeAmpm() {
    return departTimeAmpm;
  }

  public void setDepartTimeAmpm(String departTimeAmpm) {
    this.departTimeAmpm = departTimeAmpm;
  }

  public Itinerary isOvernight(Boolean isOvernight) {
    this.isOvernight = isOvernight;
    return this;
  }

   /**
   * Get isOvernight
   * @return isOvernight
  **/
  @ApiModelProperty(value = "")
  public Boolean getIsOvernight() {
    return isOvernight;
  }

  public void setIsOvernight(Boolean isOvernight) {
    this.isOvernight = isOvernight;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Itinerary itinerary = (Itinerary) o;
    return Objects.equals(this.itineraryId, itinerary.itineraryId) &&
        Objects.equals(this.itineraryUrl, itinerary.itineraryUrl) &&
        Objects.equals(this.voyageId, itinerary.voyageId) &&
        Objects.equals(this.voyageUrl, itinerary.voyageUrl) &&
        Objects.equals(this.cityId, itinerary.cityId) &&
        Objects.equals(this.cityUrl, itinerary.cityUrl) &&
        Objects.equals(this.itineraryDate, itinerary.itineraryDate) &&
        Objects.equals(this.arriveTime, itinerary.arriveTime) &&
        Objects.equals(this.arriveTimeAmpm, itinerary.arriveTimeAmpm) &&
        Objects.equals(this.departTime, itinerary.departTime) &&
        Objects.equals(this.departTimeAmpm, itinerary.departTimeAmpm) &&
        Objects.equals(this.isOvernight, itinerary.isOvernight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itineraryId, itineraryUrl, voyageId, voyageUrl, cityId, cityUrl, itineraryDate, arriveTime, arriveTimeAmpm, departTime, departTimeAmpm, isOvernight);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Itinerary {\n");
    
    sb.append("    itineraryId: ").append(toIndentedString(itineraryId)).append("\n");
    sb.append("    itineraryUrl: ").append(toIndentedString(itineraryUrl)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
    sb.append("    itineraryDate: ").append(toIndentedString(itineraryDate)).append("\n");
    sb.append("    arriveTime: ").append(toIndentedString(arriveTime)).append("\n");
    sb.append("    arriveTimeAmpm: ").append(toIndentedString(arriveTimeAmpm)).append("\n");
    sb.append("    departTime: ").append(toIndentedString(departTime)).append("\n");
    sb.append("    departTimeAmpm: ").append(toIndentedString(departTimeAmpm)).append("\n");
    sb.append("    isOvernight: ").append(toIndentedString(isOvernight)).append("\n");
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

