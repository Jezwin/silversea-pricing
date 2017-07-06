package com.silversea.aem.importers.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.s7dam.set.ImageSet;
import com.day.cq.dam.commons.util.S7SetHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

public class MigrationUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(MigrationUtils.class);

    public static void updatePagesAfterMigration(final ResourceResolverFactory resourceResolverFactory,
                                                 final String query,
                                                 final String assetsQuery,
                                                 final List<String> assetReferencesProperties) {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try {
            final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
            final Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                throw new ImporterException("Cannot initialize pageManager and session");
            }

            // Building assets mapping list
            Iterator<Resource> assets = resourceResolver.findResources(assetsQuery, "xpath");

            Map<String, String> assetsMapping = new HashMap<>();
            while (assets.hasNext()) {
                final Resource assetResource = assets.next();
                final ValueMap assetProperties = assetResource.getChild("jcr:content/metadata").getValueMap();

                if (assetProperties.get("initialPath", String.class) != null) {
                    assetsMapping.put(assetProperties.get("initialPath", String.class), assetResource.getPath());

                    LOGGER.trace("Adding {}/{} to assets mapping",
                            assetProperties.get("initialPath", String.class), assetResource.getPath());
                }
            }

            // Iterating over the cities to update properties
            final Iterator<Resource> pages = resourceResolver.findResources(query, "xpath");

            int i = 0;
            while (pages.hasNext()) {
                final Resource page = pages.next();
                final Resource childContent = page.getChild(JcrConstants.JCR_CONTENT);

                LOGGER.trace("Starting update of {}", page.getPath());

                try {
                    if (childContent == null) {
                        throw new ImporterException("Cannot get jcr:content child for " + page.getPath());
                    }

                    final ValueMap childContentProperties = childContent.getValueMap();
                    final Node childContentNode = childContent.adaptTo(Node.class);

                    String thumbnail = childContentProperties.get("thumbnail", String.class);
                    LOGGER.trace("thumbnail {}", thumbnail);

                    Map<String, String[]> assetReferences = new HashMap<>();
                    for (final String assetReferencesProperty : assetReferencesProperties) {
                        assetReferences.put(assetReferencesProperty, childContentProperties.get(assetReferencesProperty, String[].class));

                        LOGGER.trace("{} {}", assetReferencesProperty, Arrays.toString(assetReferences.get(assetReferencesProperty)));
                    }

                    if (childContentNode == null) {
                        throw new ImporterException("Cannot get node for " + page.getPath());
                    }

                    // Replacing thumbnail by image node with real path
                    if (thumbnail != null && assetsMapping.containsKey(thumbnail)) {
                        try {
                            final Node imageNode = childContentNode.addNode("image", JcrConstants.NT_UNSTRUCTURED);
                            imageNode.setProperty("fileReference", assetsMapping.get(thumbnail));

                            LOGGER.trace("Updating thumbnail of city {} by {}", page.getPath(), assetsMapping.get(thumbnail));
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot change thumbnail property on city {}", page.getPath());
                        }
                    }

                    i++;

                    // Creating image set for assetReference
                    for (final String assetReferencesProperty : assetReferences.keySet()) {
                        final String[] assetReferencesValues = assetReferences.get(assetReferencesProperty);

                        if (assetReferencesValues != null && assetReferencesValues.length > 0) {
                            try {
                                // Image set will be created in the same folder than the first asset
                                final String firstAssetInitialPath = assetReferencesValues[0];
                                final String firstAssetPath = assetsMapping.get(firstAssetInitialPath);

                                if (firstAssetPath == null) {
                                    throw new ImporterException("Cannot find asset in mapping : " + firstAssetInitialPath);
                                }

                                final String folderPath = ResourceUtil.getParent(firstAssetPath);

                                if (folderPath == null) {
                                    throw new ImporterException("Cannot find parent folder in DAM");
                                }

                                final Resource folder = resourceResolver.getResource(folderPath);

                                if (folder == null) {
                                    throw new ImporterException("Cannot get parent folder in DAM");
                                }

                                // if the image set already exists, associate it,
                                // create it and associate it if not
                                final String setName = page.getName() + "-" + assetReferencesProperty.replace("ReferenceImported", "");
                                final Resource imageSetResource = folder.getChild(setName);

                                final String propertyName = assetReferencesProperty.replace("Imported", "");

                                if (imageSetResource != null) {
                                    childContentNode.setProperty(propertyName, imageSetResource.getPath());

                                    LOGGER.trace("ImageSet {} already exists, associating it", imageSetResource.getPath());
                                } else {
                                    // Saving session due to conflicts in S7 helpers
                                    if (session.hasPendingChanges()) {
                                        session.save();
                                    }

                                    // Creating image set
                                    Map<String, Object> setProperties = new HashMap<>();
                                    final ImageSet s7ImageSet = S7SetHelper.createS7ImageSet(folder, setName, setProperties);

                                    for (final String assetInitialPath : assetReferencesValues) {
                                        final String assetPath = assetsMapping.get(assetInitialPath);

                                        if (assetPath != null) {
                                            final Resource assetResource = resourceResolver.getResource(assetPath);

                                            if (assetResource != null) {
                                                final Asset asset = assetResource.adaptTo(Asset.class);

                                                if (asset != null) {
                                                    s7ImageSet.add(asset);

                                                    final Resource setMetadata = s7ImageSet.getChild("jcr:content/metadata");
                                                    if (setMetadata != null) {
                                                        final Node setMetadataNode = setMetadata.adaptTo(Node.class);

                                                        if (setMetadataNode != null) {
                                                            setMetadataNode.setProperty("dc:title", setName);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    childContentNode.setProperty(propertyName, s7ImageSet.getPath());

                                    LOGGER.trace("ImageSet {} created and associated", s7ImageSet.getPath());
                                }
                            } catch (ImporterException e) {
                                LOGGER.error("Cannot update city {} with image set", page.getPath(), e);
                            } catch (PersistenceException e) {
                                LOGGER.error("Cannot create image set for {}", page.getPath(), e);
                            }
                        }
                    }

                    i++;

                    // Saving session
                    if (i % 100 == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} pages updated, saving session", +i);
                        } catch (RepositoryException e) {
                            try {
                                session.refresh(true);
                            } catch (RepositoryException e1) {
                                LOGGER.error("Cannot refresh session", e);
                            }
                        }
                    }
                } catch (ImporterException e) {
                    LOGGER.error("Cannot update page {}", page.getPath(), e);
                }

                LOGGER.trace("Update of {} finished", page.getPath());
            }

            // final session save
            try {
                session.save();

                LOGGER.debug("{} pages updated, saving session", +i);
            } catch (RepositoryException e) {
                session.refresh(false);
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error during pages update", e);
        }
    }
}
