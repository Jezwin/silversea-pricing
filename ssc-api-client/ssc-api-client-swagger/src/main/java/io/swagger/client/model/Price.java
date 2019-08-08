package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Price
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Price {
  @SerializedName("suite_category_url")
  private String suiteCategoryUrl = null;

  @SerializedName("suite_category_cod")
  private String suiteCategoryCod = null;

  @SerializedName("suite_category")
  private String suiteCategory = null;

  @SerializedName("currency_cod")
  private String currencyCod = null;

  @SerializedName("cruise_only_fare")
  private Integer cruiseOnlyFare = null;

  @SerializedName("bundle_fare")
  private Integer bundleFare = null;

  @SerializedName("early_booking_bonus")
  private Integer earlyBookingBonus = null;

  @SerializedName("suite_availability")
  private String suiteAvailability = null;

  @SerializedName("single_supplement_perc")
  private Integer singleSupplementPerc = null;

  @SerializedName("has_vs_saving")
  private String hasVsSaving = null;

  @SerializedName("vs_saving")
  private String vsSaving = null;

  public Price suiteCategoryUrl(String suiteCategoryUrl) {
    this.suiteCategoryUrl = suiteCategoryUrl;
    return this;
  }

   /**
   * Get suiteCategoryUrl
   * @return suiteCategoryUrl
  **/
  @ApiModelProperty(value = "")
  public String getSuiteCategoryUrl() {
    return suiteCategoryUrl;
  }

  public void setSuiteCategoryUrl(String suiteCategoryUrl) {
    this.suiteCategoryUrl = suiteCategoryUrl;
  }

  public Price suiteCategoryCod(String suiteCategoryCod) {
    this.suiteCategoryCod = suiteCategoryCod;
    return this;
  }

   /**
   * Get suiteCategoryCod
   * @return suiteCategoryCod
  **/
  @ApiModelProperty(value = "")
  public String getSuiteCategoryCod() {
    return suiteCategoryCod;
  }

  public void setSuiteCategoryCod(String suiteCategoryCod) {
    this.suiteCategoryCod = suiteCategoryCod;
  }

  public Price suiteCategory(String suiteCategory) {
    this.suiteCategory = suiteCategory;
    return this;
  }

   /**
   * Get suiteCategory
   * @return suiteCategory
  **/
  @ApiModelProperty(value = "")
  public String getSuiteCategory() {
    return suiteCategory;
  }

  public void setSuiteCategory(String suiteCategory) {
    this.suiteCategory = suiteCategory;
  }

  public Price currencyCod(String currencyCod) {
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

  public Price cruiseOnlyFare(Integer cruiseOnlyFare) {
    this.cruiseOnlyFare = cruiseOnlyFare;
    return this;
  }

   /**
   * Get cruiseOnlyFare
   * @return cruiseOnlyFare
  **/
  @ApiModelProperty(value = "")
  public Integer getCruiseOnlyFare() {
    return cruiseOnlyFare;
  }

  public void setCruiseOnlyFare(Integer cruiseOnlyFare) {
    this.cruiseOnlyFare = cruiseOnlyFare;
  }

  public Price bundleFare(Integer bundleFare) {
    this.bundleFare = bundleFare;
    return this;
  }

   /**
   * Get bundleFare
   * @return bundleFare
  **/
  @ApiModelProperty(value = "")
  public Integer getBundleFare() {
    return bundleFare;
  }

  public void setBundleFare(Integer bundleFare) {
    this.bundleFare = bundleFare;
  }

  public Price earlyBookingBonus(Integer earlyBookingBonus) {
    this.earlyBookingBonus = earlyBookingBonus;
    return this;
  }

   /**
   * Get earlyBookingBonus
   * @return earlyBookingBonus
  **/
  @ApiModelProperty(value = "")
  public Integer getEarlyBookingBonus() {
    return earlyBookingBonus;
  }

  public void setEarlyBookingBonus(Integer earlyBookingBonus) {
    this.earlyBookingBonus = earlyBookingBonus;
  }

  public Price suiteAvailability(String suiteAvailability) {
    this.suiteAvailability = suiteAvailability;
    return this;
  }

   /**
   * Get suiteAvailability
   * @return suiteAvailability
  **/
  @ApiModelProperty(value = "")
  public String getSuiteAvailability() {
    return suiteAvailability;
  }

  public void setSuiteAvailability(String suiteAvailability) {
    this.suiteAvailability = suiteAvailability;
  }

  public Price singleSupplementPerc(Integer singleSupplementPerc) {
    this.singleSupplementPerc = singleSupplementPerc;
    return this;
  }

   /**
   * Get singleSupplementPerc
   * @return singleSupplementPerc
  **/
  @ApiModelProperty(value = "")
  public Integer getSingleSupplementPerc() {
    return singleSupplementPerc;
  }

  public void setSingleSupplementPerc(Integer singleSupplementPerc) {
    this.singleSupplementPerc = singleSupplementPerc;
  }

  public Price hasVsSaving(String hasVsSaving) {
    this.hasVsSaving = hasVsSaving;
    return this;
  }

   /**
   * Get hasVsSaving
   * @return hasVsSaving
  **/
  @ApiModelProperty(value = "")
  public String getHasVsSaving() {
    return hasVsSaving;
  }

  public void setHasVsSaving(String hasVsSaving) {
    this.hasVsSaving = hasVsSaving;
  }

  public Price vsSaving(String vsSaving) {
    this.vsSaving = vsSaving;
    return this;
  }

   /**
   * Get vsSaving
   * @return vsSaving
  **/
  @ApiModelProperty(value = "")
  public String getVsSaving() {
    return vsSaving;
  }

  public void setVsSaving(String vsSaving) {
    this.vsSaving = vsSaving;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Price price = (Price) o;
    return Objects.equals(this.suiteCategoryUrl, price.suiteCategoryUrl) &&
        Objects.equals(this.suiteCategoryCod, price.suiteCategoryCod) &&
        Objects.equals(this.suiteCategory, price.suiteCategory) &&
        Objects.equals(this.currencyCod, price.currencyCod) &&
        Objects.equals(this.cruiseOnlyFare, price.cruiseOnlyFare) &&
        Objects.equals(this.bundleFare, price.bundleFare) &&
        Objects.equals(this.earlyBookingBonus, price.earlyBookingBonus) &&
        Objects.equals(this.suiteAvailability, price.suiteAvailability) &&
        Objects.equals(this.singleSupplementPerc, price.singleSupplementPerc) &&
        Objects.equals(this.hasVsSaving, price.hasVsSaving) &&
        Objects.equals(this.vsSaving, price.vsSaving);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suiteCategoryUrl, suiteCategoryCod, suiteCategory, currencyCod, cruiseOnlyFare, bundleFare, earlyBookingBonus, suiteAvailability, singleSupplementPerc, hasVsSaving, vsSaving);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Price {\n");
    
    sb.append("    suiteCategoryUrl: ").append(toIndentedString(suiteCategoryUrl)).append("\n");
    sb.append("    suiteCategoryCod: ").append(toIndentedString(suiteCategoryCod)).append("\n");
    sb.append("    suiteCategory: ").append(toIndentedString(suiteCategory)).append("\n");
    sb.append("    currencyCod: ").append(toIndentedString(currencyCod)).append("\n");
    sb.append("    cruiseOnlyFare: ").append(toIndentedString(cruiseOnlyFare)).append("\n");
    sb.append("    bundleFare: ").append(toIndentedString(bundleFare)).append("\n");
    sb.append("    earlyBookingBonus: ").append(toIndentedString(earlyBookingBonus)).append("\n");
    sb.append("    suiteAvailability: ").append(toIndentedString(suiteAvailability)).append("\n");
    sb.append("    singleSupplementPerc: ").append(toIndentedString(singleSupplementPerc)).append("\n");
    sb.append("    hasVsSaving: ").append(toIndentedString(hasVsSaving)).append("\n");
    sb.append("    vsSaving: ").append(toIndentedString(vsSaving)).append("\n");
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

