package com.silversea.aem.components.editorial;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.helper.TagHelper;
import com.silversea.aem.models.BrochureModel;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.resource.collection.ResourceCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.silversea.aem.helper.LanguageHelper.getLanguage;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class BrochureTeaserListUse extends AbstractGeolocationAwareUse {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochureTeaserListUse.class);

    private static final String SELECTOR_BROCHURE_GROUP_PREFIX = "brochure_group_";
    private static final String SELECTOR_LANGUAGE_PREFIX = "language_";
    private static final String DEFAULT_BROCHURE_GROUP = "default";

    private List<BrochureModel> brochures;
    private Map<String, String> languages;
    private String currentLanguage;
    private String brochureGroup;
    private List<String> geolocationTags;


    @Override
    public void activate() throws Exception {
        ResourceResolver resourceResolver = getResourceResolver();
        final String brochuresPath = getProperties().get("folderReference", "/content/dam/silversea-com/brochures");
        Resource brochuresRoot = resourceResolver.getResource(brochuresPath);
        if (brochuresRoot == null) {
            return;
        }
        try {
            currentLanguage = initLanguage();
            geolocationTags = initGeoTags(resourceResolver);
            brochureGroup = retrieveBrochureGroup();
            List<BrochureModel> unfilteredBrochures = retrieveBrochures(brochuresRoot.listChildren(), brochureGroup);
            languages = retrieveLanguages(unfilteredBrochures);
            List<BrochureModel> unsortedBrochures = filterByLanguage(unfilteredBrochures);
            brochures = sortBrochures(unsortedBrochures, "brochure-groups:" + brochureGroup);
        } catch (Throwable throwable) {
            LOGGER.error("error during activation", throwable);
        }
    }

    private Map<String, String> retrieveLanguages(Iterable<BrochureModel> unfilteredBrochures) {
        Map<String, String> languages = new HashMap<>();
        StreamSupport.stream(unfilteredBrochures.spliterator(), false)
                .filter(Objects::nonNull)
                .map(BrochureModel::getLanguage)
                .filter(Objects::nonNull)
                .forEach(language -> languages.put(language.getName(), language.getTitle()));
        return languages;
    }

    private List<BrochureModel> filterByLanguage(List<BrochureModel> unfilteredBrochures) {
        if (languages.size() <= 0) {
            return Collections.emptyList();
        }
        String language =
                languages.containsKey(currentLanguage) ? currentLanguage : languages.keySet().iterator().next();
        return unfilteredBrochures.stream()
                .filter(brochure -> isMatchingLanguage(brochure, language)).collect(toList());
    }

    private String retrieveBrochureGroup() {
        final String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        return Stream.of(selectors)
                .filter(selector -> selector.startsWith(SELECTOR_BROCHURE_GROUP_PREFIX))
                .map(selector -> selector.replace(SELECTOR_BROCHURE_GROUP_PREFIX, ""))
                .findFirst()
                .orElse(DEFAULT_BROCHURE_GROUP);
    }

    private List<BrochureModel> sortBrochures(List<BrochureModel> unsortedBrochures, String brochureTagId) {
        List<BrochureModel> sortedBrochures = sortBrochures(brochureTagId, unsortedBrochures);
        return sortedBrochures.isEmpty() ? unsortedBrochures : sortedBrochures;
    }

    private String initLanguage() {
        return ofNullable(getLanguage(getRequest())).orElse(getLanguage(getCurrentPage()));
    }

    private List<String> initGeoTags(ResourceResolver resourceResolver) {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if (tagManager == null) {
            return Collections.emptyList();
        }
        return ofNullable(getSlingScriptHelper().getService(GeolocationTagService.class))
                .map(service -> service.getTagIdFromRequest(getRequest()))
                .map(tagManager::resolve)
                .map(TagHelper::getTagIdsWithParents)
                .orElse(Collections.emptyList());
    }

    /**
     * Retrieve the specific order from collection based on tag.
     *
     * @param unsortedBrochures not ordered brochures.
     * @param brochureTagId:    brochure id tag to get all pdf list
     * @return map representing the order or null (not order)
     */
    private List<BrochureModel> sortBrochures(String brochureTagId, List<BrochureModel> unsortedBrochures) {
        if (unsortedBrochures.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<String, String> map = new HashMap<>();
        map.put("path", "/content/dam/collections/");
        map.put("type", "sling:collection");
        map.put("tagid", brochureTagId);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder
                .createQuery(PredicateGroup.create(map), getResourceResolver().adaptTo(Session.class));
        List<BrochureModel> sortedBrochures = Collections.emptyList();

        SearchResult result = query.getResult();
        if (result.getTotalMatches() <= 0) {
            return Collections.emptyList();
        }
        try {
            Resource resourceBrochureGroup = result.getHits().get(0).getResource();
            ResourceCollection collection = resourceBrochureGroup.adaptTo(ResourceCollection.class);
            if (collection != null) {
                Iterable<Resource> it = collection::getResources;
                List<String> orderedIds =
                        StreamSupport.stream(it.spliterator(), false)
                                .map(resource -> resource.adaptTo(Asset.class)).filter(Objects::nonNull)
                                .map(asset -> asset.adaptTo(BrochureModel.class)).filter(Objects::nonNull)
                                .map(BrochureModel::getBrochureCode)
                                .collect(toList());
                sortedBrochures = unsortedBrochures.stream()
                        .filter(brochure -> orderedIds.contains(brochure.getBrochureCode()))
                        .sorted(Comparator.comparingInt(brochure -> orderedIds.indexOf(brochure.getBrochureCode())))
                        .collect(toList());
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error during get resources from collection");
        }
        return sortedBrochures;
    }

    /**
     * @return the list of brochures
     */
    public List<BrochureModel> getBrochures() {
        return brochures;
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
        return Stream.of(request.getRequestPathInfo().getSelectors())
                .filter(selector -> selector.startsWith(SELECTOR_LANGUAGE_PREFIX))
                .map(selector -> selector.replace(SELECTOR_LANGUAGE_PREFIX, ""))
                .findFirst().orElse(null);
    }

    private String getType(Resource resource) {
        return ofNullable(resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class)).orElse("");
    }

    private Stream<Resource> retrieveAssets(Resource resource) {
        String type = getType(resource);
        if (DamConstants.NT_DAM_ASSET.equals(type)) {
            return Stream.of(resource);
        } else if (DamConstants.NT_SLING_ORDEREDFOLDER.equals(type)) {
            Iterable<Resource> resourceIterator = resource::listChildren;
            return StreamSupport.stream(resourceIterator.spliterator(), false).flatMap(this::retrieveAssets);
        } else {
            return Stream.empty();
        }
    }

    private List<BrochureModel> retrieveBrochures(Iterator<Resource> resources, String brochureGroup) {
        Spliterator<Resource> spliterator = ((Iterable<Resource>) () -> resources).spliterator();
        return StreamSupport.stream(spliterator, false)
                .flatMap(this::retrieveAssets)
                .map(resource -> resource.adaptTo(Asset.class))
                .filter(Objects::nonNull)
                .map(asset -> asset.adaptTo(BrochureModel.class))
                .filter(Objects::nonNull)
                .filter(brochure -> isMatchingLocation(brochure, geolocationTags))
                .filter(brochure -> isMatchingGroup(brochure, brochureGroup) ||
                        isMatchingGroup(brochure, DEFAULT_BROCHURE_GROUP))
                .collect(toList());
    }


    /**
     * Check if brochure match location
     *
     * @param brochure     the brochure
     * @param locationTags the location tags
     * @return true id brochure match locations
     */
    private boolean isMatchingLocation(final BrochureModel brochure, final List<String> locationTags) {
        return brochure.getLocalizations().stream().map(Tag::getTagID).anyMatch(locationTags::contains);
    }

    /**
     * Check if brochure match brochure group
     *
     * @param brochure  the brochure
     * @param groupName the group name
     * @return true if the brochure match the group
     */
    private boolean isMatchingGroup(final BrochureModel brochure, final String groupName) {
        return brochure.getGroupNames().contains(groupName);
    }

    /**
     * Check if brochure match brochure group
     *
     * @param brochure     the brochure
     * @param languageName the language name
     * @return true if the brochure match the language
     */
    private boolean isMatchingLanguage(final BrochureModel brochure, final String languageName) {
        return languageName.equalsIgnoreCase(brochure.getLanguage().getName());
    }
}
