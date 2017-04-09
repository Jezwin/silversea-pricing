
package com.silversea.aem.ws;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour RequestItem03 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="RequestItem03"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NameFirst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NameMiddle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NameLast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Address1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Address2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="County" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Zip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Zip4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PreferredDestinations" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="VSNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BookingNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="WorkingWithAgent" type="{http://www.w3.org/2001/XMLSchema}short"/&gt;
 *         &lt;element name="IsAgent" type="{http://www.w3.org/2001/XMLSchema}short"/&gt;
 *         &lt;element name="BrochuresRequested" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SiteCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SiteLanguage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="VoyageCod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Voyage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Ship" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SailDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RequestDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="RequestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RequestSubType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RequestSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InsertUTCDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="DeparturePort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ArrivalPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Completed" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MarketingEffort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MCId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SubscribeEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SuiteCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SuiteVariation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="Att01" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att02" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att03" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att04" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att05" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att06" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att07" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att08" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att09" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Att10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestItem03", propOrder = {
    "id",
    "title",
    "nameFirst",
    "nameMiddle",
    "nameLast",
    "email",
    "phone",
    "address1",
    "address2",
    "city",
    "state",
    "county",
    "zip",
    "zip4",
    "country",
    "preferredDestinations",
    "vsNumber",
    "bookingNumber",
    "workingWithAgent",
    "isAgent",
    "brochuresRequested",
    "siteCurrency",
    "siteLanguage",
    "voyageCod",
    "voyage",
    "ship",
    "sailDate",
    "comments",
    "requestDate",
    "requestType",
    "requestSubType",
    "requestSource",
    "insertUTCDate",
    "departurePort",
    "arrivalPort",
    "completed",
    "marketingEffort",
    "mcId",
    "subscribeEmail",
    "suiteCategory",
    "suiteVariation",
    "price",
    "att01",
    "att02",
    "att03",
    "att04",
    "att05",
    "att06",
    "att07",
    "att08",
    "att09",
    "att10"
})
public class RequestItem03 {

