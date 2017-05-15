package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.SpecialOffer;

/**
 * Created by mbennabi on 09/03/2017.
 */
@Service
@Component(label = "Silversea.com - Exclusive Offers importer")
public class ExclusiveOffersImporterImpl extends BaseImporter implements ExclusiveOffersImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersImporterImpl.class);

    private int errorNumber = 0;
    private int succesNumber = 0;
    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    // @Reference
    // private SlingHttpServletRequest request;

    private List<Tag> market;

    private List<String> geoMarket;

//    @Reference
//    private TagManager tagManager;

    // @Activate
    // @Modified
    // protected void activate(ComponentContext compContext) {
    // tagManager = resourceResolver.adaptTo(TagManager.class);
    // }

    @Override
    public void importData() throws IOException {
        /**
         * authentification pour le swagger
         */
        getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());
        /**
         * Récuperation du domain de l'api Swager
         */
        getApiDomain(apiConfig.getApiBaseDomain());
        /**
         * Récuperation de la session refresh
         */
        if (apiConfig.getSessionRefresh() != 0) {
            sessionRefresh = apiConfig.getSessionRefresh();
        }
        /**
         * Récuperation de per page
         */
        if (apiConfig.getPageSize() != 0) {
            pageSize = apiConfig.getPageSize();
        }
        // final String authorizationHeader =
        // getAuthorizationHeader("/api/v1/specialOffers");
        final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("spetialOffersUrl"));
        try {
            // get authentification to the Special Offers API
            SpecialOffersApi spetialOffersApi = new SpecialOffersApi();
            spetialOffersApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            // Page offersRootPage =
            // pageManager.getPage(ImportersConstants.BASEPATH_SPECIAL_OFFERS);
            Page offersRootPage = pageManager.getPage(apiConfig.apiRootPath("spetialOffersUrl"));

            int i = 1;

            List<SpecialOffer> specialOffers;

            do {

                // gets all special Offers
                specialOffers = spetialOffersApi.specialOffersGet(i, pageSize, null);

                // get root parent special offers

                int j = 0;

                for (SpecialOffer offers : specialOffers) {

                    try {

                        Iterator<Resource> resources = resourceResolver
                                .findResources("//element(*,cq:Page)[jcr:content/exclusiveOfferId=\""
                                        + offers.getVoyageSpecialOfferId() + "\"]", "xpath");

                        Page offersPage = null;

                        if (resources.hasNext()) {
                            offersPage = resources.next().adaptTo(Page.class);
                        } else {
                            offersPage = pageManager.create(offersRootPage.getPath(),
                                    JcrUtil.createValidChildName(offersRootPage.adaptTo(Node.class),
                                            StringHelper
                                                    .getFormatWithoutSpecialCharcters(offers.getVoyageSpecialOffer())),
                                    TemplateConstants.PATH_EXCLUSIVE_OFFERT,
                                    StringHelper.getFormatWithoutSpecialCharcters(offers.getVoyageSpecialOffer()),
                                    false);
                            // TODO trouver le bon nom du template exclusive
                            // offers
                        }

                        if (offersPage != null) {
                            Node offersContentNode = offersPage.getContentResource().adaptTo(Node.class);
                            offersContentNode.setProperty(JcrConstants.JCR_TITLE, offers.getVoyageSpecialOffer());
                            offersContentNode.setProperty("exclusiveOfferId", offers.getVoyageSpecialOfferId());
                            offersContentNode.setProperty("startDate", offers.getValidFrom().toString());
                            offersContentNode.setProperty("endDate", offers.getValidTo().toString());
                            // TODO decommenté la ligne
                            
                            geoMarket = offers.getMarkets();
                            market = new ArrayList<>();
                            if (GeolocationHelper.getGeoMarketCode(tagManager, geoMarket) != null) {
                                market = GeolocationHelper.getGeoMarketCode(tagManager, geoMarket);
                            }
                            for (Tag tag : market) {
                                offersContentNode.setProperty("cq:tags",market.toString());
//                                tagManager.setTags(offersPage.getContentResource(), tag.t);
                            }
                            
                            succesNumber = succesNumber + 1;
                            j++;
                        }

                        if (j % sessionRefresh == 0) {
                            if (session.hasPendingChanges()) {
                                try {
                                    session.save();
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        }
                    } catch (Exception e) {
                        errorNumber = errorNumber + 1;
                        LOGGER.debug("Exclusive offer falure error, number of faulures :", errorNumber);
                        j++;
                    }
                }

                i++;
            } while (specialOffers.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    // save migration date
                    Node rootNode = offersRootPage.getContentResource().adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing Exclusive offers", e);
        }
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }
}
