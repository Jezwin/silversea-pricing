package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ShipSuite
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class ShipSuite {
  @SerializedName("ship_suite_category_id")
  private Integer shipSuiteCategoryId = null;

  @SerializedName("suite_category_id")
  private Integer suiteCategoryId = null;

  @SerializedName("ship_id")
  private Integer shipId = null;

  @SerializedName("suite_category_cod")
  private String suiteCategoryCod = null;

  @SerializedName("suite_category")
  private String suiteCategory = null;

  public ShipSuite shipSuiteCategoryId(Integer shipSuiteCategoryId) {
    this.shipSuiteCategoryId = shipSuiteCategoryId;
    return this;
  }

   /**
   * Get shipSuiteCategoryId
   * @return shipSuiteCategoryId
  **/
  @ApiModelProperty(value = "")
  public Integer getShipSuiteCategoryId() {
    return shipSuiteCategoryId;
  }

  public void setShipSuiteCategoryId(Integer shipSuiteCategoryId) {
    this.shipSuiteCategoryId = shipSuiteCategoryId;
  }

  public ShipSuite suiteCategoryId(Integer suiteCategoryId) {
    this.suiteCategoryId = suiteCategoryId;
    return this;
  }

   /**
   * Get suiteCategoryId
   * @return suiteCategoryId
  **/
  @ApiModelProperty(value = "")
  public Integer getSuiteCategoryId() {
    return suiteCategoryId;
  }

  public void setSuiteCategoryId(Integer suiteCategoryId) {
    this.suiteCategoryId = suiteCategoryId;
  }

  public ShipSuite shipId(Integer shipId) {
    this.shipId = shipId;
    return this;
  }

   /**
   * Get shipId
   * @return shipId
  **/
  @ApiModelProperty(value = "")
  public Integer getShipId() {
    return shipId;
  }

  public void setShipId(Integer shipId) {
    this.shipId = shipId;
  }

  public ShipSuite suiteCategoryCod(String suiteCategoryCod) {
    this.suiteCategoryCod = suiteCategoryCod;
    return this;
  }

   /**
   * Get suiteCategoryCod
   * @return suiteCategoryCod
  **/
  @ApiModelProperty(value = "")
  public String getSuiteCategoryCod() {
    return suiteCategoryCod;
  }

  public void setSuiteCategoryCod(String suiteCategoryCod) {
    this.suiteCategoryCod = suiteCategoryCod;
  }

  public ShipSuite suiteCategory(String suiteCategory) {
    this.suiteCategory = suiteCategory;
    return this;
  }

   /**
   * Get suiteCategory
   * @return suiteCategory
  **/
  @ApiModelProperty(value = "")
  public String getSuiteCategory() {
    return suiteCategory;
  }

  public void setSuiteCategory(String suiteCategory) {
    this.suiteCategory = suiteCategory;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShipSuite shipSuite = (ShipSuite) o;
    return Objects.equals(this.shipSuiteCategoryId, shipSuite.shipSuiteCategoryId) &&
        Objects.equals(this.suiteCategoryId, shipSuite.suiteCategoryId) &&
        Objects.equals(this.shipId, shipSuite.shipId) &&
        Objects.equals(this.suiteCategoryCod, shipSuite.suiteCategoryCod) &&
        Objects.equals(this.suiteCategory, shipSuite.suiteCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shipSuiteCategoryId, suiteCategoryId, shipId, suiteCategoryCod, suiteCategory);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShipSuite {\n");
    
    sb.append("    shipSuiteCategoryId: ").append(toIndentedString(shipSuiteCategoryId)).append("\n");
    sb.append("    suiteCategoryId: ").append(toIndentedString(suiteCategoryId)).append("\n");
    sb.append("    shipId: ").append(toIndentedString(shipId)).append("\n");
    sb.append("    suiteCategoryCod: ").append(toIndentedString(suiteCategoryCod)).append("\n");
    sb.append("    suiteCategory: ").append(toIndentedString(suiteCategory)).append("\n");
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

