package com.silversea.aem.models;

import com.day.cq.dam.api.Asset;

import java.util.List;

public interface ShipAreaModel {

    String getThumbnail();

    String getAssetSelectionReference();

    String getVirtualTour();

    List<Asset> getAssets();

    Asset getFirstAsset();
    
    String getTitle();
}
