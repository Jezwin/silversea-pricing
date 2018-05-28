package com.silversea.aem.importers.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.impl.ImportResult;
import io.swagger.client.model.Price;
import io.swagger.client.model.VoyagePriceMarket;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.jackrabbit.vault.util.SHA1;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * TODO javadoc
 *
 * @author aurelienolivier
 */
public class CruisesImportUtils {

	static final private Logger LOGGER = LoggerFactory.getLogger(CruisesImportUtils.class);

	static public ImportResult importCruisePrice(final Session session, final Node cruiseContentNode,
			final Map.Entry<String, String> cruise, final Map<String, Map<String, Resource>> suitesMapping,
			final VoyagePriceMarket priceMarket, final Node suitesNode, int itemsWritten, int sessionRefresh)
			throws RepositoryException {
		final ImportResult importResult = new ImportResult();

		// Iterating over prices variation
		for (final Price cruiseOnlyPrice : priceMarket.getCruiseOnlyPrices()) {
			try {
				if (cruiseContentNode.getProperty("shipReference") == null) {
					throw new ImporterException("Cruise " + cruise.getKey() + " do not contains a" + " ship reference");
				}

				final String suiteCatId = PathUtils.getName(cruiseContentNode.getProperty("shipReference").getString())
						+ "-" + cruiseOnlyPrice.getSuiteCategoryCod();

				if (!suitesMapping.containsKey(suiteCatId)) {
					throw new ImporterException("Cannot get suite with category " + suiteCatId);
				}

				// Getting suite corresponding to suite category
				final Map<String, Resource> suites = suitesMapping.get(suiteCatId);
				final String suiteName = suites.get(cruise.getKey()).getName();

				final Node suiteNode = JcrUtils.getOrAddNode(suitesNode, suiteName);

				final String priceVariationNodeName = cruiseOnlyPrice.getSuiteCategoryCod() + priceMarket.getMarketCod()
						+ cruiseOnlyPrice.getCurrencyCod();

				final Node priceVariationNode = suiteNode
						.addNode(JcrUtil.createValidChildName(suiteNode, priceVariationNodeName));

				priceVariationNode.setProperty("suiteCategory", cruiseOnlyPrice.getSuiteCategoryCod());
				priceVariationNode.setProperty("price", cruiseOnlyPrice.getCruiseOnlyFare());
				if (cruiseOnlyPrice.getEarlyBookingBonus() != null) {
					priceVariationNode.setProperty("earlyBookingBonus", cruiseOnlyPrice.getEarlyBookingBonus());
				}
				priceVariationNode.setProperty("currency", cruiseOnlyPrice.getCurrencyCod());
				priceVariationNode.setProperty("availability", cruiseOnlyPrice.getSuiteAvailability());
				priceVariationNode.setProperty("cq:tags",
						new String[] { "geotagging:" + priceMarket.getMarketCod().toLowerCase() });

				// Writing suite reference based on lang
				priceVariationNode.setProperty("suiteReference", suites.get(cruise.getKey()).getPath());
				priceVariationNode.setProperty("sling:resourceType",
						"silversea/silversea-com/components/subpages/prices/pricevariation");

				cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

				importResult.incrementSuccessNumber();
			} catch (ImporterException | RepositoryException e) {
				LOGGER.warn("Cannot import price for category, {}", e.getMessage());

				importResult.incrementErrorNumber();
			}
		}

		return importResult;
	}

