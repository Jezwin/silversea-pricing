package com.silversea.aem.importers.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.BrochuresImporter;
import com.silversea.aem.services.ApiConfigurationService;
import com.silversea.aem.services.GeolocationTagService;
import io.swagger.client.ApiException;
import io.swagger.client.api.BrochuresApi;
import io.swagger.client.model.Brochure;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        LOGGER.debug("Starting brochures import");

        int successNumber = 0;
        int errorNumber = 0;

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);

            final BrochuresApi brochuresApi = new BrochuresApi(ImporterUtils.getApiClient(apiConfig));

            int j = 0, i = 1;
            List<Brochure> brochures;

            do {
                brochures = brochuresApi.brochuresGet(null, i, pageSize, null);

                for (Brochure brochure : brochures) {
                    LOGGER.trace("Starting import of {}", brochure.getTitle());

                    try {
                        final String query = "/jcr:root/content/dam/silversea-com/brochures/" + brochure.getLanguageCod().toLowerCase()
                                + "//element(*, dam:Asset)["
                                + "jcr:content/metadata/brochureCode=\"" + brochure.getBrochureCod() + "\"]";

                        final Iterator<Resource> resources = resourceResolver.findResources(query, "xpath");

                        if (!resources.hasNext()) {
                            LOGGER.error("Cannot find any brochure for {} ({}) in lang {}", brochure.getTitle(),
                                    brochure.getBrochureCod(), brochure.getLanguageCod().toLowerCase());

                            errorNumber++;
                        }

                        while (resources.hasNext()) {
                            final Resource resource = resources.next();

                            LOGGER.trace("Writing brochure data on {}", resource.getPath());

                            final Resource metadataResource = resource.getChild("jcr:content/metadata");

                            if (metadataResource == null) {
                                throw new ImporterException("Asset " + resource.getPath() + " has no metadata node");
                            }

                            final Node metadataNode = metadataResource.adaptTo(Node.class);

                            // Merging already set tags with geolocation ones
                            final List<String> tags = geolocationTagService.getTagIdsFromCountryCodeIso3(brochure.getCountries());
                            final Tag[] existingTags = tagManager.getTags(metadataResource);

                            for (Tag tag : existingTags) {
                                tags.add(tag.getTagID());
                            }

                            // Setting properties
                            metadataNode.setProperty("dc:title", brochure.getTitle());
                            metadataNode.setProperty("onlineBrochureUrl", brochure.getBrochureUrl());
                            metadataNode.setProperty("brochureDigitalOnly", brochure.getDigitalOnly());
                            metadataNode.setProperty("cq:tags", tags.toArray(new String[tags.size()]));

                            successNumber++;
                            j++;

                            LOGGER.trace("Brochure data written on {}", resource.getPath());

                            if (j % sessionRefresh == 0 && session.hasPendingChanges()) {
                                try {
                                    session.save();

                                    LOGGER.debug("{} brochure imported, saving session", +j);
                                } catch (RepositoryException e) {
                                    session.refresh(true);
                                }
                            }
                        }
                    } catch (RepositoryException | ImporterException e) {
                        LOGGER.error("Cannot set brochure properties", e);

                        errorNumber++;
                    }
                }

                i++;
            } while (brochures.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();

                    LOGGER.debug("{} brochure imported, saving session", +j);
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
        }

        LOGGER.debug("Ending brochures import, success: {}, error: {}", +successNumber, +errorNumber);

        return new ImportResult(successNumber, errorNumber);
    }

    @Override
    public ImportResult updateBrochures() {
        return null;
    }
}
