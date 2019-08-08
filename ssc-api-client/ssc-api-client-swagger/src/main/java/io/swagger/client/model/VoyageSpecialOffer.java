package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.SpecialOfferByMarket;
import java.util.ArrayList;
import java.util.List;

/**
 * VoyageSpecialOffer
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-08-25T14:49:03.640Z")
public class VoyageSpecialOffer {
  @SerializedName("voyage_id")
  private Integer voyageId = null;

  @SerializedName("special_offers")
  private List<SpecialOfferByMarket> specialOffers = new ArrayList<SpecialOfferByMarket>();

  public VoyageSpecialOffer voyageId(Integer voyageId) {
    this.voyageId = voyageId;
    return this;
  }

   /**
   * Get voyageId
   * @return voyageId
  **/
  @ApiModelProperty(value = "")
  public Integer getVoyageId() {
    return voyageId;
  }

  public void setVoyageId(Integer voyageId) {
    this.voyageId = voyageId;
  }

  public VoyageSpecialOffer specialOffers(List<SpecialOfferByMarket> specialOffers) {
    this.specialOffers = specialOffers;
    return this;
  }

  public VoyageSpecialOffer addSpecialOffersItem(SpecialOfferByMarket specialOffersItem) {
    this.specialOffers.add(specialOffersItem);
    return this;
  }

   /**
   * Get specialOffers
   * @return specialOffers
  **/
  @ApiModelProperty(value = "")
  public List<SpecialOfferByMarket> getSpecialOffers() {
    return specialOffers;
  }

  public void setSpecialOffers(List<SpecialOfferByMarket> specialOffers) {
    this.specialOffers = specialOffers;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoyageSpecialOffer voyageSpecialOffer = (VoyageSpecialOffer) o;
    return Objects.equals(this.voyageId, voyageSpecialOffer.voyageId) &&
        Objects.equals(this.specialOffers, voyageSpecialOffer.specialOffers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(voyageId, specialOffers);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoyageSpecialOffer {\n");
    
    sb.append("    voyageId: ").append(toIndentedString(voyageId)).append("\n");
    sb.append("    specialOffers: ").append(toIndentedString(specialOffers)).append("\n");
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

