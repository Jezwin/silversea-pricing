/*
 * APIv3
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.CitySimple;
import java.util.ArrayList;
import java.util.List;

/**
 * Shorex
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-01-22T13:04:26.738Z")
public class Shorex {
  @SerializedName("shorex_id")
  private Integer shorexId = null;

  @SerializedName("shorex_url")
  private String shorexUrl = null;

  @SerializedName("shorex_cod")
  private String shorexCod = null;

  @SerializedName("shorex_name")
  private String shorexName = null;

  @SerializedName("points_of_interests")
  private String pointsOfInterests = null;

  @SerializedName("short_description")
  private String shortDescription = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("cities")
  private List<CitySimple> cities = new ArrayList<CitySimple>();

  public Shorex shorexId(Integer shorexId) {
    this.shorexId = shorexId;
    return this;
  }

   /**
   * Get shorexId
   * @return shorexId
  **/
  @ApiModelProperty(example = "null", value = "")
  public Integer getShorexId() {
    return shorexId;
  }

  public void setShorexId(Integer shorexId) {
    this.shorexId = shorexId;
  }

  public Shorex shorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
    return this;
  }

   /**
   * Get shorexUrl
   * @return shorexUrl
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShorexUrl() {
    return shorexUrl;
  }

  public void setShorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
  }

  public Shorex shorexCod(String shorexCod) {
    this.shorexCod = shorexCod;
    return this;
  }

   /**
   * Get shorexCod
   * @return shorexCod
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShorexCod() {
    return shorexCod;
  }

  public void setShorexCod(String shorexCod) {
    this.shorexCod = shorexCod;
  }

  public Shorex shorexName(String shorexName) {
    this.shorexName = shorexName;
    return this;
  }

   /**
   * Get shorexName
   * @return shorexName
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShorexName() {
    return shorexName;
  }

  public void setShorexName(String shorexName) {
    this.shorexName = shorexName;
  }

  public Shorex pointsOfInterests(String pointsOfInterests) {
    this.pointsOfInterests = pointsOfInterests;
    return this;
  }

   /**
   * Get pointsOfInterests
   * @return pointsOfInterests
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getPointsOfInterests() {
    return pointsOfInterests;
  }

  public void setPointsOfInterests(String pointsOfInterests) {
    this.pointsOfInterests = pointsOfInterests;
  }

  public Shorex shortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
    return this;
  }

   /**
   * Get shortDescription
   * @return shortDescription
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public Shorex description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Shorex cities(List<CitySimple> cities) {
    this.cities = cities;
    return this;
  }

  public Shorex addCitiesItem(CitySimple citiesItem) {
    this.cities.add(citiesItem);
    return this;
  }

   /**
   * Get cities
   * @return cities
  **/
  @ApiModelProperty(example = "null", value = "")
  public List<CitySimple> getCities() {
    return cities;
  }

  public void setCities(List<CitySimple> cities) {
    this.cities = cities;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Shorex shorex = (Shorex) o;
    return Objects.equals(this.shorexId, shorex.shorexId) &&
        Objects.equals(this.shorexUrl, shorex.shorexUrl) &&
        Objects.equals(this.shorexCod, shorex.shorexCod) &&
        Objects.equals(this.shorexName, shorex.shorexName) &&
        Objects.equals(this.pointsOfInterests, shorex.pointsOfInterests) &&
        Objects.equals(this.shortDescription, shorex.shortDescription) &&
        Objects.equals(this.description, shorex.description) &&
        Objects.equals(this.cities, shorex.cities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shorexId, shorexUrl, shorexCod, shorexName, pointsOfInterests, shortDescription, description, cities);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Shorex {\n");
    
    sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
    sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
    sb.append("    shorexCod: ").append(toIndentedString(shorexCod)).append("\n");
    sb.append("    shorexName: ").append(toIndentedString(shorexName)).append("\n");
    sb.append("    pointsOfInterests: ").append(toIndentedString(pointsOfInterests)).append("\n");
    sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    cities: ").append(toIndentedString(cities)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

