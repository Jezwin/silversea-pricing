package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Suite
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Suite {
  @SerializedName("suite_category_cod")
  private String suiteCategoryCod = null;

  @SerializedName("suite_number")
  private String suiteNumber = null;

  @SerializedName("max_occupancy")
  private Integer maxOccupancy = null;

  public Suite suiteCategoryCod(String suiteCategoryCod) {
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

  public Suite suiteNumber(String suiteNumber) {
    this.suiteNumber = suiteNumber;
    return this;
  }

   /**
   * Get suiteNumber
   * @return suiteNumber
  **/
  @ApiModelProperty(value = "")
  public String getSuiteNumber() {
    return suiteNumber;
  }

  public void setSuiteNumber(String suiteNumber) {
    this.suiteNumber = suiteNumber;
  }

  public Suite maxOccupancy(Integer maxOccupancy) {
    this.maxOccupancy = maxOccupancy;
    return this;
  }

   /**
   * Get maxOccupancy
   * @return maxOccupancy
  **/
  @ApiModelProperty(value = "")
  public Integer getMaxOccupancy() {
    return maxOccupancy;
  }

  public void setMaxOccupancy(Integer maxOccupancy) {
    this.maxOccupancy = maxOccupancy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Suite suite = (Suite) o;
    return Objects.equals(this.suiteCategoryCod, suite.suiteCategoryCod) &&
        Objects.equals(this.suiteNumber, suite.suiteNumber) &&
        Objects.equals(this.maxOccupancy, suite.maxOccupancy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suiteCategoryCod, suiteNumber, maxOccupancy);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Suite {\n");
    
    sb.append("    suiteCategoryCod: ").append(toIndentedString(suiteCategoryCod)).append("\n");
    sb.append("    suiteNumber: ").append(toIndentedString(suiteNumber)).append("\n");
    sb.append("    maxOccupancy: ").append(toIndentedString(maxOccupancy)).append("\n");
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

