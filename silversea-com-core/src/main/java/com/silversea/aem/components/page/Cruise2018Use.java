package com.silversea.aem.components.page;

import com.day.cq.dam.api.Asset;
import com.silversea.aem.components.beans.EoBean;
import com.silversea.aem.components.beans.EoConfigurationBean;
import com.silversea.aem.components.beans.ExclusiveOfferItem;
import com.silversea.aem.helper.EoHelper;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ShipAreaModel;
import com.silversea.aem.models.ShipModel;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.utils.AssetUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cruise2018Use extends EoHelper {

    private static final EoConfigurationBean EO_CONFIG = new EoConfigurationBean();

    static {
        EO_CONFIG.setTitleVoyage(true);
        EO_CONFIG.setShortDescriptionVoyage(true);
        EO_CONFIG.setDescriptionMain(true);
        EO_CONFIG.setFootnoteVoyage(true);
        EO_CONFIG.setMapOverheadVoyage(true);
        EO_CONFIG.setCruiseFareVoyage(true);
        EO_CONFIG.setPriorityWeight(true);
        EO_CONFIG.setIconVoyage(true);
    }

    private List<ExclusiveOfferItem> exclusiveOffers = new ArrayList<>();
    private CruiseModel cruiseModel;
    private List<SilverseaAsset> assetsGallery;

    @Override
    public void activate() throws Exception {
        super.activate();
        cruiseModel = retrieveCruiseModel();
        assetsGallery = retrieveAssetsGallery(cruiseModel);
        //exclusiveOffers = retrieveExclusiveOffers(cruiseModel);
    }

    private CruiseModel retrieveCruiseModel() {
        if (getRequest().getAttribute("cruiseModel") != null) {
            return (CruiseModel) getRequest().getAttribute("cruiseModel");
        } else {
            CruiseModel cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
            getRequest().setAttribute("cruiseModel", cruiseModel);
            return cruiseModel;
        }
    }

    private List<SilverseaAsset> retrieveAssetsGallery(CruiseModel cruiseModel) {
        List<SilverseaAsset> assetsListResult = new ArrayList<>();
        if (cruiseModel != null) {
            if (StringUtils.isNotBlank(cruiseModel.getAssetSelectionReference())) {
                assetsListResult.addAll(AssetUtils.buildSilverseaAssetList(cruiseModel.getAssetSelectionReference(), getResourceResolver(), null));
            }
            assetsListResult.addAll(retrieveAssestsFromShip(cruiseModel.getShip()));
        }

        return assetsListResult;
    }

    private List<SilverseaAsset> retrieveAssestsFromShip(ShipModel shipModel) {
        List<SilverseaAsset> listShipAssests = new ArrayList<>();
        if (shipModel != null) {
            List<SilverseaAsset> virtualTourAssets = new ArrayList<>();
            if (StringUtils.isNotEmpty(cruiseModel.getShip().getPhotoVideoSuiteSelectionReference())) {
                listShipAssests.addAll(AssetUtils.buildSilverseaAssetList(shipModel.getPhotoVideoSuiteSelectionReference(), getResourceResolver(), null));
            } else {
                retrieveAssestsFromShip(shipModel.getSuites(),listShipAssests, virtualTourAssets);
            }
            retrieveAssestsFromShip(shipModel.getDinings(),listShipAssests, virtualTourAssets);
            retrieveAssestsFromShip(shipModel.getPublicAreas(),listShipAssests, virtualTourAssets);
            listShipAssests.addAll(virtualTourAssets);
            virtualTourAssets = null;
        }
        return listShipAssests;
    }

    private void retrieveAssestsFromShip(List<? extends ShipAreaModel> shipEntitiy, List<SilverseaAsset> classicAssets, List<SilverseaAsset> virtualTourAssets) {
        if (shipEntitiy != null && !shipEntitiy.isEmpty()) {
            Map<String, List<SilverseaAsset>> mapAsset = AssetUtils.addAllShipAreaAssets(getResourceResolver(), shipEntitiy);
            if (!mapAsset.isEmpty()) {
                classicAssets.addAll(mapAsset.get("assets"));
                virtualTourAssets.addAll(mapAsset.get("assetsVirtualTour"));
            }
        }
    }

    private List<ExclusiveOfferItem> retrieveExclusiveOffers(CruiseModel cruiseModel) {
        return cruiseModel.getExclusiveOffers().stream()
                .filter(eo -> eo.getGeomarkets() != null && eo.getGeomarkets().contains(geomarket))
                .map(exclusiveOfferModel -> {
                    EO_CONFIG.setActiveSystem(exclusiveOfferModel.getActiveSystem());
                    EoBean result = super.parseExclusiveOffer(EO_CONFIG, exclusiveOfferModel);
                    String destinationPath = cruiseModel.getDestination().getPath();
                    return new ExclusiveOfferItem(exclusiveOfferModel, countryCode, destinationPath, result);
                })
                .sorted(Comparator.comparing(ExclusiveOfferItem::getPriorityWeight).reversed())
                .collect(Collectors.toList());
    }

    public List<ExclusiveOfferItem> getExclusiveOffers() {
        return exclusiveOffers;
    }

    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

    public List<SilverseaAsset> getAssetsGallery() {
        return assetsGallery;
    }
}
