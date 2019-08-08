package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.HotelSimple;
import io.swagger.client.model.LandSimple;
import io.swagger.client.model.ShorexSimple;
import io.swagger.client.model.VoyageSimple;
import java.util.ArrayList;
import java.util.List;

/**
 * City
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class City {
  @SerializedName("pic_url")
  private String picUrl = null;
  
  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  @SerializedName("city_cod")
  private String cityCod = null;

  @SerializedName("wings_cod")
  private String wingsCod = null;

  @SerializedName("city_name")
  private String cityName = null;

  @SerializedName("latitude")
  private String latitude = null;

  @SerializedName("longitude")
  private String longitude = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("short_description")
  private String shortDescription = null;

  @SerializedName("country_id")
  private Integer countryId = null;

  @SerializedName("country_url")
  private String countryUrl = null;

  @SerializedName("country_iso3")
  private String countryIso3 = null;

  @SerializedName("country_iso2")
  private String countryIso2 = null;

  @SerializedName("voyages")
  private List<VoyageSimple> voyages = new ArrayList<VoyageSimple>();

  @SerializedName("shore_excursions")
  private List<ShorexSimple> shoreExcursions = new ArrayList<ShorexSimple>();

  @SerializedName("land_adventures")
  private List<LandSimple> landAdventures = new ArrayList<LandSimple>();

  @SerializedName("hotels")
  private List<HotelSimple> hotels = new ArrayList<HotelSimple>();
  
  public City picUrl(String picUrl) {
	this.picUrl = picUrl;
	return this;
  }
  
  /**
  * Get picUrl
  * @return picUrl
  **/
  @ApiModelProperty(value = "")
  public String getPicUrl() {
	return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  public City cityId(Integer cityId) {
    this.cityId = cityId;
    return this;
  }

   /**
   * Get cityId
   * @return cityId
  **/
  @ApiModelProperty(value = "")
  public Integer getCityId() {
    return cityId;
  }

  public void setCityId(Integer cityId) {
    this.cityId = cityId;
  }

  public City cityUrl(String cityUrl) {
    this.cityUrl = cityUrl;
    return this;
  }

   /**
   * Get cityUrl
   * @return cityUrl
  **/
  @ApiModelProperty(value = "")
  public String getCityUrl() {
    return cityUrl;
  }

  public void setCityUrl(String cityUrl) {
    this.cityUrl = cityUrl;
  }

  public City cityCod(String cityCod) {
    this.cityCod = cityCod;
    return this;
  }

   /**
   * Get cityCod
   * @return cityCod
  **/
  @ApiModelProperty(value = "")
  public String getCityCod() {
    return cityCod;
  }

  public void setCityCod(String cityCod) {
    this.cityCod = cityCod;
  }

  public City wingsCod(String wingsCod) {
    this.wingsCod = wingsCod;
    return this;
  }

   /**
   * Get wingsCod
   * @return wingsCod
  **/
  @ApiModelProperty(value = "")
  public String getWingsCod() {
    return wingsCod;
  }

  public void setWingsCod(String wingsCod) {
    this.wingsCod = wingsCod;
  }

  public City cityName(String cityName) {
    this.cityName = cityName;
    return this;
  }

   /**
   * Get cityName
   * @return cityName
  **/
  @ApiModelProperty(value = "")
  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public City latitude(String latitude) {
    this.latitude = latitude;
    return this;
  }

   /**
   * Get latitude
   * @return latitude
  **/
  @ApiModelProperty(value = "")
  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public City longitude(String longitude) {
    this.longitude = longitude;
    return this;
  }

   /**
   * Get longitude
   * @return longitude
  **/
  @ApiModelProperty(value = "")
  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public City description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public City shortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
    return this;
  }

   /**
   * Get shortDescription
   * @return shortDescription
  **/
  @ApiModelProperty(value = "")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public City countryId(Integer countryId) {
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

  public City countryUrl(String countryUrl) {
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

  public City countryIso3(String countryIso3) {
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

  public City countryIso2(String countryIso2) {
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

  public City voyages(List<VoyageSimple> voyages) {
    this.voyages = voyages;
    return this;
  }

  public City addVoyagesItem(VoyageSimple voyagesItem) {
    this.voyages.add(voyagesItem);
    return this;
  }

   /**
   * Get voyages
   * @return voyages
  **/
  @ApiModelProperty(value = "")
  public List<VoyageSimple> getVoyages() {
    return voyages;
  }

  public void setVoyages(List<VoyageSimple> voyages) {
    this.voyages = voyages;
  }

  public City shoreExcursions(List<ShorexSimple> shoreExcursions) {
    this.shoreExcursions = shoreExcursions;
    return this;
  }

  public City addShoreExcursionsItem(ShorexSimple shoreExcursionsItem) {
    this.shoreExcursions.add(shoreExcursionsItem);
    return this;
  }

   /**
   * Get shoreExcursions
   * @return shoreExcursions
  **/
  @ApiModelProperty(value = "")
  public List<ShorexSimple> getShoreExcursions() {
    return shoreExcursions;
  }

  public void setShoreExcursions(List<ShorexSimple> shoreExcursions) {
    this.shoreExcursions = shoreExcursions;
  }

  public City landAdventures(List<LandSimple> landAdventures) {
    this.landAdventures = landAdventures;
    return this;
  }

  public City addLandAdventuresItem(LandSimple landAdventuresItem) {
    this.landAdventures.add(landAdventuresItem);
    return this;
  }

   /**
   * Get landAdventures
   * @return landAdventures
  **/
  @ApiModelProperty(value = "")
  public List<LandSimple> getLandAdventures() {
    return landAdventures;
  }

  public void setLandAdventures(List<LandSimple> landAdventures) {
    this.landAdventures = landAdventures;
  }

  public City hotels(List<HotelSimple> hotels) {
    this.hotels = hotels;
    return this;
  }

  public City addHotelsItem(HotelSimple hotelsItem) {
    this.hotels.add(hotelsItem);
    return this;
  }

   /**
   * Get hotels
   * @return hotels
  **/
  @ApiModelProperty(value = "")
  public List<HotelSimple> getHotels() {
    return hotels;
  }

  public void setHotels(List<HotelSimple> hotels) {
    this.hotels = hotels;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    City city = (City) o;
    return Objects.equals(this.picUrl, city.picUrl)  &&
    	Objects.equals(this.cityId, city.cityId) &&
        Objects.equals(this.cityUrl, city.cityUrl) &&
        Objects.equals(this.cityCod, city.cityCod) &&
        Objects.equals(this.wingsCod, city.wingsCod) &&
        Objects.equals(this.cityName, city.cityName) &&
        Objects.equals(this.latitude, city.latitude) &&
        Objects.equals(this.longitude, city.longitude) &&
        Objects.equals(this.description, city.description) &&
        Objects.equals(this.shortDescription, city.shortDescription) &&
        Objects.equals(this.countryId, city.countryId) &&
        Objects.equals(this.countryUrl, city.countryUrl) &&
        Objects.equals(this.countryIso3, city.countryIso3) &&
        Objects.equals(this.countryIso2, city.countryIso2) &&
        Objects.equals(this.voyages, city.voyages) &&
        Objects.equals(this.shoreExcursions, city.shoreExcursions) &&
        Objects.equals(this.landAdventures, city.landAdventures) &&
        Objects.equals(this.hotels, city.hotels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(picUrl, cityId, cityUrl, cityCod, wingsCod, cityName, latitude, longitude, description, shortDescription, countryId, countryUrl, countryIso3, countryIso2, voyages, shoreExcursions, landAdventures, hotels);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class City {\n");
    
    sb.append("    picUrl: ").append(toIndentedString(picUrl)).append("\n");
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
    sb.append("    cityCod: ").append(toIndentedString(cityCod)).append("\n");
    sb.append("    wingsCod: ").append(toIndentedString(wingsCod)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
    sb.append("    countryId: ").append(toIndentedString(countryId)).append("\n");
    sb.append("    countryUrl: ").append(toIndentedString(countryUrl)).append("\n");
    sb.append("    countryIso3: ").append(toIndentedString(countryIso3)).append("\n");
    sb.append("    countryIso2: ").append(toIndentedString(countryIso2)).append("\n");
    sb.append("    voyages: ").append(toIndentedString(voyages)).append("\n");
    sb.append("    shoreExcursions: ").append(toIndentedString(shoreExcursions)).append("\n");
    sb.append("    landAdventures: ").append(toIndentedString(landAdventures)).append("\n");
    sb.append("    hotels: ").append(toIndentedString(hotels)).append("\n");
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

