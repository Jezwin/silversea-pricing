package com.silversea.aem.components.editorial.cardSliderLightbox;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.models.CardLightbox;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;


public class SliderUse extends WCMUsePojo {
    private List<CardLightbox> cards;
    private int slidePerPageDesktop;
    private String title;

    @Override
    public void activate() throws Exception {
        title = getProperties().get("title", String.class);
        slidePerPageDesktop = Optional.ofNullable(getProperties().get("slidePerPage", String.class)).map(Integer::parseInt).orElse(4);
        cards = retrieveMultiField(getResource(), "cards", CardLightbox.class);
    }

    public static <T> List<T> retrieveMultiField(Resource resource, String child, Class<T> adaptable) {
        return ofNullable(resource)
                .map(value -> value.getChild(child))
                .map(Resource::getChildren)
                .map(iterator -> stream(iterator.spliterator(), false))
                .map(stream -> stream.map(element -> element.adaptTo(adaptable)).filter(Objects::nonNull).collect(toList()))
                .orElse(emptyList());
    }


    public List<CardLightbox> getCards() {
        return cards;
    }

    public int getSlidePerPageDesktop() {
        return slidePerPageDesktop;
    }

    public String getTitle() {
        return title;
    }

}
