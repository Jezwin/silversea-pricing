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
 * Land
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Land {
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

  public Land landId(Integer landId) {
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

  public Land landCod(String landCod) {
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

  public Land landName(String landName) {
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

  public Land description(String description) {
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

  public Land validFrom(DateTime validFrom) {
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

  public Land validTo(DateTime validTo) {
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

  public Land cities(List<CitySimple> cities) {
    this.cities = cities;
    return this;
  }

  public Land addCitiesItem(CitySimple citiesItem) {
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
    Land land = (Land) o;
    return Objects.equals(this.landId, land.landId) &&
        Objects.equals(this.landCod, land.landCod) &&
        Objects.equals(this.landName, land.landName) &&
        Objects.equals(this.description, land.description) &&
        Objects.equals(this.validFrom, land.validFrom) &&
        Objects.equals(this.validTo, land.validTo) &&
        Objects.equals(this.cities, land.cities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(landId, landCod, landName, description, validFrom, validTo, cities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Land {\n");
    
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

