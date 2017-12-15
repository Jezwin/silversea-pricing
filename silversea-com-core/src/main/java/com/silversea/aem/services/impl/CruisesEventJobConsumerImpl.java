package com.silversea.aem.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.services.CruisesCacheService;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aurelienolivier
 */
@Component
@Service
@Property(name = JobConsumer.PROPERTY_TOPICS, value = CruisesEventListenerServiceImpl.JOB_TOPIC)
public class CruisesEventJobConsumerImpl implements JobConsumer {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesEventJobConsumerImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CruisesCacheService cruisesCacheService;

    @Override
    public JobResult process(final Job job) {
        final ResourceChange.ChangeType eventType = (ResourceChange.ChangeType) job.getProperty("eventType");
        final String resourcePath = (String) job.getProperty("resourcePath");
        final Boolean isCruise = (Boolean) job.getProperty("isCruise");

        LOGGER.debug("Handling job for {} {}", resourcePath, eventType);

       /* switch (eventType) {
            case ADDED:
            case CHANGED:
                final Map<String, Object> authenticationParams = new HashMap<>();
                authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

                try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams)) {
                    final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    final Resource resource = resourceResolver.getResource(resourcePath);

                    if (resource != null && pageManager != null) {
                        final Page page = pageManager.getContainingPage(resource);

                        if (page != null && page.getContentResource().isResourceType(WcmConstants.RT_CRUISE)) {

                            final CruiseModel cruiseModel = page.adaptTo(CruiseModel.class);

                            if (cruiseModel != null) {
                                cruisesCacheService.addOrUpdateCruise(new CruiseModelLight(cruiseModel), cruiseModel.getLang());
                                
                            }
                        }
                    }
                } catch (LoginException e) {
                    LOGGER.error("Cannot create resource resolver", e);

                    return JobResult.FAILED;
                }

                break;
            case REMOVED:
                if (isCruise != null && isCruise) {
                    final String cruisePath = PathUtils.getParentPath(resourcePath);
                    final String cruiseCode = cruisePath.substring(cruisePath.lastIndexOf("-") + 1);
                    String lang = null;

                    int i = 0;
                    for (final String pathElement : PathUtils.elements(cruisePath)) {
                        if (i == 2) {
                            lang = pathElement;
                            break;
                        }

                        i++;
                    }

                    cruisesCacheService.removeCruise(lang, cruiseCode);
                }

                break;
        }*/

        return JobResult.OK;
    }
}
