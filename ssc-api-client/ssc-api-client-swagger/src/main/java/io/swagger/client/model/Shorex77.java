package io.swagger.client.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.CitySimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Shorex77
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen",
        date = "2017-08-25T14:49:03.640Z")
public class Shorex77 {
    @SerializedName("is_deleted")
    private Boolean isDeleted = null;

    @SerializedName("image_url")
    private String imageUrl = null;

    @SerializedName("image_url2")
    private String imageUrl2 = null;

    @SerializedName("image_url3")
    private String imageUrl3 = null;


    @SerializedName("image_url4")
    private String imageUrl4 = null;

    @SerializedName("symbols")
    private String symbols = null;

    @SerializedName("free_shorex_symbol")
    private String freeShorexSymbol = null;

    @SerializedName("shorex_id")
    private Integer shorexId = null;

    @SerializedName("shorex_url")
    private String shorexUrl = null;

    @SerializedName("shorex_cod")
    private String shorexCod = null;

    @SerializedName("shorex_name")
    private String shorexName = null;

    @SerializedName("points_of_interests")
    private String pointsOfInterests = null;

    @SerializedName("short_description")
    private String shortDescription = null;

    @SerializedName("description")
    private String description = null;

    @SerializedName("note")
    private String note = null;


    @SerializedName("ok_for_embarks")
    private boolean okForEmbarks;

    @SerializedName("ok_for_debarks")
    private boolean okForDebarks;

    @SerializedName("shorex_category")
    private String shorexCategory = null;

    @SerializedName("cities")
    private List<CitySimple> cities = new ArrayList<CitySimple>();

    public Shorex77 isDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    /**
     * Get isDeleted
     *
     * @return isDeleted
     **/
    @ApiModelProperty(value = "")
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Shorex77 imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    /**
     * Get imageUrl
     *
     * @return imageUrl
     **/
    @ApiModelProperty(value = "")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @ApiModelProperty(value = "")
    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    @ApiModelProperty(value = "")
    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    @ApiModelProperty(value = "")
    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public Shorex77 symbols(String symbols) {
        this.symbols = symbols;
        return this;
    }

    /**
     * Get symbols
     *
     * @return symbols
     **/
    @ApiModelProperty(value = "")
    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public Shorex77 freeShorexSymbol(String freeShorexSymbol) {
        this.freeShorexSymbol = freeShorexSymbol;
        return this;
    }

    /**
     * Get freeShorexSymbol
     *
     * @return freeShorexSymbol
     **/
    @ApiModelProperty(value = "")
    public String getFreeShorexSymbol() {
        return freeShorexSymbol;
    }

    public void setFreeShorexSymbol(String freeShorexSymbol) {
        this.freeShorexSymbol = freeShorexSymbol;
    }

    public Shorex77 shorexId(Integer shorexId) {
        this.shorexId = shorexId;
        return this;
    }

    /**
     * Get shorexId
     *
     * @return shorexId
     **/
    @ApiModelProperty(value = "")
    public Integer getShorexId() {
        return shorexId;
    }

    public void setShorexId(Integer shorexId) {
        this.shorexId = shorexId;
    }

    public Shorex77 shorexUrl(String shorexUrl) {
        this.shorexUrl = shorexUrl;
        return this;
    }

    /**
     * Get shorexUrl
     *
     * @return shorexUrl
     **/
    @ApiModelProperty(value = "")
    public String getShorexUrl() {
        return shorexUrl;
    }

    public void setShorexUrl(String shorexUrl) {
        this.shorexUrl = shorexUrl;
    }

    public Shorex77 shorexCod(String shorexCod) {
        this.shorexCod = shorexCod;
        return this;
    }

    /**
     * Get shorexCod
     *
     * @return shorexCod
     **/
    @ApiModelProperty(value = "")
    public String getShorexCod() {
        return shorexCod;
    }

    public void setShorexCod(String shorexCod) {
        this.shorexCod = shorexCod;
    }

    public Shorex77 shorexName(String shorexName) {
        this.shorexName = shorexName;
        return this;
    }

    /**
     * Get shorexName
     *
     * @return shorexName
     **/
    @ApiModelProperty(value = "")
    public String getShorexName() {
        return shorexName;
    }

    public void setShorexName(String shorexName) {
        this.shorexName = shorexName;
    }

    public Shorex77 pointsOfInterests(String pointsOfInterests) {
        this.pointsOfInterests = pointsOfInterests;
        return this;
    }