	static public ImportResult importComboCruisePrice(final Session session, final Node cruiseContentNode,
			final Map.Entry<String, String> cruise, final Map<String, Map<String, Resource>> suitesMapping,
			final VoyagePriceMarket priceMarket, final Node suitesNode, int itemsWritten, int sessionRefresh)
			throws RepositoryException {
		final ImportResult importResult = new ImportResult();

		// Iterating over prices variation
		for (final Price cruiseOnlyPrice : priceMarket.getCruiseOnlyPrices()) {
			try {
				if (cruiseContentNode.getProperty("shipReference") == null) {
					throw new ImporterException("Cruise " + cruise.getKey() + " do not contains a" + " ship reference");
				}

				final String suiteCatId = PathUtils.getName(cruiseContentNode.getProperty("shipReference").getString())
						+ "-" + cruiseOnlyPrice.getSuiteCategoryCod();

				if (!suitesMapping.containsKey(suiteCatId)) {
					throw new ImporterException("Cannot get suite with category " + suiteCatId);
				}

				// Getting suite corresponding to suite category
				final Map<String, Resource> suites = suitesMapping.get(suiteCatId);
				final String suiteName = suites.get(cruise.getKey()).getName();

				final Node suiteNode = JcrUtils.getOrAddNode(suitesNode, suiteName);

				final String priceVariationNodeName = cruiseOnlyPrice.getSuiteCategoryCod() + priceMarket.getMarketCod()
						+ cruiseOnlyPrice.getCurrencyCod();

				final Node priceVariationNode = suiteNode
						.addNode(JcrUtil.createValidChildName(suiteNode, priceVariationNodeName));

				priceVariationNode.setProperty("suiteCategory", cruiseOnlyPrice.getSuiteCategoryCod());
				priceVariationNode.setProperty("price", cruiseOnlyPrice.getCruiseOnlyFare());
				if (cruiseOnlyPrice.getEarlyBookingBonus() != null) {
					priceVariationNode.setProperty("earlyBookingBonus", cruiseOnlyPrice.getEarlyBookingBonus());
				}
				priceVariationNode.setProperty("currency", cruiseOnlyPrice.getCurrencyCod());
				priceVariationNode.setProperty("availability", cruiseOnlyPrice.getSuiteAvailability());
				priceVariationNode.setProperty("cq:tags",
						new String[] { "geotagging:" + priceMarket.getMarketCod().toLowerCase() });

				// Writing suite reference based on lang
				priceVariationNode.setProperty("suiteReference", suites.get(cruise.getKey()).getPath());
				priceVariationNode.setProperty("sling:resourceType",
						"silversea/silversea-com/components/subpages/prices/pricevariation");

				//cruiseContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);

				importResult.incrementSuccessNumber();
			} catch (ImporterException | RepositoryException e) {
				LOGGER.warn("Cannot import price for category, {}", e.getMessage());

				importResult.incrementErrorNumber();
			}
		}

		return importResult;
	}

	public static Map<String, List<String>> getDestinationsMapping(ResourceResolver resourceResolver) {
		final Iterator<Resource> destinations = resourceResolver.findResources("/jcr:root/content/silversea-com"
				+ "//element(*,cq:PageContent)[sling:resourceType=\"silversea/silversea-com/components/pages/destination\"]",
				"xpath");

		final Map<String, List<String>> destinationsMapping = new HashMap<>();
		while (destinations.hasNext()) {
			final Resource destination = destinations.next();

			final String destinationId = destination.getValueMap().get("destinationId", String.class);

			if (destinationId != null) {
				if (destinationsMapping.containsKey(destinationId)) {
					destinationsMapping.get(destinationId).add(destination.getParent().getPath());
				} else {
					final List<String> destinationPaths = new ArrayList<>();
					destinationPaths.add(destination.getParent().getPath());
					destinationsMapping.put(destinationId, destinationPaths);
				}

				LOGGER.trace("Adding destination {} ({}) to cache", destination.getPath(), destinationId);
			}
		}
		return destinationsMapping;
	}

	public static Map<Integer, String> getFeaturesMap(ResourceResolver resourceResolver) {
		final Iterator<Resource> features = resourceResolver
				.findResources("/jcr:root/etc/tags/features//element(*,cq:Tag)[featureId]", "xpath");

		final Map<Integer, String> featuresMapping = new HashMap<>();
		while (features.hasNext()) {
			final Resource feature = features.next();

			final Integer featureId = feature.getValueMap().get("featureId", Integer.class);

			if (featureId != null) {
				final Tag tag = feature.adaptTo(Tag.class);
				if (tag != null) {
					featuresMapping.put(featureId, tag.getTagID());

					LOGGER.trace("Adding feature {} ({}) to cache", feature.getPath(), featureId);
				}
			}
		}
		return featuresMapping;
	}

