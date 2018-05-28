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
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.services.ApiConfigurationService;

@Component(immediate = true)
@Service
@Properties({ @Property(name = ResourceChangeListener.PATHS, value = { "/content/silversea-com" }),
		@Property(name = ResourceChangeListener.CHANGES, value = { "ADDED", "CHANGED" }) })
public class CreateUUIDEventListenerServiceImpl implements ResourceChangeListener {

	static final private Logger LOGGER = LoggerFactory.getLogger(CreateUUIDEventListenerServiceImpl.class);

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
					//check if sscUUID triggered the event or not
					Set<String> listPropsChanged = (resourceChange.getAddedPropertyNames() != null) ?  resourceChange.getAddedPropertyNames() : resourceChange.getChangedPropertyNames();

					boolean updateUUID = false;
					if (listPropsChanged != null) {
						updateUUID = true;
						Iterator<String> iterator = listPropsChanged.iterator();
						while (iterator.hasNext() && updateUUID) {
							updateUUID = !(iterator.next().toString().equalsIgnoreCase("sscUUID"));
						}
					}
					if (updateUUID) {
						Resource resource = resourceResolver.getResource(resourceChange.getPath());
						Node node = resource.adaptTo(Node.class);
						if (node != null && node.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) && node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) != null) {
							Value val = node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getValue();
							if (val != null && (val.getString().startsWith("silversea/silversea-ssc/components/editorial/")
									|| val.getString().startsWith("silversea/silversea-com/components/editorial/"))) {
								int sscUUID = resourceChange.getPath().hashCode();
								LOGGER.debug("Updating propery sscUUID of {} with {}", resourceChange.getPath(), sscUUID);
								node.setProperty("sscUUID", sscUUID);
								LOGGER.debug("Property updated");
								session.save();
							}
						} else {
							LOGGER.error("Cannot update property");
							return;
						}
					} else {
						LOGGER.info("SSC-UUID has triggered the event");	
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
