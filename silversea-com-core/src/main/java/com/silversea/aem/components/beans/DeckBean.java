package com.silversea.aem.components.beans;

public class DeckBean {
    private String level;
    private String imageTopPath;
    private String imageSidePath;

    public DeckBean() {
    }

    public DeckBean(String level, String imageTopPath, String imageSidePath) {
        this.level = level;
        this.imageTopPath = imageTopPath;
        this.imageSidePath = imageSidePath;
    }

    public String getImageSidePath() {
        return imageSidePath;
    }

    public void setImageSidePath(String imageSidePath) {
        this.imageSidePath = imageSidePath;
    }

    public String getImageTopPath() {
        return imageTopPath;
    }

    public void setImageTopPath(String imageSideTopPath) {
        this.imageTopPath = imageSideTopPath;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Level: " + level + " Image Top: " + imageTopPath + " Image Side: " + imageSidePath ;
    }
}
