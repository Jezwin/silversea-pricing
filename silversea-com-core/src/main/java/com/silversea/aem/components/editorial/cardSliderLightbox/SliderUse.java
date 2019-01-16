package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.CardLightboxImpl;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;
import java.util.stream.Collectors;


public class SliderUse extends AbstractSilverUse {
    private List<CardLightboxImpl> cards;
    private DeviceProperty<Integer> slidesPerPage;
    private DeviceProperty<Boolean> centeredStyle;
    private String hideArrowsPerDevice;
    private String centeredClassPerDevice;
    private String style;
    private String title;
    private String subtitle;
    private DeviceProperty<String> backgroundColour;
    private boolean showLightboxArrows;
    private boolean invertTitle;
    private boolean showProgressBar;


    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        title = properties.get("title", String.class);
        subtitle = properties.get("subtitle", String.class);
        style = getProp("style").map(String::toLowerCase).orElse("squared");
        cards = retrieveMultiField("cards", CardLightboxImpl.class);
        invertTitle = getBoolean("invertTitle", false);
        cards = retrieveMultiField("cards", resource -> {
            CardLightboxImpl cardLightbox = resource.adaptTo(CardLightboxImpl.class);
            if (invertTitle) {
                String title = cardLightbox.getTitle();
                cardLightbox.setTitle(cardLightbox.getBriefDescription());
                cardLightbox.setBriefDescription(title);
            }
            return cardLightbox;
        }).collect(Collectors.toList());
        showLightboxArrows = getBoolean("showLightboxArrows", true);
        showProgressBar = getBoolean("showProgressBar", false);
        backgroundColour = getDeviceProp("grayBackground", String.class, " ");
        int numberOfCards = cards.size();
        centeredStyle = getDeviceProp("centeredStyle", false).map((device, currentValue) -> numberOfCards > 1 ? currentValue : false);
        hideArrowsPerDevice = centeredStyle.map((device, isCentered) -> isCentered ? "hideArrows" + device : "").toString();
        centeredClassPerDevice = centeredStyle.map((device, isCentered) -> isCentered ? "centeredStyle" + device : "").toString();
        slidesPerPage = getDeviceProp("slidePerPage", Integer.class, 4)
                .map((device, currentValue) -> centeredStyle.get(device) ? 1 : currentValue);
    }


    public boolean isInvertTitle() {
        return invertTitle;
    }

    public String getCenteredClassPerDevice() {
        return centeredClassPerDevice;
    }


    public boolean isShowLightboxArrows() {
        return showLightboxArrows;
    }

    public String getSubtitle() {
        return subtitle;
    }


    public String getHideArrowsPerDevice() {
        return hideArrowsPerDevice;
    }

    public List<CardLightboxImpl> getCards() {
        return cards;
    }

    public DeviceProperty<Integer> getSlidesPerPage() {
        return slidesPerPage;
    }

    public boolean isShowProgressBar() {
        return showProgressBar;
    }

    public String getTitle() {
        return title;
    }

    public String getBackgroundColour() {
        return backgroundColour.toString();
    }

    public DeviceProperty<Boolean> getCenteredStyle() {
        return centeredStyle;
    }

    public String getStyle() {
        return style;
    }

}
