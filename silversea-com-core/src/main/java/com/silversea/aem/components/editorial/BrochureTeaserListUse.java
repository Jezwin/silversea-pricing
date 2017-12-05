package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.resource.collection.ResourceCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.TagHelper;
import com.silversea.aem.models.BrochureModel;
import com.silversea.aem.services.GeolocationTagService;

public class BrochureTeaserListUse extends AbstractGeolocationAwareUse {

	static final private Logger LOGGER = LoggerFactory.getLogger(BrochureTeaserListUse.class);

	private static final String SELECTOR_BROCHURE_GROUP_PREFIX = "brochure_group_";
	private static final String SELECTOR_BROCHURE_GROUPS_TAG_PREFIX = "brochure-groups:";
	private static final String SELECTOR_LANGUAGE_PREFIX = "language_";
	private static final String DEFAULT_BROCHURE_GROUP = "default";

	private List<BrochureModel> brochures;

	private List<BrochureModel> brochuresNotLanguageFiltered;

	private Map<String, String> languages;

	private String currentLanguage;

	private String brochureGroup = DEFAULT_BROCHURE_GROUP;

	private List<String> geolocationTags;

	@Override
	public void activate() throws Exception {

		final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(
				GeolocationTagService.class);
		final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

		// Getting context
		final String geolocationTagId = geolocationTagService != null ? geolocationTagService
				.getTagIdFromRequest(getRequest()) : null;

		// init current language
		currentLanguage = LanguageHelper.getLanguage(getRequest());
		if (currentLanguage == null) {
			currentLanguage = LanguageHelper.getLanguage(getCurrentPage());

		}
		String brochureTagId = null;

		// init selected brochure group
		final String[] selectors = getRequest().getRequestPathInfo().getSelectors();
		for (String selector : selectors) {
			if (selector.startsWith(SELECTOR_BROCHURE_GROUP_PREFIX)) {
				brochureGroup = selector.replace(SELECTOR_BROCHURE_GROUP_PREFIX, "");
				brochureTagId = SELECTOR_BROCHURE_GROUPS_TAG_PREFIX + brochureGroup;
				break;
			}
		}

		// init root path
		final String brochuresPath = getProperties().get("folderReference",
				WcmConstants.PATH_DAM_SILVERSEA + "/" + WcmConstants.FOLDER_BROCHURES);

		// Building tag list
		geolocationTags = new ArrayList<>();

		if (tagManager != null && geolocationTagId != null) {
			final Tag geolocationTag = tagManager.resolve(geolocationTagId);

			if (geolocationTag != null) {
				geolocationTags.addAll(TagHelper.getTagIdsWithParents(geolocationTag));
			}
		}

		// Searching for brochures
		List<BrochureModel> brochuresNotLanguageFilteredNotOrdered = new ArrayList<>();
		brochures = new ArrayList<>();
		languages = new LinkedHashMap<>();

		LOGGER.debug("Searching brochures with tags: {}", geolocationTags);

		// get all brochures listed in order
		Resource brochuresRoot = getResourceResolver().getResource(brochuresPath);
		Map<String, Integer> brochureGroupOrder = null;
		
		if (brochuresRoot != null) {
			// filter brochures by localization, language, and group
			filterResources(brochuresRoot.listChildren(), brochuresNotLanguageFilteredNotOrdered);

			if (brochureTagId != null) {
				LOGGER.debug("Retrieve brochure {} group order: {}", brochureTagId, brochureGroup);

				Integer numBrochures = brochuresNotLanguageFilteredNotOrdered.size();
				brochuresNotLanguageFiltered = new ArrayList<>();
				Map<Integer, BrochureModel> brochureTemp = new HashMap<Integer, BrochureModel>();
			
				brochureGroupOrder = getBrochureGroupOrder(brochureTagId);
				
				if (brochureGroupOrder != null) {
					LOGGER.debug("Ordering {} brochure in {} group", numBrochures, brochureTagId);
					
					for (BrochureModel brochure : brochuresNotLanguageFilteredNotOrdered) {
						String code = brochure.getBrochureCode();
						Integer index = brochureGroupOrder.get(code);
						if (brochureGroupOrder.containsKey(code) && index < numBrochures) {
							brochureTemp.put(index, brochure);
						}
					}
					
					LOGGER.debug("Copy {} brochures from brochureTemp to brochuresNotLanguageFiltered", brochureTemp.size());
					
					brochureTemp.forEach((k, v) -> {
						brochuresNotLanguageFiltered.add(v);
					});
					brochureTemp = null;
				}
			} 
			
			if (brochureTagId == null || brochureGroupOrder == null) {
				LOGGER.debug("Not order apply");
				brochuresNotLanguageFiltered = brochuresNotLanguageFilteredNotOrdered;
			}

			// build language list
			for (BrochureModel brochure : brochuresNotLanguageFiltered) {
				final Tag language = brochure.getLanguage();

				LOGGER.debug("Searching language for brochure: {}", brochure.getBrochurePath());

				if (language != null) {
					LOGGER.debug("Adding language {} to language list", language.getTagID());

					languages.put(language.getName(), language.getTitle());
				}
			}

			if (languages.size() > 0) {
				String language;
				if (languages.containsKey(currentLanguage)) {
					language = currentLanguage;
				} else {
					language = languages.keySet().iterator().next();
				}

				for (BrochureModel brochure : brochuresNotLanguageFiltered) {
					// check language
					if (isMatchingLanguage(brochure, language)) {
						// brochure matches all criteria
						brochures.add(brochure);
					}
				}
			}
		}
	}

