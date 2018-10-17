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
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Page.class)
public class ShipModel {

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

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String path;

    private String name;

    private List<DeckBean> deckInfoList;

    @PostConstruct
    private void init() {
        path = page.getPath();
        name = page.getName();
    }

    private List<DeckBean> transformDeckInformationFromNodeToList(String nodeToTransformName) {
        LOGGER.debug("ShipModelTransformDeckInfo start transform deck info from node to list for {}", this.name);
        try {
            if (this.page.getContentResource("deckInfoNode") != null) {
                Node nodeToTransform = this.page.getContentResource("deckInfoNode").adaptTo(Node.class);
                if (nodeToTransform != null && nodeToTransform.hasNodes()) {
                    List<DeckBean> listResult = new ArrayList<>();
                    Iterator<Node> nodeIterator = nodeToTransform.getNodes();
                    while (nodeIterator.hasNext()) {
                        DeckBean deckBean = new DeckBean();
                        Node node = nodeIterator.next();
                        if (node.hasProperty("deckLevel") && node.getProperty("deckLevel") != null) {
                            String propertyDeckLevel = node.getProperty("deckLevel").getString();
                            deckBean.setLevel(propertyDeckLevel);
                        }
                        if (node.hasProperty("deckImageTop") && node.getProperty("deckImageTop") != null) {
                            String propertyImageTopPath = node.getProperty("deckImageTop").getString();
                            deckBean.setImagePath(propertyImageTopPath);
                        }
                        listResult.add(deckBean);
                        LOGGER.debug("ShipModelTransformDeckInfo name: {} add new deck bean {}", this.name, deckBean.toString());
                    }
                    LOGGER.debug("ShipModelTransformDeckInfo name: {}  list of  {} elements", listResult.size());
                    return listResult;
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("RepositoryException in Ship Model during get deckInfoNode {}", e.getMessage());
        }
        LOGGER.debug("ShipModelTransformDeckInfo completed function name :{}", this.name);
        return null;
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

    public List<DeckBean> getDeckInfoList() {
        if (deckInfoList == null) {
            deckInfoList = transformDeckInformationFromNodeToList("deckInfoNode");
        }
        return deckInfoList;
    }

    public void setDeckInfoList(List<DeckBean> deckInfoList) {
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
}
