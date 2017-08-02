package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.beans.GeoLocation;
import com.silversea.aem.components.beans.PriceData;
import com.silversea.aem.components.beans.SuiteVariation;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.BrochureModel;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.services.GeolocationService;

public class QuoteRequestUse extends WCMUsePojo {
    private List<Resource> countries;
    private GeoLocation geoLocation;

    private String selectedCruiseCode;
    private CruiseModel selectedCruise;
    private SuiteModel selectedSuite;
    private String selectedSuiteCategoryCode;
    private Page cruisePage;
    private Page suitePage;

    private BrochureModel selectedBrochure; 
    

    @Override
    public void activate() throws Exception {
        GeolocationService geolocationService = getSlingScriptHelper().getService(GeolocationService.class);
        geoLocation = geolocationService.initGeolocation(getRequest());

        Map<String, String> queryMap = new HashMap<String, String>();

        // create query description as hash map
        queryMap.put(PathPredicateEvaluator.PATH, WcmConstants.PATH_TAGS_GEOLOCATION);
        queryMap.put(TypePredicateEvaluator.TYPE, TagConstants.NT_TAG);
        queryMap.put(JcrPropertyPredicateEvaluator.PROPERTY, "name");
        queryMap.put(JcrPropertyPredicateEvaluator.PROPERTY + "." + JcrPropertyPredicateEvaluator.OPERATION, JcrPropertyPredicateEvaluator.OP_EXISTS);
        queryMap.put(Predicate.ORDER_BY, "@" + JcrConstants.JCR_TITLE);
        queryMap.put(Predicate.ORDER_BY + "." + Predicate.PARAM_SORT, Predicate.SORT_ASCENDING);
        queryMap.put(PredicateConverter.GROUP_PARAMETER_PREFIX + "." + Predicate.PARAM_LIMIT, "-1");

        // Do query
        Session session = getResourceResolver().adaptTo(Session.class);
        QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();

        // Get result from Query
        countries = new ArrayList<Resource>();
        for (Hit hit : result.getHits()) {
            countries.add(hit.getResource());
        }

        // Prepare destination parameters for quote request
        //prepareDestinationParameters();
        
        // Prepare brochure request parameters
        //prepareBrochureParameters();
    }

    public void prepareDestinationParameters() {

        String destinationParameters = getRequest().getRequestPathInfo().getSuffix();
        String[] destinationParams = StringUtils.split(destinationParameters, '/');
        if (destinationParams.length <= 0) {
            // no destination parameters
            selectedCruiseCode = null;
        } else {
            // at least cruise selected
            selectedCruiseCode = destinationParams[0];
            selectedCruise = findCruise(selectedCruiseCode);
            selectedCruise.initByGeoLocation(geoLocation);

            if (destinationParams.length > 1) {
                // cruise and suite selected
                String selectedSuiteParam = destinationParameters.replace(selectedCruiseCode, "");
                Resource suiteResource = getResourceResolver().getResource(selectedSuiteParam);
                if (suiteResource != null) {
                    // request for suite variation
                    suitePage = suiteResource.adaptTo(Page.class);
                    selectedSuite = suitePage.adaptTo(SuiteModel.class);
                } else {
                    selectedSuiteCategoryCode = destinationParams[1];
                }
            }
        }
    }
    
    public void prepareBrochureParameters() {
        String selectedBrochurePath = getRequest().getRequestPathInfo().getSuffix();
        if (!StringUtils.isEmpty(selectedBrochurePath)) {
            selectedBrochurePath = selectedBrochurePath.endsWith(".html") ? 
                    selectedBrochurePath.substring(0, selectedBrochurePath.lastIndexOf('.')) : selectedBrochurePath;
            Resource assetResource = getResourceResolver().getResource(selectedBrochurePath);
            Asset asset = assetResource.adaptTo(Asset.class);

            if (asset != null) {
                selectedBrochure = asset.adaptTo(BrochureModel.class);
            }
        }
    }
    
    public BrochureModel getSelectedBrochure() {
        return selectedBrochure;
    }

    /**
     * @return the countries
     */
    public List<Resource> getCountries() {
        return countries;
    }

    /**
     * @return the isChecked
     */
    public Boolean getIsChecked() {
        String[] tags = getProperties().get(NameConstants.PN_TAGS, String[].class);
        TagManager tagManager;
        if (tags != null) {
            tagManager = getResourceResolver().adaptTo(TagManager.class);
            for (String tagId : tags) {
                if (tagManager.resolve(tagId).getName().equals(GeolocationHelper.getCountryCode(getRequest()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean showDestination() {
        return selectedCruiseCode != null;
    }

    public CruiseModel findCruise(String cruiseCode) {
        CruiseModel result = null;
        Iterator<Resource> resources = getResourceResolver()
                .findResources("//element(*, cq:Page)[jcr:content/@sling:resourceType='silversea/silversea-com/components/pages/cruise' and jcr:content/@cruiseCode='" + cruiseCode + "']", "xpath");
        while (resources.hasNext()) {
            cruisePage = resources.next().adaptTo(Page.class);
            if (cruisePage != null) {
                result = cruisePage.adaptTo(CruiseModel.class);
                break;
            }
        }
        return result;
    }

    public GeoLocation getGeolocation() {
        return geoLocation;
    }

    public String getCruiseThumbnail() {
        String thumbnail = "";
        if (cruisePage != null) {
            Resource imageRes = cruisePage.getContentResource("image");
            if (imageRes != null) {
                thumbnail = imageRes.getValueMap().get("fileReference", String.class);
            }
        }
        return thumbnail;
    }

    public Calendar getCruiseDepartureDate() {
        return selectedCruise.getStartDate();
    }

    public String getCruiseTitle() {
        return selectedCruise.getTitle();
    }

    public String getCruiseDuration() {
        return selectedCruise.getDuration();
    }

    public String getCruiseShipName() {
        return selectedCruise.getShip().getTitle();
    }

    public String getSuiteName() {
        String suiteName = "";
        if (selectedSuite != null) {
            /// get lowest price for suite variation
            suiteName = suitePage.getTitle();
        } else if (selectedSuiteCategoryCode != null) {
            suiteName = selectedSuiteCategoryCode;
        }
        return suiteName;
    }

    public PriceData getCruisePrice() {
        PriceData price = null;
        if (selectedSuite == null && selectedSuiteCategoryCode == null) {
            // get lowest cruise price
            price = selectedCruise.getLowestPrice();
        } else if (selectedSuite != null) {
            /// get lowest price for suite variation
            String suiteName = selectedSuite.getTitle();
            for (SuiteModel suiteModel : selectedCruise.getSuites()) {
                if (suiteModel.getTitle().equals(suiteName)) {
                    price = suiteModel.getLowestPrice();
                    break;
                }
            }
        } else if (selectedSuiteCategoryCode != null) {
            // get price of suite category
            for (SuiteModel suiteModel : selectedCruise.getSuites()) {
                for (SuiteVariation suiteCategory : suiteModel.getVariations()) {
                    if (suiteCategory.getName().equals(selectedSuiteCategoryCode)) {
                        price = suiteCategory.getPrice();
                        break;
                    }
                }
            }
        }
        return price;
    }

    public boolean isSuiteRequested() {
        return isSuiteVariationRequested() || isSuiteCategoryRequested();
    }

    public boolean isSuiteVariationRequested() {
        return selectedSuite != null;
    }

    public boolean isSuiteCategoryRequested() {
        return selectedSuiteCategoryCode != null;
    }

    public boolean isCruiseRequested() {
        return selectedCruise != null;
    }

    public String getCruiseCode() {
        return selectedCruiseCode;
    }
}