package com.silversea.aem.components.beans;

import java.math.BigDecimal;

/**
 * TODO clean
 */
public class Lead {
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String comments;
    private String requestsource;
    private String requesttype;
    private String sitecountry;
    private String sitelanguage;
    private String sitecurrency;
    // Subscribe to newletters
    private String subscribeemail;
    private String workingwithagent;
    private String isnotagent;
    // Request a quote
    private String voyagename;
    private String voyagecode;
    private String departuredate;
    private String voyagelength;
    private String shipname;
    private String suitecategory;
    private String suitevariation;
    private String price;
    // Request a brochure
    private String postaladdress;
    private String postalcode;
    private String city;
    private String country;
    private String state;
    private String brochurecode;
    // Campaign value
    private String marketingEffort;
    // Contact Us Form
    private String subject;
    private String inquiry;
    private String from_email;
    private String bookingnumber;
    private String vsnumber;
    // Lead Arrival Date
    private String submitDate;

    public String getPostaladdress() {
        return postaladdress;
    }

    public void setPostaladdress(String postaladdress) {
        this.postaladdress = postaladdress;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getSuitecategory() {
        return suitecategory;
    }

    public void setSuitecategory(String suitecategory) {
        this.suitecategory = suitecategory;
    }

    public String getSuitevariation() {
        return suitevariation;
    }

    public void setSuitevariation(String suitevariation) {
        this.suitevariation = suitevariation;
    }

    public String getPriceString() {
        return price;
    }
    
    public BigDecimal getPrice() {
        return new BigDecimal(price);
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVoyagename() {
        return voyagename;
    }

    public void setVoyagename(String voyagename) {
        this.voyagename = voyagename;
    }

    public String getDeparturedate() {
        return departuredate;
    }

    public void setDeparturedate(String departuredate) {
        this.departuredate = departuredate;
    }

    public String getVoyagelength() {
        return voyagelength;
    }

    public void setVoyagelength(String voyagelength) {
        this.voyagelength = voyagelength;
    }

    public String getShipname() {
        return shipname;
    }

    public void setShipname(String shipname) {
        this.shipname = shipname;
    }

    public Lead() {
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname
     *            the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname
     *            the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the requestsource
     */
    public String getRequestsource() {
        return requestsource;
    }

    /**
     * @param requestsource
     *            the requestsource to set
     */
    public void setRequestsource(String requestsource) {
        this.requestsource = requestsource;
    }

    /**
     * @return the subscribeemail
     */
    public String getSubscribeemail() {
        return subscribeemail;
    }

    /**
     * @param subscribeemail
     *            the subscribeemail to set
     */
    public void setSubscribeemail(String subscribeemail) {
        this.subscribeemail = subscribeemail;
    }

    /**
     * @return the workingwithagent
     */
    public String getWorkingwithagent() {
        return workingwithagent;
    }

    /**
     * @param workingwithagent
     *            the workingwithagent to set
     */
    public void setWorkingwithagent(String workingwithagent) {
        this.workingwithagent = workingwithagent;
    }

    public String getVoyagecode() {
        return voyagecode;
    }

    public void setVoyagecode(String voyagecode) {
        this.voyagecode = voyagecode;
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }

    public String getBrochurecode() {
        return brochurecode;
    }

    public void setBrochurecode(String brochurecode) {
        this.brochurecode = brochurecode;
    }

    public String getSitecountry() {
        return sitecountry;
    }

    public void setSitecountry(String sitecountry) {
        this.sitecountry = sitecountry;
    }

    public String getSitelanguage() {
        return sitelanguage;
    }

    public void setSitelanguage(String sitelanguage) {
        this.sitelanguage = sitelanguage;
    }

    public String getSitecurrency() {
        return sitecurrency;
    }

    public void setSitecurrency(String sitecurrency) {
        this.sitecurrency = sitecurrency;
    }

    public String getMarketingEffort() {
        return marketingEffort;
    }

    public void setMarketingEffort(String marketingEffort) {
        this.marketingEffort = marketingEffort;
    }

    public String getIsnotagent() {
        return isnotagent;
    }

    public void setIsnotagent(String isnotagent) {
        this.isnotagent = isnotagent;
    }

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getInquiry() {
		return inquiry;
	}

	public void setInquiry(String inquiry) {
		this.inquiry = inquiry;
	}

	public String getBookingnumber() {
		return bookingnumber;
	}

	public void setBookingnumber(String bookingnumber) {
		this.bookingnumber = bookingnumber;
	}

	public String getVsnumber() {
		return vsnumber;
	}

	public void setVsnumber(String vsnumber) {
		this.vsnumber = vsnumber;
	}

	public String getFrom_email() {
		return from_email;
	}

	public void setFrom_email(String from_email) {
		this.from_email = from_email;
	}

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}
	
}