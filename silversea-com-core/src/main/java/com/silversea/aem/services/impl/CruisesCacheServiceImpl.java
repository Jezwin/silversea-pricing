package com.silversea.aem.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImportersConstants;
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

    private int i = 0;

    @Activate
    protected void activate(final ComponentContext context) {
        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            // init language
            cruises.put("en", new ArrayList<>());
            destinations.put("en", new ArrayList<>());
            ships.put("en", new ArrayList<>());
            ports.put("en", new ArrayList<>());
            durations.put("en", new TreeSet<>((o1, o2) -> {
                try {
                    final Integer o1Int = Integer.valueOf(o1);
                    final Integer o2Int = Integer.valueOf(o2);

                    return o1Int.compareTo(o2Int);
                } catch (NumberFormatException ignored) {}

                return 0;
            }));

            // collect cruises
            final Page destinationsPage = pageManager.getPage("/content/silversea-com/en/destinations");
            collectCruisesPages(destinationsPage);

            destinations.get("en").sort(Comparator.comparing(DestinationModel::getTitle));
            ships.get("en").sort(Comparator.comparing(ShipModel::getTitle));
            ports.get("en").sort(Comparator.comparing(PortModel::getApiTitle));

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

    /**
     * Recursively collect cruise pages
     *
     * @param rootPage the root page from where to collect cruises
     */
    private void collectCruisesPages(final Page rootPage) {
        if (rootPage.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {
            final CruiseModel cruiseModel = rootPage.adaptTo(CruiseModel.class);

            if (cruiseModel != null) {
                cruises.get("en").add(cruiseModel);

                if (cruiseModel.getDestination() != null
                        && !destinations.get("en").contains(cruiseModel.getDestination())) {
                    destinations.get("en").add(cruiseModel.getDestination());
                }

                if (cruiseModel.getShip() != null && !ships.get("en").contains(cruiseModel.getShip())) {
                    ships.get("en").add(cruiseModel.getShip());
                }

                for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
                    if (itinerary.getPort() != null && !ports.get("en").contains(itinerary.getPort())) {
                        ports.get("en").add(itinerary.getPort());
                    }
                }

                try {
                    durations.get("en").add(Integer.parseInt(cruiseModel.getDuration()));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Cannot get int value for duration {} in cruise {}", cruiseModel.getDuration(),
                            cruiseModel.getPage().getPath());
                }

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
