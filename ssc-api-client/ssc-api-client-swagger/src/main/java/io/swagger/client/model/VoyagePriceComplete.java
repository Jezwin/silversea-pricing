package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.VoyagePriceMarket;
import java.util.ArrayList;
import java.util.List;

/**
 * VoyagePriceComplete
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class VoyagePriceComplete {
  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;
  
  @SerializedName("best_price_available")
  private boolean bestPriceAvailable;

  @SerializedName("market_currency")
  private List<VoyagePriceMarket> marketCurrency = new ArrayList<VoyagePriceMarket>();

  public VoyagePriceComplete voyageId(Integer voyageId) {
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

  public VoyagePriceComplete voyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
    return this;
  }

   /**
   * Get voyageCod
   * @return voyageCod
  **/
  @ApiModelProperty(value = "")
  public String getVoyageCod() {
    return voyageCod;
  }

  public void setVoyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
  }

  public VoyagePriceComplete voyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
    return this;
  }
  
  /**
   * Get bestPriceAvailable
   * @return bestPriceAvailable
  **/
  @ApiModelProperty(value = "")
  public boolean getBestPriceAvailable() {
    return bestPriceAvailable;
  }

  public void setBestPriceAvailable(boolean bestPriceAvailable) {
    this.bestPriceAvailable = bestPriceAvailable;
  }

  public VoyagePriceComplete bestPriceAvailable(boolean bestPriceAvailable) {
    this.bestPriceAvailable = bestPriceAvailable;
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

  public VoyagePriceComplete marketCurrency(List<VoyagePriceMarket> marketCurrency) {
    this.marketCurrency = marketCurrency;
    return this;
  }

  public VoyagePriceComplete addMarketCurrencyItem(VoyagePriceMarket marketCurrencyItem) {
    this.marketCurrency.add(marketCurrencyItem);
    return this;
  }

   /**
   * Get marketCurrency
   * @return marketCurrency
  **/
  @ApiModelProperty(value = "")
  public List<VoyagePriceMarket> getMarketCurrency() {
    return marketCurrency;
  }

  public void setMarketCurrency(List<VoyagePriceMarket> marketCurrency) {
    this.marketCurrency = marketCurrency;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyagePriceComplete voyagePriceComplete = (VoyagePriceComplete) o;
    return Objects.equals(this.voyageId, voyagePriceComplete.voyageId) &&
        Objects.equals(this.voyageCod, voyagePriceComplete.voyageCod) &&
        Objects.equals(this.voyageUrl, voyagePriceComplete.voyageUrl) &&
        Objects.equals(this.bestPriceAvailable, voyagePriceComplete.bestPriceAvailable) &&
        Objects.equals(this.marketCurrency, voyagePriceComplete.marketCurrency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageId, voyageCod, voyageUrl, marketCurrency);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyagePriceComplete {\n");
    
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
    sb.append("    bestPriceAvailable: ").append(toIndentedString(bestPriceAvailable)).append("\n");
    sb.append("    marketCurrency: ").append(toIndentedString(marketCurrency)).append("\n");
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

