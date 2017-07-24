package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.GeolocationTagService;
import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.SpecialOffer;
import org.apache.commons.lang3.StringUtils;
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
    public ImportResult importExclusiveOffers() {
        LOGGER.debug("Starting exclusive offers import");

        int errorNumber = 0;
        int successNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final SpecialOffersApi specialOffersApi = new SpecialOffersApi(ImporterUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("exclusiveOffersUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("exclusiveOffersUrl"));
            final List<String> locales = ImporterUtils.getSiteLocales(pageManager);

            // Iterating over locales to import exclusive offers
            for (String locale : locales) {
                final Page exclusiveOffersRootPage = ImporterUtils.getPagePathByLocale(pageManager, rootPage, locale);

                if (exclusiveOffersRootPage == null) {
                    throw new ImporterException("Exclusive offers root page does not exists " + rootPage + " for lang " + locale);
                }

                LOGGER.debug("Cleaning already imported exclusive offers");

                Iterator<Page> children = exclusiveOffersRootPage.listChildren();
                while (children.hasNext()) {
                    final Page child = children.next();

                    try {
                        LOGGER.trace("trying to remove {}", child.getPath());

                        session.removeItem(child.getPath());
                        session.save();
                    } catch (RepositoryException e) {
                        LOGGER.error("Cannot clean already existing exclusive offers");
                    }
                }

                LOGGER.debug("Importing exclusive offers for locale \"{}\"", locale);

                int j = 0, i = 1;
                List<SpecialOffer> exclusiveOffers;

                do {
                    exclusiveOffers = specialOffersApi.specialOffersGet(i, pageSize, null);

                    for (SpecialOffer exclusiveOffer : exclusiveOffers) {
                        LOGGER.trace("Importing exclusive offer: {}", exclusiveOffer.getVoyageSpecialOffer());

                        try {
                            // Create exclusive offer page
                            final Page exclusiveOfferPage = pageManager.create(exclusiveOffersRootPage.getPath(),
                                    JcrUtil.createValidChildName(exclusiveOffersRootPage.adaptTo(Node.class),
                                            StringHelper.getFormatWithoutSpecialCharcters(exclusiveOffer.getVoyageSpecialOffer())),
                                    WcmConstants.PAGE_TEMPLATE_EXCLUSIVE_OFFER,
                                    StringHelper.getFormatWithoutSpecialCharcters(exclusiveOffer.getVoyageSpecialOffer()),
                                    false);

                            // If exclusive offer is created, set the properties
                            if (exclusiveOfferPage == null) {
                                throw new ImporterException("Cannot create exclusive offer page for exclusive offer " + exclusiveOffer.getVoyageSpecialOffer());
                            }

                            Node exclusiveOfferPageContentNode = exclusiveOfferPage.getContentResource().adaptTo(Node.class);

                            if (exclusiveOfferPageContentNode == null) {
                                throw new ImporterException("Cannot set properties for exclusive offer " + exclusiveOffer.getVoyageSpecialOffer());
                            }

                            exclusiveOfferPageContentNode.setProperty(JcrConstants.JCR_TITLE, exclusiveOffer.getVoyageSpecialOffer());
                            exclusiveOfferPageContentNode.setProperty("exclusiveOfferId", exclusiveOffer.getVoyageSpecialOfferId());
                            exclusiveOfferPageContentNode.setProperty("startDate", exclusiveOffer.getValidFrom().toString());
                            exclusiveOfferPageContentNode.setProperty("endDate", exclusiveOffer.getValidTo().toString());

                            // Set geolocation tags for the market
                            List<String> geoMarketsList = exclusiveOffer.getMarkets();
                            String[] geoMarketsTagIds = new String[geoMarketsList.size()];

                            for (int k = 0; k < geoMarketsList.size(); k++) {
                                geoMarketsTagIds[k] = WcmConstants.GEOLOCATION_TAGS_PREFIX + geoMarketsList.get(k).toLowerCase();
                            }

                            exclusiveOfferPageContentNode.setProperty("cq:tags", geoMarketsTagIds);

                            // Set livecopy mixin
                            if (!locale.equals("en")) {
                                exclusiveOfferPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Exclusive offer {} successfully created", exclusiveOfferPage.getPath());

                            successNumber++;
                            j++;

                            if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} exclusive offers imported, saving session", +j);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        } catch (RepositoryException | WCMException | ImporterException e) {
                            errorNumber++;

                            LOGGER.error("Import error", e);
                        }
                    }

                    i++;
                } while (exclusiveOffers.size() > 0);
            }

            ImporterUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("exclusiveOffersUrl"),
                    "lastModificationDate");

            resourceResolver.close();
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read exclusive offers from API", e);
        }

        LOGGER.debug("Ending exclusive offers import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }
}
