package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Feature
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Feature {
  @SerializedName("feature_id")
  private Integer featureId = null;

  @SerializedName("feature_cod")
  private String featureCod = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("order")
  private Integer order = null;

  public Feature featureId(Integer featureId) {
    this.featureId = featureId;
    return this;
  }

   /**
   * Get featureId
   * @return featureId
  **/
  @ApiModelProperty(value = "")
  public Integer getFeatureId() {
    return featureId;
  }

  public void setFeatureId(Integer featureId) {
    this.featureId = featureId;
  }

  public Feature featureCod(String featureCod) {
    this.featureCod = featureCod;
    return this;
  }

   /**
   * Get featureCod
   * @return featureCod
  **/
  @ApiModelProperty(value = "")
  public String getFeatureCod() {
    return featureCod;
  }

  public void setFeatureCod(String featureCod) {
    this.featureCod = featureCod;
  }

  public Feature name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Feature order(Integer order) {
    this.order = order;
    return this;
  }

   /**
   * Get order
   * @return order
  **/
  @ApiModelProperty(value = "")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Feature feature = (Feature) o;
    return Objects.equals(this.featureId, feature.featureId) &&
        Objects.equals(this.featureCod, feature.featureCod) &&
        Objects.equals(this.name, feature.name) &&
        Objects.equals(this.order, feature.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(featureId, featureCod, name, order);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Feature {\n");
    
    sb.append("    featureId: ").append(toIndentedString(featureId)).append("\n");
    sb.append("    featureCod: ").append(toIndentedString(featureCod)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
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

