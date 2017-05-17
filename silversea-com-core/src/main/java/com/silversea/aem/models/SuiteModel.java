package com.silversea.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.components.beans.SuiteVariation;
import com.silversea.aem.enums.Currency;

@Model(adaptables = Page.class)
public class SuiteModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SuiteModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    @Optional
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/bedroomsInformation")
    @Optional
    private String bedroomsInformation;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/plan")
    @Optional
    private String plan;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/locationImage")
    @Optional
    private String locationImage;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/virtualTour")
    @Optional
    private String virtualTour;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/suiteFeature")
    @Optional
    private String[] features;

    private PriceData lowestPrice;

    private String[] suiteSubTitle;

    private String thumbnail;

    private List<SuiteVariation> variations;

    private ResourceResolver resourceResolver;

    private TagManager tagManager;

    @PostConstruct
    private void init() {
        resourceResolver = page.getContentResource().getResourceResolver();
        tagManager = resourceResolver.adaptTo(TagManager.class);
        thumbnail = page.getProperties().get("image/fileReference", String.class);
        suiteSubTitle = parseSuitesSubtitle();
        title = initProperty("suiteReference", title, "title");
        longDescription = initProperty("suiteReference", longDescription, "longDescription");
        assetSelectionReference = initProperty("suiteReference", assetSelectionReference, "assetSelectionReference");
    }

    public void initLowestPrice(Node lowestPriceNode, String geoMarketCode) {
        lowestPrice = getPriceByGeoMarketCode(lowestPriceNode, geoMarketCode, "priceMarketCode");
    }

    // TODO: duplicated code
    private String[] parseSuitesSubtitle() {
        String[] subTitle = null;
        String text = page.getProperties().get("suiteSubTitle", String.class);
        if (StringUtils.isNotEmpty(text)) {
            subTitle = text.split("\\r?\\n");
        }
        return subTitle;
    }

    private String initProperty(String reference, String property, String referenceProperty) {
        String value = property;
        if (StringUtils.isEmpty(property)) {
            Page page = getPageReference(reference);
            if (page != null) {
                value = page.getProperties().get(referenceProperty, String.class);
            }

        }
        return value;
    }

    private Page getPageReference(String reference) {
        Page pageReference = null;
        String path = page.getProperties().get(reference, String.class);
        Resource resource = resourceResolver.resolve(path);
        if (resource != null) {
            pageReference = resource.adaptTo(Page.class);
        }

        return pageReference;
    }

    public void initVarirations(Node suiteNode, String geoMarketCode) {
        variations = new ArrayList<SuiteVariation>();
        try {
            NodeIterator variationNodes = suiteNode.getNodes();
            if (variationNodes != null && variationNodes.hasNext()) {
                while (variationNodes.hasNext()) {
                    Node node = variationNodes.nextNode();
                    if (!StringUtils.equals(node.getName(), "lowest-price")) {
                        PriceData price = getPrice(node, geoMarketCode);
                        String name = Objects.toString(node.getProperty("suiteCategoryCod").getValue());
                        SuiteVariation suiteVariation = new SuiteVariation();
                        suiteVariation.setPrice(price);
                        suiteVariation.setName(name);
                        variations.add(suiteVariation);
                    }

                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception while building suites variations", e);
        }
    }

    private PriceData getPriceByGeoMarketCode(Node pricesNode, String geoMarketCode, String property) {
        PriceData price = null;
        try {
            NodeIterator nodes = pricesNode.getNodes();
            if (nodes != null && nodes.hasNext()) {
                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    String priceMarketCode = Objects.toString(node.getProperty(property).getValue());

                    if (StringUtils.equals(geoMarketCode, priceMarketCode)) {
                        String value = Objects.toString(node.getProperty("price").getValue());
                        price = initPrice(geoMarketCode, value);
                        break;
                    }
                }
            }

        } catch (RepositoryException e) {
            LOGGER.error("Exception while calculating prices", e);
        }

        return price;
    }

    private PriceData getPrice(Node pricesNode, String geoMarketCode) {
        PriceData price = null;
        try {
            NodeIterator nodes = pricesNode.getNodes();
            if (nodes != null && nodes.hasNext()) {
                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    Value[] tags = node.getProperty("cq:tags").getValues();
                    Currency currency = getCurrencyByMarKetCode(geoMarketCode);
                    String suitePriceCurrency = Objects.toString(node.getProperty("currency").getValue());
                    // TODO
                    Tag tag = tagManager.resolve(tags[0].getString());
                    if (StringUtils.equals(geoMarketCode, tag.getTitle())
                            && StringUtils.equals(suitePriceCurrency, currency.getValue())) {
                        String value = Objects.toString(node.getProperty("price").getValue());
                        price = initPrice(geoMarketCode, value);
                        break;
                    }
                }
            }

        } catch (RepositoryException e) {
            LOGGER.error("Exception while calculating prices", e);
        }

        return price;
    }

    // TODO: duplicated code
    PriceData initPrice(String geoMarketCode, String value) {
        PriceData price = new PriceData();
        Currency currency = getCurrencyByMarKetCode(geoMarketCode);
        price.setCurrency(currency.getLabel());
        price.setValue(value);
        if (StringUtils.isNumeric(value)) {
            price.setWaitList(false);
        } else {
            price.setWaitList(true);
        }
        return price;
    }

    // TODO: duplicated code
    private Currency getCurrencyByMarKetCode(String marKetCode) {
        return Arrays.stream(Currency.values()).filter(e -> e.name().equals(marKetCode)).findFirst()
                .orElseThrow(() -> new IllegalStateException());
    }

    public String getTitle() {
        return title;
    }

    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public String[] getSuiteSubTitle() {
        return suiteSubTitle;
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getFeatures() {
        return features;
    }

    public List<SuiteVariation> getVariations() {
        return variations;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public String getVirtualTour() {
        return virtualTour;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Page getPage() {
        // TODO remove getter for properties accessible from Page
        return page;
    }
}
