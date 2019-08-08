package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.GeolocationTagService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.SpecialOffer;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Service
@Component
public class ExclusiveOffersImporterImpl implements ExclusiveOffersImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersImporterImpl.class);

    private int sessionRefresh = 100;

    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private GeolocationTagService geolocationTagService;

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
        LOGGER.debug("Starting exclusive offers import");

        int errorNumber = 0;
        int successNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final SpecialOffersApi specialOffersApi = new SpecialOffersApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Create a cache of existing exclusive offers
            final Map<Integer, Map<String, String>> exclusiveOffersMapping = ImportersUtils
                    .getItemsMapping(resourceResolver,
                            "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                                    "[sling:resourceType=\"silversea/silversea-com/components/pages/exclusiveoffer\"]",
                            "exclusiveOfferId");

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("exclusiveOffersUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("exclusiveOffersUrl"));
            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            // Iterate over exclusive offers from API
            int itemsWritten = 0, apiPage = 1;

            List<SpecialOffer> exclusiveOffers;
            List<Integer> apiExclusiveOffers = new ArrayList<>();

            do {
                exclusiveOffers = specialOffersApi.specialOffersGet(apiPage, pageSize, null);

                // iterate over offers from api
                for (SpecialOffer exclusiveOffer : exclusiveOffers) {
                    LOGGER.trace("Importing exclusive offer: {}", exclusiveOffer.getVoyageSpecialOffer());

                    // exclusive offer not exists
                    if (!exclusiveOffersMapping.containsKey(exclusiveOffer.getVoyageSpecialOfferId())) {

                        // iterating over locales to create the pages
                        for (String locale : locales) {
                            final Page exclusiveOffersRootPage = ImportersUtils
                                    .getPagePathByLocale(pageManager, rootPage, locale);

                            if (exclusiveOffersRootPage == null) {
                                throw new ImporterException(
                                        "Exclusive offers root page does not exists " + rootPage + " for lang " +
                                                locale);
                            }

                            try {
                                // Create exclusive offer page
                                final Page exclusiveOfferPage = pageManager.create(exclusiveOffersRootPage.getPath(),
                                        JcrUtil.createValidChildName(exclusiveOffersRootPage.adaptTo(Node.class),
                                                StringsUtils.getFormatWithoutSpecialCharacters(
                                                        exclusiveOffer.getVoyageSpecialOffer())),
                                        WcmConstants.PAGE_TEMPLATE_EXCLUSIVE_OFFER,
                                        exclusiveOffer.getVoyageSpecialOffer(), false);

                                // If exclusive offer is created, set the properties
                                if (exclusiveOfferPage == null) {
                                    throw new ImporterException(
                                            "Cannot create exclusive offer page for exclusive offer " + exclusiveOffer
                                                    .getVoyageSpecialOffer());
                                }

                                final Node exclusiveOfferPageContentNode = writeExclusiveOfferProperties(exclusiveOffer,
                                        exclusiveOfferPage);

                                // Set livecopy mixin
                                if (!locale.equals("en")) {
                                    exclusiveOfferPageContentNode.addMixin("cq:LiveRelationship");
                                }

                                LOGGER.trace("Exclusive offer {} successfully created", exclusiveOfferPage.getPath());

                                successNumber++;
                                itemsWritten++;

                                batchSave(session, itemsWritten);
                            } catch (RepositoryException | WCMException | ImporterException e) {
                                errorNumber++;

                                LOGGER.error("Import error", e);
                            }
                        }
                    } else { // exclusive offer exists
                        final Map<String, String> exclusiveOfferPagesPath = exclusiveOffersMapping
                                .get(exclusiveOffer.getVoyageSpecialOfferId());

                        for (Map.Entry<String, String> exclusiveOfferPagePath : exclusiveOfferPagesPath.entrySet()) {
                            try {
                                final Page exclusiveOfferPage = pageManager.getPage(exclusiveOfferPagePath.getValue());

                                // check if properties are the same
                                final ValueMap exclusiveOfferProperties = exclusiveOfferPage.getProperties();

                                boolean isEqual = true;
                                final String title = exclusiveOfferProperties.get("jcr:title", String.class);
                                if (title == null || !title.equals(exclusiveOffer.getVoyageSpecialOffer())) {
                                    isEqual = false;
                                }

                                final Date startDate = exclusiveOfferProperties.get("startDate", Date.class);
                                if (startDate == null || !startDate.equals(exclusiveOffer.getValidFrom().toDate())) {
                                    isEqual = false;
                                }

                                final Date endDate = exclusiveOfferProperties.get("endDate", Date.class);
                                if (endDate == null || !endDate.equals(exclusiveOffer.getValidTo().toDate())) {
                                    isEqual = false;
                                }

                                if (!isEqual) {
                                    writeExclusiveOfferProperties(exclusiveOffer, exclusiveOfferPage);

                                    LOGGER.trace("Exclusive offer {} successfully updated",
                                            exclusiveOfferPage.getPath());

                                    successNumber++;
                                    itemsWritten++;

                                    batchSave(session, itemsWritten);
                                } else {
                                    successNumber++;

                                    LOGGER.trace("Exclusive offer {} not updated", exclusiveOfferPage.getPath());
                                }
                            } catch (RepositoryException | ImporterException e) {
                                errorNumber++;

                                LOGGER.warn("Import error {}", e.getMessage());
                            }
                        }
                    }

                    // add exclusive offer to offers list
                    apiExclusiveOffers.add(exclusiveOffer.getVoyageSpecialOfferId());
                }

                apiPage++;
            } while (exclusiveOffers.size() > 0);

            // deactivate offers which not exists anymore
            for (Map.Entry<Integer, Map<String, String>> exclusiveOffer : exclusiveOffersMapping.entrySet()) {
                if (!apiExclusiveOffers.contains(exclusiveOffer.getKey())) {
                    for (Map.Entry<String, String> exclusiveOfferPagePath : exclusiveOffer.getValue().entrySet()) {
                        try {
                            final Page exclusiveOfferPage = pageManager.getPage(exclusiveOfferPagePath.getValue());

                            Node exclusiveOfferPageContentNode = exclusiveOfferPage.getContentResource()
                                    .adaptTo(Node.class);
                            if (exclusiveOfferPageContentNode == null) {
                                throw new ImporterException(
                                        "Cannot set properties for exclusive offer " + exclusiveOfferPage.getPath());
                            }

                            exclusiveOfferPageContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

                            successNumber++;
                            itemsWritten++;

                            batchSave(session, itemsWritten);

                            LOGGER.trace("Exclusive offer {} successfully removed", exclusiveOfferPage.getPath());
                        } catch (RepositoryException | ImporterException e) {
                            errorNumber++;

                            LOGGER.warn("Import error {}", e.getMessage());
                        }
                    }
                }
            }

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("exclusiveOffersUrl"),
                    "lastModificationDate", true);
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} exclusive offers, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read exclusive offers from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending exclusive offers import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public JSONObject getJsonMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory
                    .getServiceResourceResolver(authenticationParams);

            Iterator<Resource> exclusiveOffers = resourceResolver.findResources(
                    "/jcr:root/content/silversea-com" + "//element(*,cq:Page)" +
                            "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages" +
                            "/exclusiveoffer\"]",
                    "xpath");

            while (exclusiveOffers.hasNext()) {
                final Resource exclusiveOffer = exclusiveOffers.next();
                final Page exclusiveOfferPage = exclusiveOffer.adaptTo(Page.class);

                final Resource childContent = exclusiveOffer.getChild(JcrConstants.JCR_CONTENT);

                if (exclusiveOfferPage != null && childContent != null) {
                    final ValueMap childContentProperties = childContent.getValueMap();
                    final String exclusiveOfferId = childContentProperties.get("exclusiveOfferId", String.class);
                    final String exclusiveOfferLang = exclusiveOfferPage.getAbsoluteParent(2).getName();
                    final String exclusiveOfferPath = exclusiveOfferPage.getPath();

                    if (exclusiveOfferId != null && exclusiveOfferLang != null) {
                        try {
                            if (jsonObject.has(exclusiveOfferId)) {
                                final JSONObject exclusiveOfferObject = jsonObject.getJSONObject(exclusiveOfferId);
                                exclusiveOfferObject.put(exclusiveOfferLang, exclusiveOfferPath);
                                jsonObject.put(exclusiveOfferId, exclusiveOfferObject);
                            } else {
                                JSONObject shipObject = new JSONObject();
                                shipObject.put(exclusiveOfferLang, exclusiveOfferPath);
                                jsonObject.put(exclusiveOfferId, shipObject);
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add exclusiveOffer {} with path {} to exclusiveOffers array",
                                    exclusiveOfferId, exclusiveOffer.getPath(), e);
                        }
                    }
                }
            }

        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        }

        return jsonObject;
    }

    private void batchSave(Session session, int itemsWritten) throws RepositoryException {
        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
            try {
                session.save();

                LOGGER.debug("{} exclusive offers written, saving session", +itemsWritten);
            } catch (RepositoryException e) {
                session.refresh(true);
            }
        }
    }

    private Node writeExclusiveOfferProperties(SpecialOffer exclusiveOffer, Page exclusiveOfferPage) throws
            ImporterException, RepositoryException {
        Node exclusiveOfferPageContentNode = exclusiveOfferPage.getContentResource().adaptTo(Node.class);

        if (exclusiveOfferPageContentNode == null) {
            throw new ImporterException(
                    "Cannot set properties for exclusive offer " + exclusiveOffer.getVoyageSpecialOffer());
        }

        exclusiveOfferPageContentNode.setProperty(JcrConstants.JCR_TITLE, exclusiveOffer.getVoyageSpecialOffer());
        exclusiveOfferPageContentNode.setProperty("exclusiveOfferId", exclusiveOffer.getVoyageSpecialOfferId());
        exclusiveOfferPageContentNode.setProperty("startDate", exclusiveOffer.getValidFrom().toGregorianCalendar());
        exclusiveOfferPageContentNode.setProperty("endDate", exclusiveOffer.getValidTo().toGregorianCalendar());

       // exclusiveOfferPageContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

        // Set geolocation tags for the market
        List<String> geoMarketsList = exclusiveOffer.getMarkets();
        String[] geoMarketsTagIds = new String[geoMarketsList.size()];

        for (int k = 0; k < geoMarketsList.size(); k++) {
            geoMarketsTagIds[k] = WcmConstants.GEOLOCATION_TAGS_PREFIX + geoMarketsList.get(k).toLowerCase();
        }

        exclusiveOfferPageContentNode.setProperty("cq:tags", geoMarketsTagIds);

        return exclusiveOfferPageContentNode;
    }
}
