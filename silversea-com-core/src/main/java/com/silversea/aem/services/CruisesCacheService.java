package com.silversea.aem.services;

import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.DestinationModel;
import com.silversea.aem.models.PortModel;
import com.silversea.aem.models.ShipModel;

import java.util.List;
import java.util.Set;

public interface CruisesCacheService {

    List<CruiseModel> getCruises(final String lang);

    List<DestinationModel> getDestinations(final String lang);

    List<ShipModel> getShips(final String lang);

    List<PortModel> getPorts(final String lang);

    Set<String> getDurations(final String lang);
}
