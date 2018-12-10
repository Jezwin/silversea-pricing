package com.silversea.aem.components.included.lightboxes;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.ShipModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LightboxDeckUse extends AbstractGeolocationAwareUse {

    private ShipModel ship;
    private List<SpecBean> specifications;

    @Override
    public void activate() throws Exception {
        super.activate();
        ship = retrieveShipModel();
        specifications = retrieveSpecification();
    }

    private List<SpecBean> retrieveSpecification() {
        if (ship != null) {
            List<SpecBean> spec = new ArrayList<>();
            if (StringUtils.isNotEmpty(ship.getCrewCapacity())) {
                spec.add(new SpecBean("crew", ship.getCrewCapacity()));
            }
            if (StringUtils.isNotEmpty(ship.getOfficersCapacity())) {
                spec.add(new SpecBean("officers", ship.getOfficersCapacity()));
            }
            if (StringUtils.isNotEmpty(ship.getGuestsCapacity())) {
                spec.add(new SpecBean("guests", ship.getGuestsCapacity()));
            }
            if (StringUtils.isNotEmpty(ship.getTonnage())) {
                spec.add(new SpecBean("tonnage", ship.getTonnage()));
            }
            if (isFeet()) {
                spec.add(new SpecBean("length", ship.getLengthFt()));
            } else if (StringUtils.isNotEmpty(ship.getLengthM())) {
                spec.add(new SpecBean("length", ship.getLengthM()));
            }
            if (countryCode.equals("US") && StringUtils.isNotEmpty(ship.getWidthFt())) {
                spec.add(new SpecBean("width", ship.getWidthFt()));
            } else if (StringUtils.isNotEmpty(ship.getWidthM())) {
                spec.add(new SpecBean("width", ship.getWidthM()));
            }
            if (StringUtils.isNotEmpty(ship.getSpeed())) {
                spec.add(new SpecBean("speed", ship.getSpeed()));
            }
            if (ship.getDeckInfoList() != null && !ship.getDeckInfoList().isEmpty()){
                spec.add(new SpecBean("passenger-decks", null));
            }
            if (StringUtils.isNotEmpty(ship.getThirdGuestCapacity())) {
                spec.add(new SpecBean("third-guest-capacity", ship.getThirdGuestCapacity()));
            }
            if (StringUtils.isNotEmpty(ship.getHandicapSuites())) {
                spec.add(new SpecBean("handicap-suites", ship.getHandicapSuites()));
            }
            if (StringUtils.isNotEmpty(ship.getBuiltDate())) {
                String year = ship.getBuiltDate().substring(0,4);
                spec.add(new SpecBean("built",year));
            }
            if (StringUtils.isNotEmpty(ship.getRegistry())) {
                spec.add(new SpecBean("registry", ship.getRegistry()));
            }
            if (StringUtils.isNotEmpty(ship.getConnectingSuites())) {
                spec.add(new SpecBean("connecting-suites", ship.getConnectingSuites()));
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

    public class SpecBean {
        private String label;
        private String value;

        private SpecBean(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
