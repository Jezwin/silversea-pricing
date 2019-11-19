package com.silversea.aem.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.config.ConfigurationManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.content.CrxContentLoader;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.LogzLogger;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;

import java.time.YearMonth;
import java.util.*;

import static com.silversea.aem.logging.JsonLog.jsonLog;
import static com.silversea.aem.logging.JsonLog.jsonLogWithMessage;
import static com.silversea.aem.logging.JsonLog.jsonLogWithMessageAndError;

@Service
@Component
public class CruisesCacheServiceImpl implements CruisesCacheService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    protected LogzLoggerFactory logzLoggerFactory;

    private LogzLogger logzLogger;

    private Map<String, Map<String, CruiseModelLight>> cruisesByCode = new HashMap<>();

    private Map<String, List<DestinationModelLight>> destinations = new HashMap<>();

    private Map<String, List<ShipModelLight>> ships = new HashMap<>();

    private Map<String, List<PortModelLight>> ports = new HashMap<>();

    private Map<String, Set<Integer>> durations = new HashMap<>();

    private Map<String, Set<YearMonth>> departureDates = new HashMap<>();

    private Map<String, Set<FeatureModelLight>> features = new HashMap<>();

    private Map<String, Map<String, CruiseModelLight>> cruisesByCodeTmp = new HashMap<>();

    private Map<String, List<DestinationModelLight>> destinationsTmp = new HashMap<>();

    private Map<String, List<ShipModelLight>> shipsTmp = new HashMap<>();

    private Map<String, List<PortModelLight>> portsTmp = new HashMap<>();

    private Map<String, Set<Integer>> durationsTmp = new HashMap<>();

    private Map<String, Set<YearMonth>> departureDatesTmp = new HashMap<>();

    private Map<String, Set<FeatureModelLight>> featuresTmp = new HashMap<>();

    @Override
    public ImportResult buildCruiseCache() {

        logzLogger = logzLoggerFactory.getLogger(CruisesCacheService.class.getName());
        logzLogger.logInfo(jsonLogWithMessage("FYCCacheRebuildStarting", "Start of FYCCacheRebuild"));

        ImportResult importResult = new ImportResult();
        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);


        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(
                authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            // limit memory usage locally
            List<String> languages;
            if (slingSettingsService.getRunModes().contains("local")) {
                languages = Collections.singletonList("en");
            } else {
                languages = ImportersUtils.getSiteLocales(pageManager);
            }
            cruisesByCodeTmp = new HashMap<>();
            destinationsTmp = new HashMap<>();
            shipsTmp = new HashMap<>();
            durationsTmp = new HashMap<>();
            departureDatesTmp = new HashMap<>();
            featuresTmp = new HashMap<>();

            for (final String lang : languages) {
                // init language
                cruisesByCodeTmp.put(lang, new HashMap<>());
                destinationsTmp.put(lang, new ArrayList<>());
                shipsTmp.put(lang, new ArrayList<>());
                portsTmp.put(lang, new ArrayList<>());
                durationsTmp.put(lang, new TreeSet<>());
                departureDatesTmp.put(lang, new TreeSet<>());
                featuresTmp.put(lang, new TreeSet<>(Comparator.comparing(FeatureModelLight::getName)));

                // collect cruises
                final Page destinationsPage = pageManager.getPage("/content/silversea-com/" + lang + "/destinations");
                collectCruisesPages(destinationsPage);
                destinationsTmp.get(lang).sort(Comparator.comparing(DestinationModelLight::getTitle));
                shipsTmp.get(lang).sort(Comparator.comparing(ShipModelLight::getTitle));
                portsTmp.get(lang).sort(Comparator.comparing(PortModelLight::getApiTitle));

            }

            cruisesByCode = cruisesByCodeTmp;
            destinations = destinationsTmp;
            ships = shipsTmp;
            ports = portsTmp;
            durations = durationsTmp;
            departureDates = departureDatesTmp;
            features = featuresTmp;

            cruisesByCodeTmp = null;
            destinationsTmp = null;
            shipsTmp = null;
            durationsTmp = null;
            departureDatesTmp = null;
            featuresTmp = null;

            int i = 0;
            for (Map.Entry<String, Map<String, CruiseModelLight>> cruise : cruisesByCode.entrySet()) {
                i += cruise.getValue().size();
            }
            importResult.incrementSuccessNumber();
            logzLogger.logInfo(jsonLogCruisesInCache("FYCCacheRebuildComplete", "End of FYCCacheRebuild", i));
        } catch (LoginException e) {
            importResult.incrementErrorNumber();
            logzLogger.logError(jsonLogWithMessageAndError("ResourceResolverError","Cannot create resource resolver",e));
        }
        return importResult;
    }

    @Override
    public List<CruiseModelLight> getCruises(final String lang) {
        return new ArrayList<>(cruisesByCode.get(lang).values());
    }

    @Override
    public CruiseModelLight getCruiseByCruiseCode(final String lang, final String cruiseCode) {
        return (cruisesByCode.get(lang) != null) ? cruisesByCode.get(lang).get(cruiseCode) : null;
    }

    @Override
    public List<DestinationModelLight> getDestinations(final String lang) {
        return destinations.get(lang);
    }

    @Override
    public List<ShipModelLight> getShips(final String lang) {
        return ships.get(lang);
    }

    @Override
    public List<PortModelLight> getPorts(final String lang) {
        return ports.get(lang);
    }

    @Override
    public Set<Integer> getDurations(final String lang) {
        return durations.get(lang);
    }

    @Override
    public Set<YearMonth> getDepartureDates(final String lang) {
        return departureDates.get(lang);
    }

    @Override
    public Set<FeatureModelLight> getFeatures(final String lang) {
        return features.get(lang);
    }

    @Override
    public void addOrUpdateCruise(CruiseModelLight cruiseModel, String langIn) {
        if (cruiseModel == null) {

            logzLogger.logWarning(jsonLogWithMessage("CannotUpdateCache","Cannot update cache, the cruise model provided is null"));
            return;
        }

        logzLogger.logDebug(jsonLogWithCruisePath("UpdateCruiseCache","Updating cruise cache",cruiseModel.getPath()));

        final String cruiseCode = cruiseModel.getCruiseCode();
        final String lang = langIn;

        if (cruisesByCode.containsKey(lang)) {
            //cruisesByCode.get(lang).remove(cruiseCode);
            cruisesByCode.get(lang).put(cruiseCode, cruiseModel);
        } else {
            final HashMap<String, CruiseModelLight> cruiseByCode = new HashMap<>();
            cruiseByCode.put(cruiseCode, cruiseModel);

            cruisesByCode.put(lang, cruiseByCode);
        }
    }

    @Override
    public void removeCruise(String lang, String cruiseCode) {
        if (lang == null || cruiseCode == null) {
            logzLogger.logWarning(jsonLogCruiseCodeWithLang("CannotUpdateCache","Cannot update cache",cruiseCode,lang));
            return;
        }

        logzLogger.logDebug(jsonLogCruiseCodeWithLang("RemoveCruiseFromCache","Removing cruise from cache", cruiseCode,lang));

        if (cruisesByCode.containsKey(lang) && cruisesByCode.get(lang).containsKey(cruiseCode)) {
            cruisesByCode.get(lang).remove(cruiseCode);
        }
    }

    /**
     * Recursively collect cruise pages
     *
     * @param rootPage the root page from where to collect cruises
     */
    private void collectCruisesPages(final Page rootPage) {
        if (rootPage.getContentResource() != null && rootPage.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {
            try{
                final String lang = LanguageHelper.getLanguage(rootPage);

                final CruiseModel cruiseModel = rootPage.adaptTo(CruiseModel.class);

                if (cruiseShouldBeCached(cruiseModel)) {
                    CruiseModelLight cruiseModelLight = new CruiseModelLight(cruiseModel);
                    cruisesByCodeTmp.get(lang).put(cruiseModelLight.getCruiseCode(), cruiseModelLight);

                    if (cruiseModel.getDestination() != null
                            && !destinationsTmp.get(lang).contains(new DestinationModelLight(cruiseModel.getDestination()))) {
                        destinationsTmp.get(lang).add(new DestinationModelLight(cruiseModel.getDestination()));
                    }

                    if (cruiseModel.getShip() != null && !shipsTmp.get(lang).contains(new ShipModelLight(cruiseModel.getShip()))) {
                        shipsTmp.get(lang).add(new ShipModelLight(cruiseModel.getShip()));
                    }

                    if (cruiseModel.getItineraries() != null) {
                        for (ItineraryModel itinerary : cruiseModel.getItineraries()) {
                            if (itinerary.getPort() != null && !portsTmp.get(lang).contains(new PortModelLight(itinerary.getPort()))) {
                                portsTmp.get(lang).add(new PortModelLight(itinerary.getPort()));
                            }
                        }
                    }

                    try {
                        durationsTmp.get(lang).add(Integer.parseInt(cruiseModel.getDuration()));
                    } catch (NumberFormatException e) {
                        logzLogger.logWarning(jsonLogDurationPath("CannotGetInitValueOfCruise","cannot get init value of duration of cruise",cruiseModel.getDuration(),cruiseModel.getPage().getPath()));
                    }

                    departureDatesTmp.get(lang).add(YearMonth.of(cruiseModel.getStartDate().get(Calendar.YEAR),
                            cruiseModel.getStartDate().get(Calendar.MONTH) + 1));
                    List<FeatureModel> tmpFeat = cruiseModel.getFeatures();
                    List<FeatureModelLight> tmpFeatLight = new ArrayList<>();
                    for (FeatureModel featureModel : tmpFeat) {
                        tmpFeatLight.add(new FeatureModelLight(featureModel));
                    }
                    featuresTmp.get(lang).addAll(tmpFeatLight);
                    tmpFeat = null;
                    tmpFeatLight = null;

                    logzLogger.logDebug(jsonLogWithCruisePath("AddingCruiseInPath","Adding cruise in cache",cruiseModel.getPage().getPath()));
                }
            }catch(Exception e){
                logzLogger.logError(jsonLogWithCruisePath("ErrorAddingCruiseInPath","Error adding cruise in path",rootPage.getPath()));
            }
        } else {
            final Iterator<Page> children = rootPage.listChildren();

            while (children.hasNext()) {
                collectCruisesPages(children.next());
            }
        }
    }

    private boolean cruiseShouldBeCached(CruiseModel cruiseModel) {
        if(cruiseModel == null)
            return false;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
        boolean forceVisibleOnAuthor = false;

        try(final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)){
            CrxContentLoader contentLoader = new CrxContentLoader(resourceResolver);
            ConfigurationManager configurationManager = new ConfigurationManager(contentLoader);
            AppSettingsModel appSettings = configurationManager.getAppSettings();

            forceVisibleOnAuthor = slingSettingsService.getRunModes().contains("author") && appSettings.getShowAllCruisesOnAuthorEnabled();
        }
        catch (LoginException le) {
            logzLogger.logError(jsonLogWithMessageAndError("ResourceResolverError","Cannot create resource resolver",le));
        } catch (Exception e) {
            logzLogger.logError(jsonLogWithMessageAndError("Generic error","Error on CruisesCacheServiceImpl.cruiseShouldBeCached",e));
        }

        boolean isVisible = forceVisibleOnAuthor || cruiseModel.isVisible();

        boolean departureInTheFuture = cruiseModel.getStartDate().after(Calendar.getInstance());

        return isVisible && departureInTheFuture;
    }

    private JsonLog jsonLogCruisesInCache(String event, String message, Integer cruiseInCache){
        return jsonLog(event,message)
                .with("cruiseCacheSize", cruiseInCache);
    }

    private JsonLog jsonLogWithCruisePath(String event, String message, String cruiseModelPath){
        return jsonLog(event,message)
                .with("cruiseModelPath", cruiseModelPath);
    }

    private JsonLog jsonLogCruiseCodeWithLang(String event, String message, String cruiseCode,String lang){
        return jsonLog(event,message)
                .with("cruiseCode", cruiseCode)
                .with("lang", lang);
    }

    private JsonLog jsonLogDurationPath(String event, String message, String duration, String path){
        return jsonLog(event,message)
                .with("duration", duration)
                .with("path", path);
    }

}
