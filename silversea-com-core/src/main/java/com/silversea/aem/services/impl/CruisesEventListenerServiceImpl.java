package com.silversea.aem.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.silversea.aem.services.ApiConfigurationService;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author aurelienolivier
 */
@Component(immediate = true)
@Service
@Properties({
        @Property(name = ResourceChangeListener.PATHS, value = {"/content/silversea-com"}),
        @Property(name = ResourceChangeListener.CHANGES, value = {"ADDED", "CHANGED", "REMOVED"})
})
public class CruisesEventListenerServiceImpl implements ResourceChangeListener {

    static final private Logger LOGGER = LoggerFactory.getLogger(CruisesEventListenerServiceImpl.class);

    static final public String JOB_TOPIC = "com/sling/eventing/cruises/job";

    @Reference
    private ApiConfigurationService apiConfigurationService;

    @Reference
    private JobManager jobManager;

    final private List<String> cruisesLangPath = new ArrayList<>();

    @Activate
    protected void activate(final ComponentContext context) {
        // TODO make more generic : dynamically read languages
        final String cruisesPath = apiConfigurationService.apiRootPath("cruisesUrl");
        final List<String> langs = Arrays.asList("en", "fr", "de", "es", "pt-br");

        for (final String lang : langs) {
            cruisesLangPath.add(cruisesPath.replace("/en/", "/" + lang + "/"));
        }
    }

    @Override
    public void onChange(@Nonnull List<ResourceChange> changes) {
        for (final ResourceChange resourceChange : changes) {
            for (final String cruisesPath : cruisesLangPath) {
                if (resourceChange.getPath().startsWith(cruisesPath) && resourceChange.getPath().endsWith(JcrConstants.JCR_CONTENT)) {
                    final Map<String, Object> jobInfos = new HashMap<>();

                    jobInfos.put("resourcePath", resourceChange.getPath());
                    jobInfos.put("eventType", resourceChange.getType());

                    if (resourceChange.getType().equals(ResourceChange.ChangeType.REMOVED)
                            && resourceChange.getRemovedPropertyNames() != null) {
                        jobInfos.put("isCruise", resourceChange.getRemovedPropertyNames().contains("cruiseId")
                            && resourceChange.getRemovedPropertyNames().contains("cruiseCode")
                            && resourceChange.getRemovedPropertyNames().contains("apiTitle"));
                    }

                    jobManager.addJob(JOB_TOPIC, jobInfos);

                    LOGGER.debug("Cruise event job has been started for: {}", resourceChange.getPath());

                    break;
                }
            }
        }
    }
}
