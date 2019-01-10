package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.CardLightboxImpl;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.silversea.aem.utils.MultiFieldUtils.retrieveMultiField;
import static java.util.Optional.ofNullable;


public class SliderUse extends WCMUsePojo {
    private List<CardLightboxImpl> cards;
    private int slidePerPageDesktop;
    private int slidePerPageTablet;
    private int slidePerPageMobile;
    private String style;
    private String title;
    private String subtitle;
    private String backgroundColour;
    private Boolean showArrows;

    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        title = properties.get("title", String.class);
        subtitle = properties.get("subtitle", String.class);
        slidePerPageDesktop = getIntProp(properties, "Desktop", 4);
        slidePerPageTablet = getIntProp(properties, "Tablet", slidePerPageDesktop);
        slidePerPageMobile = getIntProp(properties, "Mobile", slidePerPageTablet);
        style = getProp(properties, "style").map(String::toLowerCase).orElse("squared");
        cards = retrieveMultiField(getResource(), "cards", CardLightboxImpl.class);
        this.showArrows = "true".equals(getProperties().get("showArrows", String.class));
        backgroundColour = deviceProps(properties, "grayBackground").map(opt -> opt.orElse("")).collect(Collectors.joining(" "));
    }


    private static Optional<String> getProp(ValueMap properties, String key) {
        return ofNullable(properties.get(key, String.class));
    }

    public Boolean isShowArrows() {
        return showArrows;
    }

    private static Integer getIntProp(ValueMap properties, String device, int defaultValue) {
        return getProp(properties, "slidePerPage" + device).map(Integer::parseInt).orElse(defaultValue);
    }

    public String getSubtitle() {
        return subtitle;
    }

    private static Stream<Optional<String>> deviceProps(ValueMap properties, String key) {
        return Stream.of("Desktop", "Tablet", "Mobile").map(device -> getProp(properties, key + device));
    }


    public List<CardLightboxImpl> getCards() {
        return cards;
    }

    public int getSlidePerPageDesktop() {
        return slidePerPageDesktop;
    }

    public int getSlidePerPageTablet() {
        return slidePerPageTablet;
    }

    public int getSlidePerPageMobile() {
        return slidePerPageMobile;
    }

    public String getTitle() {
        return title;
    }

    public String getBackgroundColour() {
        return backgroundColour;
    }

    public String getStyle() {
        return style;
    }

}
