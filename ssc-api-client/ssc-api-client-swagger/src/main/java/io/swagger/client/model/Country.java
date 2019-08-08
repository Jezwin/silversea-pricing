package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Country
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Country {
  @SerializedName("country_id")
  private Integer countryId = null;

  @SerializedName("country_url")
  private String countryUrl = null;

  @SerializedName("country_iso2")
  private String countryIso2 = null;

  @SerializedName("country_iso3")
  private String countryIso3 = null;

  @SerializedName("country_name")
  private String countryName = null;

  @SerializedName("country_prefix")
  private String countryPrefix = null;

  @SerializedName("market")
  private String market = null;

  @SerializedName("region")
  private String region = null;

  @SerializedName("region_id")
  private Integer regionId = null;

  public Country countryId(Integer countryId) {
    this.countryId = countryId;
    return this;
  }

   /**
   * Get countryId
   * @return countryId
  **/
  @ApiModelProperty(value = "")
  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public Country countryUrl(String countryUrl) {
    this.countryUrl = countryUrl;
    return this;
  }

   /**
   * Get countryUrl
   * @return countryUrl
  **/
  @ApiModelProperty(value = "")
  public String getCountryUrl() {
    return countryUrl;
  }

  public void setCountryUrl(String countryUrl) {
    this.countryUrl = countryUrl;
  }

  public Country countryIso2(String countryIso2) {
    this.countryIso2 = countryIso2;
    return this;
  }

   /**
   * Get countryIso2
   * @return countryIso2
  **/
  @ApiModelProperty(value = "")
  public String getCountryIso2() {
    return countryIso2;
  }

  public void setCountryIso2(String countryIso2) {
    this.countryIso2 = countryIso2;
  }

  public Country countryIso3(String countryIso3) {
    this.countryIso3 = countryIso3;
    return this;
  }

   /**
   * Get countryIso3
   * @return countryIso3
  **/
  @ApiModelProperty(value = "")
  public String getCountryIso3() {
    return countryIso3;
  }

  public void setCountryIso3(String countryIso3) {
    this.countryIso3 = countryIso3;
  }

  public Country countryName(String countryName) {
    this.countryName = countryName;
    return this;
  }

   /**
   * Get countryName
   * @return countryName
  **/
  @ApiModelProperty(value = "")
  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public Country countryPrefix(String countryPrefix) {
    this.countryPrefix = countryPrefix;
    return this;
  }

   /**
   * Get countryPrefix
   * @return countryPrefix
  **/
  @ApiModelProperty(value = "")
  public String getCountryPrefix() {
    return countryPrefix;
  }

  public void setCountryPrefix(String countryPrefix) {
    this.countryPrefix = countryPrefix;
  }

  public Country market(String market) {
    this.market = market;
    return this;
  }

   /**
   * Get market
   * @return market
  **/
  @ApiModelProperty(value = "")
  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public Country region(String region) {
    this.region = region;
    return this;
  }

   /**
   * Get region
   * @return region
  **/
  @ApiModelProperty(value = "")
  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Country regionId(Integer regionId) {
    this.regionId = regionId;
    return this;
  }

   /**
   * Get regionId
   * @return regionId
  **/
  @ApiModelProperty(value = "")
  public Integer getRegionId() {
    return regionId;
  }

  public void setRegionId(Integer regionId) {
    this.regionId = regionId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Country country = (Country) o;
    return Objects.equals(this.countryId, country.countryId) &&
        Objects.equals(this.countryUrl, country.countryUrl) &&
        Objects.equals(this.countryIso2, country.countryIso2) &&
        Objects.equals(this.countryIso3, country.countryIso3) &&
        Objects.equals(this.countryName, country.countryName) &&
        Objects.equals(this.countryPrefix, country.countryPrefix) &&
        Objects.equals(this.market, country.market) &&
        Objects.equals(this.region, country.region) &&
        Objects.equals(this.regionId, country.regionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryId, countryUrl, countryIso2, countryIso3, countryName, countryPrefix, market, region, regionId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Country {\n");
    
    sb.append("    countryId: ").append(toIndentedString(countryId)).append("\n");
    sb.append("    countryUrl: ").append(toIndentedString(countryUrl)).append("\n");
    sb.append("    countryIso2: ").append(toIndentedString(countryIso2)).append("\n");
    sb.append("    countryIso3: ").append(toIndentedString(countryIso3)).append("\n");
    sb.append("    countryName: ").append(toIndentedString(countryName)).append("\n");
    sb.append("    countryPrefix: ").append(toIndentedString(countryPrefix)).append("\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    regionId: ").append(toIndentedString(regionId)).append("\n");
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

