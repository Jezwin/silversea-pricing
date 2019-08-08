package com.silversea.aem.models;

public class ShipModelLight {

    private String title;

    private String shipCode;

    private String shipId;

    private String path;

    private String name;

    public ShipModelLight(ShipModel shipModel) {

        title = shipModel.getTitle();
        shipCode = shipModel.getShipCode();
        shipId = shipModel.getShipId();
        path = shipModel.getPath();
        name = shipModel.getName();
    }

   
    public String getTitle() {
        return title;
    }

    public String getShipCode() {
        return shipCode;
    }

    public String getShipId() {
        return shipId;
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

        if (!(obj instanceof ShipModelLight)) {
            return false;
        }

        final ShipModelLight objShipModel = (ShipModelLight)obj;

        return objShipModel.getPath().equals(getPath());
    }
}
