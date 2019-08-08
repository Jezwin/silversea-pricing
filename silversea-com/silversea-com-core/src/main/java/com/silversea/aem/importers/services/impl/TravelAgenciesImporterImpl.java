package com.silversea.aem.importers.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.TravelAgenciesImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.TravelAgencyModel;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.model.Agency;

import org.apache.cxf.common.util.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONArray;
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
public class TravelAgenciesImporterImpl implements TravelAgenciesImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(TravelAgenciesImporterImpl.class);

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
        LOGGER.debug("Starting agencies import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final AgenciesApi agenciesApi = new AgenciesApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Map<Integer, Map<String, Page>> agenciesMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" + "[sling:resourceType=\"silversea/silversea-com/components/pages/travelagency\"]", "agencyId");

            // Getting paths to import data
            LOGGER.trace("Getting root page : {}", apiConfig.apiRootPath("agenciesUrl"));
            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("agenciesUrl"));
            final List<String> locales = ImportersUtils.getSiteLocales(pageManager);

            Set<Integer> existingAgencies = new HashSet<>();
            
            // Iterating over locales to import cities
            for (String locale : locales) {
                final Page agenciesRootPage = ImportersUtils.getPagePathByLocale(pageManager, rootPage, locale);

                if (agenciesRootPage == null) {
                    throw new ImporterException("Agencies root page does not exist");
                }

                LOGGER.debug("Importing agencies for locale \"{}\"", locale);

                int itemsWritten = 0, apiPage = 1;
                List<Agency> agencies;

                do {
                    agencies = agenciesApi.agenciesGet(null, null, null, null, null, apiPage, pageSize);

                    for (Agency agency : agencies) {
                        LOGGER.trace("Importing agency: {}", agency.getAgency());
                        existingAgencies.add(agency.getAgencyId());

                        if (agenciesMapping.containsKey(agency.getAgencyId())) {
                            // if agency is found, update it
                            final Page agencyPage = agenciesMapping.get(agency.getAgencyId()).get(locale);
                            LOGGER.trace("Updating agency {}", agency.getAgency());

                            if (agencyPage == null) {
                                throw new ImporterException("Cannot set agency page " + agency.getAgency());
                            }

                            TravelAgencyModel agencyModel = agencyPage.adaptTo(TravelAgencyModel.class);

                            if (isAgencyUpdated(agency, agencyModel)) {
                                final Node agencyContentNode = updateAgencyContentNode(agency, agencyPage);

                                agencyContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.debug("Agency {} is marked to be activated", agency.getAgency());
                            }
                        } else {

                            try {
                                final Page agencyPage = createAgencyPage(pageManager, agenciesRootPage, agency.getAgency(), agency.getCountryIso3());

                                LOGGER.trace("Creating agency {}", agency.getAgency());

                                // If agency is created, set the properties
                                if (agencyPage == null) {
                                    throw new ImporterException("Cannot create agency page for agency " + agency.getAgency());
                                }

                                Node agencyPageContentNode = agencyPage.getContentResource().adaptTo(Node.class);

                                if (agencyPageContentNode == null) {
                                    throw new ImporterException("Cannot set properties for agency " + agency.getAgency());
                                }

                                agencyPageContentNode.setProperty(JcrConstants.JCR_TITLE, agency.getAgency());
                                agencyPageContentNode.setProperty("apiTitle", agency.getAgency());
                                agencyPageContentNode.setProperty("agencyId", agency.getAgencyId());
                                agencyPageContentNode.setProperty("address", agency.getAddress());
                                agencyPageContentNode.setProperty("city", agency.getCity());
                                agencyPageContentNode.setProperty("zip", agency.getZip());
                                agencyPageContentNode.setProperty("zip4", agency.getZip4());
                                agencyPageContentNode.setProperty("countryIso3", agency.getCountryIso3());
                                agencyPageContentNode.setProperty("stateCode", agency.getStateCod());
                                agencyPageContentNode.setProperty("county", agency.getCounty());
                                agencyPageContentNode.setProperty("phone", agency.getPhone());
                                agencyPageContentNode.setProperty("latitude", agency.getLat());
                                agencyPageContentNode.setProperty("longitude", agency.getLon());
                                agencyPageContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
                                
                                // Set livecopy mixin
                                if (!locale.equals("en")) {
                                    agencyPageContentNode.addMixin("cq:LiveRelationship");
                                }

                                LOGGER.trace("Agency {} successfully created", agencyPage.getPath());

                                successNumber++;
                                itemsWritten++;

                                if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                    try {
                                        session.save();

                                        LOGGER.debug("{} agencies imported, saving session", +itemsWritten);
                                    } catch (RepositoryException e) {
                                        session.refresh(true);
                                    }
                                }
                            } catch (RepositoryException | ImporterException e) {
                                errorNumber++;

                                LOGGER.error("Import error", e);
                            }
                        }
                    }

                    apiPage++;
                } while (agencies.size() > 0);
            }
            // remove deleted pages
            for (Map.Entry<Integer, Map<String, Page>> agencyMapping : agenciesMapping.entrySet()) {
                if (!existingAgencies.contains(agencyMapping.getKey())) {
                    for (String locale : locales) {
                        // delete page for locale
                        Page agencyPageToDelete = agencyMapping.getValue().get(locale);
                        if (agencyPageToDelete != null) {
                            final Node contentNode = agencyPageToDelete.getContentResource().adaptTo(Node.class);
                            contentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                            LOGGER.debug("Agency {} is marked to be deactivated", agencyPageToDelete.getPath());
                        }
                    }
                }
            }
            
            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("agenciesUrl"), "lastModificationDate", true);
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} travel agencies imported, saving session");
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
            
        } catch (LoginException | ImporterException | RepositoryException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read cities from API", e);
        }

        LOGGER.debug("Ending agencies import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare
     * API data against CRX data before update
     */
    @Override
    public ImportResult updateItems() {
        // not used
        return new ImportResult(0, 0);
    }

    @Override
    public void importOneItem(final String cityId) {
        // TODO
    }

    @Override
    public JSONObject getJsonMapping() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            Iterator<Resource> agencies = resourceResolver
                    .findResources("/jcr:root/content/silversea-com" + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/travelagency\"]", "xpath");

            while (agencies.hasNext()) {
                Resource agency = agencies.next();

                Resource childContent = agency.getChild(JcrConstants.JCR_CONTENT);

                if (childContent != null) {
                    ValueMap childContentProperties = childContent.getValueMap();
                    String agencyId = childContentProperties.get("agencyId", String.class);

                    if (agencyId != null) {
                        try {
                            if (jsonObject.has(agencyId)) {
                                final JSONArray jsonArray = jsonObject.getJSONArray(agencyId);
                                jsonArray.put(agency.getPath());

                                jsonObject.put(agencyId, jsonArray);
                            } else {
                                jsonObject.put(agencyId, Collections.singletonList(agency.getPath()));
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add agency {} with path {} to agencies array", agencyId, agency.getPath(), e);
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        }

        return jsonObject;
    }

    /**
     * Create an agency page, based on path convention
     * /agencies/root/page/agency-country/agency-name
     *
     * @param pageManager
     *            pageManager used to create the pages
     * @param agenciesRootPage
     *            root page of the agencies in the content tree
     * @param agency
     *            the name of the agency
     * @param agencyCountry
     *            the country of the agency
     * @return the created agency page
     * @throws ImporterException
     *             if the page cannot be created
     */
    private Page createAgencyPage(PageManager pageManager, Page agenciesRootPage, String agency, String agencyCountry) throws ImporterException {
        try {
            // Agency parent page initialization
            final String agencyCountryName = JcrUtil.createValidName(agencyCountry);

            Page agencyCountryPage = pageManager.getPage(agenciesRootPage.getPath() + "/" + agencyCountryName);

            if (agencyCountryPage == null) {
                agencyCountryPage = pageManager.create(agenciesRootPage.getPath(), agencyCountryName, WcmConstants.PAGE_TEMPLATE_TRAVEL_AGENCY, agencyCountry, false);

                // Set livecopy mixin
                if (!LanguageHelper.getLanguage(agenciesRootPage).equals("en")) {
                    agencyCountryPage.getContentResource().adaptTo(Node.class).addMixin("cq:LiveRelationship");
                }

                LOGGER.trace("{} page does not exist, creating it", agencyCountryName);
            }

            // Creating agency page
            return pageManager.create(agencyCountryPage.getPath(), JcrUtil.createValidChildName(agencyCountryPage.adaptTo(Node.class), StringsUtils.getFormatWithoutSpecialCharacters(agency)),
                    WcmConstants.PAGE_TEMPLATE_TRAVEL_AGENCY, StringsUtils.getFormatWithoutSpecialCharacters(agency), false);
        } catch (RepositoryException | WCMException e) {
            throw new ImporterException("Agency page cannot be created", e);
        }
    }

    /**
     * Update agency properties from API
     *
     * @param agency
     *            agency object from API
     * @param agencyPage
     *            page of the agency
     * @return the content node of the agency page, updated
     * @throws ImporterException
     *             if the agency page cannot be updated
     */
    private Node updateAgencyContentNode(Agency agency, Page agencyPage) throws ImporterException {
        final Node agencyPageContentNode = agencyPage.getContentResource().adaptTo(Node.class);

        if (agencyPageContentNode == null) {
            throw new ImporterException("Cannot set properties for agency " + agency.getAgency());
        }

        try {
            agencyPageContentNode.setProperty(JcrConstants.JCR_TITLE, agency.getAgency());
            agencyPageContentNode.setProperty("apiTitle", agency.getAgency());
            agencyPageContentNode.setProperty("agencyId", agency.getAgencyId());
            agencyPageContentNode.setProperty("address", agency.getAddress());
            agencyPageContentNode.setProperty("city", agency.getCity());
            agencyPageContentNode.setProperty("zip", agency.getZip());
            agencyPageContentNode.setProperty("zip4", agency.getZip4());
            agencyPageContentNode.setProperty("countryIso3", agency.getCountryIso3());
            agencyPageContentNode.setProperty("stateCode", agency.getStateCod());
            agencyPageContentNode.setProperty("county", agency.getCounty());
            agencyPageContentNode.setProperty("phone", agency.getPhone());
            agencyPageContentNode.setProperty("latitude", agency.getLat());
            agencyPageContentNode.setProperty("longitude", agency.getLon());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(agencyPage).equals("en")) {
                agencyPageContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for agency " + agency.getAgency(), e);
        }

        return agencyPageContentNode;
    }

    private boolean isPropertyUpdated(String property1, String property2) {
        if ((StringUtils.isEmpty(property1) && !StringUtils.isEmpty(property2)) || (!StringUtils.isEmpty(property1) && StringUtils.isEmpty(property2)) || !property1.equals(property2)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPropertyUpdated(Double property1, Double property2) {
        if ((property1 == null && property2 != null) || (property1 != null && property2 == null) || property1.doubleValue() != property2.doubleValue()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isAgencyUpdated(Agency agency, TravelAgencyModel agencyModel) {
        if (!agency.getAgency().equals(agencyModel.getTitle()) || isPropertyUpdated(agency.getAddress(), agencyModel.getAddress()) || isPropertyUpdated(agency.getCity(), agencyModel.getCity())
                || isPropertyUpdated(agency.getZip(), agencyModel.getZip()) || isPropertyUpdated(agency.getZip4(), agencyModel.getZip4())
                || isPropertyUpdated(agency.getCountryIso3(), agencyModel.getCountry()) || isPropertyUpdated(agency.getStateCod(), agencyModel.getStateCode())
                || isPropertyUpdated(agency.getCounty(), agencyModel.getCounty()) || isPropertyUpdated(agency.getPhone(), agencyModel.getPhone())
                || isPropertyUpdated(agency.getLat(), agencyModel.getLatitude()) || isPropertyUpdated(agency.getLon(), agencyModel.getLongitude())) {
            return true;
        } else {
            return false;
        }
    }
}
