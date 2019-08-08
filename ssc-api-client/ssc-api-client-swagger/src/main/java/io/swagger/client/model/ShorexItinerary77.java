package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

/**
 * ShorexItinerary77
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class ShorexItinerary77 {
  @SerializedName("is_deleted")
  private Boolean isDeleted = null;

  @SerializedName("symbols")
  private String symbols = null;

  @SerializedName("free_shorex_symbol")
  private String freeShorexSymbol = null;

  @SerializedName("shorex_itinerary_id")
  private Integer shorexItineraryId = null;

  @SerializedName("shorex_id")
  private Integer shorexId = null;

  @SerializedName("shorex_url")
  private String shorexUrl = null;

  @SerializedName("shorex_cod")
  private String shorexCod = null;

  @SerializedName("shorex_name")
  private String shorexName = null;

  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  @SerializedName("date")
  private DateTime date = null;

  @SerializedName("planned_departure_time")
  private String plannedDepartureTime = null;

  @SerializedName("general_departure_time")
  private String generalDepartureTime = null;

  @SerializedName("duration")
  private Float duration = null;

  public ShorexItinerary77 isDeleted(Boolean isDeleted) {
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

  public ShorexItinerary77 symbols(String symbols) {
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

  public ShorexItinerary77 freeShorexSymbol(String freeShorexSymbol) {
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

  public ShorexItinerary77 shorexItineraryId(Integer shorexItineraryId) {
    this.shorexItineraryId = shorexItineraryId;
    return this;
  }

   /**
   * Get shorexItineraryId
   * @return shorexItineraryId
  **/
  @ApiModelProperty(value = "")
  public Integer getShorexItineraryId() {
    return shorexItineraryId;
  }

  public void setShorexItineraryId(Integer shorexItineraryId) {
    this.shorexItineraryId = shorexItineraryId;
  }

  public ShorexItinerary77 shorexId(Integer shorexId) {
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

  public ShorexItinerary77 shorexUrl(String shorexUrl) {
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

  public ShorexItinerary77 shorexCod(String shorexCod) {
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

  public ShorexItinerary77 shorexName(String shorexName) {
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

  public ShorexItinerary77 cityId(Integer cityId) {
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

  public ShorexItinerary77 cityUrl(String cityUrl) {
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

  public ShorexItinerary77 voyageId(Integer voyageId) {
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

  public ShorexItinerary77 voyageUrl(String voyageUrl) {
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

  public ShorexItinerary77 date(DateTime date) {
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

  public ShorexItinerary77 plannedDepartureTime(String plannedDepartureTime) {
    this.plannedDepartureTime = plannedDepartureTime;
    return this;
  }

   /**
   * Get plannedDepartureTime
   * @return plannedDepartureTime
  **/
  @ApiModelProperty(value = "")
  public String getPlannedDepartureTime() {
    return plannedDepartureTime;
  }

  public void setPlannedDepartureTime(String plannedDepartureTime) {
    this.plannedDepartureTime = plannedDepartureTime;
  }

  public ShorexItinerary77 generalDepartureTime(String generalDepartureTime) {
    this.generalDepartureTime = generalDepartureTime;
    return this;
  }

   /**
   * Get generalDepartureTime
   * @return generalDepartureTime
  **/
  @ApiModelProperty(value = "")
  public String getGeneralDepartureTime() {
    return generalDepartureTime;
  }

  public void setGeneralDepartureTime(String generalDepartureTime) {
    this.generalDepartureTime = generalDepartureTime;
  }

  public ShorexItinerary77 duration(Float duration) {
    this.duration = duration;
    return this;
  }

   /**
   * Get duration
   * @return duration
  **/
  @ApiModelProperty(value = "")
  public Float getDuration() {
    return duration;
  }

  public void setDuration(Float duration) {
    this.duration = duration;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShorexItinerary77 shorexItinerary77 = (ShorexItinerary77) o;
    return Objects.equals(this.isDeleted, shorexItinerary77.isDeleted) &&
        Objects.equals(this.symbols, shorexItinerary77.symbols) &&
        Objects.equals(this.freeShorexSymbol, shorexItinerary77.freeShorexSymbol) &&
        Objects.equals(this.shorexItineraryId, shorexItinerary77.shorexItineraryId) &&
        Objects.equals(this.shorexId, shorexItinerary77.shorexId) &&
        Objects.equals(this.shorexUrl, shorexItinerary77.shorexUrl) &&
        Objects.equals(this.shorexCod, shorexItinerary77.shorexCod) &&
        Objects.equals(this.shorexName, shorexItinerary77.shorexName) &&
        Objects.equals(this.cityId, shorexItinerary77.cityId) &&
        Objects.equals(this.cityUrl, shorexItinerary77.cityUrl) &&
        Objects.equals(this.voyageId, shorexItinerary77.voyageId) &&
        Objects.equals(this.voyageUrl, shorexItinerary77.voyageUrl) &&
        Objects.equals(this.date, shorexItinerary77.date) &&
        Objects.equals(this.plannedDepartureTime, shorexItinerary77.plannedDepartureTime) &&
        Objects.equals(this.generalDepartureTime, shorexItinerary77.generalDepartureTime) &&
        Objects.equals(this.duration, shorexItinerary77.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDeleted, symbols, freeShorexSymbol, shorexItineraryId, shorexId, shorexUrl, shorexCod, shorexName, cityId, cityUrl, voyageId, voyageUrl, date, plannedDepartureTime, generalDepartureTime, duration);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShorexItinerary77 {\n");
    
    sb.append("    isDeleted: ").append(toIndentedString(isDeleted)).append("\n");
    sb.append("    symbols: ").append(toIndentedString(symbols)).append("\n");
    sb.append("    freeShorexSymbol: ").append(toIndentedString(freeShorexSymbol)).append("\n");
    sb.append("    shorexItineraryId: ").append(toIndentedString(shorexItineraryId)).append("\n");
    sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
    sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
    sb.append("    shorexCod: ").append(toIndentedString(shorexCod)).append("\n");
    sb.append("    shorexName: ").append(toIndentedString(shorexName)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    plannedDepartureTime: ").append(toIndentedString(plannedDepartureTime)).append("\n");
    sb.append("    generalDepartureTime: ").append(toIndentedString(generalDepartureTime)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
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