    @XmlElement(name = "ID")
    protected int id;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "NameFirst")
    protected String nameFirst;
    @XmlElement(name = "NameMiddle")
    protected String nameMiddle;
    @XmlElement(name = "NameLast")
    protected String nameLast;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "Phone")
    protected String phone;
    @XmlElement(name = "Address1")
    protected String address1;
    @XmlElement(name = "Address2")
    protected String address2;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "State")
    protected String state;
    @XmlElement(name = "County")
    protected String county;
    @XmlElement(name = "Zip")
    protected String zip;
    @XmlElement(name = "Zip4")
    protected String zip4;
    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "PreferredDestinations")
    protected String preferredDestinations;
    @XmlElement(name = "VSNumber")
    protected String vsNumber;
    @XmlElement(name = "BookingNumber")
    protected String bookingNumber;
    @XmlElement(name = "WorkingWithAgent", required = true, type = Short.class, nillable = true)
    protected Short workingWithAgent;
    @XmlElement(name = "IsAgent", required = true, type = Short.class, nillable = true)
    protected Short isAgent;
    @XmlElement(name = "BrochuresRequested")
    protected String brochuresRequested;
    @XmlElement(name = "SiteCurrency")
    protected String siteCurrency;
    @XmlElement(name = "SiteLanguage")
    protected String siteLanguage;
    @XmlElement(name = "VoyageCod")
    protected String voyageCod;
    @XmlElement(name = "Voyage")
    protected String voyage;
    @XmlElement(name = "Ship")
    protected String ship;
    @XmlElement(name = "SailDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sailDate;
    @XmlElement(name = "Comments")
    protected String comments;
    @XmlElement(name = "RequestDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDate;
    @XmlElement(name = "RequestType")
    protected String requestType;
    @XmlElement(name = "RequestSubType")
    protected String requestSubType;
    @XmlElement(name = "RequestSource")
    protected String requestSource;
    @XmlElement(name = "InsertUTCDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar insertUTCDate;
    @XmlElement(name = "DeparturePort")
    protected String departurePort;
    @XmlElement(name = "ArrivalPort")
    protected String arrivalPort;
    @XmlElement(name = "Completed")
    protected int completed;
    @XmlElement(name = "MarketingEffort")
    protected String marketingEffort;
    @XmlElement(name = "MCId", required = true, type = Integer.class, nillable = true)
    protected Integer mcId;
    @XmlElement(name = "SubscribeEmail")
    protected String subscribeEmail;
    @XmlElement(name = "SuiteCategory")
    protected String suiteCategory;
    @XmlElement(name = "SuiteVariation")
    protected String suiteVariation;
    @XmlElement(name = "Price", required = true, nillable = true)
    protected BigDecimal price;
    @XmlElement(name = "Att01")
    protected String att01;
    @XmlElement(name = "Att02")
    protected String att02;
    @XmlElement(name = "Att03")
    protected String att03;
    @XmlElement(name = "Att04")
    protected String att04;
    @XmlElement(name = "Att05")
    protected String att05;
    @XmlElement(name = "Att06")
    protected String att06;
    @XmlElement(name = "Att07")
    protected String att07;
    @XmlElement(name = "Att08")
    protected String att08;
    @XmlElement(name = "Att09")
    protected String att09;
    @XmlElement(name = "Att10")
    protected String att10;

    /**
     * Obtient la valeur de la propriété id.
     * 
     */
    public int getID() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     */
    public void setID(int value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété nameFirst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameFirst() {
        return nameFirst;
    }

    /**
     * Définit la valeur de la propriété nameFirst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameFirst(String value) {
        this.nameFirst = value;
    }

    /**
     * Obtient la valeur de la propriété nameMiddle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameMiddle() {
        return nameMiddle;
    }

    /**
     * Définit la valeur de la propriété nameMiddle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameMiddle(String value) {
        this.nameMiddle = value;
    }

    /**
     * Obtient la valeur de la propriété nameLast.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameLast() {
        return nameLast;
    }

    /**
     * Définit la valeur de la propriété nameLast.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameLast(String value) {
        this.nameLast = value;
    }

    /**
     * Obtient la valeur de la propriété email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit la valeur de la propriété email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Obtient la valeur de la propriété phone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Définit la valeur de la propriété phone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Obtient la valeur de la propriété address1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Définit la valeur de la propriété address1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress1(String value) {
        this.address1 = value;
    }

    /**
     * Obtient la valeur de la propriété address2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Définit la valeur de la propriété address2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress2(String value) {
        this.address2 = value;
    }

    /**
     * Obtient la valeur de la propriété city.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Définit la valeur de la propriété city.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Obtient la valeur de la propriété state.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Définit la valeur de la propriété state.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Obtient la valeur de la propriété county.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCounty() {
        return county;
    }

    /**
     * Définit la valeur de la propriété county.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCounty(String value) {
        this.county = value;
    }

    /**
     * Obtient la valeur de la propriété zip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZip() {
        return zip;
    }

    /**
     * Définit la valeur de la propriété zip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZip(String value) {
        this.zip = value;
    }

    /**
     * Obtient la valeur de la propriété zip4.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZip4() {
        return zip4;
    }

    /**
     * Définit la valeur de la propriété zip4.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZip4(String value) {
        this.zip4 = value;
    }

    /**
     * Obtient la valeur de la propriété country.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Définit la valeur de la propriété country.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Obtient la valeur de la propriété preferredDestinations.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredDestinations() {
        return preferredDestinations;
    }

    /**
     * Définit la valeur de la propriété preferredDestinations.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredDestinations(String value) {
        this.preferredDestinations = value;
    }

    /**
     * Obtient la valeur de la propriété vsNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVSNumber() {
        return vsNumber;
    }

    /**
     * Définit la valeur de la propriété vsNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVSNumber(String value) {
        this.vsNumber = value;
    }

    /**
     * Obtient la valeur de la propriété bookingNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingNumber() {
        return bookingNumber;
    }

    /**
     * Définit la valeur de la propriété bookingNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingNumber(String value) {
        this.bookingNumber = value;
    }

    /**
     * Obtient la valeur de la propriété workingWithAgent.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getWorkingWithAgent() {
        return workingWithAgent;
    }

    /**
     * Définit la valeur de la propriété workingWithAgent.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setWorkingWithAgent(Short value) {
        this.workingWithAgent = value;
    }

    /**
     * Obtient la valeur de la propriété isAgent.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getIsAgent() {
        return isAgent;
    }

    /**
     * Définit la valeur de la propriété isAgent.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setIsAgent(Short value) {
        this.isAgent = value;
    }

    /**
     * Obtient la valeur de la propriété brochuresRequested.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrochuresRequested() {
        return brochuresRequested;
    }

    /**
     * Définit la valeur de la propriété brochuresRequested.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrochuresRequested(String value) {
        this.brochuresRequested = value;
    }

    /**
     * Obtient la valeur de la propriété siteCurrency.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSiteCurrency() {
        return siteCurrency;
    }

    /**
     * Définit la valeur de la propriété siteCurrency.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSiteCurrency(String value) {
        this.siteCurrency = value;
    }

    /**
     * Obtient la valeur de la propriété siteLanguage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSiteLanguage() {
        return siteLanguage;
    }

    /**
     * Définit la valeur de la propriété siteLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSiteLanguage(String value) {
        this.siteLanguage = value;
    }

    /**
     * Obtient la valeur de la propriété voyageCod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoyageCod() {
        return voyageCod;
    }

    /**
     * Définit la valeur de la propriété voyageCod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoyageCod(String value) {
        this.voyageCod = value;
    }

    /**
     * Obtient la valeur de la propriété voyage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoyage() {
        return voyage;
    }

    /**
     * Définit la valeur de la propriété voyage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoyage(String value) {
        this.voyage = value;
    }

    /**
     * Obtient la valeur de la propriété ship.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShip() {
        return ship;
    }

    /**
     * Définit la valeur de la propriété ship.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShip(String value) {
        this.ship = value;
    }

    /**
     * Obtient la valeur de la propriété sailDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSailDate() {
        return sailDate;
    }

    /**
     * Définit la valeur de la propriété sailDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSailDate(XMLGregorianCalendar value) {
        this.sailDate = value;
    }

    /**
     * Obtient la valeur de la propriété comments.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Définit la valeur de la propriété comments.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Obtient la valeur de la propriété requestDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Définit la valeur de la propriété requestDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

    /**
     * Obtient la valeur de la propriété requestType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Définit la valeur de la propriété requestType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestType(String value) {
        this.requestType = value;
    }

    /**
     * Obtient la valeur de la propriété requestSubType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestSubType() {
        return requestSubType;
    }

    /**
     * Définit la valeur de la propriété requestSubType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestSubType(String value) {
        this.requestSubType = value;
    }

    /**
     * Obtient la valeur de la propriété requestSource.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestSource() {
        return requestSource;
    }

    /**
     * Définit la valeur de la propriété requestSource.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestSource(String value) {
        this.requestSource = value;
    }

    /**
     * Obtient la valeur de la propriété insertUTCDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInsertUTCDate() {
        return insertUTCDate;
    }

    /**
     * Définit la valeur de la propriété insertUTCDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInsertUTCDate(XMLGregorianCalendar value) {
        this.insertUTCDate = value;
    }

    /**
     * Obtient la valeur de la propriété departurePort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeparturePort() {
        return departurePort;
    }

    /**
     * Définit la valeur de la propriété departurePort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeparturePort(String value) {
        this.departurePort = value;
    }

    /**
     * Obtient la valeur de la propriété arrivalPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalPort() {
        return arrivalPort;
    }

    /**
     * Définit la valeur de la propriété arrivalPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalPort(String value) {
        this.arrivalPort = value;
    }

    /**
     * Obtient la valeur de la propriété completed.
     * 
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * Définit la valeur de la propriété completed.
     * 
     */
    public void setCompleted(int value) {
        this.completed = value;
    }

    /**
     * Obtient la valeur de la propriété marketingEffort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarketingEffort() {
        return marketingEffort;
    }

    /**
     * Définit la valeur de la propriété marketingEffort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarketingEffort(String value) {
        this.marketingEffort = value;
    }

    /**
     * Obtient la valeur de la propriété mcId.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMCId() {
        return mcId;
    }

    /**
     * Définit la valeur de la propriété mcId.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMCId(Integer value) {
        this.mcId = value;
    }

    /**
     * Obtient la valeur de la propriété subscribeEmail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscribeEmail() {
        return subscribeEmail;
    }

    /**
     * Définit la valeur de la propriété subscribeEmail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscribeEmail(String value) {
        this.subscribeEmail = value;
    }

    /**
     * Obtient la valeur de la propriété suiteCategory.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuiteCategory() {
        return suiteCategory;
    }

    /**
     * Définit la valeur de la propriété suiteCategory.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuiteCategory(String value) {
        this.suiteCategory = value;
    }

    /**
     * Obtient la valeur de la propriété suiteVariation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuiteVariation() {
        return suiteVariation;
    }

    /**
     * Définit la valeur de la propriété suiteVariation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuiteVariation(String value) {
        this.suiteVariation = value;
    }

    /**
     * Obtient la valeur de la propriété price.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Définit la valeur de la propriété price.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPrice(BigDecimal value) {
        this.price = value;
    }

    /**
     * Obtient la valeur de la propriété att01.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt01() {
        return att01;
    }

    /**
     * Définit la valeur de la propriété att01.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt01(String value) {
        this.att01 = value;
    }

    /**
     * Obtient la valeur de la propriété att02.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt02() {
        return att02;
    }

    /**
     * Définit la valeur de la propriété att02.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt02(String value) {
        this.att02 = value;
    }

    /**
     * Obtient la valeur de la propriété att03.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt03() {
        return att03;
    }

    /**
     * Définit la valeur de la propriété att03.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt03(String value) {
        this.att03 = value;
    }

    /**
     * Obtient la valeur de la propriété att04.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt04() {
        return att04;
    }

    /**
     * Définit la valeur de la propriété att04.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt04(String value) {
        this.att04 = value;
    }

    /**
     * Obtient la valeur de la propriété att05.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt05() {
        return att05;
    }

    /**
     * Définit la valeur de la propriété att05.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt05(String value) {
        this.att05 = value;
    }

    /**
     * Obtient la valeur de la propriété att06.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt06() {
        return att06;
    }

    /**
     * Définit la valeur de la propriété att06.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt06(String value) {
        this.att06 = value;
    }

    /**
     * Obtient la valeur de la propriété att07.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt07() {
        return att07;
    }

    /**
     * Définit la valeur de la propriété att07.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt07(String value) {
        this.att07 = value;
    }

    /**
     * Obtient la valeur de la propriété att08.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt08() {
        return att08;
    }

    /**
     * Définit la valeur de la propriété att08.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt08(String value) {
        this.att08 = value;
    }

    /**
     * Obtient la valeur de la propriété att09.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt09() {
        return att09;
    }

    /**
     * Définit la valeur de la propriété att09.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt09(String value) {
        this.att09 = value;
    }

    /**
     * Obtient la valeur de la propriété att10.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtt10() {
        return att10;
    }

    /**
     * Définit la valeur de la propriété att10.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtt10(String value) {
        this.att10 = value;
    }

}
