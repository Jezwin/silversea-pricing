package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Agency
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Agency {
  @SerializedName("agency_id")
  private Integer agencyId = null;

  @SerializedName("agency")
  private String agency = null;

  @SerializedName("address")
  private String address = null;

  @SerializedName("city")
  private String city = null;

  @SerializedName("zip")
  private String zip = null;

  @SerializedName("zip4")
  private String zip4 = null;

  @SerializedName("country_iso3")
  private String countryIso3 = null;

  @SerializedName("state_cod")
  private String stateCod = null;

  @SerializedName("county")
  private String county = null;

  @SerializedName("phone")
  private String phone = null;

  @SerializedName("lat")
  private Double lat = null;

  @SerializedName("lon")
  private Double lon = null;

  public Agency agencyId(Integer agencyId) {
    this.agencyId = agencyId;
    return this;
  }

   /**
   * Get agencyId
   * @return agencyId
  **/
  @ApiModelProperty(value = "")
  public Integer getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(Integer agencyId) {
    this.agencyId = agencyId;
  }

  public Agency agency(String agency) {
    this.agency = agency;
    return this;
  }

   /**
   * Get agency
   * @return agency
  **/
  @ApiModelProperty(value = "")
  public String getAgency() {
    return agency;
  }

  public void setAgency(String agency) {
    this.agency = agency;
  }

  public Agency address(String address) {
    this.address = address;
    return this;
  }

   /**
   * Get address
   * @return address
  **/
  @ApiModelProperty(value = "")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Agency city(String city) {
    this.city = city;
    return this;
  }

   /**
   * Get city
   * @return city
  **/
  @ApiModelProperty(value = "")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Agency zip(String zip) {
    this.zip = zip;
    return this;
  }

   /**
   * Get zip
   * @return zip
  **/
  @ApiModelProperty(value = "")
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public Agency zip4(String zip4) {
    this.zip4 = zip4;
    return this;
  }

   /**
   * Get zip4
   * @return zip4
  **/
  @ApiModelProperty(value = "")
  public String getZip4() {
    return zip4;
  }

  public void setZip4(String zip4) {
    this.zip4 = zip4;
  }

  public Agency countryIso3(String countryIso3) {
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

  public Agency stateCod(String stateCod) {
    this.stateCod = stateCod;
    return this;
  }

   /**
   * Get stateCod
   * @return stateCod
  **/
  @ApiModelProperty(value = "")
  public String getStateCod() {
    return stateCod;
  }

  public void setStateCod(String stateCod) {
    this.stateCod = stateCod;
  }

  public Agency county(String county) {
    this.county = county;
    return this;
  }

   /**
   * Get county
   * @return county
  **/
  @ApiModelProperty(value = "")
  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public Agency phone(String phone) {
    this.phone = phone;
    return this;
  }

   /**
   * Get phone
   * @return phone
  **/
  @ApiModelProperty(value = "")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Agency lat(Double lat) {
    this.lat = lat;
    return this;
  }

   /**
   * Get lat
   * @return lat
  **/
  @ApiModelProperty(value = "")
  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Agency lon(Double lon) {
    this.lon = lon;
    return this;
  }

   /**
   * Get lon
   * @return lon
  **/
  @ApiModelProperty(value = "")
  public Double getLon() {
    return lon;
  }

  public void setLon(Double lon) {
    this.lon = lon;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Agency agency = (Agency) o;
    return Objects.equals(this.agencyId, agency.agencyId) &&
        Objects.equals(this.agency, agency.agency) &&
        Objects.equals(this.address, agency.address) &&
        Objects.equals(this.city, agency.city) &&
        Objects.equals(this.zip, agency.zip) &&
        Objects.equals(this.zip4, agency.zip4) &&
        Objects.equals(this.countryIso3, agency.countryIso3) &&
        Objects.equals(this.stateCod, agency.stateCod) &&
        Objects.equals(this.county, agency.county) &&
        Objects.equals(this.phone, agency.phone) &&
        Objects.equals(this.lat, agency.lat) &&
        Objects.equals(this.lon, agency.lon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agencyId, agency, address, city, zip, zip4, countryIso3, stateCod, county, phone, lat, lon);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Agency {\n");
    
    sb.append("    agencyId: ").append(toIndentedString(agencyId)).append("\n");
    sb.append("    agency: ").append(toIndentedString(agency)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
    sb.append("    zip4: ").append(toIndentedString(zip4)).append("\n");
    sb.append("    countryIso3: ").append(toIndentedString(countryIso3)).append("\n");
    sb.append("    stateCod: ").append(toIndentedString(stateCod)).append("\n");
    sb.append("    county: ").append(toIndentedString(county)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    lat: ").append(toIndentedString(lat)).append("\n");
    sb.append("    lon: ").append(toIndentedString(lon)).append("\n");
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

