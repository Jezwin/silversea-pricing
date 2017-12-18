package com.silversea.aem.models;

public class PortModelLight {

    private String title;

    private String apiTitle;

    private Integer cityId;

    private String country;

    private String path;

    private String name;
    
    public PortModelLight(PortModel portModel) {

        title = portModel.getTitle();
        apiTitle = portModel.getApiTitle();
        cityId = portModel.getCityId();
        country = portModel.getCountry();
        path = portModel.getPath();
        name = portModel.getName();
    }


    public String getTitle() {
        return title;
    }

    public String getApiTitle() {
        return apiTitle;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getCountry() {
        return country;
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

        if (!(obj instanceof PortModelLight)) {
            return false;
        }

        final PortModelLight objShipModel = (PortModelLight)obj;

        return objShipModel.getPath().equals(getPath());
    }
}
