package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Gateway
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Gateway {
  @SerializedName("gateway_cod")
  private String gatewayCod = null;

  @SerializedName("gateway")
  private String gateway = null;

  @SerializedName("gateway_zone")
  private String gatewayZone = null;

  @SerializedName("area_cod")
  private String areaCod = null;

  public Gateway gatewayCod(String gatewayCod) {
    this.gatewayCod = gatewayCod;
    return this;
  }

   /**
   * Get gatewayCod
   * @return gatewayCod
  **/
  @ApiModelProperty(value = "")
  public String getGatewayCod() {
    return gatewayCod;
  }

  public void setGatewayCod(String gatewayCod) {
    this.gatewayCod = gatewayCod;
  }

  public Gateway gateway(String gateway) {
    this.gateway = gateway;
    return this;
  }

   /**
   * Get gateway
   * @return gateway
  **/
  @ApiModelProperty(value = "")
  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }

  public Gateway gatewayZone(String gatewayZone) {
    this.gatewayZone = gatewayZone;
    return this;
  }

   /**
   * Get gatewayZone
   * @return gatewayZone
  **/
  @ApiModelProperty(value = "")
  public String getGatewayZone() {
    return gatewayZone;
  }

  public void setGatewayZone(String gatewayZone) {
    this.gatewayZone = gatewayZone;
  }

  public Gateway areaCod(String areaCod) {
    this.areaCod = areaCod;
    return this;
  }

   /**
   * Get areaCod
   * @return areaCod
  **/
  @ApiModelProperty(value = "")
  public String getAreaCod() {
    return areaCod;
  }

  public void setAreaCod(String areaCod) {
    this.areaCod = areaCod;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Gateway gateway = (Gateway) o;
    return Objects.equals(this.gatewayCod, gateway.gatewayCod) &&
        Objects.equals(this.gateway, gateway.gateway) &&
        Objects.equals(this.gatewayZone, gateway.gatewayZone) &&
        Objects.equals(this.areaCod, gateway.areaCod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gatewayCod, gateway, gatewayZone, areaCod);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Gateway {\n");
    
    sb.append("    gatewayCod: ").append(toIndentedString(gatewayCod)).append("\n");
    sb.append("    gateway: ").append(toIndentedString(gateway)).append("\n");
    sb.append("    gatewayZone: ").append(toIndentedString(gatewayZone)).append("\n");
    sb.append("    areaCod: ").append(toIndentedString(areaCod)).append("\n");
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

