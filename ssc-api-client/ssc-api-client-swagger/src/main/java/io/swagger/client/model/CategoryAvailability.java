package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.SuiteCategoryTEST;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * CategoryAvailability
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class CategoryAvailability {
  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("sail_date")
  private DateTime sailDate = null;

  @SerializedName("categories_available")
  private List<SuiteCategoryTEST> categoriesAvailable = new ArrayList<SuiteCategoryTEST>();

  public CategoryAvailability voyageCod(String voyageCod) {
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

  public CategoryAvailability sailDate(DateTime sailDate) {
    this.sailDate = sailDate;
    return this;
  }

   /**
   * Get sailDate
   * @return sailDate
  **/
  @ApiModelProperty(value = "")
  public DateTime getSailDate() {
    return sailDate;
  }

  public void setSailDate(DateTime sailDate) {
    this.sailDate = sailDate;
  }

  public CategoryAvailability categoriesAvailable(List<SuiteCategoryTEST> categoriesAvailable) {
    this.categoriesAvailable = categoriesAvailable;
    return this;
  }

  public CategoryAvailability addCategoriesAvailableItem(SuiteCategoryTEST categoriesAvailableItem) {
    this.categoriesAvailable.add(categoriesAvailableItem);
    return this;
  }

   /**
   * Get categoriesAvailable
   * @return categoriesAvailable
  **/
  @ApiModelProperty(value = "")
  public List<SuiteCategoryTEST> getCategoriesAvailable() {
    return categoriesAvailable;
  }

  public void setCategoriesAvailable(List<SuiteCategoryTEST> categoriesAvailable) {
    this.categoriesAvailable = categoriesAvailable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CategoryAvailability categoryAvailability = (CategoryAvailability) o;
    return Objects.equals(this.voyageCod, categoryAvailability.voyageCod) &&
        Objects.equals(this.sailDate, categoryAvailability.sailDate) &&
        Objects.equals(this.categoriesAvailable, categoryAvailability.categoriesAvailable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageCod, sailDate, categoriesAvailable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CategoryAvailability {\n");
    
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    sailDate: ").append(toIndentedString(sailDate)).append("\n");
    sb.append("    categoriesAvailable: ").append(toIndentedString(categoriesAvailable)).append("\n");
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

