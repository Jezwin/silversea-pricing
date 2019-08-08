package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ShorexSimple
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class ShorexSimple {
  @SerializedName("shorex_id")
  private Integer shorexId = null;

  @SerializedName("shorex_url")
  private String shorexUrl = null;

  public ShorexSimple shorexId(Integer shorexId) {
    this.shorexId = shorexId;
    return this;
  }

   /**
   * Get shorexId
   * @return shorexId
  **/
  @ApiModelProperty(value = "")
  public Integer getShorexId() {
    return shorexId;
  }

  public void setShorexId(Integer shorexId) {
    this.shorexId = shorexId;
  }

  public ShorexSimple shorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
    return this;
  }

   /**
   * Get shorexUrl
   * @return shorexUrl
  **/
  @ApiModelProperty(value = "")
  public String getShorexUrl() {
    return shorexUrl;
  }

  public void setShorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShorexSimple shorexSimple = (ShorexSimple) o;
    return Objects.equals(this.shorexId, shorexSimple.shorexId) &&
        Objects.equals(this.shorexUrl, shorexSimple.shorexUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shorexId, shorexUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShorexSimple {\n");
    
    sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
    sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
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

