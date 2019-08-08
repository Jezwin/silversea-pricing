package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SuiteCategory
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class SuiteCategory {
  @SerializedName("suite_category_cod")
  private String suiteCategoryCod = null;

  @SerializedName("suite_category")
  private String suiteCategory = null;

  public SuiteCategory suiteCategoryCod(String suiteCategoryCod) {
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

  public SuiteCategory suiteCategory(String suiteCategory) {
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
    SuiteCategory suiteCategory = (SuiteCategory) o;
    return Objects.equals(this.suiteCategoryCod, suiteCategory.suiteCategoryCod) &&
        Objects.equals(this.suiteCategory, suiteCategory.suiteCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suiteCategoryCod, suiteCategory);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuiteCategory {\n");
    
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

