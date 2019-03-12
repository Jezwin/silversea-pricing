package com.silversea.aem.models;

class CruiseDestinationModel extends DestinationModel {

    private final DestinationModel destinationModel;
    private final String customDestination;

    CruiseDestinationModel(DestinationModel destinationModel, String customDestination) {
        this.destinationModel = destinationModel;
        this.customDestination = customDestination;
        this.title = destinationModel.getTitle();
        this.mapLabel = destinationModel.getMapLabel();
        this.excerpt = destinationModel.getExcerpt();
        this.destinationId = destinationModel.getDestinationId();
        this.longDescription = destinationModel.getLongDescription();
        this.description = destinationModel.getDescription();
        this.footnote = destinationModel.getFootnote();
        this.assetselectionreference = destinationModel.getAssetselectionreference();
        this.category = destinationModel.getCategory();
        this.customHtml = destinationModel.getCustomHtml();
        this.path = destinationModel.getPath();
        this.name = destinationModel.getName();
        this.splitDestinationFareAdditionsClassic = destinationModel.getDestinationFareAdditionsClassic();
        this.splitDestinationFareAdditionsExpedition = destinationModel.getDestinationFareAdditionsExpedition();
    }

    public String getNavigationTitle() {
        return customDestination == null ? destinationModel.getNavigationTitle() : customDestination;
    }
}
