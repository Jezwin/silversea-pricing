package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * AirPrice
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class AirPrice {
  @SerializedName("gateway_zone")
  private String gatewayZone = null;

  @SerializedName("currency_cod")
  private String currencyCod = null;

  @SerializedName("rt_economy_price_flag")
  private String rtEconomyPriceFlag = null;

  @SerializedName("rt_economy_price")
  private Integer rtEconomyPrice = null;

  @SerializedName("rt_economy_price_promo_flag")
  private String rtEconomyPricePromoFlag = null;

  @SerializedName("rt_economy_price_promo")
  private Integer rtEconomyPricePromo = null;

  @SerializedName("rt_business_price_flag")
  private String rtBusinessPriceFlag = null;

  @SerializedName("rt_business_price")
  private Integer rtBusinessPrice = null;

  @SerializedName("rt_business_price_promo_flag")
  private String rtBusinessPricePromoFlag = null;

  @SerializedName("rt_business_price_promo")
  private Integer rtBusinessPricePromo = null;

  public AirPrice gatewayZone(String gatewayZone) {
    this.gatewayZone = gatewayZone;
    return this;
  }

   /**
   * Get gatewayZone
   * @return gatewayZone
  **/
  @ApiModelProperty(value = "")
  public String getGatewayZone() {
    return gatewayZone;
  }

  public void setGatewayZone(String gatewayZone) {
    this.gatewayZone = gatewayZone;
  }

  public AirPrice currencyCod(String currencyCod) {
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

  public AirPrice rtEconomyPriceFlag(String rtEconomyPriceFlag) {
    this.rtEconomyPriceFlag = rtEconomyPriceFlag;
    return this;
  }

   /**
   * Get rtEconomyPriceFlag
   * @return rtEconomyPriceFlag
  **/
  @ApiModelProperty(value = "")
  public String getRtEconomyPriceFlag() {
    return rtEconomyPriceFlag;
  }

  public void setRtEconomyPriceFlag(String rtEconomyPriceFlag) {
    this.rtEconomyPriceFlag = rtEconomyPriceFlag;
  }

  public AirPrice rtEconomyPrice(Integer rtEconomyPrice) {
    this.rtEconomyPrice = rtEconomyPrice;
    return this;
  }

   /**
   * Get rtEconomyPrice
   * @return rtEconomyPrice
  **/
  @ApiModelProperty(value = "")
  public Integer getRtEconomyPrice() {
    return rtEconomyPrice;
  }

  public void setRtEconomyPrice(Integer rtEconomyPrice) {
    this.rtEconomyPrice = rtEconomyPrice;
  }

  public AirPrice rtEconomyPricePromoFlag(String rtEconomyPricePromoFlag) {
    this.rtEconomyPricePromoFlag = rtEconomyPricePromoFlag;
    return this;
  }

   /**
   * Get rtEconomyPricePromoFlag
   * @return rtEconomyPricePromoFlag
  **/
  @ApiModelProperty(value = "")
  public String getRtEconomyPricePromoFlag() {
    return rtEconomyPricePromoFlag;
  }

  public void setRtEconomyPricePromoFlag(String rtEconomyPricePromoFlag) {
    this.rtEconomyPricePromoFlag = rtEconomyPricePromoFlag;
  }

  public AirPrice rtEconomyPricePromo(Integer rtEconomyPricePromo) {
    this.rtEconomyPricePromo = rtEconomyPricePromo;
    return this;
  }

   /**
   * Get rtEconomyPricePromo
   * @return rtEconomyPricePromo
  **/
  @ApiModelProperty(value = "")
  public Integer getRtEconomyPricePromo() {
    return rtEconomyPricePromo;
  }

  public void setRtEconomyPricePromo(Integer rtEconomyPricePromo) {
    this.rtEconomyPricePromo = rtEconomyPricePromo;
  }

  public AirPrice rtBusinessPriceFlag(String rtBusinessPriceFlag) {
    this.rtBusinessPriceFlag = rtBusinessPriceFlag;
    return this;
  }

   /**
   * Get rtBusinessPriceFlag
   * @return rtBusinessPriceFlag
  **/
  @ApiModelProperty(value = "")
  public String getRtBusinessPriceFlag() {
    return rtBusinessPriceFlag;
  }

  public void setRtBusinessPriceFlag(String rtBusinessPriceFlag) {
    this.rtBusinessPriceFlag = rtBusinessPriceFlag;
  }

  public AirPrice rtBusinessPrice(Integer rtBusinessPrice) {
    this.rtBusinessPrice = rtBusinessPrice;
    return this;
  }

   /**
   * Get rtBusinessPrice
   * @return rtBusinessPrice
  **/
  @ApiModelProperty(value = "")
  public Integer getRtBusinessPrice() {
    return rtBusinessPrice;
  }

  public void setRtBusinessPrice(Integer rtBusinessPrice) {
    this.rtBusinessPrice = rtBusinessPrice;
  }

  public AirPrice rtBusinessPricePromoFlag(String rtBusinessPricePromoFlag) {
    this.rtBusinessPricePromoFlag = rtBusinessPricePromoFlag;
    return this;
  }

   /**
   * Get rtBusinessPricePromoFlag
   * @return rtBusinessPricePromoFlag
  **/
  @ApiModelProperty(value = "")
  public String getRtBusinessPricePromoFlag() {
    return rtBusinessPricePromoFlag;
  }

  public void setRtBusinessPricePromoFlag(String rtBusinessPricePromoFlag) {
    this.rtBusinessPricePromoFlag = rtBusinessPricePromoFlag;
  }

  public AirPrice rtBusinessPricePromo(Integer rtBusinessPricePromo) {
    this.rtBusinessPricePromo = rtBusinessPricePromo;
    return this;
  }

   /**
   * Get rtBusinessPricePromo
   * @return rtBusinessPricePromo
  **/
  @ApiModelProperty(value = "")
  public Integer getRtBusinessPricePromo() {
    return rtBusinessPricePromo;
  }

  public void setRtBusinessPricePromo(Integer rtBusinessPricePromo) {
    this.rtBusinessPricePromo = rtBusinessPricePromo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AirPrice airPrice = (AirPrice) o;
    return Objects.equals(this.gatewayZone, airPrice.gatewayZone) &&
        Objects.equals(this.currencyCod, airPrice.currencyCod) &&
        Objects.equals(this.rtEconomyPriceFlag, airPrice.rtEconomyPriceFlag) &&
        Objects.equals(this.rtEconomyPrice, airPrice.rtEconomyPrice) &&
        Objects.equals(this.rtEconomyPricePromoFlag, airPrice.rtEconomyPricePromoFlag) &&
        Objects.equals(this.rtEconomyPricePromo, airPrice.rtEconomyPricePromo) &&
        Objects.equals(this.rtBusinessPriceFlag, airPrice.rtBusinessPriceFlag) &&
        Objects.equals(this.rtBusinessPrice, airPrice.rtBusinessPrice) &&
        Objects.equals(this.rtBusinessPricePromoFlag, airPrice.rtBusinessPricePromoFlag) &&
        Objects.equals(this.rtBusinessPricePromo, airPrice.rtBusinessPricePromo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gatewayZone, currencyCod, rtEconomyPriceFlag, rtEconomyPrice, rtEconomyPricePromoFlag, rtEconomyPricePromo, rtBusinessPriceFlag, rtBusinessPrice, rtBusinessPricePromoFlag, rtBusinessPricePromo);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AirPrice {\n");
    
    sb.append("    gatewayZone: ").append(toIndentedString(gatewayZone)).append("\n");
    sb.append("    currencyCod: ").append(toIndentedString(currencyCod)).append("\n");
    sb.append("    rtEconomyPriceFlag: ").append(toIndentedString(rtEconomyPriceFlag)).append("\n");
    sb.append("    rtEconomyPrice: ").append(toIndentedString(rtEconomyPrice)).append("\n");
    sb.append("    rtEconomyPricePromoFlag: ").append(toIndentedString(rtEconomyPricePromoFlag)).append("\n");
    sb.append("    rtEconomyPricePromo: ").append(toIndentedString(rtEconomyPricePromo)).append("\n");
    sb.append("    rtBusinessPriceFlag: ").append(toIndentedString(rtBusinessPriceFlag)).append("\n");
    sb.append("    rtBusinessPrice: ").append(toIndentedString(rtBusinessPrice)).append("\n");
    sb.append("    rtBusinessPricePromoFlag: ").append(toIndentedString(rtBusinessPricePromoFlag)).append("\n");
    sb.append("    rtBusinessPricePromo: ").append(toIndentedString(rtBusinessPricePromo)).append("\n");
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

