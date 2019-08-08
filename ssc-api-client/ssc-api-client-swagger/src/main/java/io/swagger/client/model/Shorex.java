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
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Shorex {
  @SerializedName("shorex_id")
  private Integer shorexId = null;

  @SerializedName("shorex_url")
  private String shorexUrl = null;

  @SerializedName("symbols")
  private String symbols = null;

  @SerializedName("free_shorex_symbol")
  private String freeShorexSymbol = null;

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
  
  @SerializedName("note")
  private String note = null;

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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
  public String getShorexUrl() {
    return shorexUrl;
  }

  public void setShorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
  }

  public Shorex symbols(String symbols) {
    this.symbols = symbols;
    return this;
  }

   /**
   * Get symbols
   * @return symbols
  **/
  @ApiModelProperty(value = "")
  public String getSymbols() {
    return symbols;
  }

  public void setSymbols(String symbols) {
    this.symbols = symbols;
  }

  public Shorex freeShorexSymbol(String freeShorexSymbol) {
    this.freeShorexSymbol = freeShorexSymbol;
    return this;
  }

   /**
   * Get freeShorexSymbol
   * @return freeShorexSymbol
  **/
  @ApiModelProperty(value = "")
  public String getFreeShorexSymbol() {
    return freeShorexSymbol;
  }

  public void setFreeShorexSymbol(String freeShorexSymbol) {
    this.freeShorexSymbol = freeShorexSymbol;
  }

  public Shorex shorexCod(String shorexCod) {
    this.shorexCod = shorexCod;
    return this;
  }

   /**
   * Get shorexCod
   * @return shorexCod
  **/
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
  public List<CitySimple> getCities() {
    return cities;
  }

  public void setCities(List<CitySimple> cities) {
    this.cities = cities;
  }
  
  public Shorex note(String note) {
	    this.note = note;
	    return this;
	  }

	  @ApiModelProperty(value = "")
	  public String getNote() {
	    return note;
	  }

	  public void setNOte(String note) {
	    this.note = note;
	  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Shorex shorex = (Shorex) o;
    return Objects.equals(this.shorexId, shorex.shorexId) &&
        Objects.equals(this.shorexUrl, shorex.shorexUrl) &&
        Objects.equals(this.symbols, shorex.symbols) &&
        Objects.equals(this.freeShorexSymbol, shorex.freeShorexSymbol) &&
        Objects.equals(this.shorexCod, shorex.shorexCod) &&
        Objects.equals(this.shorexName, shorex.shorexName) &&
        Objects.equals(this.pointsOfInterests, shorex.pointsOfInterests) &&
        Objects.equals(this.shortDescription, shorex.shortDescription) &&
        Objects.equals(this.description, shorex.description) &&
        Objects.equals(this.cities, shorex.cities) &&
        Objects.equals(this.note, shorex.note);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shorexId, shorexUrl, symbols, freeShorexSymbol, shorexCod, shorexName, pointsOfInterests, shortDescription, description, cities, note);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Shorex {\n");
    
    sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
    sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
    sb.append("    symbols: ").append(toIndentedString(symbols)).append("\n");
    sb.append("    freeShorexSymbol: ").append(toIndentedString(freeShorexSymbol)).append("\n");
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

