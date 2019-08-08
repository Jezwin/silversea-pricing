package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.CitySimple;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Hotel
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Hotel {
  @SerializedName("hotel_id")
  private Integer hotelId = null;

  @SerializedName("hotel_cod")
  private String hotelCod = null;

  @SerializedName("hotel_name")
  private String hotelName = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("valid_from")
  private DateTime validFrom = null;

  @SerializedName("valid_to")
  private DateTime validTo = null;

  @SerializedName("image_url")
  private String imageUrl = null;

  @SerializedName("cities")
  private List<CitySimple> cities = new ArrayList<CitySimple>();

  public Hotel hotelId(Integer hotelId) {
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

  public Hotel hotelCod(String hotelCod) {
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

  public Hotel hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

   /**
   * Get hotelName
   * @return hotelName
  **/
  @ApiModelProperty(value = "")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public Hotel description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Hotel validFrom(DateTime validFrom) {
    this.validFrom = validFrom;
    return this;
  }

   /**
   * Get validFrom
   * @return validFrom
  **/
  @ApiModelProperty(value = "")
  public DateTime getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(DateTime validFrom) {
    this.validFrom = validFrom;
  }

  public Hotel validTo(DateTime validTo) {
    this.validTo = validTo;
    return this;
  }

   /**
   * Get validTo
   * @return validTo
  **/
  @ApiModelProperty(value = "")
  public DateTime getValidTo() {
    return validTo;
  }

  public void setValidTo(DateTime validTo) {
    this.validTo = validTo;
  }

  public Hotel imageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

   /**
   * Get imageUrl
   * @return imageUrl
  **/
  @ApiModelProperty(value = "")
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Hotel cities(List<CitySimple> cities) {
    this.cities = cities;
    return this;
  }

  public Hotel addCitiesItem(CitySimple citiesItem) {
    this.cities.add(citiesItem);
    return this;
  }

   /**
   * Get cities
   * @return cities
  **/
  @ApiModelProperty(value = "")
  public List<CitySimple> getCities() {
    return cities;
  }

  public void setCities(List<CitySimple> cities) {
    this.cities = cities;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Hotel hotel = (Hotel) o;
    return Objects.equals(this.hotelId, hotel.hotelId) &&
        Objects.equals(this.hotelCod, hotel.hotelCod) &&
        Objects.equals(this.hotelName, hotel.hotelName) &&
        Objects.equals(this.description, hotel.description) &&
        Objects.equals(this.validFrom, hotel.validFrom) &&
        Objects.equals(this.validTo, hotel.validTo) &&
        Objects.equals(this.imageUrl, hotel.imageUrl) &&
        Objects.equals(this.cities, hotel.cities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, hotelCod, hotelName, description, validFrom, validTo, imageUrl, cities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Hotel {\n");
    
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelCod: ").append(toIndentedString(hotelCod)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    validFrom: ").append(toIndentedString(validFrom)).append("\n");
    sb.append("    validTo: ").append(toIndentedString(validTo)).append("\n");
    sb.append("    imageUrl: ").append(toIndentedString(imageUrl)).append("\n");
    sb.append("    cities: ").append(toIndentedString(cities)).append("\n");
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

