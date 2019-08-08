package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * CitySimple
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class CitySimple {
  @SerializedName("city_id")
  private Integer cityId = null;

  @SerializedName("city_url")
  private String cityUrl = null;

  public CitySimple cityId(Integer cityId) {
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

  public CitySimple cityUrl(String cityUrl) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CitySimple citySimple = (CitySimple) o;
    return Objects.equals(this.cityId, citySimple.cityId) &&
        Objects.equals(this.cityUrl, citySimple.cityUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cityId, cityUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CitySimple {\n");
    
    sb.append("    cityId: ").append(toIndentedString(cityId)).append("\n");
    sb.append("    cityUrl: ").append(toIndentedString(cityUrl)).append("\n");
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

