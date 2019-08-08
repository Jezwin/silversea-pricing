package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * VoyageSimple
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class VoyageSimple {
  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("voyage_url")
  private String voyageUrl = null;

  public VoyageSimple voyageId(Integer voyageId) {
    this.voyageId = voyageId;
    return this;
  }

   /**
   * Get voyageId
   * @return voyageId
  **/
  @ApiModelProperty(value = "")
  public Integer getVoyageId() {
    return voyageId;
  }

  public void setVoyageId(Integer voyageId) {
    this.voyageId = voyageId;
  }

  public VoyageSimple voyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
    return this;
  }

   /**
   * Get voyageCod
   * @return voyageCod
  **/
  @ApiModelProperty(value = "")
  public String getVoyageCod() {
    return voyageCod;
  }

  public void setVoyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
  }

  public VoyageSimple voyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
    return this;
  }

   /**
   * Get voyageUrl
   * @return voyageUrl
  **/
  @ApiModelProperty(value = "")
  public String getVoyageUrl() {
    return voyageUrl;
  }

  public void setVoyageUrl(String voyageUrl) {
    this.voyageUrl = voyageUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyageSimple voyageSimple = (VoyageSimple) o;
    return Objects.equals(this.voyageId, voyageSimple.voyageId) &&
        Objects.equals(this.voyageCod, voyageSimple.voyageCod) &&
        Objects.equals(this.voyageUrl, voyageSimple.voyageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageId, voyageCod, voyageUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyageSimple {\n");
    
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    voyageUrl: ").append(toIndentedString(voyageUrl)).append("\n");
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

