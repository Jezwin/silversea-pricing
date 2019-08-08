package com.silversea.ssc.aem.components.editorial;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.UrlHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SmartButtonHelper extends AbstractGeolocationAwareUse {

    static private final String GEO_PROP = "listText";
    static private final Logger LOGGER = LoggerFactory.getLogger(SmartButtonHelper.class);

    private ValueMap props;

    private DeviceProperty text = new DeviceProperty("text");
    private String url;
    private String urlMobile;

    private String suffixUrl;
	private String selectorUrl;

    @Override
    public void activate() throws Exception {
        super.activate();
        props = getProperties();
        text.setValues(this::fromProps);
        url = fromProps("url");
        urlMobile = fromProps("urlMobile");
        if (StringUtils.isEmpty(urlMobile)) {
            urlMobile = url;
        }
        
        setFromAdvancedGeoLocation();
        Boolean externalLinkDesktop = getProperties().get("externalLinkDesktop", Boolean.class);
        Boolean externalLinkMobile = getProperties().get("externalLinkMobile", Boolean.class);

        String analyticType = getProperties().get("analyticType", String.class);
        if (analyticType != null && analyticType.equalsIgnoreCase("clic-RAQ")) {
            Page currentPage = getCurrentPage();
            String[] selectorSuffixUrl = UrlHelper.createSuffixAndSelectorUrl(currentPage);
            this.selectorUrl = selectorSuffixUrl[0];
            this.suffixUrl = selectorSuffixUrl[1];
        }

        if (externalLinkDesktop == null || !externalLinkDesktop) {
            this.url = this.url + ".html";
        }

        if (externalLinkMobile == null || !externalLinkMobile) {
            this.urlMobile = this.urlMobile + ".html";
        }


    }

    public String getUrl() {
        return url;
    }

    public String getUrlMobile() {
        return urlMobile;
    }

    public String getSelectorUrl() {
		return selectorUrl;
	}

	public String getSuffixUrl() {
		return suffixUrl;
	}

    public String getTextDesktop() {
        return text.getDesktopValue();
    }

    public String getTextTablet() {
        return text.getTabletValue();
    }

    public String getTextMobile() {
        return text.getMobileValue();
    }

    private void assignValues(Node node) {
        text.setValues(geoOrProps(node));
        url = getProp(node, "urlgeo").orElse(null);
        urlMobile = getProp(node, "urlgeoMobile").orElse(null);
    }

    private void setFromAdvancedGeoLocation() {
        if (getResource().hasChildren() && getResource().getChild(GEO_PROP) != null) {
            for (Resource child : getResource().getChild(GEO_PROP).getChildren()) {
                Node node = child.adaptTo(Node.class);
                getProp(node, "geoTag").ifPresent(geoTagProperty -> {
                    for (String tag : geoTagProperty.split(",")) {
                        String[] geoTag = tag.split(WcmConstants.GEOLOCATION_TAGS_PREFIX);
                        if (geoTag.length > 1 && isBestMatch(geoTag[1])) {
                            assignValues(node);
                            break;
                        }
                    }
                });
            }
        }
    }

    private Optional<String> getProp(Node node, String key) {
        try {
            if(!node.hasProperty(key)) return Optional.empty();
            return Optional.ofNullable(node.getProperty(key)).map(property -> {
                try {
                    return property.getString();
                } catch (RepositoryException exception) {
                    return null;
                }
            });
        } catch (RepositoryException exception) {
            return Optional.empty();
        }
    }

    private Function<String, String> geoOrProps(Node node) {
        return key -> getProp(node, key + "geo").orElse(fromProps(key));
    }


    private String fromProps(String key) {
        return props.get(key, String.class);
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
