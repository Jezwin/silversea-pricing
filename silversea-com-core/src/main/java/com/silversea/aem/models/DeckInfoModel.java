package com.silversea.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class DeckInfoModel {

    @Inject
    @Optional
    private String deckLevel;

    @Inject
    @Optional
    private String deckImageTop;

    public String getDeckLevel() {
        return deckLevel;
    }

    public void setDeckLevel(String deckLevel) {
        this.deckLevel = deckLevel;
    }

    public String getDeckImageTop() {
        return deckImageTop;
    }

    public void setDeckImageTop(String deckImageTop) {
        this.deckImageTop = deckImageTop;
    }

    @Override
    public String toString() {
        return "Level: " + deckLevel + " Image: " + deckImageTop;
    }
}

