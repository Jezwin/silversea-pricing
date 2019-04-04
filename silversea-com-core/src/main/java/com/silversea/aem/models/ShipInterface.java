package com.silversea.aem.models;

import java.util.Date;
import java.util.List;


public interface ShipInterface {
    String getBuiltDate();

    String getConnectingSuites();

    String getCrewCapacity();

    String getGuestsCapacity();

    String getHandicapSuites();

    String getLengthFt();

    String getLengthM();

    String getOfficersCapacity();

    String getPassengersDeck();

    String getRefurbDate();

    String getRegistry();

    String getSpeed();

    String getThirdGuestCapacity();

    String getTonnage();

    Date getValidityDate();

    String getWidthFt();

    String getWidthM();

    List<DeckInfoModel> getDeckInfoList();

    String getDeckPlan();
}