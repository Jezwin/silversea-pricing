package com.silversea.aem.importers.services.impl;

import java.io.IOException;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.components.beans.ImporterStatus;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.services.TravelAgenciesUpdateImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.model.Agency;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Travel Agencies update importer")
public class TravelAgenciesUpdateImporterImpl extends BaseImporter implements TravelAgenciesUpdateImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(TravelAgenciesUpdateImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private Replicator replicat;

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
        final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("agenciesUrl"));

        AgenciesApi travelAgenciesApi = new AgenciesApi();
        travelAgenciesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Page travelRootPage = pageManager.getPage(apiConfig.apiRootPath("agenciesUrl"));

            int i = 1;

            List<Agency> travelAgencies;

            do {
                travelAgencies = travelAgenciesApi.agenciesGet(null, null, null, null, null, i, pageSize);
                int j = 0;

                for (Agency agency : travelAgencies) {
                    try {
                        Iterator<Resource> resources = resourceResolver.findResources(
                                "//element(*,cq:Page)[jcr:content/agencyId=\"" + agency.getAgencyId() + "\"]", "xpath");

                        Page agencyTravelPage = null;

                        if (resources.hasNext()) {
                            agencyTravelPage = resources.next().adaptTo(Page.class);
                        }

                        else {
                            Page agencyTravelContryPage = pageManager
                                    .getPage("/content/silversea-com/en/other-resources/find-a-travel-agent/"
                                            + agency.getCountryIso3().toLowerCase());
                            if (agencyTravelContryPage == null) {
                                agencyTravelContryPage = pageManager.create(travelRootPage.getPath(),
                                        JcrUtil.createValidChildName(travelRootPage.adaptTo(Node.class),
                                                StringHelper.getFormatWithoutSpecialCharcters(agency.getCountryIso3())),
                                        "/apps/silversea/silversea-com/templates/page",
                                        StringHelper.getFormatWithoutSpecialCharcters(agency.getCountryIso3()), false);
                            }
                            if (agencyTravelContryPage != null) {
//                                if (!replicat.getReplicationStatus(session, travelRootPage.getPath()).isActivated()) {
//                                	if (!replicat.getReplicationStatus(session, agencyTravelContryPage.getPath()).isActivated()) {
                                    replicat.replicate(session, ReplicationActionType.ACTIVATE,
                                            agencyTravelContryPage.getPath());
//                                	}
//                                }
                                agencyTravelPage = pageManager.create(agencyTravelContryPage.getPath(),
                                        JcrUtil.createValidChildName(agencyTravelContryPage.adaptTo(Node.class),
                                                StringHelper.getFormatWithoutSpecialCharcters(agency.getAgency())),
                                        TemplateConstants.PATH_TRAVEL_AGENCY,
                                        StringHelper.getFormatWithoutSpecialCharcters(agency.getAgency()), false);
                            }
                        }

                        diff.add(agency.getAgencyId());

                        if (agencyTravelPage != null) {
                            Node agencyContentNode = agencyTravelPage.getContentResource().adaptTo(Node.class);
                            agencyContentNode.setProperty(JcrConstants.JCR_TITLE, agency.getAgency());
                            agencyContentNode.setProperty("agencyId", agency.getAgencyId());
                            agencyContentNode.setProperty("address", agency.getAddress());
                            agencyContentNode.setProperty("city", agency.getCity());
                            agencyContentNode.setProperty("zip", agency.getZip());
                            agencyContentNode.setProperty("zip4", agency.getZip4());
                            agencyContentNode.setProperty("countryIso3", agency.getCountryIso3());
                            agencyContentNode.setProperty("stateCode", agency.getStateCod());
                            agencyContentNode.setProperty("county", agency.getCounty());
                            agencyContentNode.setProperty("phone", agency.getPhone());
                            agencyContentNode.setProperty("latitude", agency.getLat());
                            agencyContentNode.setProperty("longitude", agency.getLon());
                            succesNumber = succesNumber + 1;

//                            if (!replicat.getReplicationStatus(session, travelRootPage.getPath()).isActivated()) {
                                replicat.replicate(session, ReplicationActionType.ACTIVATE, agencyTravelPage.getPath());
//                            }
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
                        LOGGER.debug("Travel agency error, number of faulures :", errorNumber);
                        j++;
                    }
                }

                i++;
            } while (travelAgencies.size() > 0 && i<1);

            // TODO Depublication of deleted pages
            Iterator<Page> resourcess = travelRootPage.listChildren();
            while (resourcess.hasNext()) {
                Page page = resourcess.next();

                if (page.getContentResource().getValueMap().get("agencyId")!= null &&!diff.contains(
                        Integer.parseInt(page.getContentResource().getValueMap().get("agencyId").toString()))) {
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
                    Node rootNode = travelRootPage.getContentResource().adaptTo(Node.class);
                    rootNode.setProperty("lastModificationDate", Calendar.getInstance());
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing travel agencies", e);
        }
        status.setErrorNumber(errorNumber);
        status.setSuccesNumber(succesNumber);

        return status;
    }

}
