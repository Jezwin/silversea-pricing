/*
 * APIv3
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ShorexSimple
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-01-22T13:04:26.738Z")
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
  @ApiModelProperty(example = "null", value = "")
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
  @ApiModelProperty(example = "null", value = "")
  public String getShorexUrl() {
    return shorexUrl;
  }

  public void setShorexUrl(String shorexUrl) {
    this.shorexUrl = shorexUrl;
  }


  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

