package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Destination
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Destination {
  @SerializedName("destination_id")
  private Integer destinationId = null;

  @SerializedName("destination_name")
  private String destinationName = null;

  @SerializedName("destination_group")
  private String destinationGroup = null;

  @SerializedName("destination_url")
  private String destinationUrl = null;

  public Destination destinationId(Integer destinationId) {
    this.destinationId = destinationId;
    return this;
  }

   /**
   * Get destinationId
   * @return destinationId
  **/
  @ApiModelProperty(value = "")
  public Integer getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(Integer destinationId) {
    this.destinationId = destinationId;
  }

  public Destination destinationName(String destinationName) {
    this.destinationName = destinationName;
    return this;
  }

   /**
   * Get destinationName
   * @return destinationName
  **/
  @ApiModelProperty(value = "")
  public String getDestinationName() {
    return destinationName;
  }

  public void setDestinationName(String destinationName) {
    this.destinationName = destinationName;
  }

  public Destination destinationGroup(String destinationGroup) {
    this.destinationGroup = destinationGroup;
    return this;
  }

   /**
   * Get destinationGroup
   * @return destinationGroup
  **/
  @ApiModelProperty(value = "")
  public String getDestinationGroup() {
    return destinationGroup;
  }

  public void setDestinationGroup(String destinationGroup) {
    this.destinationGroup = destinationGroup;
  }

  public Destination destinationUrl(String destinationUrl) {
    this.destinationUrl = destinationUrl;
    return this;
  }

   /**
   * Get destinationUrl
   * @return destinationUrl
  **/
  @ApiModelProperty(value = "")
  public String getDestinationUrl() {
    return destinationUrl;
  }

  public void setDestinationUrl(String destinationUrl) {
    this.destinationUrl = destinationUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Destination destination = (Destination) o;
    return Objects.equals(this.destinationId, destination.destinationId) &&
        Objects.equals(this.destinationName, destination.destinationName) &&
        Objects.equals(this.destinationGroup, destination.destinationGroup) &&
        Objects.equals(this.destinationUrl, destination.destinationUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(destinationId, destinationName, destinationGroup, destinationUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Destination {\n");
    
    sb.append("    destinationId: ").append(toIndentedString(destinationId)).append("\n");
    sb.append("    destinationName: ").append(toIndentedString(destinationName)).append("\n");
    sb.append("    destinationGroup: ").append(toIndentedString(destinationGroup)).append("\n");
    sb.append("    destinationUrl: ").append(toIndentedString(destinationUrl)).append("\n");
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

