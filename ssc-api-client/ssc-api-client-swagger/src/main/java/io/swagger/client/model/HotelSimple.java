package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * HotelSimple
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class HotelSimple {
  @SerializedName("hotel_id")
  private Integer hotelId = null;

  @SerializedName("hotel_url")
  private String hotelUrl = null;

  public HotelSimple hotelId(Integer hotelId) {
    this.hotelId = hotelId;
    return this;
  }

   /**
   * Get hotelId
   * @return hotelId
  **/
  @ApiModelProperty(value = "")
  public Integer getHotelId() {
    return hotelId;
  }

  public void setHotelId(Integer hotelId) {
    this.hotelId = hotelId;
  }

  public HotelSimple hotelUrl(String hotelUrl) {
    this.hotelUrl = hotelUrl;
    return this;
  }

   /**
   * Get hotelUrl
   * @return hotelUrl
  **/
  @ApiModelProperty(value = "")
  public String getHotelUrl() {
    return hotelUrl;
  }

  public void setHotelUrl(String hotelUrl) {
    this.hotelUrl = hotelUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelSimple hotelSimple = (HotelSimple) o;
    return Objects.equals(this.hotelId, hotelSimple.hotelId) &&
        Objects.equals(this.hotelUrl, hotelSimple.hotelUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, hotelUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelSimple {\n");
    
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelUrl: ").append(toIndentedString(hotelUrl)).append("\n");
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

