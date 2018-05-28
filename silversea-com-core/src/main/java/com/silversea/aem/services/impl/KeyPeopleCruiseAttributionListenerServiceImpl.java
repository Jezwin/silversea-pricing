package com.silversea.aem.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.collections.CollectionUtils;
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

	// TODO : REFACTORRRR !!!!!!!!
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
					// Make sure that we are talking about an update of a KeyPeople
					Resource resource = resourceResolver.getResource(resourceChange.getPath());
					if (resource != null) {
						Node node = resource.adaptTo(Node.class);
						if (node != null && node.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)
								&& node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) != null) {
							Value val = node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getValue();
							if (val != null && val.getString().equals(WcmConstants.RT_KEY_PEOPLE)) {
								// A key People has been modified
								Set<String> listPropsChanged = resourceChange.getChangedPropertyNames();
								Set<String> listPropsAdded = resourceChange.getAddedPropertyNames();
								Set<String> listPropsRemoved = resourceChange.getRemovedPropertyNames();

								Boolean valueToCompare = false;
								Boolean valueToDelete = false;
								Boolean valueToAdd = false;

								if (listPropsChanged != null) {
									Iterator<String> iterator = listPropsChanged.iterator();
									while (iterator.hasNext()) {
										if (iterator.next().toString().equalsIgnoreCase("cruiseAssignation")) {
											valueToCompare = true;
										}
									}
								}

								if (listPropsAdded != null) {
									Iterator<String> iterator = listPropsAdded.iterator();
									while (iterator.hasNext()) {
										if (iterator.next().toString().equalsIgnoreCase("cruiseAssignation")) {
											valueToAdd = true;
										}
									}
								}

								if (listPropsRemoved != null) {
									Iterator<String> iterator = listPropsRemoved.iterator();
									while (iterator.hasNext()) {
										if (iterator.next().toString().equalsIgnoreCase("cruiseAssignation")) {
											valueToDelete = true;
										}
									}
								}

								Resource resourceC = resourceResolver.getResource(resourceChange.getPath());
								Node nodeC = resourceC.adaptTo(Node.class);
								Value[] previousVal = null;
								Value[] currentVal = null;
								String currentPath = nodeC.getParent().getPath();

								if (nodeC.hasProperty("cruiseAssignationOld")) {
									if (nodeC.getProperty("cruiseAssignationOld").isMultiple()) {
										previousVal = nodeC.getProperty("cruiseAssignationOld").getValues();
									} else {
										Value tmpPreviousVal = nodeC.getProperty("cruiseAssignationOld").getValue();
										previousVal = new Value[1];
										previousVal[0] = tmpPreviousVal;
									}
								}
								if (nodeC.hasProperty("cruiseAssignation")) {
									if (nodeC.getProperty("cruiseAssignation").isMultiple()) {
										currentVal = nodeC.getProperty("cruiseAssignation").getValues();
									} else {
										Value tmpCurrentVal = nodeC.getProperty("cruiseAssignation").getValue();
										currentVal = new Value[1];
										currentVal[0] = tmpCurrentVal;
									}
								}

								if (valueToCompare) {
									// Go and Add / Delete the diff between old and new cruise assignation
									if (currentVal != null && previousVal != null) {
										Set<String> currentValSet = new HashSet<>();
										Set<String> previousValSet = new HashSet<>();
										for (int i = 0; i < currentVal.length; i++) {
											currentValSet.add(currentVal[i].getString());
										}
										for (int i = 0; i < previousVal.length; i++) {
											previousValSet.add(previousVal[i].getString());
										}
										Collection<String> toDelete = CollectionUtils.removeAll(previousValSet,
												currentValSet);
										Collection<String> toAdd = CollectionUtils.removeAll(currentValSet,
												previousValSet);
										Collection<String> toUpdate = CollectionUtils.intersection(currentValSet,
												previousValSet);

										for (String toDel : toDelete) {
											String[] valSplit = toDel.split(",");
											String isLeader = valSplit[0].split(":")[1];
											String cruisePath = valSplit[1].split(":")[1];
											isLeader = isLeader.replaceAll("\"", "");
											cruisePath = cruisePath.replace("\"", "");
											cruisePath = cruisePath.replace("}", "");

											Resource cruiseToModify = resourceResolver.getResource(cruisePath);
											if (cruiseToModify != null) {
												Node cruiseNode = cruiseToModify.getChild("jcr:content")
														.adaptTo(Node.class);
												if (cruiseNode.hasProperty("reference")) {
													Value[] keyPeopleList = cruiseNode.getProperty("reference")
															.getValues();
													List<String> newKeyPeopleList = new ArrayList<String>();
													for (int j = 0; j < keyPeopleList.length; j++) {
														Value keyPeople = keyPeopleList[j];
														if (!keyPeople.getString().equalsIgnoreCase(currentPath)) {
															newKeyPeopleList.add(keyPeople.getString());
														}

													}
													cruiseNode.setProperty("reference", newKeyPeopleList
															.toArray(new String[newKeyPeopleList.size()]));
													Boolean isVisible = cruiseNode.getProperty("isVisible")
															.getBoolean();
													if (isVisible) {
														cruiseNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
													}
												}
											}
										}

										for (String toAd : toAdd) {
											String[] valSplit = toAd.split(",");
											String isLeader = valSplit[0].split(":")[1];
											String cruisePath = valSplit[1].split(":")[1];
											isLeader = isLeader.replaceAll("\"", "");
											cruisePath = cruisePath.replace("\"", "");
											cruisePath = cruisePath.replace("}", "");

											Resource cruiseToModify = resourceResolver.getResource(cruisePath);
											if (cruiseToModify != null) {
												Node cruiseNode = cruiseToModify.getChild("jcr:content")
														.adaptTo(Node.class);
												Value[] keyPeopleList = null;
												List<String> newKeyPeopleList = new ArrayList<String>();
												Boolean needToAdd = true;
												if (cruiseNode.hasProperty("reference")) {
													keyPeopleList = cruiseNode.getProperty("reference").getValues();

													for (int j = 0; j < keyPeopleList.length; j++) {
														Value keyPeople = keyPeopleList[j];
														if (!keyPeople.getString().equalsIgnoreCase(currentPath)) {
															newKeyPeopleList.add(keyPeople.getString());
														} else {
															needToAdd = false;
															newKeyPeopleList.add(keyPeople.getString());
														}

													}
												}
												if (needToAdd) {
													newKeyPeopleList.add(currentPath);
												}

												cruiseNode.setProperty("reference",
														newKeyPeopleList.toArray(new String[newKeyPeopleList.size()]));
												Boolean isVisible = cruiseNode.getProperty("isVisible").getBoolean();
												if (isVisible) {
													cruiseNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
												}
											}
										}

										for (String toUp : toUpdate) {
											String[] valSplit = toUp.split(",");
											String isLeader = valSplit[0].split(":")[1];
											String cruisePath = valSplit[1].split(":")[1];
											isLeader = isLeader.replaceAll("\"", "");
											cruisePath = cruisePath.replace("\"", "");
											cruisePath = cruisePath.replace("}", "");

											// TODO implement when taking in account the isleader
										}
									}
								} else if (valueToAdd) {
									// Go and Add all cruise assignation
									if (currentVal != null) {
										for (int i = 0; i < currentVal.length; i++) {
											// Go and delete all cruise assignation
											String uniqueVal = currentVal[i].getString();
											String[] valSplit = uniqueVal.split(",");
											String isLeader = valSplit[0].split(":")[1];
											String cruisePath = valSplit[1].split(":")[1];
											isLeader = isLeader.replaceAll("\"", "");
											cruisePath = cruisePath.replace("\"", "");
											cruisePath = cruisePath.replace("}", "");

											Resource cruiseToModify = resourceResolver.getResource(cruisePath);
											if (cruiseToModify != null) {
												Node cruiseNode = cruiseToModify.getChild("jcr:content")
														.adaptTo(Node.class);
												Value[] keyPeopleList = null;
												List<String> newKeyPeopleList = new ArrayList<String>();
												Boolean needToAdd = true;
												if (cruiseNode.hasProperty("reference")) {
													keyPeopleList = cruiseNode.getProperty("reference").getValues();

													for (int j = 0; j < keyPeopleList.length; j++) {
														Value keyPeople = keyPeopleList[j];
														if (!keyPeople.getString().equalsIgnoreCase(currentPath)) {
															newKeyPeopleList.add(keyPeople.getString());
														} else {
															needToAdd = false;
															newKeyPeopleList.add(keyPeople.getString());
														}

													}
												}
												if (needToAdd) {
													newKeyPeopleList.add(currentPath);
												}

												cruiseNode.setProperty("reference",
														newKeyPeopleList.toArray(new String[newKeyPeopleList.size()]));
												Boolean isVisible = cruiseNode.getProperty("isVisible").getBoolean();
												if (isVisible) {
													cruiseNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
												}
											}
										}
									}
								} else if (valueToDelete) {
									if (previousVal != null) {
										for (int i = 0; i < previousVal.length; i++) {
											// Go and delete all cruise assignation
											String uniqueVal = previousVal[i].getString();
											String[] valSplit = uniqueVal.split(",");
											String cruisePath = valSplit[1].split(":")[1];
											cruisePath = cruisePath.replace("\"", "");
											cruisePath = cruisePath.replace("}", "");

											Resource cruiseToModify = resourceResolver.getResource(cruisePath);
											if (cruiseToModify != null) {
												Node cruiseNode = cruiseToModify.getChild("jcr:content")
														.adaptTo(Node.class);
												if (cruiseNode.hasProperty("reference")) {
													Value[] keyPeopleList = cruiseNode.getProperty("reference")
															.getValues();
													List<String> newKeyPeopleList = new ArrayList<String>();
													for (int j = 0; j < keyPeopleList.length; j++) {
														Value keyPeople = keyPeopleList[j];
														if (!keyPeople.getString().equalsIgnoreCase(currentPath)) {
															newKeyPeopleList.add(keyPeople.getString());
														}

													}
													cruiseNode.setProperty("reference", newKeyPeopleList
															.toArray(new String[newKeyPeopleList.size()]));
													Boolean isVisible = cruiseNode.getProperty("isVisible")
															.getBoolean();
													if (isVisible) {
														cruiseNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
													}
												}
											}
										}
									}
								}
								if (currentVal != null) {
									nodeC.setProperty("cruiseAssignationOld", currentVal);
								} else {
									if (nodeC.hasProperty("cruiseAssginationOld")) {
										nodeC.getProperty("cruiseAssginationOld").remove();
									}
								}
								session.save();
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
