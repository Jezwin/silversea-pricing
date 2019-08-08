package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Image
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Image {
  @SerializedName("image_id")
  private Integer imageId = null;

  @SerializedName("image_url")
  private String imageUrl = null;

  @SerializedName("image_alt")
  private String imageAlt = null;

  public Image imageId(Integer imageId) {
    this.imageId = imageId;
    return this;
  }

   /**
   * Get imageId
   * @return imageId
  **/
  @ApiModelProperty(value = "")
  public Integer getImageId() {
    return imageId;
  }

  public void setImageId(Integer imageId) {
    this.imageId = imageId;
  }

  public Image imageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

   /**
   * Get imageUrl
   * @return imageUrl
  **/
  @ApiModelProperty(value = "")
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Image imageAlt(String imageAlt) {
    this.imageAlt = imageAlt;
    return this;
  }

   /**
   * Get imageAlt
   * @return imageAlt
  **/
  @ApiModelProperty(value = "")
  public String getImageAlt() {
    return imageAlt;
  }

  public void setImageAlt(String imageAlt) {
    this.imageAlt = imageAlt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Image image = (Image) o;
    return Objects.equals(this.imageId, image.imageId) &&
        Objects.equals(this.imageUrl, image.imageUrl) &&
        Objects.equals(this.imageAlt, image.imageAlt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(imageId, imageUrl, imageAlt);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Image {\n");
    
    sb.append("    imageId: ").append(toIndentedString(imageId)).append("\n");
    sb.append("    imageUrl: ").append(toIndentedString(imageUrl)).append("\n");
    sb.append("    imageAlt: ").append(toIndentedString(imageAlt)).append("\n");
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

