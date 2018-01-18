package com.silversea.aem.models;


public class DestinationModelLight {

    private String title;

    private String navigationTitle;

    private String destinationId;

    private String category;

    private String path;

    private String name;

    public DestinationModelLight(DestinationModel destinationModel) {

        title = destinationModel.getTitle();
        navigationTitle = destinationModel.getNavigationTitle();
        destinationId = destinationModel.getDestinationId();
        category = destinationModel.getCategory();
        path = destinationModel.getPath();
        name = destinationModel.getName();
    }

    public String getTitle() {
        return title;
    }

    public String getNavigationTitle() {
        return navigationTitle;
    }


    public String getDestinationId() {
        return destinationId;
    }


    public String getCategory() {
        return category;
    }


    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DestinationModelLight)) {
            return false;
        }

        final DestinationModelLight objDestinationModel = (DestinationModelLight)obj;

        return objDestinationModel.getPath().equals(getPath());
    }
}
