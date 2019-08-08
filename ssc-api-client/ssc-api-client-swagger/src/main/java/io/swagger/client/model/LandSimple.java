package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * LandSimple
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class LandSimple {
  @SerializedName("land_id")
  private Integer landId = null;

  @SerializedName("land_url")
  private String landUrl = null;

  public LandSimple landId(Integer landId) {
    this.landId = landId;
    return this;
  }

   /**
   * Get landId
   * @return landId
  **/
  @ApiModelProperty(value = "")
  public Integer getLandId() {
    return landId;
  }

  public void setLandId(Integer landId) {
    this.landId = landId;
  }

  public LandSimple landUrl(String landUrl) {
    this.landUrl = landUrl;
    return this;
  }

   /**
   * Get landUrl
   * @return landUrl
  **/
  @ApiModelProperty(value = "")
  public String getLandUrl() {
    return landUrl;
  }

  public void setLandUrl(String landUrl) {
    this.landUrl = landUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LandSimple landSimple = (LandSimple) o;
    return Objects.equals(this.landId, landSimple.landId) &&
        Objects.equals(this.landUrl, landSimple.landUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(landId, landUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LandSimple {\n");
    
    sb.append("    landId: ").append(toIndentedString(landId)).append("\n");
    sb.append("    landUrl: ").append(toIndentedString(landUrl)).append("\n");
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

