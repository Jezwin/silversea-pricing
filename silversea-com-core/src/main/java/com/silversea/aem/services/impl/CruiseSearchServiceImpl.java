package com.silversea.aem.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.Cruise;
import com.silversea.aem.components.beans.SearchFilter;
import com.silversea.aem.components.beans.SearchParameter;
import com.silversea.aem.components.beans.SearchResultData;
import com.silversea.aem.constants.ServiceConstants;
import com.silversea.aem.enums.FacetKey;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.services.CruiseSearchService;
import com.silversea.aem.utils.DateUtils;

@Service
@Component(label = "Silversea.com - Cruises Search service")
public class CruiseSearchServiceImpl implements CruiseSearchService{

    private static final Logger LOGGER = LoggerFactory.getLogger(CruiseSearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private ResourceResolver resourceResolver;
    private Session session;
    private TagManager tagManager;
    private QueryManager queryManager;
    
    private static final long DEFAULT_OFFSET = 0;
    private static final long DEFAULT_LIMIT  = 10;

    @Activate
    public void activate(final ComponentContext context) {
        try {      
            Map<String, Object> authenticationPrams = new HashMap<String, Object>();
            authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
            session = resourceResolver.adaptTo(Session.class);
            tagManager = resourceResolver.adaptTo(TagManager.class);
            queryManager = session.getWorkspace().getQueryManager();
        } catch (LoginException e) {
            LOGGER.debug("Cruise search service --  login exception ", e);
        } catch (RepositoryException e) {
       
        }
    }

    /**{@inheritDoc}**/
    @Override
    public SearchResultData search(SearchParameter searchParameter) {
        SearchResultData searchResultData = null;
        if(searchParameter != null){
            Query query = createQuery(searchParameter);
            searchResultData =  executeQuery(query,searchParameter);
        }
        else{
            LOGGER.error("Cruise search service -- request parameters is null"); 
        }

        return searchResultData;
    }
    
    /**
     * Execute a query built by  a query builder
     * @param query: query to execute
     * @param searchParameter: search filters
     * @return SearchResultData: search result
     */
    private SearchResultData executeQuery(Query query, SearchParameter searchParameter){
        SearchResult result = query.getResult();
        List<Cruise> cruises = null;
        if(result != null && result.getHits() != null && !result.getHits().isEmpty()){
            
            List<Hit> hits = result.getHits();
            cruises = hits.stream()
                          .map(hit -> {
                                return mapHits(hit,searchParameter);
                          })
                          .collect(Collectors.toList());
        }
        
        SearchResultData searchResultData = new SearchResultData();
        searchResultData.setCruises(cruises);
        extractFacets(result,searchResultData);
        return searchResultData;
    }
    
    /**
     * Hits mapping to a cruise bean
     * @param hit: Query result's hit
     * @param searchParameter: search filters
     * @return cruise: cruise bean contains cruise data
     */
    private Cruise mapHits(Hit hit,SearchParameter searchParameter){
        Cruise cruise = null;
        try {
            if(hit != null){
                Page page = hit.getResource().adaptTo(Page.class);
                CruiseModel cruiseModel = page.adaptTo(CruiseModel.class) ;
                if(cruiseModel != null){
                    cruiseModel.initByGeoLocation(searchParameter.getGeolocation());
                    cruise = mapCruise(cruiseModel);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cruise search service -- exception while mappign cruises",e); 
        }
        return cruise;
    }
    /**
     * Facet extraction and mapping with filters
     * @param result: search result
     * @param searchResultData: search result data
     */
    private void extractFacets(SearchResult result,SearchResultData searchResultData){
        try {
            Map<String, Facet> facets = result.getFacets();
            if(facets != null && !facets.isEmpty()){
                facets.forEach((k,v)->mapFilters(v,k,searchResultData));
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cruise search service -- exception while extracting facets",e); 
        }
    }
    
    /**
     * Buckets mapping with filters
     * @param facet: search result's facet
     * @param key: facet's key
     * @param searchResultData: search result data
     */
    private void mapFilters(Facet facet,String key,SearchResultData searchResultData){
        
        if(facet != null && facet.getContainsHit()){
            List<SearchFilter> filters = null;
            if(FacetKey.DESTINATION.getValue().equals(key)){        
                 filters = mapSearchFilters(facet,ServiceConstants.SEARCH_CRUISE_ROOT_PATH,ServiceConstants.DESTINATION_RESOURCE_TYPE,"destinationId");
                searchResultData.setDestinations(filters);
            }
            else if(FacetKey.CITIES.getValue().equals(key)){
                filters = mapSearchFilters(facet, ServiceConstants.SEARCH_PORT_ROOT_PATH,ServiceConstants.PORT_RESOURCE_TYPE,"cityId");
                searchResultData.setCities(filters);
            }
            else if(FacetKey.SHIP.getValue().equals(key)){
                filters = mapSearchFilters(facet, ServiceConstants.SEARCH_SHIP_ROOT_PATH,ServiceConstants.SHIP_RESOURCE_TYPE,"shipId");
                searchResultData.setShips(filters);
            }
            else if(FacetKey.DURATION.getValue().equals(key)){
                searchResultData.setDurations(getBuckets(facet));
            }
            else if(FacetKey.DATES.getValue().equals(key)){
                searchResultData.setDates(getBuckets(facet));
            }
            else if(FacetKey.TAGS.getValue().equals(key)){
                searchResultData.setFeatures(getTagsFilter(getBuckets(facet),"features"));
                searchResultData.setTypes(getTagsFilter(getBuckets(facet),"cruise-type"));
            }
        }
    }
    /**
     * Format buckets list with comma separator
     * @param facet: result's facet
     * @return formatted bucket
     */
    private String getFormatedBuckets(Facet facet){
        return facet.getBuckets().stream().map(Bucket::getValue).collect(Collectors.joining(","));
    }
    
    /**
     * Get buckets list of a facet
     * @param facet: result's facet
     * @return list of buckets
     */
    private List<String> getBuckets(Facet facet){
        return facet.getBuckets().stream().map(Bucket::getValue).collect(Collectors.toList());
    }
    
    /**
     * Build filters from tags
     * @param tags: list of tag id
     * @param tagPrefix: tag's prefix
     * @return list of search filter
     */
    private List<SearchFilter> getTagsFilter(List<String> tags,String tagPrefix){
        List<SearchFilter> filters = null;
        if(tags != null && !tags.isEmpty()){
            filters = tags.stream().map((tagId ->{
                SearchFilter searchFilter = null;
                if (StringUtils.contains(tagId, tagPrefix)) {
                    Tag tag = tagManager.resolve(tagId);
                    if(tag!=null){
                        searchFilter= new SearchFilter();
                        searchFilter.setTitle(tag.getTitle());
                        searchFilter.setId(tagId);
                    }
                }
                return searchFilter;
            }))
            .filter(e-> e != null)
            .collect(Collectors.toList());
        }
        
        return filters;
    }
    
    /**
     * Build filters from query result
     * @param queryResult: search result
     * @param property: node's property
     * @return list of search filter
     */
    private List<SearchFilter> buildFilters(QueryResult queryResult,String property){
        List<SearchFilter> filters =null;
        try {
            if(queryResult != null && queryResult.getNodes() != null){
                filters = new ArrayList<SearchFilter>();
                NodeIterator nodes = queryResult.getNodes();
                while(nodes.hasNext()){
                   Node node =  nodes.nextNode();
                   String title = Objects.toString(node.getProperty(JcrConstants.JCR_TITLE).getValue());
                   String id = Objects.toString(node.getProperty(property).getValue());
                   SearchFilter searchFilter = new SearchFilter();
                   searchFilter.setTitle(title);
                   searchFilter.setId(id);
                   filters.add(searchFilter);
                }
            }
        }catch (RepositoryException e) {
            LOGGER.error("Cruise search service -- exception while building filters",e); 
        }
        return filters;
    }
    
    /**
     * Map buckets to search filters
     * @param facet: result's facet
     * @param path: path for search resource  buckets
     * @param resourceType: resource type of resource related to buckets
     * @param id: resource's id attribute
     * @return list of search filter
     */
    private List<SearchFilter> mapSearchFilters(Facet facet,String path,String resourceType, String id){
        String buckets = getFormatedBuckets(facet);
        QueryResult queryResult = findResource(path,resourceType, id, buckets);
        List<SearchFilter> filters = buildFilters(queryResult, id);
        return filters;
    }

    /**
     * Create a a predicate query from search parameters
     * @param searchParameter: search's paramters
     * @return Query: search's query
     */
    private Query createQuery(SearchParameter searchParameter){

        PredicateGroup predicateGroup = new PredicateGroup();
        //Build simple predicates property/value
        createPredicate(ServiceConstants.SEARCH_PATH_PROPERTY, ServiceConstants.SEARCH_CRUISE_ROOT_PATH,false, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_TYPE_PROPERTY, NameConstants.NT_PAGE,false, predicateGroup);
        createPredicate(ServiceConstants.SLIN_RESOURCE_TYPE, ServiceConstants.CRUISE_RESOURCE_TYPE,true, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_CMP_DESTINATION, searchParameter.getDestinationId(), true, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_CMP_SHIP, searchParameter.getShipId(), true, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_CMP_DURATION, searchParameter.getDuration(), true, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_CMP_CITIES, searchParameter.getCityId(), true, predicateGroup);
        createPredicate(ServiceConstants.SEARCH_CMP_DATE, searchParameter.getDepartureDate(), true, predicateGroup);
        //Build tags predicates : cruise types and features
        createTagPredicate(ServiceConstants.CRUISE_TYPE_TAGS_PREFIX, searchParameter.getCruiseType(),predicateGroup);
        if(ArrayUtils.isNotEmpty(searchParameter.getFeatures())){
            Arrays.asList(searchParameter.getFeatures())
                  .forEach(e->createTagPredicate(ServiceConstants.FEATURE_TAGS_PREFIX,e,predicateGroup));
        }
        
        predicateGroup.setAllRequired(true);        
        Query query = queryBuilder.createQuery(predicateGroup, session);
        //Update pagination properties
        setPagination(query, searchParameter);
        return query;
    }
    
    /**
     * Update query pagination
     * @param query: query to set
     * @param searchParameter: search parameters
     */
    private void setPagination(Query query, SearchParameter searchParameter){
        if(searchParameter.getOffset() != null){
            query.setStart(searchParameter.getOffset());
        }
        else{
            query.setStart(DEFAULT_OFFSET);
        }
        if(searchParameter.getLimit() != null){
            query.setHitsPerPage(searchParameter.getLimit());
        }
        else{
            query.setHitsPerPage(DEFAULT_LIMIT);
        }
    }
    
    /**
     * Create a simple predicate property/value
     * @param name: predicate's name
     * @param value: predicate's value
     * @param hasProperty: boolean indicates if predicate has a property
     * @param predicateGroup: predicate's group
     */
    private void createPredicate(String name,String value,boolean hasProperty,PredicateGroup predicateGroup){
        Predicate predicate;

        if(hasProperty){
            predicate = new Predicate(JcrPropertyPredicateEvaluator.PROPERTY);
            predicate.set(JcrPropertyPredicateEvaluator.PROPERTY, name);
        }
        else{
            predicate = new Predicate(name);
        }
        if(value != null){
            predicate.set(JcrPropertyPredicateEvaluator.VALUE, value);
        }
        predicateGroup.add(predicate);
    }
   
    /**
     * Create a predicate for searching by tags
     * @param tagPrefix: tag id prefix
     * @param tagId: tag's id
     * @param predicateGroup: predicate's group
     */
    private void createTagPredicate(String tagPrefix, String tagId,PredicateGroup predicateGroup){
        Predicate tagPredicate = new Predicate(ServiceConstants.SEARCH_TAGS_KEY,ServiceConstants.SEARCH_TAG_ID_KEY);
        tagPredicate.set(JcrPropertyPredicateEvaluator.PROPERTY, "jcr:content/cq:tags");
        if(StringUtils.isNotEmpty(tagId)){
            tagPredicate.set(ServiceConstants.SEARCH_TAG_ID_KEY, tagPrefix + tagId);   
        }
        predicateGroup.add(tagPredicate);
    }
    
    /**
     * Cruise bean mapping
     * @param cruiseModel: model to map with cruise bean
     * @return Cruise: cruise data
     */
    private Cruise mapCruise(CruiseModel cruiseModel){
        String startDate = null;
        List<String> itineraries = null;
        //Format cruise's date
        if(cruiseModel.getStartDate() != null){
            startDate = DateUtils.formatDate(ServiceConstants.SEARCH_CRUISE_DATE_FORMAT, cruiseModel.getStartDate().getTime());
        }
        //Extract itineraries
        if(cruiseModel.getItineraries() != null){
            itineraries = cruiseModel.getItineraries()
                                     .stream()
                                     .map(ItineraryModel::getTitle)
                                     .collect(Collectors.toList());
        }
        //Bean mapping
        Cruise cruise = new Cruise();
        cruise.setThumbnail(cruiseModel.getThumbnail());
        cruise.setTitle(cruiseModel.getTitle());
        cruise.setType(cruiseModel.getCruiseType());
        cruise.setShip(cruiseModel.getShip().getTitle());
        cruise.setStartDate(startDate);
        cruise.setDuration(cruiseModel.getDuration());
        cruise.setLowestPrice(cruiseModel.getLowestPrice());
        cruise.setItitneraries(itineraries);
        
        return cruise;
    }

    /**
     * Find a resource by 1..* id
     * @param rootPath: root path for search
     * @param resourceType: resource type of resources to search
     * @param property: attribute for search
     * @param values: 1..* id
     * @return QueryResult: Query search result
     */
    private QueryResult findResource(String rootPath,String resourceType,String property, String values){
        
        QueryResult queryResult = null;
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("rootPath", rootPath);
        queryMap.put("resourceType", resourceType);
        queryMap.put("property", property);
        queryMap.put("values", values);
        String queryTemplate = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${rootPath}]) AND [sling:resourceType]='${resourceType}' AND s.${property} in (${values})";
        StrSubstitutor substitutor = new StrSubstitutor(queryMap);
        try {
            javax.jcr.query.Query query = queryManager.createQuery(substitutor.replace(queryTemplate), "JCR-SQL2");
           queryResult = query.execute();
            
        }catch (RepositoryException e) {
            LOGGER.error("Cruise search service -- exception while retrieving data with id {}",values,e); 
        }
        return queryResult; 
    }
}