	public static String getTranslatedCruiseTitle(String language, String voyageName) {
		String cruiseTitle = voyageName;

		switch (language) {
		case "fr":
			cruiseTitle = voyageName.replace(" to ", " à ");
			break;
		case "es":
		case "pt-br":
			cruiseTitle = voyageName.replace(" to ", " a ");
			break;
		case "de":
			cruiseTitle = voyageName.replace(" to ", " nach ");
			break;
		}

		return cruiseTitle;
	}

	public static Node createCruisePage(PageManager pageManager, String destinationPath, String voyageName,
			String voyageCode) throws WCMException, RepositoryException {
		// old : [Voyage Code]-[Departure port]-to-[Arrival port]
		// new : [Departure port]-to(i18n)-[Arrival port]-[Voyage Code]
		final String pageName = JcrUtil.createValidName(StringUtils.stripAccents(voyageName + " - " + voyageCode),
				JcrUtil.HYPHEN_LABEL_CHAR_MAPPING).replaceAll("-+", "-");

		// creating cruise page - uniqueness is derived from cruise code
		final Page cruisePage = pageManager.create(destinationPath, pageName, WcmConstants.PAGE_TEMPLATE_CRUISE,
				voyageCode + " - " + voyageName, false);

		final Resource cruiseContentResource = cruisePage.getContentResource();
		final Node cruiseContentNode = cruiseContentResource.adaptTo(Node.class);

		// setting alias
		// TODO replace by check on language
		if (destinationPath.contains("/fr/") || destinationPath.contains("/es/")
				|| destinationPath.contains("/pt-br/")) {
			cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-a-"));
		} else if (destinationPath.contains("/de/")) {
			cruiseContentNode.setProperty("sling:alias", pageName.replace("-to-", "-nach-"));
		}
		return cruiseContentNode;
	}

	public static void associateMapAsset(final Session session, final Node cruiseContentNode,
			final String destinationPageName, final String mapUrl, final MimeTypeService mimeTypeService,
			final ResourceResolver resourceResolver) throws RepositoryException {

		final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);

		// download and associate map
		if (StringUtils.isNotEmpty(mapUrl)) {
			try {
				final String filename = StringUtils.substringAfterLast(mapUrl, "/");

				final String assetPath = "/content/dam/silversea-com/api-provided/cruises/" + destinationPageName + "/"
						+ cruiseContentNode.getParent().getName() + "/" + filename;

				// SSC-2416 API Import - map update not taken in account
				// check if the asset with same name has been changed
				boolean updateAsset = false;
				if (session.itemExists(assetPath)) {
					Resource existingAsset = resourceResolver.getResource(assetPath);
					String existingSha1 = existingAsset.getChild("jcr:content/metadata").adaptTo(ValueMap.class)
							.get("dam:sha1", String.class);
					// if asset exists in session but does not have child nodes, the asset was
					// already created in a previous iteration,
					// no need to update again
					if (existingSha1 != null) {
						final InputStream inputStream = new URL(mapUrl).openStream();
						SHA1 newSha1 = SHA1.digest(inputStream);
						if (newSha1 != null) {
							String newChecksum = newSha1.toString();
							updateAsset = !existingSha1.equals(newChecksum);
						}
					}
				} else {
					updateAsset = true;
				}

				if (!updateAsset) {
					cruiseContentNode.setProperty("itinerary", assetPath);
				} else {
					LOGGER.info("Creating itinerary asset {}", assetPath);
					final InputStream mapStream = new URL(mapUrl).openStream();
					final Asset asset = assetManager.createAsset(assetPath, mapStream,
							mimeTypeService.getMimeType(mapUrl), false);
					LOGGER.info("Creating itinerary asset {} SAVED.", assetPath);
					// setting to activate flag on asset
					final Node assetNode = asset.adaptTo(Node.class);
					if (assetNode != null && assetNode.hasNode(JcrConstants.JCR_CONTENT)) {
						final Node assetContentNode = assetNode.getNode(JcrConstants.JCR_CONTENT);

						if (assetContentNode != null) {
							assetContentNode.setProperty(ImportersConstants.PN_TO_ACTIVATE, true);
						}
					}

					cruiseContentNode.setProperty("itinerary", asset.getPath());
				}

				LOGGER.trace("Creating itinerary asset {}", assetPath);
			} catch (IOException e) {
				LOGGER.warn("Cannot import itinerary image {}", mapUrl);
			}
		}
	}
}
