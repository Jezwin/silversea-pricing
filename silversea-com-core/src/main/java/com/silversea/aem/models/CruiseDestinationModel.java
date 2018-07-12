package com.silversea.aem.models;

class CruiseDestinationModel extends DestinationModel {

    private final DestinationModel destinationModel;
    private final String customDestination;

    CruiseDestinationModel(DestinationModel destinationModel, String customDestination) {
        this.destinationModel = destinationModel;
        this.customDestination = customDestination;
    }

    public String getTitle() {
        return destinationModel.getTitle();
    }

    public String getMapLabel() {
        return destinationModel.getMapLabel();
    }

    public String getExcerpt() {
        return destinationModel.getExcerpt();
    }

    public String getDestinationId() {
        return destinationModel.getDestinationId();
    }

    public String getDescription() {
        return destinationModel.getDescription();
    }

    public String getLongDescription() {
        return destinationModel.getLongDescription();
    }

    public String getFootnote() {
        return destinationModel.getFootnote();
    }

    public String getAssetselectionreference() {
        return destinationModel.getAssetselectionreference();
    }

    public String getCategory() {
        return destinationModel.getCategory();
    }

    public String getCustomHtml() {
        return destinationModel.getCustomHtml();
    }

    public String getPath() {
        return destinationModel.getPath();
    }

    public String getName() {
        return destinationModel.getName();
    }


    public String getNavigationTitle() {
        return customDestination == null ? destinationModel.getNavigationTitle() : customDestination;
    }
}
