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
 * VoyagePriceMarket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class VoyagePriceMarket {
  @SerializedName("market_cod")
  private String marketCod = null;

  @SerializedName("currency_cod")
  private String currencyCod = null;

  @SerializedName("cruise_only_prices")
  private List<Price> cruiseOnlyPrices = new ArrayList<Price>();

  @SerializedName("air_prices")
  private List<AirPrice> airPrices = new ArrayList<AirPrice>();

  @SerializedName("priority_fare")
  private String priorityFare = null;

  public VoyagePriceMarket marketCod(String marketCod) {
    this.marketCod = marketCod;
    return this;
  }

   /**
   * Get marketCod
   * @return marketCod
  **/
  @ApiModelProperty(value = "")
  public String getMarketCod() {
    return marketCod;
  }

  public void setMarketCod(String marketCod) {
    this.marketCod = marketCod;
  }

  public VoyagePriceMarket currencyCod(String currencyCod) {
    this.currencyCod = currencyCod;
    return this;
  }

  /**
   * Get currencyCod
   * @return currencyCod
  **/
  @ApiModelProperty(value = "")
  public String getCurrencyCod() {
    return currencyCod;
  }

  public void setCurrencyCod(String currencyCod) {
    this.currencyCod = currencyCod;
  }

  public VoyagePriceMarket cruiseOnlyPrices(List<Price> cruiseOnlyPrices) {
    this.cruiseOnlyPrices = cruiseOnlyPrices;
    return this;
  }

  public VoyagePriceMarket addCruiseOnlyPricesItem(Price cruiseOnlyPricesItem) {
    this.cruiseOnlyPrices.add(cruiseOnlyPricesItem);
    return this;
  }

   /**
   * Get cruiseOnlyPrices
   * @return cruiseOnlyPrices
  **/
  @ApiModelProperty(value = "")
  public List<Price> getCruiseOnlyPrices() {
    return cruiseOnlyPrices;
  }

  public void setCruiseOnlyPrices(List<Price> cruiseOnlyPrices) {
    this.cruiseOnlyPrices = cruiseOnlyPrices;
  }

  public VoyagePriceMarket airPrices(List<AirPrice> airPrices) {
    this.airPrices = airPrices;
    return this;
  }

  public VoyagePriceMarket addAirPricesItem(AirPrice airPricesItem) {
    this.airPrices.add(airPricesItem);
    return this;
  }

   /**
   * Get airPrices
   * @return airPrices
  **/
  @ApiModelProperty(value = "")
  public List<AirPrice> getAirPrices() {
    return airPrices;
  }

  public void setAirPrices(List<AirPrice> airPrices) {
    this.airPrices = airPrices;
  }

  public String getPriorityFare() {
    return priorityFare;
  }

  public void setPriorityFare(String priorityFare) {
    this.priorityFare = priorityFare;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyagePriceMarket voyagePriceMarket = (VoyagePriceMarket) o;
    return Objects.equals(this.marketCod, voyagePriceMarket.marketCod) &&
        Objects.equals(this.currencyCod, voyagePriceMarket.currencyCod) &&
        Objects.equals(this.cruiseOnlyPrices, voyagePriceMarket.cruiseOnlyPrices) &&
        Objects.equals(this.priorityFare, voyagePriceMarket.priorityFare) &&
        Objects.equals(this.airPrices, voyagePriceMarket.airPrices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(marketCod, currencyCod, cruiseOnlyPrices, airPrices,priorityFare);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyagePriceMarket {\n");
    
    sb.append("    marketCod: ").append(toIndentedString(marketCod)).append("\n");
    sb.append("    currencyCod: ").append(toIndentedString(currencyCod)).append("\n");
    sb.append("    cruiseOnlyPrices: ").append(toIndentedString(cruiseOnlyPrices)).append("\n");
    sb.append("    airPrices: ").append(toIndentedString(airPrices)).append("\n");
    sb.append("    priorityFare: ").append(toIndentedString(priorityFare)).append("\n");
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

