package com.silversea.aem.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.services.ApiConfigurationService;

@Component(immediate = true)
@Service
@Properties({ @Property(name = ResourceChangeListener.PATHS, value = { "/content/silversea-com" }),
		@Property(name = ResourceChangeListener.CHANGES, value = { "ADDED", "CHANGED", "REMOVED" }) })
public class KeyPeopleCruiseAttributionListenerServiceImpl implements ResourceChangeListener {

	static final private Logger LOGGER = LoggerFactory.getLogger(KeyPeopleCruiseAttributionListenerServiceImpl.class);

	@Reference
	private ApiConfigurationService apiConfigurationService;

	@Reference
	private SlingSettingsService slingSettingsService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private JobManager jobManager;

	@Activate
	protected void activate(final ComponentContext context) {
	}

	@Override
	public void onChange(@Nonnull List<ResourceChange> changes) {
		if (slingSettingsService.getRunModes().contains("author")) {
			Map<String, Object> authenticationParams = new HashMap<>();
			authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			Session session = null;
			try (ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(authenticationParams)) {
				session = resourceResolver.adaptTo(Session.class);
				for (ResourceChange resourceChange : changes) {
					//Make sure that we are talking about an update of a KeyPeople
					Resource resource = resourceResolver.getResource(resourceChange.getPath());
					Node node = resource.adaptTo(Node.class);
					if (node != null && node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) != null) {
						Value val = node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getValue();
						 if(val != null && val.getString().equals(WcmConstants.RT_KEY_PEOPLE)){
							 //A key People has been modified 
							 Set<String> listPropsChanged = resourceChange.getChangedPropertyNames();
							 Set<String> listPropsAdded = resourceChange.getAddedPropertyNames();
							 Set<String> listPropsRemoved = resourceChange.getRemovedPropertyNames();
							 
							 
						 }
					}
				}
				session.logout();
			} catch (Exception e) {
				LOGGER.error("Cannot update resource", e);
				if (session != null) {
					session.logout();
				}
			}
		}
	}
}
