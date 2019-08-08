package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Currency
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class Currency {
  @SerializedName("currency_cod")
  private String currencyCod = null;

  @SerializedName("currency_name")
  private String currencyName = null;

  @SerializedName("currency_symbol")
  private String currencySymbol = null;

  public Currency currencyCod(String currencyCod) {
    this.currencyCod = currencyCod;
    return this;
  }

   /**
   * Get currencyCod
   * @return currencyCod
  **/
  @ApiModelProperty(value = "")
  public String getCurrencyCod() {
    return currencyCod;
  }

  public void setCurrencyCod(String currencyCod) {
    this.currencyCod = currencyCod;
  }

  public Currency currencyName(String currencyName) {
    this.currencyName = currencyName;
    return this;
  }

   /**
   * Get currencyName
   * @return currencyName
  **/
  @ApiModelProperty(value = "")
  public String getCurrencyName() {
    return currencyName;
  }

  public void setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
  }

  public Currency currencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
    return this;
  }

   /**
   * Get currencySymbol
   * @return currencySymbol
  **/
  @ApiModelProperty(value = "")
  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Currency currency = (Currency) o;
    return Objects.equals(this.currencyCod, currency.currencyCod) &&
        Objects.equals(this.currencyName, currency.currencyName) &&
        Objects.equals(this.currencySymbol, currency.currencySymbol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCod, currencyName, currencySymbol);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Currency {\n");
    
    sb.append("    currencyCod: ").append(toIndentedString(currencyCod)).append("\n");
    sb.append("    currencyName: ").append(toIndentedString(currencyName)).append("\n");
    sb.append("    currencySymbol: ").append(toIndentedString(currencySymbol)).append("\n");
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

