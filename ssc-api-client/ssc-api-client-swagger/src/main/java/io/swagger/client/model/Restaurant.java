package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Restaurant
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Restaurant {
  @SerializedName("restaurant_id")
  private Integer restaurantId = null;

  @SerializedName("restaurant_name")
  private String restaurantName = null;

  @SerializedName("ship_id")
  private String shipId = null;

  @SerializedName("ship_url")
  private String shipUrl = null;

  public Restaurant restaurantId(Integer restaurantId) {
    this.restaurantId = restaurantId;
    return this;
  }

   /**
   * Get restaurantId
   * @return restaurantId
  **/
  @ApiModelProperty(value = "")
  public Integer getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(Integer restaurantId) {
    this.restaurantId = restaurantId;
  }

  public Restaurant restaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
    return this;
  }

   /**
   * Get restaurantName
   * @return restaurantName
  **/
  @ApiModelProperty(value = "")
  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public Restaurant shipId(String shipId) {
    this.shipId = shipId;
    return this;
  }

   /**
   * Get shipId
   * @return shipId
  **/
  @ApiModelProperty(value = "")
  public String getShipId() {
    return shipId;
  }

  public void setShipId(String shipId) {
    this.shipId = shipId;
  }

  public Restaurant shipUrl(String shipUrl) {
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
    Restaurant restaurant = (Restaurant) o;
    return Objects.equals(this.restaurantId, restaurant.restaurantId) &&
        Objects.equals(this.restaurantName, restaurant.restaurantName) &&
        Objects.equals(this.shipId, restaurant.shipId) &&
        Objects.equals(this.shipUrl, restaurant.shipUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restaurantId, restaurantName, shipId, shipUrl);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Restaurant {\n");
    
    sb.append("    restaurantId: ").append(toIndentedString(restaurantId)).append("\n");
    sb.append("    restaurantName: ").append(toIndentedString(restaurantName)).append("\n");
    sb.append("    shipId: ").append(toIndentedString(shipId)).append("\n");
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

