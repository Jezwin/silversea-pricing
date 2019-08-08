package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

/**
 * HotelItinerary
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class HotelItinerary {
  @SerializedName("hotel_itinerary_id")
  private Integer hotelItineraryId = null;

  @SerializedName("hotel_id")
  private Integer hotelId = null;

  @SerializedName("hotel_cod")
  private String hotelCod = null;

  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("date")
  private DateTime date = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  @SerializedName("hotel_url")
  private String hotelUrl = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  public HotelItinerary hotelItineraryId(Integer hotelItineraryId) {
    this.hotelItineraryId = hotelItineraryId;
    return this;
  }

   /**
   * Get hotelItineraryId
   * @return hotelItineraryId
  **/
  @ApiModelProperty(value = "")
  public Integer getHotelItineraryId() {
    return hotelItineraryId;
  }

  public void setHotelItineraryId(Integer hotelItineraryId) {
    this.hotelItineraryId = hotelItineraryId;
  }

  public HotelItinerary hotelId(Integer hotelId) {
    this.hotelId = hotelId;
    return this;
  }

   /**
   * Get hotelId
   * @return hotelId
  **/
  @ApiModelProperty(value = "")
  public Integer getHotelId() {
    return hotelId;
  }

  public void setHotelId(Integer hotelId) {
    this.hotelId = hotelId;
  }

  public HotelItinerary hotelCod(String hotelCod) {
    this.hotelCod = hotelCod;
    return this;
  }

   /**
   * Get hotelCod
   * @return hotelCod
  **/
  @ApiModelProperty(value = "")
  public String getHotelCod() {
    return hotelCod;
  }

  public void setHotelCod(String hotelCod) {
    this.hotelCod = hotelCod;
  }

  public HotelItinerary cityId(Integer cityId) {
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

  public HotelItinerary voyageId(Integer voyageId) {
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

  public HotelItinerary date(DateTime date) {
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

  public HotelItinerary cityUrl(String cityUrl) {
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

  public HotelItinerary hotelUrl(String hotelUrl) {
    this.hotelUrl = hotelUrl;
    return this;
  }

   /**
   * Get hotelUrl
   * @return hotelUrl
  **/
  @ApiModelProperty(value = "")
  public String getHotelUrl() {
    return hotelUrl;
  }

  public void setHotelUrl(String hotelUrl) {
    this.hotelUrl = hotelUrl;
  }

  public HotelItinerary voyageUrl(String voyageUrl) {
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
    HotelItinerary hotelItinerary = (HotelItinerary) o;
    return Objects.equals(this.hotelItineraryId, hotelItinerary.hotelItineraryId) &&
        Objects.equals(this.hotelId, hotelItinerary.hotelId) &&
        Objects.equals(this.hotelCod, hotelItinerary.hotelCod) &&
        Objects.equals(this.cityId, hotelItinerary.cityId) &&
        Objects.equals(this.voyageId, hotelItinerary.voyageId) &&
        Objects.equals(this.date, hotelItinerary.date) &&
        Objects.equals(this.cityUrl, hotelItinerary.cityUrl) &&
        Objects.equals(this.hotelUrl, hotelItinerary.hotelUrl) &&
        Objects.equals(this.voyageUrl, hotelItinerary.voyageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelItineraryId, hotelId, hotelCod, cityId, voyageId, date, cityUrl, hotelUrl, voyageUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelItinerary {\n");
    
    sb.append("    hotelItineraryId: ").append(toIndentedString(hotelItineraryId)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelCod: ").append(toIndentedString(hotelCod)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
    sb.append("    hotelUrl: ").append(toIndentedString(hotelUrl)).append("\n");
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

