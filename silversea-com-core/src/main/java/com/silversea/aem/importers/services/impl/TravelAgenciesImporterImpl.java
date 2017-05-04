package com.silversea.aem.importers.services.impl;

import java.io.IOException;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.TravelAgenciesImporter;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.model.Agency;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Service
@Component(label = "Silversea.com - Travel Agencies importer")
public class TravelAgenciesImporterImpl extends BaseImporter implements TravelAgenciesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(TravelAgenciesImporterImpl.class);

    private int errorNumber = 0;
    private int succesNumber = 0;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Override
    public void importData() throws IOException {
        /**
         * authentification pour le swagger
         */
         getAuthentification(apiConfig.getLogin(), apiConfig.getPassword());

        // final String authorizationHeader =
        // getAuthorizationHeader("/api/v1/agencies");
        final String authorizationHeader = getAuthorizationHeader(apiConfig.apiUrlConfiguration("agenciesUrl"));

        // get authentification to the Travel Agencies API
        AgenciesApi travelAgenciesApi = new AgenciesApi();
        travelAgenciesApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Page travelRootPage = pageManager.getPage(ImportersConstants.BASEPATH_TRAVEL_AGENCIES);

            int i = 1;

            List<Agency> travelAgencies;

            do {

                // gets all lands
                travelAgencies = travelAgenciesApi.agenciesGet(null, null, null, null, null, i, 100);

                // get root parent travel agencies

                int j = 0;

                for (Agency agency : travelAgencies) {

                    try {
                        // TODO remove this conditions, just to test
                        // if(j==2){
                        // String test = null;
                        // test.toString();
                        // }

                        Iterator<Resource> resources = resourceResolver.findResources(
                                "//element(*,cq:Page)[jcr:content/agencyId=\"" + agency.getAgencyId() + "\"]", "xpath");

                        Page agencyTravelPage = null;

                        if (resources.hasNext()) {
                            agencyTravelPage = resources.next().adaptTo(Page.class);
                        }

                        else {
                            // sous quel neouds faut créer les pages !!!!!
                            // BASEPATH_TRAVEL_AGENCIES
                            // TODO vérifié si le noeud pays existe
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
                                agencyTravelPage = pageManager.create(agencyTravelContryPage.getPath(),
                                        JcrUtil.createValidChildName(agencyTravelContryPage.adaptTo(Node.class),
                                                StringHelper.getFormatWithoutSpecialCharcters(agency.getAgency())),
                                        TemplateConstants.PATH_TRAVEL_AGENCY,
                                        StringHelper.getFormatWithoutSpecialCharcters(agency.getAgency()), false);
                            }
                            // agencyTravelPage =
                            // pageManager.create(travelRootPage.getPath(),
                            // JcrUtil.createValidChildName(travelRootPage.adaptTo(Node.class),
                            // agency.getAgency()),
                            // TemplateConstants.PATH_TRAVEL_AGENCY,
                            // agency.getAgency(), false);
                        }

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
                            j++;
                        }

                        if (j % 100 == 0) {
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
                        LOGGER.debug("Hotel error, number of faulures :", errorNumber);
                        j++;
                    }
                }

                i++;
            } while (travelAgencies.size() > 0);

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
            LOGGER.error("Exception importing shorexes", e);
        }
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getSuccesNumber() {
        return succesNumber;
    }
}
