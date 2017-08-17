package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.components.beans.SuiteVariation;
import com.silversea.aem.enums.Currency;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Model(adaptables = Page.class)
public class SuiteModel extends AbstractModel implements ShipAreaModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(SuiteModel.class);

    @Inject @Self
    private Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE) @Optional
    private String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    private String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference") @Optional
    private String assetSelectionReference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/bedroomsInformation") @Optional
    private String bedroomsInformation;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/plan") @Optional
    private String plan;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/locationImage") @Optional
    private String locationImage;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/virtualTour") @Optional
    private String virtualTour;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/image/fileReference") @Optional
    private String thumbnail;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteFeature") @Optional
    private String[] features;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteReference") @Optional
    private String suiteReference;

    // TODO replace by injector
    private SuiteModel genericSuite;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/suiteSubTitle") @Optional
    private String suiteSubTitle;

    private String[] splitSuiteSubTitle;

    private TagManager tagManager;

    @PostConstruct
    private void init() {
        final PageManager pageManager = page.getPageManager();

        // init reference
        if (suiteReference != null) {
            final Page genericSuitePage = pageManager.getPage(suiteReference);

            if (genericSuitePage != null) {
                genericSuite = genericSuitePage.adaptTo(SuiteModel.class);
            }
        }

        // init suite sub title
        if (suiteSubTitle != null) {
            splitSuiteSubTitle = suiteSubTitle.split("\\r?\\n");
        }
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription != null ? longDescription :
                (genericSuite != null ? genericSuite.getLongDescription() : null);
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference != null ? assetSelectionReference :
                (genericSuite != null ? genericSuite.getAssetSelectionReference() : null);
    }

    public String[] getSuiteSubTitle() {
        return splitSuiteSubTitle;
    }

    public String getBedroomsInformation() {
        return bedroomsInformation;
    }

    public String getPlan() {
        return plan;
    }

    public String[] getFeatures() {
        return features;
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
        return page;
    }























    private PriceData lowestPrice;

    private List<SuiteVariation> variations;

    public List<SuiteVariation> getVariations() {
        return variations;
    }

    public PriceData getLowestPrice() {
        return lowestPrice;
    }

    public void initLowestPrice(Node lowestPriceNode, String geoMarketCode) {
        lowestPrice = getPriceByGeoMarketCode(lowestPriceNode, geoMarketCode, "priceMarketCode");
    }

    public void initVarirations(Node suiteNode, String geoMarketCode) {
        variations = new ArrayList<SuiteVariation>();
        try {
            NodeIterator variationNodes = suiteNode.getNodes();
            if (variationNodes != null && variationNodes.hasNext()) {
                while (variationNodes.hasNext()) {
                    Node node = variationNodes.nextNode();
                    if (!StringUtils.equals(node.getName(), "lowest-prices")) {
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
                    Tag tag = tagManager.resolve(tags[0].getString());
                    if (tag != null && StringUtils.equals(geoMarketCode, tag.getTitle())
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
}
