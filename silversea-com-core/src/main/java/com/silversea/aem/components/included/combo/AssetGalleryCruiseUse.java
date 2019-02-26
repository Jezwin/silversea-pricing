package com.silversea.aem.components.included.combo;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.*;
import com.silversea.aem.utils.AssetUtils;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class AssetGalleryCruiseUse extends AbstractSilverUse {

    private List<SilverseaAsset> assetsGallery;
    private String arrivalPortName;
    private String departurePortName;
    private String bigItineraryMap;

    @Override
    public void activate() throws Exception {
        String selectorString = Optional.ofNullable(getRequest().getRequestPathInfo().getSelectorString()).orElse("");
        boolean isComboCruise = selectorString.contains("silversea-combocruise");
        if (!isComboCruise) {
            return;
        }

        //handle the map lightbox
        boolean isLigthboxMap = selectorString.contains("lg-map");
        if (isLigthboxMap) {
            //handle the map lightbox of the segment
            boolean isLigthboxSegmentMap = selectorString.contains("lg-segmentmap");
            if (isLigthboxSegmentMap) {

                String segmentName = firstSelectorDifferentFrom("lightboxes.lg-map.lg-segmentmap.silversea-combocruise").orElse("");
                retrireveSegmentMap(getCurrentPage(), segmentName).ifPresent(this::setBigItineraryMap);
            } else {
                retrieveBigItineraryMap(getCurrentPage()).ifPresent(this::setBigItineraryMap);
            }
        } else {
            ComboCruiseModel comboCruiseModel = getCurrentPage().adaptTo(ComboCruiseModel.class);
            List<SilverseaAsset> cruisesAssetsGallery = retrieveAssetsGallery(getResource(), getResourceResolver(), getCurrentPage(), true);
            setAssetsGallery(cruisesAssetsGallery);
            setArrivalPortName(comboCruiseModel.getArrivalPortName());
            setDeparturePortName(comboCruiseModel.getDeparturePortName());
        }
    }

    private Optional<String> retrireveSegmentMap(Page currentPage, String segmentName) {
        if (currentPage.hasChild(segmentName)) {
            Iterator<Page> pageIterator = currentPage.listChildren();
            while (pageIterator.hasNext()) {
                Page p = pageIterator.next();
                if (p.getName().equalsIgnoreCase(segmentName)) {
                    return getProp(p.getContentResource().getValueMap(), "focusedMapReference");
                }
            }
        }
        return Optional.empty();
    }

    public static List<SilverseaAsset> retrieveAssetsGallery(Resource itinerariesResource, ResourceResolver resourceResolver, Page currentPage, boolean onlyAssetSelectionReference) {

        PageManager pageManager = currentPage.getPageManager();
        ShipModel ship = null;
        String assetSelectionReference;
        if (pageManager == null) {
            return null;
        }
        ValueMap vmProperties = currentPage.getProperties();
        if (vmProperties == null) {
            return null;
        }
        String shipReference = vmProperties.get("shipReference", String.class);
        if (StringUtils.isNotEmpty(shipReference)) {
            Page shipPage = pageManager.getPage(shipReference);
            if (shipPage != null) {
                ship = shipPage.adaptTo(ShipModel.class);
            }
        }
        assetSelectionReference = vmProperties.get("assetSelectionReference", String.class);
        List<SilverseaAsset> assetsListResult = new ArrayList<>();
        if (StringUtils.isNotBlank(assetSelectionReference)) {
            assetsListResult.addAll(AssetUtils
                    .buildSilverseaAssetList(assetSelectionReference, resourceResolver,
                            null));
        }
        if (!onlyAssetSelectionReference) {
            List<SilverseaAsset> portsAssetsList = retrieveAssetsFromPort(itinerariesResource, resourceResolver);
            assetsListResult.addAll(portsAssetsList);
            if (ship != null) {
                assetsListResult.addAll(retrieveAssetsFromShip(ship, resourceResolver));
            }
            String map = CruiseUtils.firstNonNull(vmProperties.get("bigItineraryMap", String.class),
                    vmProperties.get("bigThumbnailItineraryMap", String.class),
                    vmProperties.get("smallItineraryMap", String.class));
            String type = null;
            if (map == null) {
                map = vmProperties.get("itinerary", String.class);
                type = "itinerary";
            }
            if (map != null) {
                assetsListResult.add(0, AssetUtils.buildSilverseaAsset(map, resourceResolver, null, type));
            }
        }

        return assetsListResult.stream().distinct().collect(toList());
    }

    private static List<SilverseaAsset> retrieveAssetsFromPort(Resource itinerariesResource, ResourceResolver resourceResolver) {
        List<SilverseaAsset> portsAssetsList = new ArrayList<>();
        if (itinerariesResource != null && itinerariesResource.hasChildren()) {
            Iterator<Resource> children = itinerariesResource.getChildren().iterator();
            ItineraryModel itineraryModel;
            while (children.hasNext()) {
                Resource it = children.next();
                itineraryModel = it.adaptTo(ItineraryModel.class);
                if (itineraryModel != null && itineraryModel.getPort() != null) {
                    PortModel portModel = itineraryModel.getPort();
                    if (!"day-at-sea".equals(portModel.getName())) {
                        String assetSelectionReference = portModel.getAssetSelectionReference();
                        if (StringUtils.isNotBlank(assetSelectionReference)) {
                            List<SilverseaAsset> portAssets = AssetUtils
                                    .buildSilverseaAssetList(assetSelectionReference, resourceResolver,
                                            portModel.getTitle());
                            if (portAssets != null && !portAssets.isEmpty()) {
                                portsAssetsList.addAll(portAssets);
                            }
                        }
                    }
                }
            }
        }
        return portsAssetsList;
    }

    private Optional<String> retrieveBigItineraryMap(Page page) {
        return Optional.ofNullable(page).map(Page::getProperties).map(prop -> prop.get("bigItineraryMap", String.class));
    }

    public static String retrieveArrivalPortName(Resource itinerariesResource) {
        String arrivalPortName = null;
        if (itinerariesResource != null && itinerariesResource.hasChildren()) {
            Iterator<Resource> children = itinerariesResource.getChildren().iterator();
            ItineraryModel itineraryModel;
            while (children.hasNext()) {
                Resource it = children.next();
                itineraryModel = it.adaptTo(ItineraryModel.class);
                if (itineraryModel != null && itineraryModel.getPort() != null) {
                    PortModel portModel = itineraryModel.getPort();
                    if (!"day-at-sea".equals(portModel.getName())) {
                        arrivalPortName = portModel.getApiTitle();
                    }
                }
            }
        }
        return arrivalPortName;
    }

    private static List<SilverseaAsset> retrieveAssetsFromShip(ShipModel shipModel, ResourceResolver resourceResolver) {
        List<SilverseaAsset> listShipAssets = new ArrayList<>();
        if (shipModel != null) {
            List<SilverseaAsset> virtualTourAssets = new ArrayList<>();
            if (StringUtils.isNotEmpty(shipModel.getPhotoVideoSuiteSelectionReference())) {
                listShipAssets.addAll(AssetUtils
                        .buildSilverseaAssetList(shipModel.getPhotoVideoSuiteSelectionReference(),
                                resourceResolver, null));
            } else {
                retrieveAssetsFromShip(shipModel.getSuites(), listShipAssets, virtualTourAssets, resourceResolver);
            }
            retrieveAssetsFromShip(shipModel.getDinings(), listShipAssets, virtualTourAssets, resourceResolver);
            retrieveAssetsFromShip(shipModel.getPublicAreas(), listShipAssets, virtualTourAssets, resourceResolver);
            listShipAssets.addAll(virtualTourAssets);
        }
        return listShipAssets;
    }

    private static void retrieveAssetsFromShip(List<? extends ShipAreaModel> shipEntitiy, List<SilverseaAsset> classicAssets,
                                               List<SilverseaAsset> virtualTourAssets, ResourceResolver resourceResolver) {
        if (shipEntitiy != null && !shipEntitiy.isEmpty()) {
            Map<String, List<SilverseaAsset>> mapAsset =
                    AssetUtils.addAllShipAreaAssets(resourceResolver, shipEntitiy);
            if (!mapAsset.isEmpty()) {
                classicAssets.addAll(mapAsset.get("assets"));
                virtualTourAssets.addAll(mapAsset.get("assetsVirtualTour"));
            }
        }
    }


    public List<SilverseaAsset> getAssetsGallery() {
        return assetsGallery;
    }

    public void setAssetsGallery(List<SilverseaAsset> assetsGallery) {
        this.assetsGallery = assetsGallery;
    }

    public String getArrivalPortName() {
        return arrivalPortName;
    }

    public void setArrivalPortName(String arrivalPortName) {
        this.arrivalPortName = arrivalPortName;
    }

    public String getDeparturePortName() {
        return departurePortName;
    }

    public void setDeparturePortName(String departurePortName) {
        this.departurePortName = departurePortName;
    }

    public String getBigItineraryMap() {
        return bigItineraryMap;
    }

    public void setBigItineraryMap(String bigItineraryMap) {
        this.bigItineraryMap = bigItineraryMap;
    }
}
