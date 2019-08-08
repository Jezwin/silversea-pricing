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
 * Hotel77
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Hotel77 {
  @SerializedName("is_deleted")
  private Boolean isDeleted = null;

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


  @SerializedName("image_url2")
  private String imageUrl2 = null;

  @SerializedName("cities")
  private List<CitySimple> cities = new ArrayList<CitySimple>();

  public Hotel77 isDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
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

  public Hotel77 hotelId(Integer hotelId) {
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

  public Hotel77 hotelCod(String hotelCod) {
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

  public Hotel77 hotelName(String hotelName) {
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

  public Hotel77 description(String description) {
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

  public Hotel77 validFrom(DateTime validFrom) {
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

  public Hotel77 validTo(DateTime validTo) {
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

  public Hotel77 imageUrl(String imageUrl) {
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

  @ApiModelProperty(value = "")
  public String getImageUrl2() {
    return imageUrl2;
  }

  public void setImageUrl2(String imageUrl2) {
    this.imageUrl2 = imageUrl2;
  }

  public Hotel77 cities(List<CitySimple> cities) {
    this.cities = cities;
    return this;
  }

  public Hotel77 addCitiesItem(CitySimple citiesItem) {
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
    Hotel77 hotel77 = (Hotel77) o;
    return Objects.equals(this.isDeleted, hotel77.isDeleted) &&
        Objects.equals(this.hotelId, hotel77.hotelId) &&
        Objects.equals(this.hotelCod, hotel77.hotelCod) &&
        Objects.equals(this.hotelName, hotel77.hotelName) &&
        Objects.equals(this.description, hotel77.description) &&
        Objects.equals(this.validFrom, hotel77.validFrom) &&
        Objects.equals(this.validTo, hotel77.validTo) &&
        Objects.equals(this.imageUrl, hotel77.imageUrl) &&
        Objects.equals(this.cities, hotel77.cities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDeleted, hotelId, hotelCod, hotelName, description, validFrom, validTo, imageUrl, cities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Hotel77 {\n");
    
    sb.append("    isDeleted: ").append(toIndentedString(isDeleted)).append("\n");
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