	/**
	 * Retrieve the specific order from collection based on tag.
	 * @param brochureTagId:  brochure id tag to get all pdf list
	 * @throws RepositoryException 
	 * @return map representing the order or null (not order)
	 */
	private Map<String, Integer> getBrochureGroupOrder(String brochureTagId) {
		Map<String, Integer> resultOrder = null;
		if (brochureTagId != null) {
			
			//create query to QueryBuilder. Search collection under /dam/collection
			final Map<String, String> map = new HashMap<String, String>();
			map.put("path", "/content/dam/collections/");
			//map.put("type", "sling:collection");
			map.put("tagid", brochureTagId);
			QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
			
			Query query = queryBuilder
					.createQuery(PredicateGroup.create(map), getResourceResolver().adaptTo(Session.class));
			
			SearchResult result = query.getResult();
			if (result.getTotalMatches() > 0) {
				try {
					Resource resourceBrochureGroup = null;
					//get the first element. First order
					resourceBrochureGroup = result.getHits().get(0).getResource();
					ResourceCollection collection = resourceBrochureGroup.adaptTo(ResourceCollection.class);
					Iterator<Resource> it = collection.getResources();
					resultOrder = new HashMap<>();
					int i = 0;
					while (it.hasNext()) {
						Resource resourcePdf = it.next();
						Asset assetPdf = resourcePdf.adaptTo(Asset.class);
						BrochureModel brochurePdf = assetPdf.adaptTo(BrochureModel.class);
						resultOrder.put(brochurePdf.getBrochureCode(), i++);
					}
				} catch (RepositoryException e) {
					LOGGER.error("Error during get resources from collection");
				}
			}
		}
		return resultOrder;
	}

	/**
	 * @return the list of brochures
	 */
	public List<BrochureModel> getBrochures() {
		return brochures;
	}

	/**
	 * @return the list of brochures, not filtered per language
	 */
	public List<BrochureModel> getBrochuresNotLanguageFiltered() {
		return brochuresNotLanguageFiltered;
	}

	/**
	 * @return the list of languages
	 */
	public Map<String, String> getLanguages() {
		return languages;
	}

	/**
	 * @return the current language
	 */
	public String getCurrentLanguage() {
		return currentLanguage;
	}

	/**
	 * @return brochure group
	 */
	public String getCurrentBrochureGroupSelector() {
		return SELECTOR_BROCHURE_GROUP_PREFIX + brochureGroup;
	}

	/**
	 * TODO check if used
	 * 
	 * @param request
	 * @return
	 */
	public String getRequestedLanguage(final SlingHttpServletRequest request) {
		final String[] selectors = request.getRequestPathInfo().getSelectors();

		for (final String selector : selectors) {
			if (selector.startsWith(SELECTOR_LANGUAGE_PREFIX)) {
				return selector.replace(SELECTOR_LANGUAGE_PREFIX, "");
			}
		}

		return null;
	}

	/**
	 * Filter brochures
	 *
	 * @param resources
	 *            first level of brochures folder in DAM
	 */
	private void filterResources(final Iterator<Resource> resources, List<BrochureModel> brochuresList) {
		while (resources.hasNext()) {
			final Resource resource = resources.next();
			final String type = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);

			if (type != null) {
				if (type.equals(DamConstants.NT_DAM_ASSET)) {
					final Asset asset = resource.adaptTo(Asset.class);

					if (asset != null) {
						final BrochureModel brochure = asset.adaptTo(BrochureModel.class);

						// check localization and brochure group
						if (isMatchingLocation(brochure, geolocationTags) && isMatchingGroup(brochure, brochureGroup)) {
							brochuresList.add(brochure);
						}
					}
				} else if (type.equals(DamConstants.NT_SLING_ORDEREDFOLDER)) {
					filterResources(resource.listChildren(), brochuresList);
				}
			}
		}
	}

	/**
	 * Check if brochure match location
	 *
	 * @param brochure
	 *            the brochure
	 * @param locationTags
	 *            the location tags
	 * @return true id brochure match locations
	 */
	private boolean isMatchingLocation(final BrochureModel brochure, final List<String> locationTags) {
		final ArrayList<Tag> brochureLocations = brochure.getLocalizations();

		for (Tag brochureLocation : brochureLocations) {
			if (locationTags.contains(brochureLocation.getTagID())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if brochure match brochure group
	 *
	 * @param brochure
	 *            the brochure
	 * @param groupName
	 *            the group name
	 * @return true if the brochure match the group
	 */
	private boolean isMatchingGroup(final BrochureModel brochure, final String groupName) {
		return brochure.getGroupNames().contains(groupName);
	}

	/**
	 * Check if brochure match brochure group
	 *
	 * @param brochure
	 *            the brochure
	 * @param languageName
	 *            the language name
	 * @return true if the brochure match the language
	 */
	private boolean isMatchingLanguage(final BrochureModel brochure, final String languageName) {
		final Tag brochureLang = brochure.getLanguage();

		return brochureLang != null && brochureLang.getName().toUpperCase().equals(languageName.toUpperCase());
	}
}