package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.CardLightbox;
import com.silversea.aem.utils.MultiFieldUtils;

import java.util.List;
import java.util.Optional;

import static java.util.stream.StreamSupport.stream;


public class SliderUse extends WCMUsePojo {
    private List<CardLightbox> cards;
    private int slidePerPageDesktop;
    private int slidePerPageTablet;
    private int slidePerPageMobile;
    private Boolean showArrows;
    private String title;


    @Override
    public void activate() throws Exception {
        title = getProperties().get("title", String.class);
        slidePerPageDesktop = getSlidePerPage("Desktop", 4);
        slidePerPageTablet = getSlidePerPage("Tablet", slidePerPageDesktop);
        slidePerPageMobile = getSlidePerPage("Mobile", slidePerPageTablet);
        cards = MultiFieldUtils.retrieveMultiField(getResource(), "cards", CardLightbox.class);
        this.showArrows = "true".equals(getProperties().get("showArrows", String.class));
    }

    private Integer getSlidePerPage(String device, int defaultValue) {
        return Optional.ofNullable(getProperties().get("slidePerPage" + device, String.class)).map(Integer::parseInt).orElse(defaultValue);
    }

    public Boolean isShowArrows() {
        return showArrows;
    }


    public List<CardLightbox> getCards() {
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

}
