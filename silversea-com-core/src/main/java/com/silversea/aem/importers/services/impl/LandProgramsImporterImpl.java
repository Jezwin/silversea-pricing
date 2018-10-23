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
import com.silversea.aem.importers.services.LandProgramsImporter;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.models.LandProgramModel;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.utils.StringsUtils;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.LandsApi;
import io.swagger.client.api.ShorexesApi;
import io.swagger.client.model.Land;
import io.swagger.client.model.Land77;
import io.swagger.client.model.Shorex;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Component
public class LandProgramsImporterImpl implements LandProgramsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(LandProgramsImporterImpl.class);

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
    public ImportResult importAllLandPrograms() {
        LOGGER.debug("Starting land programs import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            List<Land> landPrograms;
            int apiPage = 1, itemsWritten = 0;

            LOGGER.debug("Importing land programs");

            do {
                landPrograms = landsApi.landsGet(null, apiPage, pageSize, null);

                for (Land landProgram : landPrograms) {
                    LOGGER.trace("Importing land program: {}", landProgram.getLandName());

                    try {
                        // Getting cities with the city id read from the land
                        // program
                        Integer cityId = landProgram.getCities().size() > 0 ? landProgram.getCities().get(0).getCityId()
                                : null;

                        if (cityId == null) {
                            throw new ImporterException("Land program have no city");
                        }

                        // TODO create cache of cityId / Resource in order to speed up the process
                        Iterator<Resource> portsResources = resourceResolver
                                .findResources("/jcr:root/content/silversea-com"
                                        + "//element(*,cq:Page)[jcr:content/cityId=\"" + cityId + "\"]", "xpath");

                        if (!portsResources.hasNext()) {
                            throw new ImporterException("Cannot find city with id " + cityId);
                        }

                        while (portsResources.hasNext()) {
                            // Getting port page
                            Page portPage = portsResources.next().adaptTo(Page.class);

                            if (portPage == null) {
                                throw new ImporterException("Error getting port page " + cityId);
                            }

                            LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                            // Creating subpage "land-programs" if not present
                            Page landProgramsPage;
                            if (portPage.hasChild(WcmConstants.NN_LAND_PROGRAMS)) {
                                landProgramsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_LAND_PROGRAMS);
                            } else {
                                landProgramsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_LAND_PROGRAMS,
                                        WcmConstants.PAGE_TEMPLATE_PAGE, "Land Programs", false);

                                LOGGER.trace("{} page is not existing, creating it", landProgramsPage.getPath());
                            }

                            final Page landProgramPage = pageManager.create(landProgramsPage.getPath(),
                                    JcrUtil.createValidChildName(landProgramsPage.adaptTo(Node.class),
                                            StringsUtils.getFormatWithoutSpecialCharacters(landProgram.getLandName())),
                                    WcmConstants.PAGE_TEMPLATE_LAND_PROGRAM,
                                    StringsUtils.getFormatWithoutSpecialCharacters(landProgram.getLandName()), false);

                            LOGGER.trace("Creating land program {} in city {}", landProgram.getLandName(),
                                    portPage.getPath());

                            // If land program is created, set the properties
                            if (landProgramPage == null) {
                                throw new ImporterException(
                                        "Cannot create land program page for landprogram " + landProgram.getLandName());
                            }

                            Node landProgramPageContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

                            if (landProgramPageContentNode == null) {
                                throw new ImporterException(
                                        "Cannot set properties for land program " + landProgram.getLandName());
                            }

                            landProgramPageContentNode.setProperty(JcrConstants.JCR_TITLE, landProgram.getLandName());
                            landProgramPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                                    landProgram.getDescription());
                            landProgramPageContentNode.setProperty("landId", landProgram.getLandId());
                            landProgramPageContentNode.setProperty("landCode", landProgram.getLandCod());

                            // Set livecopy mixin
                            if (!LanguageHelper.getLanguage(portPage).equals("en")) {
                                landProgramPageContentNode.addMixin("cq:LiveRelationship");
                            }

                            LOGGER.trace("Land program {} successfully created", landProgramPage.getPath());

                            successNumber++;
                            itemsWritten++;
                        }

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (WCMException | RepositoryException | ImporterException e) {
                        errorNumber++;

                        LOGGER.error("Import error", e);
                    }
                }

                apiPage++;
            } while (landPrograms.size() > 0);

            ImportersUtils.setLastModificationDate(pageManager, session, apiConfig.apiRootPath("citiesUrl"),
                    "lastModificationDateLandPrograms");
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Cannot save modification", e);
        }

        LOGGER.debug("Ending land programs import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    /**
     * TODO it seems a lot of elements are marked as modified on API, compare API data against CRX data before update
     */
    @Override
    public ImportResult updateLandPrograms() {
        LOGGER.debug("Starting land programs update");

        int successNumber = 0, errorNumber = 0, apiPage = 1;

        final Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

            if (pageManager == null || session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            final Page rootPage = pageManager.getPage(apiConfig.apiRootPath("citiesUrl"));
            final String lastModificationDate = ImportersUtils.getDateFromPageProperties(rootPage, "lastModificationDateLandPrograms");

            LOGGER.debug("Last import date for land programs {}", lastModificationDate);

            // cities mapping
            final Map<Integer, Map<String, Page>> portsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/port\"]", "cityId");

            // land programs mapping
            final Map<Integer, Map<String, Page>> landProgramsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"]", "landId");

            int itemsWritten = 0;
            List<Land77> landPrograms;

            do {
                final ApiResponse<List<Land77>> apiResponse = landsApi.landsGetChangesWithHttpInfo(lastModificationDate, null,
                        apiPage , pageSize, null);
                landPrograms = apiResponse.getData();

                // TODO replace by header
                LOGGER.trace("Total landPrograms : {}, page : {}, landPrograms for this page : {}", landPrograms.size(), apiPage, landPrograms.size());

                for (Land77 landProgram : landPrograms) {
                    final String landProgramName = landProgram.getLandName();

                    LOGGER.debug("Updating landProgram: {}", landProgramName);

                    try {
                        if (landProgramsMapping.containsKey(landProgram.getLandId())) {
                            // if landPrograms are found, update it
                            for (Map.Entry<String, Page> landProgramsPages : landProgramsMapping.get(landProgram.getLandId()).entrySet()) {
                                final Page landProgramPage = landProgramsPages.getValue();

                                LOGGER.trace("Updating landProgram {}", landProgramName);

                                if (landProgramPage == null) {
                                    throw new ImporterException("Cannot set landProgram page " + landProgramName);
                                }

                                // depending of the city status, mark the page to be activated or deactivated
                                if (BooleanUtils.isTrue(landProgram.getIsDeleted())) {
                                    final Node landProgramContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

                                    if (landProgramContentNode == null) {
                                        throw new ImporterException("Cannot set properties for landProgram " + landProgramName);
                                    }

                                    landProgramContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                                    landProgramContentNode.setProperty("image", landProgram.getImageUrl());
                                    landProgramContentNode.setProperty("image2", landProgram.getImageUrl2());
                                    landProgramContentNode.setProperty("image3", landProgram.getImageUrl3());
                                    landProgramContentNode.setProperty("image4", landProgram.getImageUrl4());
                                    landProgramContentNode.setProperty("image5", landProgram.getImageUrl5());
                                    landProgramContentNode.setProperty("image6", landProgram.getImageUrl6());

                                    LOGGER.trace("Land program {} is marked to be deactivated", landProgramName);
                                } else {
                                    final Node landProgramContentNode = updateLandProgramContentNode(landProgram, landProgramPage);
                                    landProgramContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    LOGGER.trace("Land program {} is marked to be activated", landProgramName);
                                }
                            }
                        } else {
                            // Getting cities with the city id read from the landProgram
                            Integer cityId = landProgram.getCities().size() > 0 ? landProgram.getCities().get(0).getCityId() : null;

                            if (cityId == null) {
                                throw new ImporterException("Land program have no city");
                            }

                            if (!portsMapping.containsKey(cityId)) {
                                throw new ImporterException("Cannot find city with id " + cityId);
                            }

                            for (Map.Entry<String, Page> portsPage : portsMapping.get(cityId).entrySet()) {
                                // Getting port page
                                Page portPage = portsPage.getValue();

                                if (portPage == null) {
                                    throw new ImporterException("Error getting port page " + cityId);
                                }

                                LOGGER.trace("Found port {} with id {}", portPage.getTitle(), cityId);

                                // Creating subpage "landProgram" if not present
                                Page landProgramsPage;
                                if (portPage.hasChild(WcmConstants.NN_LAND_PROGRAMS)) {
                                    landProgramsPage = pageManager.getPage(portPage.getPath() + "/" + WcmConstants.NN_LAND_PROGRAMS);
                                } else {
                                    landProgramsPage = pageManager.create(portPage.getPath(), WcmConstants.NN_LAND_PROGRAMS,
                                            WcmConstants.PAGE_TEMPLATE_PAGE, "Land programs", false);

                                    LOGGER.trace("{} page is not existing, creating it", landProgramsPage.getPath());
                                }

                                // Creating landProgram page
                                final Page landProgramPage = pageManager.create(landProgramsPage.getPath(),
                                        JcrUtil.createValidChildName(landProgramsPage.adaptTo(Node.class),
                                                StringsUtils.getFormatWithoutSpecialCharacters(landProgramName)),
                                        WcmConstants.PAGE_TEMPLATE_LAND_PROGRAM,
                                        StringsUtils.getFormatWithoutSpecialCharacters(landProgramName), false);

                                LOGGER.trace("Creating landProgram {} in city {}", landProgramName, portPage.getPath());

                                // If landProgram is created, set the properties
                                if (landProgramPage == null) {
                                    throw new ImporterException(
                                            "Cannot create landProgram page for landProgram " + landProgramName);
                                }

                                final Node landProgramContentNode = updateLandProgramContentNode(landProgram, landProgramPage);
                                landProgramContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                LOGGER.trace("Land program {} successfully created", landProgramPage.getPath());
                            }
                        }

                        successNumber++;
                        itemsWritten++;

                        if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                            try {
                                session.save();

                                LOGGER.debug("{} landPrograms imported, saving session", +itemsWritten);
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    } catch (RepositoryException | ImporterException | WCMException e) {
                        errorNumber++;

                        LOGGER.warn("Import error {}", e.getMessage());
                    }
                }

                apiPage++;
            } while (landPrograms.size() > 0);

            ImportersUtils.setLastModificationDate(session, apiConfig.apiRootPath("citiesUrl"), "lastModificationDateLandPrograms", true);
            
            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} lands programs imported, saving session");
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read land programs from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error writing data", e);
        }

        LOGGER.debug("Ending land programs update, success: {}, error: {}, api calls: {}", +successNumber, +errorNumber, apiPage);

        return new ImportResult(successNumber, errorNumber);
    }
    
	public ImportResult disactiveAllItemDeltaByAPI() {
		LOGGER.debug("Starting land programs disactive delta API");

		int successNumber = 0, errorNumber = 0;

		Map<String, Object> authenticationParams = new HashMap<>();
		authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

		try (ResourceResolver resourceResolver = resourceResolverFactory
				.getServiceResourceResolver(authenticationParams)) {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Session session = resourceResolver.adaptTo(Session.class);

			LandsApi landsApi = new LandsApi(ImportersUtils.getApiClient(apiConfig));

			if (pageManager == null || session == null) {
				throw new ImporterException("Cannot initialize pageManager and session");
			}

	        Map<Integer, Map<String, Page>> landProgramsMapping = ImportersUtils.getItemsPageMapping(resourceResolver,
                    "/jcr:root/content/silversea-com//element(*,cq:PageContent)" +
                            "[sling:resourceType=\"silversea/silversea-com/components/pages/landprogram\"]", "landId");

			int itemsWritten = 0, page = 1, perPage = 1000;
			Land landItem = null;
			List<Land> landAPI = landsApi.landsGet(null, page, perPage, null);
			List<Land> landListAPI = new ArrayList<>();
			
			while(landAPI.size() != 0){
				landListAPI.addAll(landAPI);
				page++;
				landAPI = landsApi.landsGet(null, page, perPage, null);
			}
						
			LOGGER.debug("Check all land programs in jcr: {}", landProgramsMapping.size());
			
			for (Map.Entry<Integer, Map<String, Page>> land : landProgramsMapping.entrySet()) {
				landItem = null;
				Integer landID = land.getKey();

				for (Land lAPI : landListAPI) {
					
					if (landID.intValue() == lAPI.getLandId().intValue()) {
						landItem = lAPI;
					}
				}
				if (landItem == null) {
					LOGGER.debug("Check landId: {}", landID);
					for (Map.Entry<String, Page> landPages : landProgramsMapping
							.get(landID).entrySet()) {
						Page landPage = landPages.getValue();

						LOGGER.debug("Updating landing {}", landID);

						if (landPage == null) {
							throw new ImporterException(
									"Cannot set land page " + landID);
						}

						Node landProgramContentNode = landPage.getContentResource().adaptTo(Node.class);

						if (landProgramContentNode == null) {
							throw new ImporterException(
									"Cannot set properties for land " + landPages);
						}

						landProgramContentNode.setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);

						LOGGER.trace("Land program {} is marked to be deactivated", landID);
					}
					successNumber++;
					itemsWritten++;

				} 
				try {
					if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
						try {
							session.save();

							LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
						} catch (RepositoryException e) {
							session.refresh(true);
						}
					}
				} catch (RepositoryException e) {
					errorNumber++;

					LOGGER.warn("Import error {}", e.getMessage());
				}
			}


			if (session.hasPendingChanges()) {
				try {
					session.save();

					LOGGER.debug("{} land programs imported, saving session", +itemsWritten);
				} catch (RepositoryException e) {
					session.refresh(false);
				}
			}
		} catch (LoginException | ImporterException e) {
			LOGGER.error("Cannot create resource resolver", e);
		} catch (ApiException e) {
			LOGGER.error("Cannot read excursions from API", e);
		} catch (RepositoryException e) {
			LOGGER.error("Error writing data", e);
		}

		LOGGER.debug("Ending land programs disactive update, success: {}, error: {}", +successNumber, +errorNumber);

		return new ImportResult(successNumber, errorNumber);
	}


    @Override
    public void importOneLandProgram(String landProgramId) {
        // TODO implement
    }

    /**
     * Update land program properties from API
     *
     * @param landProgram landProgram object from API
     * @param landProgramPage page of the landProgram
     * @return the content node of the landProgram page, updated
     * @throws ImporterException if the landProgram page cannot be updated
     */
    private Node updateLandProgramContentNode(Land77 landProgram, Page landProgramPage) throws ImporterException {
        final Node landProgramPageContentNode = landProgramPage.getContentResource().adaptTo(Node.class);

        if (landProgramPageContentNode == null) {
            throw new ImporterException("Cannot set properties for landProgram " + landProgram.getLandName());
        }

        try {
            landProgramPageContentNode.setProperty(JcrConstants.JCR_TITLE, landProgram.getLandName());
            landProgramPageContentNode.setProperty(JcrConstants.JCR_DESCRIPTION,
                    landProgram.getDescription());
            landProgramPageContentNode.setProperty("landId", landProgram.getLandId());
            landProgramPageContentNode.setProperty("landCode", landProgram.getLandCod());

            // Set livecopy mixin
            if (!LanguageHelper.getLanguage(landProgramPage).equals("en")) {
                landProgramPageContentNode.addMixin("cq:LiveRelationship");
            }
        } catch (RepositoryException e) {
            throw new ImporterException("Cannot set properties for landProgram " + landProgram.getLandName(), e);
        }

        return landProgramPageContentNode;
    }
}
