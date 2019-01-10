package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

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

    @Inject @Optional @Named(JcrConstants.JCR_CONTENT + "/bigItineraryMap")
    private String bigItineraryMap;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/comboCruiseFareAdditions")
    @Optional
    private String comboCruiseFareAdditions;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/comboCruiseFareAdditionsDescription")
    @Optional
    private String comboCruiseFareAdditionsDescription;

    private List<String> splitComboCruiseFareAdditions = new ArrayList<>();

    private List<SegmentModel> segments = new ArrayList<>();

    private ShipModel ship;

    private List<PriceModel> prices = new ArrayList<>();

    private String thumbnail;

    private int duration = 0;

    private String departurePortName;

    private String arrivalPortName;

    private Calendar startDate;

    private Calendar endDate;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init ship
        final Page shipPage = pageManager.getPage(shipReference);
        if (shipPage != null) {
            ship = shipPage.adaptTo(ShipModel.class);
        }

        // init prices
        final Resource suitesResource = page.getContentResource().getChild("suites");
        if (suitesResource != null) {
            CruiseUtils.collectPrices(prices, suitesResource);
            prices.sort((o1, o2) -> -o1.getPrice().compareTo(o2.getPrice()));
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
                        startDate = segmentModel.getCruise().getStartDate();
                    }

                    if (!children.hasNext()) {
                        arrivalPortName = segmentModel.getCruise().getArrivalPortName();
                        endDate = segmentModel.getCruise().getEndDate();
                    }

                    try {
                        duration += Integer.valueOf(segmentModel.getCruise().getDuration());
                    } catch (NumberFormatException ignored) {}
                }

                segments.add(segmentModel);
            }
        }
        duration = duration + 1;

        if (comboCruiseFareAdditions != null) {
            final String[] split = comboCruiseFareAdditions.split("\\r?\\n");

            if (split.length > 0) {
                splitComboCruiseFareAdditions.addAll(Arrays.asList(split));
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
        return segments.size();
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

    public List<PriceModel> getPrices() {
        return prices;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public List<SegmentModel> getSegments() {
        return segments;
    }
    
    public Page getPage() {
        return page;
    }

    public List<String> getComboCruiseFareAdditions() {
        return splitComboCruiseFareAdditions;
    }

    public String getArrivalPortName() {
        return arrivalPortName;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getBigItineraryMap() {
        return bigItineraryMap;
    }

    public String getComboCruiseFareAdditionsDescription() {
        return comboCruiseFareAdditionsDescription;
    }
}