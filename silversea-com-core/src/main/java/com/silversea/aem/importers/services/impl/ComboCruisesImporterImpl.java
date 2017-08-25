package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ComboCruisesImporter;
import com.silversea.aem.importers.utils.CruisesImportUtils;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialVoyagesApi;
import io.swagger.client.model.SpecialVoyage;
import io.swagger.client.model.VoyagePriceMarket;
import io.swagger.client.model.VoyageWithItinerary;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Component
public class ComboCruisesImporterImpl implements ComboCruisesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ComboCruisesImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Activate
    protected void activate(final ComponentContext context) {
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }

        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
    }

    @Override
    public ImportResult importAllItems() {
        LOGGER.debug("Starting combo cruises import");

        int successNumber = 0;
        int errorNumber = 0;
        int itemsWritten = 0;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final SpecialVoyagesApi specialVoyagesApi = new SpecialVoyagesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Initializing elements necessary to import itineraries
            // cruises
            final Iterator<Resource> comboCruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/combocruise\"]", "xpath");

            final Map<String, Map<String, String>> comboCruisesMapping = new HashMap<>();
            while (comboCruises.hasNext()) {
                final Resource cruise = comboCruises.next();

                final Page comboCruisePage = cruise.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(comboCruisePage);

                final String comboCruiseCode = cruise.getValueMap().get("comboCruiseCode", String.class);

                if (comboCruiseCode != null) {
                    if (comboCruisesMapping.containsKey(comboCruiseCode)) {
                        comboCruisesMapping.get(comboCruiseCode).put(language, comboCruisePage.getPath());
                    } else {
                        final HashMap<String, String> cruisePaths = new HashMap<>();
                        cruisePaths.put(language, comboCruisePage.getPath());
                        comboCruisesMapping.put(comboCruiseCode, cruisePaths);
                    }

                    LOGGER.trace("Adding cruise {} ({}) with lang {} to cache", cruise.getPath(), comboCruiseCode, language);
                }
            }

            // TODO duplicated from cruises prices importer
            // suites (by category code)
            final Iterator<Resource> suitesForMapping = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"]", "xpath");

            final Map<String, Map<String, Resource>> suitesMapping = new HashMap<>();
            while (suitesForMapping.hasNext()) {
                final Resource suite = suitesForMapping.next();
                final String language = LanguageHelper.getLanguage(pageManager, suite);

                final String[] suiteCategoryCodes = suite.getValueMap().get("suiteCategoryCode", String[].class);

                if (suiteCategoryCodes != null) {
                    for (final String suiteCategoryCode : suiteCategoryCodes) {
                        // generate unique key with ship name and suite category code
                        final String suiteCatId = suite.getParent().getParent().getParent().getName() + "-" +
                                suiteCategoryCode;

                        if (suitesMapping.containsKey(suiteCatId)) {
                            suitesMapping.get(suiteCatId).put(language, suite.getParent());
                        } else {
                            final HashMap<String, Resource> suitesResources = new HashMap<>();
                            suitesResources.put(language, suite.getParent());
                            suitesMapping.put(suiteCatId, suitesResources);
                        }
                    }

                    LOGGER.trace("Adding suite {} ({}) with lang {} to cache", suite.getPath(), suiteCategoryCodes, language);
                }
            }

            // Initializing elements necessary to import itineraries
            // cruises
            final Iterator<Resource> cruises = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/cruise\"]", "xpath");

            final Map<Integer, Map<String, String>> cruisesMapping = new HashMap<>();
            while (cruises.hasNext()) {
                final Resource cruise = cruises.next();

                final Page cruisePage = cruise.getParent().adaptTo(Page.class);
                final String language = LanguageHelper.getLanguage(cruisePage);

                final Integer cruiseId = cruise.getValueMap().get("cruiseId", Integer.class);

                if (cruiseId != null) {
                    if (cruisesMapping.containsKey(cruiseId)) {
                        cruisesMapping.get(cruiseId).put(language, cruisePage.getPath());
                    } else {
                        final HashMap<String, String> cruisePaths = new HashMap<>();
                        cruisePaths.put(language, cruisePage.getPath());
                        cruisesMapping.put(cruiseId, cruisePaths);
                    }

                    LOGGER.trace("Adding cruise {} ({}) with lang {} to cache", cruise.getPath(), cruiseId, language);
                }
            }

            // writing combo cruises informations
            final List<SpecialVoyage> specialVoyages = specialVoyagesApi.specialVoyagesGet(null);

            for (final SpecialVoyage specialVoyage : specialVoyages) {
                if (comboCruisesMapping.containsKey(specialVoyage.getSpecialVoyageId())) {
                    final Map<String, String> comboCruisesPages = comboCruisesMapping.get(specialVoyage.getSpecialVoyageId());

                    for (final Map.Entry<String, String> comboCruisePath : comboCruisesPages.entrySet()) {
                        try {
                            final Node comboCruiseContentNode = session.getNode(comboCruisePath.getValue() + "/jcr:content");

                            if (comboCruiseContentNode != null) {
                                comboCruiseContentNode.setProperty("apiTitle", specialVoyage.getSpecialVoyageName());

                                // Creating prices root node under the cruise
                                final Node suitesNode = JcrUtils.getOrAddNode(comboCruiseContentNode, "suites");
                                suitesNode.setProperty("sling:resourceType", "silversea/silversea-com/components/subpages/prices");

                                for (final VoyagePriceMarket priceMarket : specialVoyage.getPrices()) {
                                    CruisesImportUtils.importCruisePrice(session, comboCruiseContentNode, comboCruisePath,
                                            suitesMapping, priceMarket, suitesNode, successNumber, errorNumber, itemsWritten, sessionRefresh);
                                }
                            }

                            for (VoyageWithItinerary voyage : specialVoyage.getVoyages()) {
                                try {
                                    if (!cruisesMapping.containsKey(voyage.getVoyageId())) {
                                        throw new ImporterException("Cannot find cruise with id " + voyage.getVoyageId() + " in mapping");
                                    }

                                    final String pageName = JcrUtil.createValidName(StringUtils
                                            .stripAccents(voyage.getVoyageName() + " - " + voyage.getVoyageCod()), JcrUtil.HYPHEN_LABEL_CHAR_MAPPING)
                                            .replaceAll("-+", "-");

                                    // creating cruise page - uniqueness is derived from cruise code
                                    final Page segmentPage = pageManager.create(comboCruisePath.getValue(),
                                            pageName, WcmConstants.PAGE_TEMPLATE_COMBO_CRUISE_SEGMENT, voyage.getVoyageCod() + " - " + voyage.getVoyageName(), false);

                                    final Node segmentPageContentNode = segmentPage.getContentResource().adaptTo(Node.class);

                                    if (segmentPageContentNode != null) {
                                        segmentPageContentNode.setProperty("cruiseReference", cruisesMapping.get(voyage.getVoyageId()).get(comboCruisePath.getKey()));
                                    }

                                    successNumber++;
                                    itemsWritten++;

                                    if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                        try {
                                            session.save();

                                            LOGGER.info("{} itineraries imported, saving session", +itemsWritten);
                                        } catch (RepositoryException e) {
                                            session.refresh(true);
                                        }
                                    }
                                } catch (RepositoryException | WCMException | ImporterException e) {
                                    LOGGER.warn("Cannot write combo cruise itinerary informations {}", e.getMessage());

                                    errorNumber++;
                                }
                            }
                        } catch (RepositoryException e) {
                            LOGGER.warn("Cannot write combo cruise informations {}", e.getMessage());

                            errorNumber++;
                        }
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.info("{} prices imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException | ImporterException e) {
            LOGGER.error("Cannot import combo cruises", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read combo cruises from API", e);
        }

        LOGGER.info("Ending cruises import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }
}