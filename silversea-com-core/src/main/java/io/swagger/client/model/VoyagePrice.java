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
import io.swagger.client.model.AirPrice;
import io.swagger.client.model.Price;
import java.util.ArrayList;
import java.util.List;

/**
 * VoyagePrice
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-01-22T13:04:26.738Z")
public class VoyagePrice {
  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  @SerializedName("cruise_only_prices")
  private List<Price> cruiseOnlyPrices = new ArrayList<Price>();

  @SerializedName("air_prices")
  private List<AirPrice> airPrices = new ArrayList<AirPrice>();

  public VoyagePrice voyageId(Integer voyageId) {
    this.voyageId = voyageId;
    return this;
  }

   /**
   * Get voyageId
   * @return voyageId
  **/
  @ApiModelProperty(example = "null", value = "")
  public Integer getVoyageId() {
    return voyageId;
  }

  public void setVoyageId(Integer voyageId) {
    this.voyageId = voyageId;
  }

  public VoyagePrice voyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
    return this;
  }

   /**
   * Get voyageCod
   * @return voyageCod
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getVoyageCod() {
    return voyageCod;
  }

  public void setVoyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
  }

  public VoyagePrice voyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
    return this;
  }

   /**
   * Get voyageUrl
   * @return voyageUrl
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getVoyageUrl() {
    return voyageUrl;
  }

  public void setVoyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
  }

  public VoyagePrice cruiseOnlyPrices(List<Price> cruiseOnlyPrices) {
    this.cruiseOnlyPrices = cruiseOnlyPrices;
    return this;
  }

  public VoyagePrice addCruiseOnlyPricesItem(Price cruiseOnlyPricesItem) {
    this.cruiseOnlyPrices.add(cruiseOnlyPricesItem);
    return this;
  }

   /**
   * Get cruiseOnlyPrices
   * @return cruiseOnlyPrices
  **/
  @ApiModelProperty(example = "null", value = "")
  public List<Price> getCruiseOnlyPrices() {
    return cruiseOnlyPrices;
  }

  public void setCruiseOnlyPrices(List<Price> cruiseOnlyPrices) {
    this.cruiseOnlyPrices = cruiseOnlyPrices;
  }

  public VoyagePrice airPrices(List<AirPrice> airPrices) {
    this.airPrices = airPrices;
    return this;
  }

  public VoyagePrice addAirPricesItem(AirPrice airPricesItem) {
    this.airPrices.add(airPricesItem);
    return this;
  }

   /**
   * Get airPrices
   * @return airPrices
  **/
  @ApiModelProperty(example = "null", value = "")
  public List<AirPrice> getAirPrices() {
    return airPrices;
  }

  public void setAirPrices(List<AirPrice> airPrices) {
    this.airPrices = airPrices;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyagePrice voyagePrice = (VoyagePrice) o;
    return Objects.equals(this.voyageId, voyagePrice.voyageId) &&
        Objects.equals(this.voyageCod, voyagePrice.voyageCod) &&
        Objects.equals(this.voyageUrl, voyagePrice.voyageUrl) &&
        Objects.equals(this.cruiseOnlyPrices, voyagePrice.cruiseOnlyPrices) &&
        Objects.equals(this.airPrices, voyagePrice.airPrices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageId, voyageCod, voyageUrl, cruiseOnlyPrices, airPrices);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyagePrice {\n");
    
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    cruiseOnlyPrices: ").append(toIndentedString(cruiseOnlyPrices)).append("\n");
    sb.append("    airPrices: ").append(toIndentedString(airPrices)).append("\n");
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

