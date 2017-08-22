package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Page.class)
public class ComboCruiseModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruiseModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    private String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/apiTitle") @Optional
    private String apiTitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/portsAmount") @Optional
    private Long portsAmount;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/countriesAmount") @Optional
    private Long countriesAmount;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/shipReference")
    private String shipReference;

    private List<SegmentModel> segments = new ArrayList<>();

    private ShipModel ship;

    private List<PriceModel> prices = new ArrayList<>();

    private String thumbnail;

    private int routesAmount = 0;

    private int duration = 0;

    private String departurePortName;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();
        final ResourceResolver resourceResolver = page.getContentResource().getResourceResolver();

        // init ship
        final Page shipPage = pageManager.getPage(shipReference);
        if (shipPage != null) {
            ship = shipPage.adaptTo(ShipModel.class);
        }

        // init prices
        final Resource suitesResource = page.getContentResource().getChild("suites");
        if (suitesResource != null) {
            CruiseUtils.collectPrices(prices, suitesResource);
        }

        // init thumbnail
        final Resource imageResource = page.getContentResource().getChild("image");
        if (imageResource != null) {
            thumbnail = imageResource.getValueMap().get("fileReference", String.class);
        }

        // init segments
        final Iterator<Page> children = page.listChildren();
        while (children.hasNext()) {
            final SegmentModel segmentModel = children.next().adaptTo(SegmentModel.class);

            if (segmentModel != null) {
                if (segmentModel.getCruise() != null) {
                    if (departurePortName == null) {
                        departurePortName = segmentModel.getCruise().getDeparturePortName();
                    }

                    routesAmount += segmentModel.getCruise().getItineraries().size();

                    try {
                        duration += Integer.valueOf(segmentModel.getCruise().getDuration());
                    } catch (NumberFormatException ignored) {}
                }

                segments.add(segmentModel);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public Long getPortsAmount() {
        return portsAmount;
    }

    public int getRoutesAmount() {
        return routesAmount;
    }

    public Long getCountriesAmount() {
        return countriesAmount;
    }

    public String getDeparturePortName() {
        return departurePortName;
    }

    public ShipModel getShip() {
        return ship;
    }

    /**
     * @return prices of each suite variation for this cruise
     */
    public List<PriceModel> getPrices() {
        return prices;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public List<SegmentModel> getSegments() {
        return segments;
    }
}