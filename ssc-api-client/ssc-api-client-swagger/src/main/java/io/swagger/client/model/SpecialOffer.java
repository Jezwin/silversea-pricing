package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * SpecialOffer
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class SpecialOffer {
  @SerializedName("voyage_special_offer_id")
  private Integer voyageSpecialOfferId = null;

  @SerializedName("voyage_special_offer")
  private String voyageSpecialOffer = null;

  @SerializedName("valid_from")
  private DateTime validFrom = null;

  @SerializedName("valid_to")
  private DateTime validTo = null;

  @SerializedName("markets")
  private List<String> markets = new ArrayList<String>();

  public SpecialOffer voyageSpecialOfferId(Integer voyageSpecialOfferId) {
    this.voyageSpecialOfferId = voyageSpecialOfferId;
    return this;
  }

   /**
   * Get voyageSpecialOfferId
   * @return voyageSpecialOfferId
  **/
  @ApiModelProperty(value = "")
  public Integer getVoyageSpecialOfferId() {
    return voyageSpecialOfferId;
  }

  public void setVoyageSpecialOfferId(Integer voyageSpecialOfferId) {
    this.voyageSpecialOfferId = voyageSpecialOfferId;
  }

  public SpecialOffer voyageSpecialOffer(String voyageSpecialOffer) {
    this.voyageSpecialOffer = voyageSpecialOffer;
    return this;
  }

   /**
   * Get voyageSpecialOffer
   * @return voyageSpecialOffer
  **/
  @ApiModelProperty(value = "")
  public String getVoyageSpecialOffer() {
    return voyageSpecialOffer;
  }

  public void setVoyageSpecialOffer(String voyageSpecialOffer) {
    this.voyageSpecialOffer = voyageSpecialOffer;
  }

  public SpecialOffer validFrom(DateTime validFrom) {
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

  public SpecialOffer validTo(DateTime validTo) {
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

  public SpecialOffer markets(List<String> markets) {
    this.markets = markets;
    return this;
  }

  public SpecialOffer addMarketsItem(String marketsItem) {
    this.markets.add(marketsItem);
    return this;
  }

   /**
   * Get markets
   * @return markets
  **/
  @ApiModelProperty(value = "")
  public List<String> getMarkets() {
    return markets;
  }

  public void setMarkets(List<String> markets) {
    this.markets = markets;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpecialOffer specialOffer = (SpecialOffer) o;
    return Objects.equals(this.voyageSpecialOfferId, specialOffer.voyageSpecialOfferId) &&
        Objects.equals(this.voyageSpecialOffer, specialOffer.voyageSpecialOffer) &&
        Objects.equals(this.validFrom, specialOffer.validFrom) &&
        Objects.equals(this.validTo, specialOffer.validTo) &&
        Objects.equals(this.markets, specialOffer.markets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageSpecialOfferId, voyageSpecialOffer, validFrom, validTo, markets);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpecialOffer {\n");
    
    sb.append("    voyageSpecialOfferId: ").append(toIndentedString(voyageSpecialOfferId)).append("\n");
    sb.append("    voyageSpecialOffer: ").append(toIndentedString(voyageSpecialOffer)).append("\n");
    sb.append("    validFrom: ").append(toIndentedString(validFrom)).append("\n");
    sb.append("    validTo: ").append(toIndentedString(validTo)).append("\n");
    sb.append("    markets: ").append(toIndentedString(markets)).append("\n");
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

