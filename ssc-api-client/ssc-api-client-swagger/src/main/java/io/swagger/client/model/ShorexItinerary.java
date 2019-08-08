package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

/**
 * ShorexItinerary
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class ShorexItinerary {
  @SerializedName("shorex_itinerary_id")
  private Integer shorexItineraryId = null;

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

  public ShorexItinerary shorexItineraryId(Integer shorexItineraryId) {
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

  public ShorexItinerary shorexId(Integer shorexId) {
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

  public ShorexItinerary shorexUrl(String shorexUrl) {
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

  public ShorexItinerary symbols(String symbols) {
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

  public ShorexItinerary freeShorexSymbol(String freeShorexSymbol) {
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

  public ShorexItinerary shorexCod(String shorexCod) {
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

  public ShorexItinerary shorexName(String shorexName) {
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

  public ShorexItinerary cityId(Integer cityId) {
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

  public ShorexItinerary cityUrl(String cityUrl) {
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

  public ShorexItinerary voyageId(Integer voyageId) {
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

  public ShorexItinerary voyageUrl(String voyageUrl) {
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

  public ShorexItinerary date(DateTime date) {
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

  public ShorexItinerary plannedDepartureTime(String plannedDepartureTime) {
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

  public ShorexItinerary generalDepartureTime(String generalDepartureTime) {
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

  public ShorexItinerary duration(Float duration) {
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
    ShorexItinerary shorexItinerary = (ShorexItinerary) o;
    return Objects.equals(this.shorexItineraryId, shorexItinerary.shorexItineraryId) &&
        Objects.equals(this.shorexId, shorexItinerary.shorexId) &&
        Objects.equals(this.shorexUrl, shorexItinerary.shorexUrl) &&
        Objects.equals(this.symbols, shorexItinerary.symbols) &&
        Objects.equals(this.freeShorexSymbol, shorexItinerary.freeShorexSymbol) &&
        Objects.equals(this.shorexCod, shorexItinerary.shorexCod) &&
        Objects.equals(this.shorexName, shorexItinerary.shorexName) &&
        Objects.equals(this.cityId, shorexItinerary.cityId) &&
        Objects.equals(this.cityUrl, shorexItinerary.cityUrl) &&
        Objects.equals(this.voyageId, shorexItinerary.voyageId) &&
        Objects.equals(this.voyageUrl, shorexItinerary.voyageUrl) &&
        Objects.equals(this.date, shorexItinerary.date) &&
        Objects.equals(this.plannedDepartureTime, shorexItinerary.plannedDepartureTime) &&
        Objects.equals(this.generalDepartureTime, shorexItinerary.generalDepartureTime) &&
        Objects.equals(this.duration, shorexItinerary.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shorexItineraryId, shorexId, shorexUrl, symbols, freeShorexSymbol, shorexCod, shorexName, cityId, cityUrl, voyageId, voyageUrl, date, plannedDepartureTime, generalDepartureTime, duration);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShorexItinerary {\n");
    
    sb.append("    shorexItineraryId: ").append(toIndentedString(shorexItineraryId)).append("\n");
    sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
    sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
    sb.append("    symbols: ").append(toIndentedString(symbols)).append("\n");
    sb.append("    freeShorexSymbol: ").append(toIndentedString(freeShorexSymbol)).append("\n");
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

