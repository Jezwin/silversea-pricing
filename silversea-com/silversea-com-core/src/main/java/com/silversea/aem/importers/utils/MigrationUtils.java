package com.silversea.aem.importers.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.s7dam.set.MediaSet;
import com.day.cq.dam.commons.util.S7SetHelper;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;

/**
 * Utils for JSON migration
 */
public class MigrationUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(MigrationUtils.class);

    public static void updatePagesAfterMigration(final ResourceResolverFactory resourceResolverFactory,
                                                 final String query,
                                                 final String assetsQuery,
                                                 final List<String> assetReferencesProperties,
                                                 final List<String> assetReferenceProperties) {
        updatePagesAfterMigration(resourceResolverFactory, query, assetsQuery, assetReferencesProperties, assetReferenceProperties, false);
    }

    /**
     * Helper method to update pages (after a JSON import)
     *
     * @param resourceResolverFactory the resource resolver factory used to create a resource resolver
     * @param query the query to get the pages to update
     * @param assetsQuery the query to find assets which be inserted in pages properties
     * @param assetReferencesProperties property names of properties containing information to create a MixedMediaSet
     *                                  with multiple assets, the property is kept in the repository, and a new one
     *                                  is created without the suffix "Imported"
     * @param assetReferenceProperties property names of properties containing information to associate an asset,
     *                                 the property is kept in the repository, and a new one is created without the
     *                                 suffix "Imported"
     */
    public static void updatePagesAfterMigration(final ResourceResolverFactory resourceResolverFactory,
                                                 final String query,
                                                 final String assetsQuery,
                                                 final List<String> assetReferencesProperties,
                                                 final List<String> assetReferenceProperties,
                                                 boolean isVariation) {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);
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

                    LOGGER.trace("Adding to assets mapping : {} - {}",
                            assetProperties.get("initialPath", String.class), assetResource.getPath());
                }
            }

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

                    if (childContentNode.hasProperty("navigationTitle")) {
                        childContentNode.setProperty("navTitle", childContentNode.getProperty("navigationTitle").getString());
                    }

                    Map<String, String[]> assetReferences = new HashMap<>();
                    if (assetReferencesProperties != null) {
                        for (final String assetReferencesProperty : assetReferencesProperties) {
                            assetReferences.put(assetReferencesProperty, childContentProperties.get(assetReferencesProperty, String[].class));

                            LOGGER.trace("{} {}", assetReferencesProperty, Arrays.toString(assetReferences.get(assetReferencesProperty)));
                        }
                    }

                    if (childContentNode == null) {
                        throw new ImporterException("Cannot get node for " + page.getPath());
                    }

                    // Replacing thumbnail by image node with real path
                    if (thumbnail != null && assetsMapping.containsKey(thumbnail)) {
                        try {
                            Node imageNode;
                            if (childContentNode.hasNode("image")) {
                                imageNode = childContentNode.getNode("image");
                            } else {
                                imageNode = childContentNode.addNode("image", JcrConstants.NT_UNSTRUCTURED);
                            }

                            imageNode.setProperty("fileReference", assetsMapping.get(thumbnail));

                            LOGGER.trace("Updating thumbnail of page {} by {}", page.getPath(), assetsMapping.get(thumbnail));

                            i++;
                        } catch (RepositoryException e) {
                            LOGGER.error("Cannot change thumbnail property on page {}", page.getPath());
                        }
                    }

                    // Replacing properties
                    if (assetReferenceProperties != null) {
                        for (final String assetReferenceProperty : assetReferenceProperties) {
                            final String assetInitialPath = childContentProperties.get(assetReferenceProperty, String.class);

                            if (assetInitialPath != null && assetsMapping.containsKey(assetInitialPath)) {
                                childContentNode.setProperty(assetReferenceProperty.replace("Imported", ""),
                                        assetsMapping.get(assetInitialPath));

                                LOGGER.trace("Setting {} with {}", assetReferenceProperty, assetsMapping.get(assetInitialPath));

                                i++;
                            }
                        }
                    }

                    // Creating image set for assetReference
                    if (assetReferencesProperties != null) {
                        for (final String assetReferencesProperty : assetReferences.keySet()) {
                            final String[] assetReferencesValues = assetReferences.get(assetReferencesProperty);

                            String damFolder = null;
                            if (isVariation) {
                                damFolder = "/content/dam/silversea-com/ships"
                                        + "/" + page.getParent().getParent().getName()
                                        + "/" + page.getParent().getName()
                                        + "/" + page.getName();

                                JcrUtil.createPath(damFolder, DamConstants.NT_SLING_ORDEREDFOLDER, DamConstants.NT_SLING_ORDEREDFOLDER, session, true);

                                LOGGER.trace("Creating DAM folder {}", damFolder);
                            }

                            createOrAssociateMediaSet(resourceResolver, session, assetsMapping, page, childContentNode,
                                    assetReferencesProperty, assetReferencesValues, damFolder);

                            i++;
                        }
                    }

                    // managing exception for locationImage stored as JSON Object
                    final String[] locationImagesImported = childContentProperties.get("locationImageImported", String[].class);

                    if (childContentNode.hasProperty("locationImage")) {
                        childContentNode.getProperty("locationImage").remove();
                        session.save();
                    }

                    if (locationImagesImported != null) {
                        LOGGER.trace("Dealing with specific property 'locationImage'");

                        List<String> imagePathsList = new ArrayList<>();

                        for (final String locationImageImported : locationImagesImported) {
                            try {
                                JSONObject jsonObject = new JSONObject(locationImageImported);

                                final String image = jsonObject.getString("image");
                                final String deckNumber = jsonObject.getString("deckNumber");

                                if (assetsMapping.containsKey(image)) {
                                    imagePathsList.add(image);

                                    final Resource assetMetadataResource = resourceResolver
                                            .getResource(assetsMapping.get(image) + "/jcr:content/metadata");

                                    if (assetMetadataResource != null) {
                                        final Node assetMetadataNode = assetMetadataResource.adaptTo(Node.class);

                                        if (assetMetadataNode != null) {
                                            assetMetadataNode.setProperty("deckNumber", deckNumber);

                                            LOGGER.trace("Writing deckNumber {} to asset {}", deckNumber, assetMetadataResource.getPath());
                                        }
                                    }

                                }
                            } catch (JSONException e) {
                                LOGGER.error("Cannot read JSON object for {}", locationImageImported, e);
                            }
                        }

                        String damFolder = null;
                        if (isVariation) {
                            damFolder = "/content/dam/silversea-com/ships"
                                    + "/" + page.getParent().getParent().getName()
                                    + "/" + page.getParent().getName()
                                    + "/" + page.getName();

                            JcrUtil.createPath(damFolder, DamConstants.NT_SLING_ORDEREDFOLDER, DamConstants.NT_SLING_ORDEREDFOLDER, session, true);

                            LOGGER.trace("Creating DAM folder {}", damFolder);
                        }

                        createOrAssociateMediaSet(resourceResolver, session, assetsMapping, page, childContentNode,
                                "locationImageImported", imagePathsList.toArray(new String[imagePathsList.size()]), damFolder);

                        i++;
                    }

                    // Saving session
                    if (i % 100 == 0 && session.hasPendingChanges()) {
                        try {
                            session.save();

                            LOGGER.debug("{} updates, saving session", +i);
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

                LOGGER.debug("{} updates, saving session", +i);
            } catch (RepositoryException e) {
                session.refresh(false);
            }
        } catch (LoginException | ImporterException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } catch (RepositoryException e) {
            LOGGER.error("Error during pages update", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    private static void createOrAssociateMediaSet(ResourceResolver resourceResolver, Session session,
                                                  Map<String, String> assetsMapping, Resource page, Node childContentNode,
                                                  String assetReferencesProperty, String[] assetReferencesValues,
                                                  final String damFolder) {
        if (assetReferencesValues != null && assetReferencesValues.length > 0) {
            try {
                // Image set will be created in the same folder than the first asset
                final String firstAssetInitialPath = assetReferencesValues[0];
                final String firstAssetPath = assetsMapping.get(firstAssetInitialPath);

                if (firstAssetPath == null) {
                    throw new ImporterException("Cannot find asset in mapping : " + firstAssetInitialPath);
                }

                final String folderPath = damFolder != null ? damFolder : ResourceUtil.getParent(firstAssetPath);

                if (folderPath == null) {
                    throw new ImporterException("Cannot find parent folder in DAM");
                }

                final Resource folder = resourceResolver.getResource(folderPath);

                if (folder == null) {
                    throw new ImporterException("Cannot get parent folder in DAM");
                }

                // if the image set already exists, associate it,
                // create it and associate it if not
                final String setName = page.getName() + "-" + assetReferencesProperty.replace("Imported", "")
                        .replace("Reference", "");
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
                    final MediaSet s7MediaSet = S7SetHelper.createS7MixedMediaSet(folder, setName, setProperties);

                    for (final String assetInitialPath : assetReferencesValues) {
                        final String assetPath = assetsMapping.get(assetInitialPath);

                        if (assetPath != null) {
                            final Resource assetResource = resourceResolver.getResource(assetPath);

                            if (assetResource != null) {
                                final Asset asset = assetResource.adaptTo(Asset.class);

                                if (asset != null && !s7MediaSet.contains(asset)) {
                                    s7MediaSet.add(asset);

                                    final Resource setMetadata = s7MediaSet.getChild("jcr:content/metadata");
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

                    childContentNode.setProperty(propertyName, s7MediaSet.getPath());

                    LOGGER.trace("ImageSet {} created and associated", s7MediaSet.getPath());
                }
            } catch (ImporterException e) {
                LOGGER.error("Cannot update page {} with image set", page.getPath(), e);
            } catch (PersistenceException e) {
                LOGGER.error("Cannot create image set for {}", page.getPath(), e);
            } catch (RepositoryException e) {
                LOGGER.error("Cannot write data to repository for {}", page.getPath(), e);
            }
        }
    }
}
