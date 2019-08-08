package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Brochure
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Brochure {
  @SerializedName("brochure_id")
  private Integer brochureId = null;

  @SerializedName("brochure_url")
  private String brochureUrl = null;

  @SerializedName("brochure_cod")
  private String brochureCod = null;

  @SerializedName("title")
  private String title = null;

  @SerializedName("subtitle")
  private String subtitle = null;

  @SerializedName("language_cod")
  private String languageCod = null;

  @SerializedName("brochure_type")
  private String brochureType = null;

  @SerializedName("digital_only")
  private Boolean digitalOnly = null;

  @SerializedName("countries")
  private List<String> countries = new ArrayList<String>();

  public Brochure brochureId(Integer brochureId) {
    this.brochureId = brochureId;
    return this;
  }

   /**
   * Get brochureId
   * @return brochureId
  **/
  @ApiModelProperty(value = "")
  public Integer getBrochureId() {
    return brochureId;
  }

  public void setBrochureId(Integer brochureId) {
    this.brochureId = brochureId;
  }

  public Brochure brochureUrl(String brochureUrl) {
    this.brochureUrl = brochureUrl;
    return this;
  }

   /**
   * Get brochureUrl
   * @return brochureUrl
  **/
  @ApiModelProperty(value = "")
  public String getBrochureUrl() {
    return brochureUrl;
  }

  public void setBrochureUrl(String brochureUrl) {
    this.brochureUrl = brochureUrl;
  }

  public Brochure brochureCod(String brochureCod) {
    this.brochureCod = brochureCod;
    return this;
  }

   /**
   * Get brochureCod
   * @return brochureCod
  **/
  @ApiModelProperty(value = "")
  public String getBrochureCod() {
    return brochureCod;
  }

  public void setBrochureCod(String brochureCod) {
    this.brochureCod = brochureCod;
  }

  public Brochure title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @ApiModelProperty(value = "")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Brochure subtitle(String subtitle) {
    this.subtitle = subtitle;
    return this;
  }

   /**
   * Get subtitle
   * @return subtitle
  **/
  @ApiModelProperty(value = "")
  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public Brochure languageCod(String languageCod) {
    this.languageCod = languageCod;
    return this;
  }

   /**
   * Get languageCod
   * @return languageCod
  **/
  @ApiModelProperty(value = "")
  public String getLanguageCod() {
    return languageCod;
  }

  public void setLanguageCod(String languageCod) {
    this.languageCod = languageCod;
  }

  public Brochure brochureType(String brochureType) {
    this.brochureType = brochureType;
    return this;
  }

   /**
   * Get brochureType
   * @return brochureType
  **/
  @ApiModelProperty(value = "")
  public String getBrochureType() {
    return brochureType;
  }

  public void setBrochureType(String brochureType) {
    this.brochureType = brochureType;
  }

  public Brochure digitalOnly(Boolean digitalOnly) {
    this.digitalOnly = digitalOnly;
    return this;
  }

   /**
   * Get digitalOnly
   * @return digitalOnly
  **/
  @ApiModelProperty(value = "")
  public Boolean getDigitalOnly() {
    return digitalOnly;
  }

  public void setDigitalOnly(Boolean digitalOnly) {
    this.digitalOnly = digitalOnly;
  }

  public Brochure countries(List<String> countries) {
    this.countries = countries;
    return this;
  }

  public Brochure addCountriesItem(String countriesItem) {
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
    Brochure brochure = (Brochure) o;
    return Objects.equals(this.brochureId, brochure.brochureId) &&
        Objects.equals(this.brochureUrl, brochure.brochureUrl) &&
        Objects.equals(this.brochureCod, brochure.brochureCod) &&
        Objects.equals(this.title, brochure.title) &&
        Objects.equals(this.subtitle, brochure.subtitle) &&
        Objects.equals(this.languageCod, brochure.languageCod) &&
        Objects.equals(this.brochureType, brochure.brochureType) &&
        Objects.equals(this.digitalOnly, brochure.digitalOnly) &&
        Objects.equals(this.countries, brochure.countries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brochureId, brochureUrl, brochureCod, title, subtitle, languageCod, brochureType, digitalOnly, countries);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Brochure {\n");
    
    sb.append("    brochureId: ").append(toIndentedString(brochureId)).append("\n");
    sb.append("    brochureUrl: ").append(toIndentedString(brochureUrl)).append("\n");
    sb.append("    brochureCod: ").append(toIndentedString(brochureCod)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    subtitle: ").append(toIndentedString(subtitle)).append("\n");
    sb.append("    languageCod: ").append(toIndentedString(languageCod)).append("\n");
    sb.append("    brochureType: ").append(toIndentedString(brochureType)).append("\n");
    sb.append("    digitalOnly: ").append(toIndentedString(digitalOnly)).append("\n");
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

