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

import org.apache.commons.lang3.StringUtils;
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
import com.silversea.aem.utils.StringsUtils;

@Component(immediate = true)
@Service
@Properties({ @Property(name = ResourceChangeListener.PATHS, value = { "/content/silversea-com/en" }),
		@Property(name = ResourceChangeListener.CHANGES, value = { "ADDED", "CHANGED" }) })
public class ReplicateThumbnailEventListenerServiceImpl implements ResourceChangeListener {

	static final private Logger LOGGER = LoggerFactory.getLogger(ReplicateThumbnailEventListenerServiceImpl.class);

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
			String[] langList = {"/es/", "/pt-br/", "/de/", "/fr/"};
			Map<String, Object> authenticationParams = new HashMap<>();
			authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			Session session = null;
			try (ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(authenticationParams)) {
				session = resourceResolver.adaptTo(Session.class);
				for (ResourceChange resourceChange : changes) {
					//check if thumbnail has been changed or not
					if(resourceChange.getPath().contains("jcr:content/image") ){ // we are talking about a thumbnail
						boolean updateThumbnail = true;

						if (updateThumbnail) {
							Resource resource = resourceResolver.getResource(resourceChange.getPath());
							Resource parent = resource.getParent();
							Node parentNode = parent.adaptTo(Node.class);
							Node node = resource.adaptTo(Node.class);
							if (node != null && parent != null) {
								Value val = parentNode.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getValue();
								if (val != null && (val.getString().contains("/pages/destination")
										|| val.getString().contains("/pages/cruise")
										|| val.getString().contains("/pages/dining")
										|| val.getString().contains("/pages/diningvariation")
										|| val.getString().contains("/pages/exclusiveoffer")
										|| val.getString().contains("/pages/page")
										|| val.getString().contains("/pages/port")
										|| val.getString().contains("/pages/publicareavariation")
										|| val.getString().contains("/pages/suite")
										|| val.getString().contains("/pages/suitevariation")
										|| val.getString().contains("/pages/ship"))) {
									String fileRef = node.getProperty("fileReference").getValue().getString();
									if(StringUtils.isNotEmpty(fileRef)){
									LOGGER.debug("Updating propery thumbnail of {} (other lang) with {}", resourceChange.getPath(), fileRef);
										String enPath = resource.getPath();
										for (String lng : langList) {
											String otherPath = enPath.replace("/en/", lng);
											Resource otherRs = resourceResolver.getResource(otherPath);
											if(otherRs != null){
												Node otherNode = otherRs.adaptTo(Node.class);
												otherNode.setProperty("fileReference", fileRef);
											}else {
												LOGGER.warn("Cannot find path {} - will try to create image node from the parent path", otherPath);
												//TODO need to create the image node starting from the parent path
												String otherPathParent = otherPath.replace("/image", "");
												Resource otherRsParent = resourceResolver.getResource(otherPathParent);
												if(otherRsParent != null){
													Node otherNodeParent = otherRsParent.adaptTo(Node.class);
													Node newOtherNode = otherNodeParent.addNode("image");
													newOtherNode.setProperty("fileReference", fileRef);
												}else{
													LOGGER.warn("Not able to create {} ", otherPath);
												}
											}
										}
										LOGGER.debug("Property updated");
										session.save();
									}
								}
							} else {
								LOGGER.error("Cannot update property");
								return;
							}
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
