package com.silversea.ssc.aem.components.editorial;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.common.base.Strings;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.services.RunModesService;
import com.silversea.aem.utils.AssetUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HeroBannerDestination extends AbstractGeolocationAwareUse {

    static private final Logger LOGGER = LoggerFactory.getLogger(HeroBannerDestination.class);
    static private final String GEO_PROP = "listText";
    static private final String[] LIST_PROPERTIES_OLD_FRAMEWORK = {"backgroundPositionDesktop", "backgroundPositionTablet", "backgroundPositionMobile", "backgroundPositionDesktopgeo",
            "backgroundPositionTabletgeo", "backgroundPositionMobilegeo"};

    private ValueMap props;
    private String title;
    private String description;

    private String titleMobile;
    private String titleTablet;
    private String descriptionMobile;
    private DeviceProperty background = new DeviceProperty("assetReference");
    private DeviceProperty backgPosition = new DeviceProperty("backgroundPosition");

    private DeviceProperty showScrollDown = new DeviceProperty("showScrollDown");
    private DeviceProperty textScrollDown = new DeviceProperty("scrollActionText");
    private DeviceProperty colorScrollDown = new DeviceProperty("scrollActionTextColor");
    private boolean inlineGallery;
    private String inlineGalleryId;

    private String randomId;

    private RunModesService runMode;
    private ValueMap sbProperties;


    @Override
    public void activate() throws Exception {
        super.activate();
        runMode = getSlingScriptHelper().getService(RunModesService.class);
        props = getProperties();
        sbProperties = new ValueMapDecorator(new LinkedHashMap<>());
        if (runMode.isAuthor()) {
            switchToFrameworkVar();
        }
        sbProperties.put("sscFwBackgroundPositionDesktop", fromProps("sscFwBackgroundPositionDesktop"));
        sbProperties.put("sscFwBackgroundPositionTablet", fromProps("sscFwBackgroundPositionTablet"));
        sbProperties.put("sscFwBackgroundPositionMobile", fromProps("sscFwBackgroundPositionMobile"));

        inlineGallery = fromPropsBool("inlineGallery");
        setBackground();
        setScrollDown();
        title = fromProps("text");
        titleMobile = fromProps("titleMobile");
        titleTablet = fromProps("titleTablet");
        description = fromProps("description");
        descriptionMobile = fromProps("descriptionMobile");
        if (props.get("autoDescription", Boolean.FALSE)) {
            description = getPageProperties().get("jcr:description", String.class);
        }
        setFromAdvancedGeoLocation();
        randomId = "hbid" + RandomStringUtils.randomAlphabetic(6);
    }

    private void switchToFrameworkVar() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
        Node node = getResource().adaptTo(Node.class);
        final Session session = getResourceResolver().adaptTo(Session.class);
        if (node == null || session == null) return;
        for (String prop : LIST_PROPERTIES_OLD_FRAMEWORK) {
            getProp(node, prop).ifPresent(value -> {
                try {
                    LOGGER.debug("Updating property {}", prop);
                    setNewFrameworkValue(node, prop, value);
                    LOGGER.debug("Property updated");
                    session.save();
                } catch (RepositoryException e) {
                    LOGGER.error("Cannot update resource", e);
                }
            });
        }
        if (getResource().hasChildren() && getResource().getChild(GEO_PROP) != null) {
            for (Resource child : getResource().getChild(GEO_PROP).getChildren()) {
                for (String prop : LIST_PROPERTIES_OLD_FRAMEWORK) {
                    Node nodeChild = child.adaptTo(Node.class);
                    getProp(nodeChild, prop).ifPresent(valueProp -> {
                        try {
                            LOGGER.debug("Updating property {}", prop);
                            setNewFrameworkValue(nodeChild, prop, valueProp);
                            LOGGER.debug("Property updated");
                            session.save();
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot update resource", e);
                        }
                    });
                }
            }
        }
    }

    private void setNewFrameworkValue(Node node, String prop, String value) throws RepositoryException {
        switch (prop) {
            case "backgroundPositionDesktop":
            case "backgroundPositionDesktopgeo":
                String newValue = value.replace("backgroundPosition", "").replace("Desktop", "").toLowerCase();
                node.getProperty(prop).remove();
                node.setProperty("sscFwBackgroundPositionDesktop", newValue);
                break;
            case "backgroundPositionTablet":
            case "backgroundPositionTabletgeo":
                newValue = value.replace("backgroundPosition", "").replace("Tablet", "").toLowerCase();
                node.getProperty(prop).remove();
                node.setProperty("sscFwBackgroundPositionTablet", newValue);
                break;
            case "backgroundPositionMobile":
            case "backgroundPositionMobilegeo":
                newValue = value.replace("backgroundPosition", "").replace("Mobile", "").toLowerCase();
                node.getProperty(prop).remove();
                node.setProperty("sscFwBackgroundPositionMobile", newValue);
                break;
        }
    }

    private void setScrollDown() {
        showScrollDown.setValues((key, device) -> (fromPropsBool(key) ? "displayBlock" : "displayNone") + device);
        textScrollDown.setValues(this::fromProps);
        colorScrollDown.setValues(this::fromProps);
    }

    private void setBackground() {
        Function<String, String> retrieveBackground =
                key -> withDefault(fromProps(key), fromProps("assetReferenceDesktop"));
        if (inlineGallery) {
            background.setValues(key -> {
                if (key.endsWith("Mobile")) {
                    return getFirstImageInlineGalleryPath("inlinegallery")
                            .orElseGet(() -> getFirstImageInlineGalleryPath("inlinegalleryLanding")
                                    .orElseGet(() -> getFirstImageInlineGalleryPath("inlineGallery")
                                            .orElseGet(() -> retrieveBackground.apply(key))));
                } else {
                    return retrieveBackground.apply(key);
                }
            });
        } else {
            background.setValues(retrieveBackground);
        }
        backgPosition.setValues(this::fromProps);
    }

    private void assignValues(Node node) {
        title = getProp(node, "titleGeo").orElse(title);
        description = getProp(node, "descriptionGeo").orElse(description);
        titleMobile = getProp(node, "titleMobileGeo").orElse(titleMobile);
        titleTablet = getProp(node, "titleTabletGeo").orElse(titleTablet);
        descriptionMobile = getProp(node, "descriptionMobileGeo").orElse(descriptionMobile);
        getProp(node, "sscFwBackgroundPositionDesktop").ifPresent(value-> {
            sbProperties.put("sscFwBackgroundPositionDesktop", value);
        });
        getProp(node, "sscFwBackgroundPositionTablet").ifPresent(value-> {
            sbProperties.put("sscFwBackgroundPositionTablet", value);
        });
        getProp(node, "sscFwBackgroundPositionMobile").ifPresent(value-> {
            sbProperties.put("sscFwBackgroundPositionMobile", value);
        });
        if (inlineGallery) {
            Function<String, String> retrieveBackground =
                    key -> withDefault(geoOrProps(node).apply(key), fromProps("assetReferenceDesktop"));
            background.setValues(key -> {
                if (key.endsWith("Mobile")) {
                    return getFirstImageInlineGalleryPath("inlinegallery")
                            .orElseGet(() -> getFirstImageInlineGalleryPath("inlinegalleryLanding")
                                    .orElseGet(() -> getFirstImageInlineGalleryPath("inlineGallery")
                                            .orElseGet(() -> retrieveBackground.apply(key))));
                } else {
                    return retrieveBackground.apply(key);
                }
            });
        } else {
            background.setValues(key -> withDefault(geoOrProps(node).apply(key), fromProps("assetReferenceDesktop")));
        }
        backgPosition.setValues(geoOrProps(node));
    }

    private void setFromAdvancedGeoLocation() {
        if (getResource().hasChildren() && getResource().getChild(GEO_PROP) != null) {
            for (Resource child : getResource().getChild(GEO_PROP).getChildren()) {
                Node node = child.adaptTo(Node.class);
                getProp(node, "geoTag").ifPresent(geoTagProperty -> {
                    for (String tag : geoTagProperty.split(",")) {
                        String[] geoTag = tag.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
                        if (geoTag != null && geoTag.length > 1 && isBestMatch(geoTag[1])) {
                            assignValues(node);
                            break;
                        }
                    }
                });
            }
        }
    }

    private Optional<String> getFirstImageInlineGalleryPath(String type) {
        String path = getCurrentPage().getPath();
        Map<String, String> map = new HashMap<String, String>();

        map.put("path", path);

        map.put("property", "sling:resourceType");
        if (type.equals("inlinegallery")) {
            map.put("property.value", "silversea/silversea-com/components/editorial/inlinegallery");
        } else if (type.equalsIgnoreCase("inlinegalleryLanding")) {
            map.put("property.value", "silversea/silversea-ssc/components/editorial/inlinegalleryLanding");
        } else if (type.equals("inlineGallery")) {
            map.put("property.value", "silversea/silversea-ssc/components/editorial/inlineGallery");
        }

        map.put("boolproperty", "enableInlineGalleryHB");
        map.put("boolproperty.value", "true");

        // Order By
        map.put("orderby", "@jcr:content/jcr:created");
        map.put("orderby.sort", "asc");
        map.put("orderby.case", "ignore");

        map.put("p.offset", "0");
        map.put("p.limit", "1");

        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(map),
                getResourceResolver().adaptTo(Session.class));

        SearchResult result = query.getResult();

        try {
            for (Hit hit : result.getHits()) {
                ValueMap properties = hit.getProperties();
                if (type.equals("inlinegallery")) {
                    this.inlineGalleryId = "#c-inline-gallery-" + properties.get("sscUUID", String.class);
                } else if (type.equalsIgnoreCase("inlinegalleryLanding")) {
                    this.inlineGalleryId = "#c-inline-gallery-landing-" + properties.get("sscUUID", String.class);
                } else if (type.equals("inlineGallery")) {
                    this.inlineGalleryId = "#c-inlinegallery-" + properties.get("sscUUID", String.class);
                }
                String assetSelectionReference = properties.get("assetSelectionReference", String.class);
                Resource resourceAsset = getResourceResolver().getResource(assetSelectionReference);

                if (resourceAsset != null) {
                    Asset asset = resourceAsset.adaptTo(Asset.class);
                    if (asset != null) {
                        if (!DamUtil.isImage(asset)) {
                            String dcFormat = asset.getMetadata().get(DamConstants.DC_FORMAT) != null
                                    ? asset.getMetadata().get(DamConstants.DC_FORMAT).toString()
                                    : null;
                            if (dcFormat.contains(S7damConstants.S7_MIXED_MEDIA_SET)) {
                                List<Asset> assetlist = AssetUtils.buildAssetList(assetSelectionReference,
                                        getResourceResolver());
                                if (assetlist.size() > 0) {
                                    return Optional.ofNullable(assetlist.get(0).getPath());
                                }
                            }
                        } else {
                            return Optional.ofNullable(assetSelectionReference);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> getProp(Node node, String key) {
        try {
            return Optional.ofNullable(node.getProperty(key)).map(property -> {
                try {
                    return property.getString();
                } catch (RepositoryException exception) {
                    return null;
                }
            });
        } catch (Throwable throwable) {
            return Optional.empty();
        }
    }

    private Function<String, String> geoOrProps(Node node) {
        return key -> getProp(node, key + "geo").orElse(fromProps(key));
    }

    private String fromProps(String key) {
        return props.get(key, String.class);
    }

    private static boolean toBool(String value) {
        return "true".equalsIgnoreCase(Strings.nullToEmpty(value).trim());
    }

    private boolean fromPropsBool(String key) {
        return toBool(props.get(key, String.class));
    }

    private String withDefault(String value, String defaultValue) {
        return Optional.ofNullable(Strings.emptyToNull(value)).orElse(defaultValue);
    }

    public String getTitle() {
        return title;
    }

    public String getTitleMobile() {
        return titleMobile;
    }

    public String getTitleTablet() {
        return titleTablet;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionMobile() {
        return descriptionMobile;
    }

    public String getBackgroundDesktop() {
        return background.getDesktopValue();
    }

    public String getBackgroundTablet() {
        return background.getTabletValue();
    }

    public String getRandomId() {
        return randomId;
    }

    public String getBackgroundMobile() {
        return background.getMobileValue();
    }

    public String getBackgroundPosition() {
        return backgPosition.getValues();
    }

    public String getScrollDownShowDesktop() {
        return showScrollDown.getDesktopValue();
    }

    public String getScrollDownShowTablet() {
        return showScrollDown.getTabletValue();
    }

    public String getScrollDownShowMobile() {
        return showScrollDown.getMobileValue();
    }

    public String getScrollActionTextDesktop() {
        return textScrollDown.getDesktopValue();
    }

    public String getScrollActionTextTablet() {
        return textScrollDown.getTabletValue();
    }

    public String getScrollActionTextMobile() {
        return textScrollDown.getMobileValue();
    }

    public String getScrollActionTextColorDesktop() {
        return colorScrollDown.getDesktopValue();
    }

    public String getScrollActionTextColorTablet() {
        return colorScrollDown.getTabletValue();
    }

    public String getScrollActionTextColorMobile() {
        return colorScrollDown.getMobileValue();
    }

    public String getInlineGalleryId() {
        return inlineGalleryId;
    }

    public ValueMap getSbProperties() {
        return sbProperties;
    }

    private static class DeviceProperty {

        private final String name;
        private final String suffix;
        private String mobileValue;
        private String tabletValue;
        private String desktopValue;

        DeviceProperty(String name) {
            this.name = name;
            this.suffix = "";
        }

        DeviceProperty(String name, String suffix) {
            this.name = name;
            this.suffix = suffix;
        }

        String getKeyOf(String device) {
            return name + device + suffix;
        }

        public String getValues() {
            return desktopValue + " " + tabletValue + " " + mobileValue;
        }

        public void setValues(Function<String, String> valueRetriever) {
            setMobileValue(valueRetriever.apply(this.getKeyOf("Mobile")));
            setTabletValue(valueRetriever.apply(this.getKeyOf("Tablet")));
            setDesktopValue(valueRetriever.apply(this.getKeyOf("Desktop")));
        }

        public void setValues(BiFunction<String, String, String> valueRetriever) {
            setMobileValue(valueRetriever.apply(this.getKeyOf("Mobile"), "Mobile"));
            setTabletValue(valueRetriever.apply(this.getKeyOf("Tablet"), "Tablet"));
            setDesktopValue(valueRetriever.apply(this.getKeyOf("Desktop"), "Desktop"));
        }

        public String getMobileValue() {
            return mobileValue;
        }

        public void setMobileValue(String mobileValue) {
            this.mobileValue = mobileValue;
        }

        public String getTabletValue() {
            return tabletValue;
        }

        public void setTabletValue(String tabletValue) {
            this.tabletValue = tabletValue;
        }

        public String getDesktopValue() {
            return desktopValue;
        }

        public void setDesktopValue(String desktopValue) {
            this.desktopValue = desktopValue;
        }

        @Override
        public String toString() {
            return "DeviceProperty [name=" + name + ", suffix=" + suffix + ", mobileValue=" + mobileValue
                    + ", tabletValue=" + tabletValue + ", desktopValue=" + desktopValue + "]";
        }

    }
}