    /**
     * Get pointsOfInterests
     *
     * @return pointsOfInterests
     **/
    @ApiModelProperty(value = "")
    public String getPointsOfInterests() {
        return pointsOfInterests;
    }

    public void setPointsOfInterests(String pointsOfInterests) {
        this.pointsOfInterests = pointsOfInterests;
    }

    public Shorex77 shortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    /**
     * Get shortDescription
     *
     * @return shortDescription
     **/
    @ApiModelProperty(value = "")
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Shorex77 description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @ApiModelProperty(value = "")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Shorex77 cities(List<CitySimple> cities) {
        this.cities = cities;
        return this;
    }

    public Shorex77 addCitiesItem(CitySimple citiesItem) {
        this.cities.add(citiesItem);
        return this;
    }

    /**
     * Get cities
     *
     * @return cities
     **/
    @ApiModelProperty(value = "")
    public List<CitySimple> getCities() {
        return cities;
    }

    public void setCities(List<CitySimple> cities) {
        this.cities = cities;
    }

    @ApiModelProperty(value = "true")
    public boolean isOkForEmbarks() {
        return okForEmbarks;
    }

    public void setOkForEmbarks(boolean okForEmbarks) {
        this.okForEmbarks = okForEmbarks;
    }

    @ApiModelProperty(value = "true")
    public boolean isOkForDebarks() {
        return okForDebarks;
    }

    public void setOkForDebarks(boolean okForDebarks) {
        this.okForDebarks = okForDebarks;
    }

    @ApiModelProperty(value = "")
    public String getShorexCategory() {
        return shorexCategory;
    }

    public void setShorexCategory(String shorexCategory) {
        this.shorexCategory = shorexCategory;
    }
    public Shorex77 note(String note) {
        this.note = note;
        return this;
    }

    @ApiModelProperty(value = "")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Shorex77 shorex77 = (Shorex77) o;
        return Objects.equals(this.isDeleted, shorex77.isDeleted) &&
                Objects.equals(this.imageUrl, shorex77.imageUrl) &&
                Objects.equals(this.symbols, shorex77.symbols) &&
                Objects.equals(this.freeShorexSymbol, shorex77.freeShorexSymbol) &&
                Objects.equals(this.shorexId, shorex77.shorexId) &&
                Objects.equals(this.shorexUrl, shorex77.shorexUrl) &&
                Objects.equals(this.shorexCod, shorex77.shorexCod) &&
                Objects.equals(this.shorexName, shorex77.shorexName) &&

                Objects.equals(this.okForDebarks, shorex77.okForDebarks) &&
                Objects.equals(this.okForEmbarks, shorex77.okForEmbarks) &&
                Objects.equals(this.shorexCategory, shorex77.shorexCategory) &&

                Objects.equals(this.pointsOfInterests, shorex77.pointsOfInterests) &&
                Objects.equals(this.shortDescription, shorex77.shortDescription) &&
                Objects.equals(this.description, shorex77.description) &&
                Objects.equals(this.note, shorex77.note) &&
                Objects.equals(this.cities, shorex77.cities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDeleted, imageUrl, symbols, freeShorexSymbol, shorexId, shorexUrl, shorexCod, shorexName,
                okForDebarks, okForEmbarks, shorexCategory, pointsOfInterests, shortDescription, description, cities,
                note);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Shorex77 {\n");

        sb.append("    isDeleted: ").append(toIndentedString(isDeleted)).append("\n");
        sb.append("    imageUrl: ").append(toIndentedString(imageUrl)).append("\n");
        sb.append("    symbols: ").append(toIndentedString(symbols)).append("\n");
        sb.append("    freeShorexSymbol: ").append(toIndentedString(freeShorexSymbol)).append("\n");
        sb.append("    shorexId: ").append(toIndentedString(shorexId)).append("\n");
        sb.append("    shorexUrl: ").append(toIndentedString(shorexUrl)).append("\n");
        sb.append("    shorexCod: ").append(toIndentedString(shorexCod)).append("\n");
        sb.append("    shorexName: ").append(toIndentedString(shorexName)).append("\n");

        sb.append("    okForEmbarks: ").append(toIndentedString(okForEmbarks)).append("\n");
        sb.append("    okForDebarks: ").append(toIndentedString(okForDebarks)).append("\n");
        sb.append("    shorexCategory: ").append(toIndentedString(shorexCategory)).append("\n");

        sb.append("    pointsOfInterests: ").append(toIndentedString(pointsOfInterests)).append("\n");
        sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    cities: ").append(toIndentedString(cities)).append("\n");
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

