package com.silversea.aem.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.models.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface CruisesCacheService {

    /**
     * @param lang the lang
     * @return cruises for the defined <code>lang</code>
     */
    List<CruiseModelLight> getCruises(final String lang);

    /**
     * @param lang
     * @param cruiseCode
     * @return
     */
    CruiseModelLight getCruiseByCruiseCode(final String lang, final String cruiseCode);

    /**
     * @param lang the lang
     * @return destinations of the cruises for the <code>lang</code>
     */
    List<DestinationModelLight> getDestinations(final String lang);

    /**
     * @param lang the lang
     * @return ships of the cruises for the <code>lang</code>
     */
    List<ShipModelLight> getShips(final String lang);

    /**
     * @param lang the lang
     * @return ports of the cruises for the <code>lang</code>
     */
    List<PortModelLight> getPorts(final String lang);

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
    Set<FeatureModelLight> getFeatures(final String lang);

    /**
     * Update the cache with the corresponding <code>cruiseModel</code>, if the cruise is modified, it will remove and re-add it to the cache,
     * if the cruise is new, it will add it
     * @param cruiseModel to add to cache
     */
    void addOrUpdateCruise(CruiseModelLight cruiseModel, String langIn);

    /**
     * Remove targeted cruise from cache
     * @param lang the lang
     * @param cruiseCode the cruise code
     */
    void removeCruise(String lang, String cruiseCode);

    /**
     * Build the cache
     */
    ImportResult buildCruiseCache();
}
