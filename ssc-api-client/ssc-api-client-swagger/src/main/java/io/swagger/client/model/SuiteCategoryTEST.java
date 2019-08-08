package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SuiteCategoryTEST
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class SuiteCategoryTEST {
  @SerializedName("suite_category_cod")
  private String suiteCategoryCod = null;

  @SerializedName("suite_category")
  private String suiteCategory = null;

  @SerializedName("is_avaialable")
  private Boolean isAvaialable = null;

  public SuiteCategoryTEST suiteCategoryCod(String suiteCategoryCod) {
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

  public SuiteCategoryTEST suiteCategory(String suiteCategory) {
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

  public SuiteCategoryTEST isAvaialable(Boolean isAvaialable) {
    this.isAvaialable = isAvaialable;
    return this;
  }

   /**
   * Get isAvaialable
   * @return isAvaialable
  **/
  @ApiModelProperty(value = "")
  public Boolean getIsAvaialable() {
    return isAvaialable;
  }

  public void setIsAvaialable(Boolean isAvaialable) {
    this.isAvaialable = isAvaialable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuiteCategoryTEST suiteCategoryTEST = (SuiteCategoryTEST) o;
    return Objects.equals(this.suiteCategoryCod, suiteCategoryTEST.suiteCategoryCod) &&
        Objects.equals(this.suiteCategory, suiteCategoryTEST.suiteCategory) &&
        Objects.equals(this.isAvaialable, suiteCategoryTEST.isAvaialable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suiteCategoryCod, suiteCategory, isAvaialable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuiteCategoryTEST {\n");
    
    sb.append("    suiteCategoryCod: ").append(toIndentedString(suiteCategoryCod)).append("\n");
    sb.append("    suiteCategory: ").append(toIndentedString(suiteCategory)).append("\n");
    sb.append("    isAvaialable: ").append(toIndentedString(isAvaialable)).append("\n");
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

