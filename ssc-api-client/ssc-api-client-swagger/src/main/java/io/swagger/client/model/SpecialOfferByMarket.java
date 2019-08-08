package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * SpecialOfferByMarket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class SpecialOfferByMarket {
  @SerializedName("voyage_special_offer_id")
  private Integer voyageSpecialOfferId = null;

  @SerializedName("voyage_special_offer")
  private String voyageSpecialOffer = null;

  @SerializedName("valid_from")
  private DateTime validFrom = null;

  @SerializedName("valid_to")
  private DateTime validTo = null;

  @SerializedName("market_cod")
  private String marketCod = null;

  @SerializedName("countries")
  private List<String> countries = new ArrayList<String>();

  public SpecialOfferByMarket voyageSpecialOfferId(Integer voyageSpecialOfferId) {
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

  public SpecialOfferByMarket voyageSpecialOffer(String voyageSpecialOffer) {
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

  public SpecialOfferByMarket validFrom(DateTime validFrom) {
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

  public SpecialOfferByMarket validTo(DateTime validTo) {
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

  public SpecialOfferByMarket marketCod(String marketCod) {
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

  public SpecialOfferByMarket countries(List<String> countries) {
    this.countries = countries;
    return this;
  }

  public SpecialOfferByMarket addCountriesItem(String countriesItem) {
    this.countries.add(countriesItem);
    return this;
  }

   /**
   * Get countries
   * @return countries
  **/
  @ApiModelProperty(value = "")
  public List<String> getCountries() {
    return countries;
  }

  public void setCountries(List<String> countries) {
    this.countries = countries;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpecialOfferByMarket specialOfferByMarket = (SpecialOfferByMarket) o;
    return Objects.equals(this.voyageSpecialOfferId, specialOfferByMarket.voyageSpecialOfferId) &&
        Objects.equals(this.voyageSpecialOffer, specialOfferByMarket.voyageSpecialOffer) &&
        Objects.equals(this.validFrom, specialOfferByMarket.validFrom) &&
        Objects.equals(this.validTo, specialOfferByMarket.validTo) &&
        Objects.equals(this.marketCod, specialOfferByMarket.marketCod) &&
        Objects.equals(this.countries, specialOfferByMarket.countries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageSpecialOfferId, voyageSpecialOffer, validFrom, validTo, marketCod, countries);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpecialOfferByMarket {\n");
    
    sb.append("    voyageSpecialOfferId: ").append(toIndentedString(voyageSpecialOfferId)).append("\n");
    sb.append("    voyageSpecialOffer: ").append(toIndentedString(voyageSpecialOffer)).append("\n");
    sb.append("    validFrom: ").append(toIndentedString(validFrom)).append("\n");
    sb.append("    validTo: ").append(toIndentedString(validTo)).append("\n");
    sb.append("    marketCod: ").append(toIndentedString(marketCod)).append("\n");
    sb.append("    countries: ").append(toIndentedString(countries)).append("\n");
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

