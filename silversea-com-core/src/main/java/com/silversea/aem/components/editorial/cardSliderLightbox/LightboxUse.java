package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.CardLightbox;
import com.silversea.aem.utils.MultiFieldUtils;
import org.apache.sling.api.request.RequestPathInfo;

import java.util.List;
import java.util.Optional;


public class LightboxUse extends WCMUsePojo {
    private String prev;
    private String next;
    private CardLightbox prevCard;
    private CardLightbox nextCard;
    private CardLightbox card;

    @Override
    public void activate() throws Exception {
        RequestPathInfo requestPathInfo = getRequest().getRequestPathInfo();
        String[] selectors = requestPathInfo.getSelectors();
        int currentCard = Optional.of(selectors).map(array -> array[1]).map(Integer::parseInt).orElse(0);
        List<CardLightbox> cards = MultiFieldUtils.retrieveMultiField(getResource(), "cards", CardLightbox.class);
        card = cards.get(currentCard);
        int size = cards.size();
        int prevIndex = ((currentCard - 1 + size) % size);
        int nextIndex = ((currentCard + 1 + size)) % size;
        this.prev = requestPathInfo.getResourcePath() + "." + selectors[0] + "." + prevIndex + ".html";
        this.next = requestPathInfo.getResourcePath() + "." + selectors[0] + "." + nextIndex + ".html";
        this.prevCard = cards.get(prevIndex);
        this.nextCard = cards.get(nextIndex);
    }

    public String getPrev() {
        return prev;
    }

    public String getNext() {
        return next;
    }

    public CardLightbox getPrevCard() {
        return prevCard;
    }

    public CardLightbox getNextCard() {
        return nextCard;
    }

    public CardLightbox getCard() {
        return card;
    }
}
