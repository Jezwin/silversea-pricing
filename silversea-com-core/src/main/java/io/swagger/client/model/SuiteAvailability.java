/*
 * APIv3
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v2
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
import io.swagger.client.model.Suite;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 * SuiteAvailability
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-03-13T10:46:18.788Z")
public class SuiteAvailability {
  @SerializedName("voyage_cod")
  private String voyageCod = null;

  @SerializedName("sail_date")
  private DateTime sailDate = null;

  @SerializedName("suites_available")
  private List<Suite> suitesAvailable = new ArrayList<Suite>();

  public SuiteAvailability voyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
    return this;
  }

   /**
   * Get voyageCod
   * @return voyageCod
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getVoyageCod() {
    return voyageCod;
  }

  public void setVoyageCod(String voyageCod) {
    this.voyageCod = voyageCod;
  }

  public SuiteAvailability sailDate(DateTime sailDate) {
    this.sailDate = sailDate;
    return this;
  }

   /**
   * Get sailDate
   * @return sailDate
  **/
  @ApiModelProperty(example = "null", value = "")
  public DateTime getSailDate() {
    return sailDate;
  }

  public void setSailDate(DateTime sailDate) {
    this.sailDate = sailDate;
  }

  public SuiteAvailability suitesAvailable(List<Suite> suitesAvailable) {
    this.suitesAvailable = suitesAvailable;
    return this;
  }

  public SuiteAvailability addSuitesAvailableItem(Suite suitesAvailableItem) {
    this.suitesAvailable.add(suitesAvailableItem);
    return this;
  }

   /**
   * Get suitesAvailable
   * @return suitesAvailable
  **/
  @ApiModelProperty(example = "null", value = "")
  public List<Suite> getSuitesAvailable() {
    return suitesAvailable;
  }

  public void setSuitesAvailable(List<Suite> suitesAvailable) {
    this.suitesAvailable = suitesAvailable;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuiteAvailability suiteAvailability = (SuiteAvailability) o;
    return Objects.equals(this.voyageCod, suiteAvailability.voyageCod) &&
        Objects.equals(this.sailDate, suiteAvailability.sailDate) &&
        Objects.equals(this.suitesAvailable, suiteAvailability.suitesAvailable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageCod, sailDate, suitesAvailable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SuiteAvailability {\n");
    
    sb.append("    voyageCod: ").append(toIndentedString(voyageCod)).append("\n");
    sb.append("    sailDate: ").append(toIndentedString(sailDate)).append("\n");
    sb.append("    suitesAvailable: ").append(toIndentedString(suitesAvailable)).append("\n");
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

