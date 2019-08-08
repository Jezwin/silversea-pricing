package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Ship
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Ship {
  @SerializedName("ship_id")
  private Integer shipId = null;

  @SerializedName("ship_name")
  private String shipName = null;

  @SerializedName("ship_cod")
  private String shipCod = null;

  @SerializedName("ship_type")
  private String shipType = null;

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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
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
  @ApiModelProperty(value = "")
  public String getShipCod() {
    return shipCod;
  }

  public void setShipCod(String shipCod) {
    this.shipCod = shipCod;
  }

  public Ship shipType(String shipType) {
    this.shipType = shipType;
    return this;
  }

   /**
   * Get shipType
   * @return shipType
  **/
  @ApiModelProperty(value = "")
  public String getShipType() {
    return shipType;
  }

  public void setShipType(String shipType) {
    this.shipType = shipType;
  }

  public Ship shipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
    return this;
  }

   /**
   * Get shipUrl
   * @return shipUrl
  **/
  @ApiModelProperty(value = "")
  public String getShipUrl() {
    return shipUrl;
  }

  public void setShipUrl(String shipUrl) {
    this.shipUrl = shipUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
        Objects.equals(this.shipType, ship.shipType) &&
        Objects.equals(this.shipUrl, ship.shipUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shipId, shipName, shipCod, shipType, shipUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Ship {\n");
    
    sb.append("    shipId: ").append(toIndentedString(shipId)).append("\n");
    sb.append("    shipName: ").append(toIndentedString(shipName)).append("\n");
    sb.append("    shipCod: ").append(toIndentedString(shipCod)).append("\n");
    sb.append("    shipType: ").append(toIndentedString(shipType)).append("\n");
    sb.append("    shipUrl: ").append(toIndentedString(shipUrl)).append("\n");
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

