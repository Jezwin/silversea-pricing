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
 * Land77
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Land77 {
  @SerializedName("is_deleted")
  private Boolean isDeleted = null;

  @SerializedName("land_id")
  private Integer landId = null;

  @SerializedName("land_cod")
  private String landCod = null;

  @SerializedName("land_name")
  private String landName = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("valid_from")
  private DateTime validFrom = null;

  @SerializedName("valid_to")
  private DateTime validTo = null;

  @SerializedName("cities")
  private List<CitySimple> cities = new ArrayList<CitySimple>();

  @SerializedName("image_url")
  private String imageUrl = null;

  @SerializedName("image_url2")
  private String imageUrl2 = null;

  @SerializedName("image_url3")
  private String imageUrl3 = null;


  @SerializedName("image_url4")
  private String imageUrl4 = null;

  @SerializedName("image_url5")
  private String imageUrl5 = null;


  @SerializedName("image_url6")
  private String imageUrl6 = null;

  public Land77 isDeleted(Boolean isDeleted) {
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

  public Land77 landId(Integer landId) {
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

  public Land77 landCod(String landCod) {
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

  public Land77 landName(String landName) {
    this.landName = landName;
    return this;
  }

   /**
   * Get landName
   * @return landName
  **/
  @ApiModelProperty(value = "")
  public String getLandName() {
    return landName;
  }

  public void setLandName(String landName) {
    this.landName = landName;
  }

  public Land77 description(String description) {
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

  public Land77 validFrom(DateTime validFrom) {
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

  public Land77 validTo(DateTime validTo) {
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

  public Land77 cities(List<CitySimple> cities) {
    this.cities = cities;
    return this;
  }

  public Land77 addCitiesItem(CitySimple citiesItem) {
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

  @ApiModelProperty(value = "")
  public String getImageUrl3() {
    return imageUrl3;
  }

  public void setImageUrl3(String imageUrl3) {
    this.imageUrl3 = imageUrl3;
  }

  @ApiModelProperty(value = "")
  public String getImageUrl4() {
    return imageUrl4;
  }

  public void setImageUrl4(String imageUrl4) {
    this.imageUrl4 = imageUrl4;
  }

  @ApiModelProperty(value = "")
  public String getImageUrl5() {
    return imageUrl5;
  }

  public void setImageUrl5(String imageUrl5) {
    this.imageUrl5 = imageUrl5;
  }

  @ApiModelProperty(value = "")
  public String getImageUrl6() {
    return imageUrl6;
  }

  public void setImageUrl6(String imageUrl6) {
    this.imageUrl6 = imageUrl6;
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
    Land77 land77 = (Land77) o;
    return Objects.equals(this.isDeleted, land77.isDeleted) &&
        Objects.equals(this.landId, land77.landId) &&
        Objects.equals(this.landCod, land77.landCod) &&
        Objects.equals(this.landName, land77.landName) &&
        Objects.equals(this.description, land77.description) &&
        Objects.equals(this.validFrom, land77.validFrom) &&
        Objects.equals(this.validTo, land77.validTo) &&
        Objects.equals(this.cities, land77.cities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDeleted, landId, landCod, landName, description, validFrom, validTo, cities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Land77 {\n");
    
    sb.append("    isDeleted: ").append(toIndentedString(isDeleted)).append("\n");
    sb.append("    landId: ").append(toIndentedString(landId)).append("\n");
    sb.append("    landCod: ").append(toIndentedString(landCod)).append("\n");
    sb.append("    landName: ").append(toIndentedString(landName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    validFrom: ").append(toIndentedString(validFrom)).append("\n");
    sb.append("    validTo: ").append(toIndentedString(validTo)).append("\n");
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

