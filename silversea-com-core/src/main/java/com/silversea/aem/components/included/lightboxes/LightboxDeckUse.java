package com.silversea.aem.components.included.lightboxes;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.DeckInfoModel;
import com.silversea.aem.models.ShipFactsAndPlanByDateModel;
import com.silversea.aem.models.ShipInterface;
import com.silversea.aem.models.ShipModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LightboxDeckUse extends AbstractGeolocationAwareUse {

    private ShipModel ship;
    private List<SpecBean> specifications;
    private ShipFactsAndPlanByDateModel shipDataByDate;
    private List<DeckInfoModel> deckInfoList;
    private String deckPlan;

    @Override
    public void activate() throws Exception {
        super.activate();
        ship = retrieveShipModel();
        Date startDateCruise = retrieveStartDateCruise(getCurrentPage());
        shipDataByDate = retrieveShipDataByDate(ship, startDateCruise);
        specifications = retrieveSpecification(shipDataByDate);
        deckInfoList = shipDataByDate.getDeckInfoList();
        deckPlan = shipDataByDate.getDeckPlan();
    }

    private ShipFactsAndPlanByDateModel retrieveShipDataByDate(ShipModel ship, Date startDateCruise) {
        List<ShipFactsAndPlanByDateModel> propsfactsAndPlanByDate = ship.getPropsfactsAndPlanByDate();
        boolean isFactsAndPlanByDatePresent = propsfactsAndPlanByDate != null && !propsfactsAndPlanByDate.isEmpty();
        boolean isStartDateNotEmpty = startDateCruise != null;
        if (isFactsAndPlanByDatePresent && isStartDateNotEmpty) {
            ShipFactsAndPlanByDateModel shipFactsAndPlanByDateModel = null;
            for (ShipFactsAndPlanByDateModel propsItem : propsfactsAndPlanByDate) {
                boolean isValidityDatePresent = propsItem.getValidityDate() != null;
                boolean isCruiseStartsAfterTheValidityDate = isValidityDatePresent && startDateCruise.after(propsItem.getValidityDate());
                if (isCruiseStartsAfterTheValidityDate) {
                    shipFactsAndPlanByDateModel = propsItem;
                    break;
                }
            }
            if (shipFactsAndPlanByDateModel != null) {
                return retrieveModelToShow(shipFactsAndPlanByDateModel);
            }
        }
        return retrieveModelToShow(ship);

    }

    private Date retrieveStartDateCruise(Page currentPage) {
        ValueMap propertiesPage = currentPage.getProperties();
        if (propertiesPage != null) {
            return propertiesPage.get("startDate", Date.class);
        }
        return null;
    }

    private List<SpecBean> retrieveSpecification(ShipFactsAndPlanByDateModel model) {
        if (model != null) {
            List<SpecBean> spec = new ArrayList<>();
            if (StringUtils.isNotEmpty(model.getCrewCapacity())) {
                spec.add(new SpecBean("crew", model.getCrewCapacity()));
            }
            if (StringUtils.isNotEmpty(model.getOfficersCapacity())) {
                spec.add(new SpecBean("officers", model.getOfficersCapacity()));
            }
            if (StringUtils.isNotEmpty(model.getGuestsCapacity())) {
                spec.add(new SpecBean("guests", model.getGuestsCapacity()));
            }
            if (StringUtils.isNotEmpty(model.getTonnage())) {
                spec.add(new SpecBean("tonnage", model.getTonnage()));
            }
            if (isFeet() && StringUtils.isNotEmpty(model.getLengthFt())) {
                spec.add(new SpecBean("length", model.getLengthFt()));
            } else if (StringUtils.isNotEmpty(model.getLengthM())) {
                spec.add(new SpecBean("length", model.getLengthM()));
            }
            if (isFeet() && StringUtils.isNotEmpty(model.getWidthFt())) {
                spec.add(new SpecBean("width", model.getWidthFt()));
            } else if (StringUtils.isNotEmpty(model.getWidthM())) {
                spec.add(new SpecBean("width", model.getWidthM()));
            }
            if (StringUtils.isNotEmpty(model.getSpeed())) {
                spec.add(new SpecBean("speed", model.getSpeed()));
            }
            if (StringUtils.isNotEmpty(model.getPassengersDeck())) {
                spec.add(new SpecBean("passenger-decks", model.getPassengersDeck()));
            }
            if (StringUtils.isNotEmpty(model.getThirdGuestCapacity())) {
                spec.add(new SpecBean("third-guest-capacity", model.getThirdGuestCapacity()));
            }
            if (StringUtils.isNotEmpty(model.getHandicapSuites())) {
                spec.add(new SpecBean("handicap-suites", model.getHandicapSuites()));
            }
            if (StringUtils.isNotEmpty(model.getBuiltDate())) {
                String year = model.getBuiltDate().substring(0, 4);
                spec.add(new SpecBean("built", year));
            }
            if (StringUtils.isNotEmpty(model.getRegistry())) {
                spec.add(new SpecBean("registry", model.getRegistry()));
            }
            if (StringUtils.isNotEmpty(model.getConnectingSuites())) {
                spec.add(new SpecBean("connecting-suites", model.getConnectingSuites()));
            }
            return spec;
        }
        return null;
    }

    private ShipModel retrieveShipModel() {
        Page currentPage = getCurrentPage();
        ValueMap propertiesPage = currentPage.getProperties();
        if (propertiesPage != null) {
            String shipReference = propertiesPage.get("shipReference", String.class);
            if (StringUtils.isNotEmpty(shipReference)) {
                Page shipPage = getPageManager().getPage(shipReference);
                if (shipPage != null) {
                    return shipPage.adaptTo(ShipModel.class);
                }
            }
        }
        return null;
    }

    public boolean isFeet() {
        return super.countryCode.equals("US");
    }

    public ShipModel getShip() {
        return ship;
    }

    public List<SpecBean> getSpecifications() {
        return specifications;
    }

    private ShipFactsAndPlanByDateModel retrieveModelToShow(ShipInterface model) {
        ShipFactsAndPlanByDateModel dataModel = new ShipFactsAndPlanByDateModel();
        dataModel.setBuiltDate(model.getBuiltDate());
        dataModel.setConnectingSuites(model.getConnectingSuites());
        dataModel.setCrewCapacity(model.getCrewCapacity());
        dataModel.setGuestsCapacity(model.getGuestsCapacity());
        dataModel.setHandicapSuites(model.getHandicapSuites());
        dataModel.setLengthFt(model.getLengthFt());
        dataModel.setLengthM(model.getLengthM());
        dataModel.setOfficersCapacity(model.getOfficersCapacity());
        dataModel.setPassengersDeck(model.getPassengersDeck());
        dataModel.setRefurbDate(model.getRefurbDate());
        dataModel.setRegistry(model.getRegistry());
        dataModel.setSpeed(model.getSpeed());
        dataModel.setThirdGuestCapacity(model.getThirdGuestCapacity());
        dataModel.setTonnage(model.getTonnage());
        dataModel.setWidthFt(model.getWidthFt());
        dataModel.setWidthM(model.getWidthM());
        dataModel.setDeckInfo(model.getDeckInfoList());
        dataModel.setDeckPlan(model.getDeckPlan());
        return dataModel;
    }

    public List<DeckInfoModel> getDeckInfoList() {
        return deckInfoList;
    }

    public String getDeckPlan() {
        return deckPlan;
    }

    public class SpecBean {
        private String label;
        private String value;
        private List<DeckInfoModel> deckInfoList;

        private SpecBean(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public SpecBean(String label, String value, List<DeckInfoModel> deckInfoList) {
            this.label = label;
            this.value = value;
            this.deckInfoList = deckInfoList;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
