package com.silversea.aem.services;

import com.silversea.aem.models.*;

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

    /**
     * @param lang lang the lang
     * @return departures dates of the cruises for the <code>lang</code>
     */
    Set<YearMonth> getDepartureDates(final String lang);

    /**
     * @param lang lang the lang
     * @return features of the cruises for the <code>lang</code>
     */
    Set<FeatureModel> getFeatures(final String lang);
}
