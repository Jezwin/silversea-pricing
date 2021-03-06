package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.silversea.aem.models.CardLightbox;
import org.apache.sling.api.request.RequestPathInfo;

import java.util.List;
import java.util.Optional;


public class LightboxUse extends SliderUse {
    private String prev;
    private String next;
    private CardLightbox prevCard;
    private CardLightbox nextCard;
    private CardLightbox card;

    @Override
    public void activate() throws Exception {
        super.activate();
        RequestPathInfo requestPathInfo = getRequest().getRequestPathInfo();
        String[] selectors = requestPathInfo.getSelectors();
        init(requestPathInfo.getResourcePath(), selectors[0], retrieveCurrentIndex(selectors), getCards());

    }

    protected Integer retrieveCurrentIndex(String[] selectors) {
        int index = selectors.length - 1;
        while (index > 0 && selectors[index].startsWith("country")) {
            index--;
        }
        int finalIndex = index;
        return Optional.of(selectors).map(array -> array[finalIndex]).map(s -> {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return null;
            }
        }).orElse(0);
    }

    protected void init(String path, String selector, int currentCard, List<? extends CardLightbox> cards) {
        this.card = cards.get(currentCard);
        int size = cards.size();
        if (size > 1) {
            int prevIndex = (currentCard - 1 + size) % size;
            int nextIndex = (currentCard + 1 + size) % size;
            this.prev = path + "." + selector + "." + prevIndex + ".html";
            this.next = path + "." + selector + "." + nextIndex + ".html";
            this.prevCard = cards.get(prevIndex);
            this.nextCard = cards.get(nextIndex);
        }
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
