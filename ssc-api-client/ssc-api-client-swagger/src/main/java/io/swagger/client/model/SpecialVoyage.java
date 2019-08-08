package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageWithItinerary;
import java.util.ArrayList;
import java.util.List;

/**
 * SpecialVoyage
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class SpecialVoyage {
  @SerializedName("special_voyage_id")
  private String specialVoyageId = null;

  @SerializedName("special_voyage_name")
  private String specialVoyageName = null;

  @SerializedName("voyages")
  private List<VoyageWithItinerary> voyages = new ArrayList<VoyageWithItinerary>();

  @SerializedName("prices")
  private List<VoyagePriceMarket> prices = new ArrayList<VoyagePriceMarket>();

  public SpecialVoyage specialVoyageId(String specialVoyageId) {
    this.specialVoyageId = specialVoyageId;
    return this;
  }

   /**
   * Get specialVoyageId
   * @return specialVoyageId
  **/
  @ApiModelProperty(value = "")
  public String getSpecialVoyageId() {
    return specialVoyageId;
  }

  public void setSpecialVoyageId(String specialVoyageId) {
    this.specialVoyageId = specialVoyageId;
  }

  public SpecialVoyage specialVoyageName(String specialVoyageName) {
    this.specialVoyageName = specialVoyageName;
    return this;
  }

   /**
   * Get specialVoyageName
   * @return specialVoyageName
  **/
  @ApiModelProperty(value = "")
  public String getSpecialVoyageName() {
    return specialVoyageName;
  }

  public void setSpecialVoyageName(String specialVoyageName) {
    this.specialVoyageName = specialVoyageName;
  }

  public SpecialVoyage voyages(List<VoyageWithItinerary> voyages) {
    this.voyages = voyages;
    return this;
  }

  public SpecialVoyage addVoyagesItem(VoyageWithItinerary voyagesItem) {
    this.voyages.add(voyagesItem);
    return this;
  }

   /**
   * Get voyages
   * @return voyages
  **/
  @ApiModelProperty(value = "")
  public List<VoyageWithItinerary> getVoyages() {
    return voyages;
  }

  public void setVoyages(List<VoyageWithItinerary> voyages) {
    this.voyages = voyages;
  }

  public SpecialVoyage prices(List<VoyagePriceMarket> prices) {
    this.prices = prices;
    return this;
  }

  public SpecialVoyage addPricesItem(VoyagePriceMarket pricesItem) {
    this.prices.add(pricesItem);
    return this;
  }

   /**
   * Get prices
   * @return prices
  **/
  @ApiModelProperty(value = "")
  public List<VoyagePriceMarket> getPrices() {
    return prices;
  }

  public void setPrices(List<VoyagePriceMarket> prices) {
    this.prices = prices;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpecialVoyage specialVoyage = (SpecialVoyage) o;
    return Objects.equals(this.specialVoyageId, specialVoyage.specialVoyageId) &&
        Objects.equals(this.specialVoyageName, specialVoyage.specialVoyageName) &&
        Objects.equals(this.voyages, specialVoyage.voyages) &&
        Objects.equals(this.prices, specialVoyage.prices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(specialVoyageId, specialVoyageName, voyages, prices);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpecialVoyage {\n");
    
    sb.append("    specialVoyageId: ").append(toIndentedString(specialVoyageId)).append("\n");
    sb.append("    specialVoyageName: ").append(toIndentedString(specialVoyageName)).append("\n");
    sb.append("    voyages: ").append(toIndentedString(voyages)).append("\n");
    sb.append("    prices: ").append(toIndentedString(prices)).append("\n");
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

