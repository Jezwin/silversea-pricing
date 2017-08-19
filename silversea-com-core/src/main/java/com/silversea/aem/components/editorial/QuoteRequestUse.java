package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;
import java.util.List;

/**
 * TODO split in multiple use class : call request, brochure request and quote request
 */
public class QuoteRequestUse extends WCMUsePojo {

    private List<GeolocationTagModel> countries;

    private String currentMarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

    private String siteCountry = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

    private String siteCurrency = WcmConstants.DEFAULT_CURRENCY;

    private CruiseModel selectedCruise;

    private SuiteModel selectedSuite;

    private PriceModel selectedPrice;

    private PriceModel lowestPrice;

    private String selectedSuiteCategoryCode;

    private BrochureModel selectedBrochure;

    @Override
    public void activate() throws Exception {
        // init geolocations informations
        final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(
                GeolocationTagService.class);

        if (geolocationTagService != null) {
            final GeolocationTagModel geolocationTagModel = geolocationTagService.getGeolocationTagModelFromRequest(
                    getRequest());

            if (geolocationTagModel != null) {
                currentMarket = geolocationTagModel.getMarket();
                siteCountry = geolocationTagModel.getCountryCode();
                siteCurrency = geolocationTagModel.getCurrency();
            }
        }

        // init countries list
        final Resource geotaggingNamespace = getResourceResolver().getResource(WcmConstants.PATH_TAGS_GEOLOCATION);
        if (geotaggingNamespace != null) {
            final Tag geotaggingTag = geotaggingNamespace.adaptTo(Tag.class);

            collectCountries(geotaggingTag);
        }
    }

    /**
     * TODO used
     */
    public void prepareDestinationParameters() {
        final String suffix = getRequest().getRequestPathInfo().getSuffix();
        final String[] splitSuffix = StringUtils.split(suffix, '/');

        String suiteName = null;
        String suiteCategory = null;

        if (splitSuffix != null) {
            if (splitSuffix.length > 0) {
                final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(
                        CruisesCacheService.class);

                if (cruisesCacheService != null) {
                    selectedCruise = cruisesCacheService.getCruiseByCruiseCode(
                            LanguageHelper.getLanguage(getCurrentPage()), splitSuffix[0]);

                    if (selectedCruise != null && splitSuffix.length > 1) {
                        suiteName = splitSuffix[1];

                        if (splitSuffix.length > 2) {
                            suiteCategory = splitSuffix[2];
                        }
                    }
                }
            }
        }

        if (selectedCruise != null) {
            for (PriceModel price : selectedCruise.getPrices()) {

                if (price.getGeomarket().equals(currentMarket.toLowerCase())
                        && price.getCurrency().equals(siteCurrency)) {

                    if (suiteName == null) {
                        if (lowestPrice == null || price.getPrice() < lowestPrice.getPrice()) {
                            lowestPrice = price;
                        }
                    } else if (price.getSuite().getName().equals(suiteName)) {
                        if (suiteCategory != null && price.getSuiteCategory().equals(suiteCategory)) {
                            selectedPrice = price;
                            selectedSuite = price.getSuite();

                            lowestPrice = price;

                            break;
                        } else if (suiteCategory == null) {
                            if (lowestPrice == null || price.getPrice() < lowestPrice.getPrice()) {
                                selectedSuite = price.getSuite();
                                lowestPrice = price;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * TODO used
     */
    public void prepareBrochureParameters() {
        String selectedBrochurePath = getRequest().getRequestPathInfo().getSuffix();

        if (!StringUtils.isEmpty(selectedBrochurePath)) {
            selectedBrochurePath = selectedBrochurePath.endsWith(".html")
                    ? selectedBrochurePath.substring(0, selectedBrochurePath.lastIndexOf('.'))
                    : selectedBrochurePath;

            final Resource assetResource = getResourceResolver().getResource(selectedBrochurePath);

            if (assetResource != null) {
                final Asset asset = assetResource.adaptTo(Asset.class);

                if (asset != null) {
                    selectedBrochure = asset.adaptTo(BrochureModel.class);
                }
            }
        }
    }

    /**
     * used
     *
     * @return
     */
    public BrochureModel getSelectedBrochure() {
        return selectedBrochure;
    }

    /**
     * TODO in html used
     *
     * @return the countries
     */
    public List<GeolocationTagModel> getCountries() {
        return countries;
    }

    /**
     * used
     *
     * @return the isChecked
     */
    public Boolean getIsChecked() {
        final String[] tags = getProperties().get(NameConstants.PN_TAGS, String[].class);

        if (tags != null) {
            final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

            // TODO replace by TagManager#getTags
            if (tagManager != null) {
                for (String tagId : tags) {
                    if (tagManager.resolve(tagId).getName().equals(GeolocationHelper.getCountryCode(getRequest()))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * used
     *
     * @return
     */
    public String getSuiteName() {
        return selectedPrice != null
                ? selectedSuite.getTitle() + " " + selectedPrice.getSuiteCategory()
                : selectedSuite.getTitle();
    }

    /**
     * used
     *
     * @return
     */
    public PriceModel getCruisePrice() {
        return lowestPrice;
    }

    /**
     * used
     *
     * @return
     */
    public boolean isSuiteRequested() {
        return isSuiteVariationRequested() || isSuiteCategoryRequested();
    }

    /**
     * used
     *
     * @return
     */
    public boolean isSuiteVariationRequested() {
        return selectedSuite != null;
    }

    /**
     * used
     *
     * @return
     */
    public boolean isSuiteCategoryRequested() {
        return selectedSuiteCategoryCode != null;
    }

    /**
     * used
     *
     * @return
     */
    @Deprecated
    public boolean isCruiseRequested() {
        return selectedCruise != null;
    }

    /**
     * used
     *
     * @return
     */
    public String getSiteCountry() {
        return siteCountry;
    }

    /**
     * used
     *
     * @return
     */
    public String getSiteCurrency() {
        return siteCurrency;
    }

    /**
     * @return selected cruise
     */
    public CruiseModel getSelectedCruise() {
        return selectedCruise;
    }

    /**
     * Collect the countries list - all the leaf of the tree starting with the root <code>tag</code>
     *
     * @param tag root tag of the countries
     */
    private void collectCountries(final Tag tag) {
        Iterator<Tag> children = tag.listChildren();

        if (!children.hasNext()) {
            // Mapping tag name (iso2) with tag ID
            final GeolocationTagModel geolocationTagModel = tag.adaptTo(GeolocationTagModel.class);

            if (geolocationTagModel != null) {
                countries.add(geolocationTagModel);
            }
        } else {
            while (children.hasNext()) {
                Tag child = children.next();
                collectCountries(child);
            }
        }
    }
}