package com.silversea.aem.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.util.*;

@Service
@Component
public class CruisesCacheServiceImpl implements CruisesCacheService {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesCacheServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private Map<String, List<CruiseModel>> cruises = new HashMap<>();

    private Map<String, List<DestinationModel>> destinations = new HashMap<>();

    private Map<String, List<ShipModel>> ships = new HashMap<>();

    private Map<String, List<PortModel>> ports = new HashMap<>();

    private Map<String, Set<Integer>> durations = new HashMap<>();

    private Map<String, Set<YearMonth>> departureDates = new HashMap<>();

    private Map<String, Set<FeatureModel>> features = new HashMap<>();

    private int i = 0;

    @Activate
    protected void activate(final ComponentContext context) {
        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            final List<String> languages = ImportersUtils.getSiteLocales(pageManager);

            for (final String lang : languages) {
                // init language
                cruises.put(lang, new ArrayList<>());
                destinations.put(lang, new ArrayList<>());
                ships.put(lang, new ArrayList<>());
                ports.put(lang, new ArrayList<>());
                durations.put(lang, new TreeSet<>());
                departureDates.put(lang, new TreeSet<>());
                features.put(lang, new TreeSet<>(Comparator.comparing(FeatureModel::getName)));

                // collect cruises
                final Page destinationsPage = pageManager.getPage("/content/silversea-com/" + lang + "/destinations");
                collectCruisesPages(destinationsPage);

                destinations.get(lang).sort(Comparator.comparing(DestinationModel::getTitle));
                ships.get(lang).sort(Comparator.comparing(ShipModel::getTitle));
                ports.get(lang).sort(Comparator.comparing(PortModel::getApiTitle));
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    @Override
    public List<CruiseModel> getCruises(final String lang) {
        return cruises.get(lang);
    }

    @Override
    public List<DestinationModel> getDestinations(final String lang) {
        return destinations.get(lang);
    }

    @Override
    public List<ShipModel> getShips(String lang) {
        return ships.get(lang);
    }

    @Override
    public List<PortModel> getPorts(String lang) {
        return ports.get(lang);
    }

    @Override
    public Set<Integer> getDurations(String lang) {
        return durations.get(lang);
    }

    @Override
    public Set<YearMonth> getDepartureDates(String lang) {
        return departureDates.get(lang);
    }

    @Override
    public Set<FeatureModel> getFeatures(String lang) {
        return features.get(lang);
    }

    /**
     * Recursively collect cruise pages
     *
     * @param rootPage the root page from where to collect cruises
     */
    private void collectCruisesPages(final Page rootPage) {
        if (rootPage.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {
            final String lang = LanguageHelper.getLanguage(rootPage);

            final CruiseModel cruiseModel = rootPage.adaptTo(CruiseModel.class);

            if (cruiseModel != null) {
                cruises.get(lang).add(cruiseModel);

                if (cruiseModel.getDestination() != null
                        && !destinations.get(lang).contains(cruiseModel.getDestination())) {
                    destinations.get(lang).add(cruiseModel.getDestination());
                }

                if (cruiseModel.getShip() != null && !ships.get(lang).contains(cruiseModel.getShip())) {
                    ships.get(lang).add(cruiseModel.getShip());
                }

                for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
                    if (itinerary.getPort() != null && !ports.get(lang).contains(itinerary.getPort())) {
                        ports.get(lang).add(itinerary.getPort());
                    }
                }

                try {
                    durations.get(lang).add(Integer.parseInt(cruiseModel.getDuration()));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Cannot get int value for duration {} in cruise {}", cruiseModel.getDuration(),
                            cruiseModel.getPage().getPath());
                }

                departureDates.get(lang).add(YearMonth.of(cruiseModel.getStartDate().get(Calendar.YEAR),
                        cruiseModel.getStartDate().get(Calendar.MONTH) + 1));

                features.get(lang).addAll(cruiseModel.getFeatures());

                LOGGER.debug("Adding cruise at path {} in cache", cruiseModel.getPage().getPath());

                i++;
            }
        } else {
            if (i > 20) return;

            final Iterator<Page> children = rootPage.listChildren();

            while (children.hasNext()) {
                collectCruisesPages(children.next());
            }
        }
    }
}
