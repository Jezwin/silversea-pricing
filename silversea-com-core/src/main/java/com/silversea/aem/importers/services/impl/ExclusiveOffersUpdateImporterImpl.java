package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.services.ExclusiveOffersUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.SpecialOffer;

/**
 * Created by mbennabi on 09/03/2017.
 */
@Service
@Component(label = "Silversea.com - Exclusive Offers importer")
public class ExclusiveOffersUpdateImporterImpl extends BaseImporter implements ExclusiveOffersUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersUpdateImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private Replicator replicat;

    // @Reference
    // private SlingHttpServletRequest request;

    private List<Tag> market;

    private List<String> geoMarket;

    // @Reference
    // private TagManager tagManager;

    // @Activate
    // @Modified
    // protected void activate(ComponentContext compContext) {
    // tagManager = resourceResolver.adaptTo(TagManager.class);
    // }

    @Override
    public ImporterStatus updateImporData() throws IOException {

        ImporterStatus status = new ImporterStatus();

        Set<Integer> diff = new HashSet<Integer>();

        int errorNumber = 0;
        int succesNumber = 0;
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

                        diff.add(offers.getVoyageSpecialOfferId());

                        if (offersPage != null) {
                            Node offersContentNode = offersPage.getContentResource().adaptTo(Node.class);
                            offersContentNode.setProperty(JcrConstants.JCR_TITLE, offers.getVoyageSpecialOffer());
                            offersContentNode.setProperty("exclusiveOfferId", offers.getVoyageSpecialOfferId());
                            offersContentNode.setProperty("startDate", offers.getValidFrom().toString());
                            offersContentNode.setProperty("endDate", offers.getValidTo().toString());

                            offersPage.adaptTo(Node.class).addMixin("cq:Taggable");

                            geoMarket = offers.getMarkets();
                            market = new ArrayList<Tag>();
                            if (GeolocationHelper.getGeoMarketCode(tagManager, geoMarket) != null) {
                                market = GeolocationHelper.getGeoMarketCode(tagManager, geoMarket);
                            }
                            tagManager.setTags(offersPage.getContentResource(), market.stream().toArray((Tag[]::new)));

                            succesNumber = succesNumber + 1;
                            if (!replicat.getReplicationStatus(session, offersRootPage.getPath()).isActivated()) {
                                replicat.replicate(session, ReplicationActionType.ACTIVATE, offersPage.getPath());
                            }
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

            // TODO duplication des pages supprimé dans le retour d'api
            Iterator<Page> resourcess = offersRootPage.listChildren();
            while (resourcess.hasNext()) {
                Page page = resourcess.next();

                if (!diff.contains(
                        Integer.parseInt(page.getContentResource().getValueMap().get("exclusiveOfferId").toString()))) {
                    try {
                        replicat.replicate(session, ReplicationActionType.DEACTIVATE, page.getPath());
                    } catch (ReplicationException e) {
                        e.printStackTrace();
                    }
                }
            }

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

            // try {
            // if(!replicat.getReplicationStatus(session,
            // offersRootPage.getPath()).isActivated()){
            // replicat.replicate(session, ReplicationActionType.ACTIVATE,
            // offersRootPage.getPath());
            // }
            // Iterator<Page> childPages =
            // resourceResolver.getResource(offersRootPage.getPath())
            // .adaptTo(Page.class).listChildren();
            // while (childPages.hasNext()) {
            // Page childPage = childPages.next();
            // replicat.replicate(session, ReplicationActionType.ACTIVATE,
            // childPage.getPath());
            // }
            //
            // } catch (ReplicationException e) {
            // e.printStackTrace();
            // }

            resourceResolver.close();
        } catch (ApiException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing Exclusive offers", e);
        }

        status.setErrorNumber(errorNumber);
        status.setSuccesNumber(succesNumber);

        return status;
    }

    // public int getErrorNumber() {
    // return errorNumber;
    // }
    //
    // public int getSuccesNumber() {
    // return succesNumber;
    // }
}
