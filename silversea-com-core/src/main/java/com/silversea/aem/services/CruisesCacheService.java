package com.silversea.aem.services;

import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.DestinationModel;
import com.silversea.aem.models.PortModel;
import com.silversea.aem.models.ShipModel;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface CruisesCacheService {

    /**
     * @param lang the lang
     * @return cruise for the defined <code>lang</code>
     */
    List<CruiseModel> getCruises(final String lang);

    /**
     * @param lang the lang
     * @return destinations of the cruises for the <code>lang</code>
     */
    List<DestinationModel> getDestinations(final String lang);

    /**
     * @param lang the lang
     * @return ships of the cruises for the <code>lang</code>
     */
    List<ShipModel> getShips(final String lang);

    /**
     * @param lang the lang
     * @return ports of the cruises for the <code>lang</code>
     */
    List<PortModel> getPorts(final String lang);

    /**
     * @param lang the lang
     * @return durations of the cruises for the <code>lang</code>
     */
    Set<Integer> getDurations(final String lang);

    Set<YearMonth> getDepartureDates(final String lang);
}
