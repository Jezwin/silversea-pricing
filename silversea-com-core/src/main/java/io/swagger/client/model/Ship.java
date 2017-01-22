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
 * Ship
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-01-22T13:04:26.738Z")
public class Ship {
  @SerializedName("ship_id")
  private Integer shipId = null;

  @SerializedName("ship_name")
  private String shipName = null;

  @SerializedName("ship_cod")
  private String shipCod = null;

  @SerializedName("ship_url")
  private String shipUrl = null;

  public Ship shipId(Integer shipId) {
    this.shipId = shipId;
    return this;
  }

   /**
   * Get shipId
   * @return shipId
  **/
  @ApiModelProperty(example = "null", value = "")
  public Integer getShipId() {
    return shipId;
  }

  public void setShipId(Integer shipId) {
    this.shipId = shipId;
  }

  public Ship shipName(String shipName) {
    this.shipName = shipName;
    return this;
  }

   /**
   * Get shipName
   * @return shipName
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShipName() {
    return shipName;
  }

  public void setShipName(String shipName) {
    this.shipName = shipName;
  }

  public Ship shipCod(String shipCod) {
    this.shipCod = shipCod;
    return this;
  }

   /**
   * Get shipCod
   * @return shipCod
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShipCod() {
    return shipCod;
  }

  public void setShipCod(String shipCod) {
    this.shipCod = shipCod;
  }

  public Ship shipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
    return this;
  }

   /**
   * Get shipUrl
   * @return shipUrl
  **/
  @ApiModelProperty(example = "null", value = "")
  public String getShipUrl() {
    return shipUrl;
  }

  public void setShipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ship ship = (Ship) o;
    return Objects.equals(this.shipId, ship.shipId) &&
        Objects.equals(this.shipName, ship.shipName) &&
        Objects.equals(this.shipCod, ship.shipCod) &&
        Objects.equals(this.shipUrl, ship.shipUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shipId, shipName, shipCod, shipUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Ship {\n");
    
    sb.append("    shipId: ").append(toIndentedString(shipId)).append("\n");
    sb.append("    shipName: ").append(toIndentedString(shipName)).append("\n");
    sb.append("    shipCod: ").append(toIndentedString(shipCod)).append("\n");
    sb.append("    shipUrl: ").append(toIndentedString(shipUrl)).append("\n");
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

