package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.CardLightboxImpl;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Stream.of;


public class SliderUse extends AbstractSilverUse {
    private List<CardLightboxImpl> cards;
    private int slidePerPageDesktop;
    private int slidePerPageTablet;
    private int slidePerPageMobile;
    private String style;
    private String title;
    private String subtitle;
    private String backgroundColour;
    private boolean showArrows;
    private boolean invertTitle;


    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        title = properties.get("title", String.class);
        subtitle = properties.get("subtitle", String.class);
        slidePerPageDesktop = getInt("slidePerPageDesktop", 4);
        slidePerPageTablet = getInt("slidePerPageTablet", slidePerPageDesktop);
        slidePerPageMobile = getInt("slidePerPageMobile", slidePerPageTablet);
        style = getProp("style").map(String::toLowerCase).orElse("squared");
        cards = retrieveMultiField("cards", CardLightboxImpl.class);
        showArrows = getBoolean("showArrows", true);
        invertTitle = getBoolean("invertTitle", false);
        backgroundColour = of("Desktop", "Tablet", "Mobile").map(device -> getProp("grayBackground" + device, " ")).collect(Collectors.joining(" "));
    }



    public boolean isInvertTitle() {
        return invertTitle;
    }


    public boolean isShowArrows() {
        return showArrows;
    }

    public String getSubtitle() {
        return subtitle;
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
