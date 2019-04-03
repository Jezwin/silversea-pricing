package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.DeckBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Page.class)
public class ShipModel implements ShipInterface{

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/excerpt")
    @Optional
    private String excerpt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipCode")
    @Optional
    private String shipCode;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shipId")
    @Optional
    private String shipId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/heroBanner")
    @Optional
    private String heroBanner;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetGallerySelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/photoVideoSuiteSelectionReference")
    @Optional
    private String photoVideoSuiteSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/deckPlan")
    @Optional
    private String deckPlan;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/guestsCapacity")
    @Optional
    private String guestsCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/shortVoyageDescription")
    @Optional
    private String shortVoyageDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/crewCapacity")
    @Optional
    private String crewCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/builtDate")
    @Optional
    private String builtDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/refurbDate")
    @Optional
    private String refurbDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/tonnage")
    @Optional
    private String tonnage;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/lengthFt")
    @Optional
    private String lengthFt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/lengthM")
    @Optional
    private String lengthM;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/widthFt")
    @Optional
    private String widthFt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/widthM")
    @Optional
    private String widthM;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/speed")
    @Optional
    private String speed;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/registry")
    @Optional
    private String registry;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/image/fileReference")
    @Optional
    private String thumbnail;

    @Inject
    @Named("dinings")
    private List<DiningModel> dinings;

    @Inject
    @Named("public-areas")
    private List<PublicAreaModel> publicAreas;

    @Inject
    @Named("suites")
    private List<SuiteModel> suites;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/officersCapacity")
    @Optional
    private String officersCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/connectingSuites")
    @Optional
    private String connectingSuites;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/handicapSuites")
    @Optional
    private String handicapSuites;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/thirdGuestCapacity")
    @Optional
    private String thirdGuestCapacity;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/passengersDeck")
    @Optional
    private String passengersDeck;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String path;

    private String name;

    private List<DeckInfoModel> deckInfoList;

    private List<ShipFactsAndPlanByDateModel> propsfactsAndPlanByDate;

    @PostConstruct
    private void init() {
        path = page.getPath();
        name = page.getName();
        propsfactsAndPlanByDate = readNodeFactsAndPlanDate();
    }

    public Page getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getShipCode() {
        return shipCode;
    }

    public String getShipId() {
        return shipId;
    }

    public String getHeroBanner() {
        return heroBanner;
    }

    public String getAssetGallerySelectionReference() {
        return assetGallerySelectionReference;
    }

    public String getPhotoVideoSuiteSelectionReference() {
        return photoVideoSuiteSelectionReference;
    }

    public String getDeckPlan() {
        return deckPlan;
    }

    public List<DiningModel> getDinings() {
        return dinings;
    }

    public List<PublicAreaModel> getPublicAreas() {
        return publicAreas;
    }

    public List<SuiteModel> getSuites() {
        return suites;
    }

    public String getGuestsCapacity() {
        return guestsCapacity;
    }

    public String getCrewCapacity() {
        return crewCapacity;
    }

    public String getBuiltDate() {
        return builtDate;
    }

    public String getRefurbDate() {
        return refurbDate;
    }

    public String getTonnage() {
        return tonnage;
    }

    @Override
    public Date getValidityDate() {
        return null;
    }

    public String getLengthFt() {
        return lengthFt;
    }

    public String getLengthM() {
        return lengthM;
    }

    public String getSpeed() {
        return speed;
    }

    public String getRegistry() {
        return registry;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPath() {
        return path;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getName() {
        return name;
    }

    public String getOfficersCapacity() {
        return officersCapacity;
    }

    public void setOfficersCapacity(String officersCapacity) {
        this.officersCapacity = officersCapacity;
    }

    public String getConnectingSuites() {
        return connectingSuites;
    }

    public void setConnectingSuites(String connectingSuites) {
        this.connectingSuites = connectingSuites;
    }

    public String getHandicapSuites() {
        return handicapSuites;
    }

    public void setHandicapSuites(String handicapSuites) {
        this.handicapSuites = handicapSuites;
    }

    public String getThirdGuestCapacity() {
        return thirdGuestCapacity;
    }

    public void setThirdGuestCapacity(String thirdGuestCapacity) {
        this.thirdGuestCapacity = thirdGuestCapacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ShipModel)) {
            return false;
        }

        final ShipModel objShipModel = (ShipModel) obj;

        return objShipModel.getPath().equals(getPath());
    }

    public List<DeckInfoModel> getDeckInfoList() {
        if (deckInfoList == null || deckInfoList.isEmpty()) {
            deckInfoList = readNodeDeckInfo("deckInfoNode");
        }
        return deckInfoList;
    }

    private List<DeckInfoModel> readNodeDeckInfo(String nodeToTransformName) {
        LOGGER.debug("ShipModelTransformDeckInfo start transform deck info from node to list for {}", this.name);
        List<DeckInfoModel> deckInfoList = new ArrayList<>();
        boolean nodeIsPresent = this.page.getContentResource(nodeToTransformName) != null;
        if (nodeIsPresent) {
            Resource deckInfoNodeResource = this.page.getContentResource(nodeToTransformName);
            boolean canReadChildren = deckInfoNodeResource.hasChildren();
            if (canReadChildren) {
                Iterator<Resource> iteratorChildren = deckInfoNodeResource.getChildren().iterator();
                while (iteratorChildren.hasNext()) {
                    DeckInfoModel entryToAdd = iteratorChildren.next().adaptTo(DeckInfoModel.class);
                    deckInfoList.add(entryToAdd);
                }
            }
        }
        LOGGER.debug("ShipModelTransformDeckInfo completed function name :{}", this.name);
        return deckInfoList;
    }

    public void setDeckInfoList(List<DeckInfoModel> deckInfoList) {
        this.deckInfoList = deckInfoList;
    }

    public String getShortVoyageDescription() {
        return shortVoyageDescription;
    }

    public String getWidthFt() {
        return widthFt;
    }

    public String getWidthM() {
        return widthM;
    }

    public String getPassengersDeck() {
        return passengersDeck;
    }


    public List<ShipFactsAndPlanByDateModel> getPropsfactsAndPlanByDate() {
        return propsfactsAndPlanByDate;
    }

    private List<ShipFactsAndPlanByDateModel> readNodeFactsAndPlanDate() {
        LOGGER.debug("Start read node FactsAndPlan for {}", this.name);
        boolean nodeIsPresent = page.getContentResource("factsAndPlanNode") != null;
        List<ShipFactsAndPlanByDateModel> shipFactsAndPlanByDateModelList = new ArrayList<>();
        if (nodeIsPresent) {
            Resource factsAndPlanDateResource = page.getContentResource("factsAndPlanNode");
            boolean canReadChildren = factsAndPlanDateResource != null && factsAndPlanDateResource.hasChildren();
            if (canReadChildren) {
                Iterator<Resource> iteratorChildren = factsAndPlanDateResource.getChildren().iterator();
                while (iteratorChildren.hasNext()) {
                    ShipFactsAndPlanByDateModel entryToAdd = iteratorChildren.next().adaptTo(ShipFactsAndPlanByDateModel.class);
                    shipFactsAndPlanByDateModelList.add(entryToAdd);
                }
            }
        }
        LOGGER.debug("End read node FactsAndPlan for {}", this.name);
        return shipFactsAndPlanByDateModelList;
    }
}
