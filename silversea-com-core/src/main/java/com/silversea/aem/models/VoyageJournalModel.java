package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import io.swagger.client.model.Ship;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class VoyageJournalModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(VoyageJournalModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Self
    private ValueMap pageProperties;

//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
//    private String title;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/duration")
//    @Optional
//    private String duration;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/date")
//    @Optional
//    private Date date;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
//    @Optional
//    private String assetSelectionReference;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/shipReference")
//    @Optional
//    private String shipReference;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/destinationReference")
//    @Optional
//    private DestinationModel destinationReference;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/cruiseReference")
//    @Optional
//    private CruiseModel cruiseReference;
//
//    @Inject
//    @Named(JcrConstants.JCR_CONTENT + "/relatedCruiseReference")
//    @Optional
//    private CruiseModel[] relatedCruiseReference;

    private ShipModel ship;

    protected ResourceResolver resourceResolver;

    protected Resource resource;

    @PostConstruct
    private void init() {
        //src = ship.adaptTo(Resource.class);
//        if (pageProperties.get("shipReference", String.class) != null) {
//            LOGGER.debug("-----> YOUHOU {}", pageProperties.get("shipReference", String.class));
//            resource = resourceResolver.getResource(pageProperties.get("shipReference", String.class));
//            LOGGER.debug("-----> YOUHOU2 {}", resource.getPath());
//            if (resource != null) {
//                ship = resource.adaptTo(ShipModel.class);
//                LOGGER.debug("-----> YOUHOU3 {}", ship.getTitle());
//
//            }
//
        }
//    }

    public ValueMap getPageProperties() {
        return pageProperties;
    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getDuration () {
//        return duration;
//    }
//
//    public String getDate() {
//        return date.toString();
//    }
//
//    public String getAssetSelectionReference() {
//        return assetSelectionReference;
//    }
//
//    public String getShipReference() { return shipReference; }
//
    public ShipModel getShip() { return ship; }
//
//    public DestinationModel getDestinationReference() { return destinationReference; }
//
//    public CruiseModel getCruiseReference() { return cruiseReference; }
//
//    public CruiseModel[] getRelatedCruiseReference() { return relatedCruiseReference; }
//

}
