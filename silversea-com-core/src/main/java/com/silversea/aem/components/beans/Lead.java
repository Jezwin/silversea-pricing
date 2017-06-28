package com.silversea.aem.components.beans;

public class Lead {
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String comments;
    private String requestsource;
    // Subscribe to newletters
    private String att02;
    private Short workingwithagent;
    //Request a quote
    private String voyagename;
    private String departuredate;
    private String voyagelength;
    private String shipname;
    private String suitecategory;
    private String suitevariation;
    private String price;

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

    public String getPrice() {
        return price;
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
     * @param title the title to set
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
     * @param firstname the firstname to set
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
     * @param lastname the lastname to set
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
     * @param email the email to set
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
     * @param phone the phone to set
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
     * @param comments the comments to set
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
     * @param requestsource the requestsource to set
     */
    public void setRequestsource(String requestsource) {
        this.requestsource = requestsource;
    }

    /**
     * @return the att02
     */
    public String getAtt02() {
        return att02;
    }

    /**
     * @param att02 the att02 to set
     */
    public void setAtt02(String att02) {
        this.att02 = att02;
    }

    /**
     * @return the workingwithagent
     */
    public Short getWorkingwithagent() {
        return workingwithagent;
    }

    /**
     * @param workingwithagent the workingwithagent to set
     */
    public void setWorkingwithagent(Short workingwithagent) {
        this.workingwithagent = workingwithagent;
    }
}