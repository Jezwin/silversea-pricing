package com.silversea.aem.importers.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.BrochuresImporter;
import com.silversea.aem.importers.servlets.UpdateImportServlet;
import com.silversea.aem.importers.utils.ImportersUtils;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.GeolocationTagService;

import io.swagger.client.ApiException;
import io.swagger.client.api.BrochuresApi;
import io.swagger.client.model.Brochure;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.*;

import static com.silversea.aem.logging.JsonLog.jsonLog;

@Service
@Component
public class BrochuresImporterImpl implements BrochuresImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrochuresImporterImpl.class);

    private int sessionRefresh = 100;
    private int pageSize = 100;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ApiConfigurationService apiConfig;

    @Reference
    private GeolocationTagService geolocationTagService;

    @Reference
    private LogzLoggerFactory sscLogFactory;

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
    public ImportResult importAllBrochures() {
        return importOrUpdateBrochures("import");
    }

    @Override
    public ImportResult updateBrochures() {
        return importOrUpdateBrochures("update");
    }

    private ImportResult importOrUpdateBrochures(final String mode) {
        SSCLogger logger = sscLogFactory.getLogger(BrochuresImporterImpl.class);
        LOGGER.debug("Starting brochures import");

        final ImportResult importResult = new ImportResult();

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final BrochuresApi brochuresApi = new BrochuresApi(ImportersUtils.getApiClient(apiConfig));

            int itemsWritten = 0, apiPage = 1;
            List<Brochure> brochures;
            List<String> brochureCodes = new ArrayList<>();

            do {
                brochures = brochuresApi.brochuresGet(null, apiPage, pageSize, null);

                for (Brochure brochure : brochures) {
                    LOGGER.trace("Starting import of {} ({})", brochure.getTitle(), brochure.getBrochureCod());

                    brochureCodes.add(brochure.getBrochureCod());

                    try {
                        final String query = "/jcr:root/content/dam/silversea-com/brochures/" + brochure.getLanguageCod().toLowerCase()
                                + "//element(*, dam:Asset)["
                                + "jcr:content/metadata/brochureCode=\"" + brochure.getBrochureCod() + "\"]";

                        final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

                        if (!resources.hasNext()) {
                            LOGGER.error("Cannot find any brochure for {} ({}) in lang {}", brochure.getTitle(),
                                    brochure.getBrochureCod(), brochure.getLanguageCod().toLowerCase());
                            logger.logError(getLogMessage("Cannot find brochure", brochure));
                            importResult.incrementErrorNumber();
                        }

                        while (resources.hasNext()) {
                            final Resource resource = resources.next();

                            LOGGER.trace("Found brochure at {}", resource.getPath());

                            final Resource metadataResource = resource.getChild("jcr:content/metadata");

                            if (metadataResource == null) {
                                throw new ImporterException("Asset " + resource.getPath() + " has no metadata node");
                            }

                            final Node metadataNode = metadataResource.adaptTo(Node.class);

                            // Merging already set tags with geolocation ones
                            final List<String> tags = geolocationTagService.getTagIdsFromCountryCodeIso3(brochure.getCountries());

                            if (mode.equals("import")) {
                                LOGGER.trace("Starting import of brochure ({}) data at {}", brochure.getBrochureCod(), resource.getPath());

                                final Tag[] existingTags = tagManager.getTags(metadataResource);

                                for (Tag tag : existingTags) {
                                    tags.add(tag.getTagID());
                                }

                                // Setting properties
                                metadataNode.setProperty("dc:title", brochure.getTitle());
                                metadataNode.setProperty("onlineBrochureUrl", brochure.getBrochureUrl());
                                metadataNode.setProperty("brochureDigitalOnly", brochure.getDigitalOnly());
                                metadataNode.setProperty("cq:tags", tags.toArray(new String[tags.size()]));

                                logger.logInfo(getLogMessage("Successfully imported brochure", brochure));
                                importResult.incrementSuccessNumber();
                                itemsWritten++;

                                LOGGER.trace("Brochure data written on {}", resource.getPath());
                            } else if (mode.equals("update")) {
                                LOGGER.trace("Starting update of brochure ({}) data at {}", brochure.getBrochureCod(), resource.getPath());

                                final ValueMap metadataProperties = metadataResource.getValueMap();

                                final String title = metadataProperties.get("dc:title", String.class);
                                final String onlineBrochureUrl = metadataProperties.get("onlineBrochureUrl", String.class);
                                final Boolean brochureDigitalOnly = metadataProperties.get("brochureDigitalOnly", false);

                                final Tag[] existingTags = tagManager.getTags(metadataResource);

                                // Building tag list id for comparison
                                final List<String> existingGeolocationTagsList = new ArrayList<>();
                                for (Tag tag : existingTags) {
                                    if (tag.getTagID().startsWith(WcmConstants.GEOLOCATION_TAGS_PREFIX)) {
                                        existingGeolocationTagsList.add(tag.getTagID());
                                    }
                                }

                                if (!Objects.equals(title, brochure.getTitle())
                                        || !Objects.equals(onlineBrochureUrl, brochure.getBrochureUrl())
                                        || !Objects.equals(brochureDigitalOnly, brochure.getDigitalOnly())
                                        || !compareLists(tags, existingGeolocationTagsList)) {
                                    for (Tag tag : existingTags) {
                                        if (!tag.getTagID().startsWith(WcmConstants.GEOLOCATION_TAGS_PREFIX)) {
                                            tags.add(tag.getTagID());
                                        }
                                    }

                                    // Setting properties
                                    
    	                                if(title == null){
    	                                	metadataNode.getProperty("dc:title").remove();
    	                                	metadataNode.setProperty("dc:title", brochure.getTitle());
    	                                }else {
    	                                	if(title.equals("")){
    	                                		metadataNode.getProperty("dc:title").remove();
    	                                		metadataNode.setProperty("dc:title", brochure.getTitle());
    	                                	}
    	                                }
                                    
                                    metadataNode.setProperty("onlineBrochureUrl", brochure.getBrochureUrl());
                                    metadataNode.setProperty("brochureDigitalOnly", brochure.getDigitalOnly());
                                    metadataNode.setProperty("cq:tags", tags.toArray(new String[tags.size()]));

                                    metadataNode.getParent().setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

                                    logger.logInfo(getLogMessage("Successfully imported brochure", brochure));
                                    importResult.incrementSuccessNumber();
                                    itemsWritten++;

                                    LOGGER.trace("Brochure data written on {}", resource.getPath());
                                } else {
                                    LOGGER.trace("Brochure data have not change for {}", resource.getPath());
                                }
                            }

                            if (itemsWritten % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} brochure imported, saving session", +itemsWritten);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.error("Cannot set brochure properties", e);

                        logger.logError(getLogMessage("Cannot set brochure properties", brochure));

                        importResult.incrementErrorNumber();
                    }
                }

                apiPage++;
            } while (brochures.size() > 0);

            // Mark as "to deactivate" all brochures not present on API side
            if (mode.equals("update")) {
                LOGGER.debug("Starting mark as deactivate brochures");

                final String query = "/jcr:root/content/dam/silversea-com/brochures" +
                        "//element(*, dam:Asset)[jcr:content/metadata/brochureCode]";

                final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

                while (resources.hasNext()) {
                    final Resource resource = resources.next();
                    final Resource metadataResource = resource.getChild("jcr:content/metadata");

                    if (metadataResource != null) {
                        final String brochureCode = metadataResource.getValueMap().get("brochureCode", String.class);

                        if (brochureCode != null && !brochureCodes.contains(brochureCode)) {
                            LOGGER.debug("Brochure at path {} ({}) is not existing anymore, deactivating it",
                                    resource.getPath(), brochureCode);

                            final Node metadataNode = metadataResource.adaptTo(Node.class);
                            metadataNode.getParent().setProperty(ImportersConstants.PN_TO_DEACTIVATE, true);
                        }
                    }
                }
            }

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} brochure imported, saving session", +itemsWritten);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (ApiException e) {
            LOGGER.error("Cannot read brochures from API", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error saving data", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        LOGGER.debug("Ending brochures import, success: {}, error: {}", +importResult.getSuccessNumber(), +importResult.getErrorNumber());

        return importResult;
    }

    private JsonLog getLogMessage(String message, Brochure brochure) {
        return jsonLog("BrochureImport")
                .with("title", brochure.getTitle())
                .with("message", message)
                .with("brochureCode", brochure.getBrochureCod())
                .with("brochureLanguage", brochure.getLanguageCod().toLowerCase())
                .with("brochureId", brochure.getBrochureId())
                .with("brochureUrl", brochure.getBrochureUrl());
    }

    private static boolean compareLists(List<String> l1, List<String> l2) {
        List<String> cp = new ArrayList<>(l1);

        for (String o : l2) {
            if (!cp.remove(o)) {
                return false;
            }
        }

        return cp.isEmpty();
    }
}
