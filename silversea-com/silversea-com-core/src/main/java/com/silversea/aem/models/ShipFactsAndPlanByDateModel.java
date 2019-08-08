package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class)
public class ShipFactsAndPlanByDateModel implements ShipInterface {

    @Inject
    @Optional
    private String builtDate;

    @Inject
    @Optional
    private String connectingSuites;

    @Inject
    @Optional
    private String crewCapacity;

    @Inject
    @Optional
    private String guestsCapacity;

    @Inject
    @Optional
    private String handicapSuites;

    @Inject
    @Optional
    private String lengthFt;

    @Inject
    @Optional
    private String lengthM;

    @Inject
    @Optional
    private String officersCapacity;

    @Inject
    @Optional
    private String passengersDeck;

    @Inject
    @Optional
    private String refurbDate;

    @Inject
    @Optional
    private String registry;

    @Inject
    @Optional
    private String speed;

    @Inject
    @Optional
    private String thirdGuestCapacity;

    @Inject
    @Optional
    private String tonnage;

    @Inject
    @Optional
    private Date validityDate;

    @Inject
    @Optional
    private String widthFt;

    @Inject
    @Optional
    private String widthM;

    @Inject
    @Optional
    private Resource deckInfoNode;

    @Inject
    @Optional
    private String deckPlan;

    private List<DeckInfoModel> deckInfo;

    @PostConstruct
    private void init() {
        deckInfo = readNodeDeckInfo();
    }


    @Override
    public String getBuiltDate() {
        return this.builtDate;
    }

    public String getConnectingSuites() {
        return connectingSuites;
    }

    public void setConnectingSuites(String connectingSuites) {
        this.connectingSuites = connectingSuites;
    }

    public String getCrewCapacity() {
        return crewCapacity;
    }

    public void setCrewCapacity(String crewCapacity) {
        this.crewCapacity = crewCapacity;
    }

    public String getGuestsCapacity() {
        return guestsCapacity;
    }

    public void setGuestsCapacity(String guestsCapacity) {
        this.guestsCapacity = guestsCapacity;
    }

    public String getHandicapSuites() {
        return handicapSuites;
    }

    public void setHandicapSuites(String handicapSuites) {
        this.handicapSuites = handicapSuites;
    }

    public String getLengthFt() {
        return lengthFt;
    }

    public void setLengthFt(String lengthFt) {
        this.lengthFt = lengthFt;
    }

    public String getLengthM() {
        return lengthM;
    }

    public void setLengthM(String lengthM) {
        this.lengthM = lengthM;
    }

    public String getOfficersCapacity() {
        return officersCapacity;
    }

    public void setOfficersCapacity(String officersCapacity) {
        this.officersCapacity = officersCapacity;
    }

    public String getPassengersDeck() {
        return passengersDeck;
    }

    public void setPassengersDeck(String passengersDeck) {
        this.passengersDeck = passengersDeck;
    }

    public String getRefurbDate() {
        return refurbDate;
    }

    public void setRefurbDate(String refurbDate) {
        this.refurbDate = refurbDate;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getThirdGuestCapacity() {
        return thirdGuestCapacity;
    }

    public void setThirdGuestCapacity(String thirdGuestCapacity) {
        this.thirdGuestCapacity = thirdGuestCapacity;
    }

    public String getTonnage() {
        return tonnage;
    }

    public void setTonnage(String tonnage) {
        this.tonnage = tonnage;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public String getWidthFt() {
        return widthFt;
    }

    public void setWidthFt(String widthFt) {
        this.widthFt = widthFt;
    }

    public String getWidthM() {
        return widthM;
    }

    public List<DeckInfoModel> getDeckInfoList() {
        return this.deckInfo;
    }

    public String getDeckPlan() {
        return this.deckPlan;
    }

    public void setWidthM(String widthM) {
        this.widthM = widthM;
    }

    private List<DeckInfoModel> readNodeDeckInfo() {
        List<DeckInfoModel> deckInfoList = new ArrayList<>();
        boolean nodeIsPresent = deckInfoNode != null;
        if (nodeIsPresent) {
            boolean canReadChildren = deckInfoNode.hasChildren();
            if (canReadChildren) {
                Iterator<Resource> iteratorChildren = deckInfoNode.getChildren().iterator();
                while (iteratorChildren.hasNext()) {
                    DeckInfoModel entryToAdd = iteratorChildren.next().adaptTo(DeckInfoModel.class);
                    deckInfoList.add(entryToAdd);
                }
            }
        }
        return deckInfoList;
    }

    public void setDeckInfo(List<DeckInfoModel> deckInfo) {
        this.deckInfo = deckInfo;
    }

    public void setBuiltDate(String builtDate) {
        this.builtDate = builtDate;
    }

    public void setDeckPlan(String deckPlan) {
        this.deckPlan = deckPlan;
    }

}