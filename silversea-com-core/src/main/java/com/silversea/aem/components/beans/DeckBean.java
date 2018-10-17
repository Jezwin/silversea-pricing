package com.silversea.aem.components.beans;

public class DeckBean {
    private String level;

    private String imagePath;

    public DeckBean() {
    }

    public DeckBean(String level, String imagePath) {
        this.level = level;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Level: " + level + " Image: " + imagePath;
    }
}
