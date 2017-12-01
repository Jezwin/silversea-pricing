package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.*;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GeolocationTagService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * TODO split in multiple use class : call request, brochure request and quote request
 * TODO use {@link com.silversea.aem.components.AbstractGeolocationAwareUse}
 */
public class QuoteRequestUse extends WCMUsePojo {

    private List<GeolocationTagModel> countries = new ArrayList<>();

    private String currentMarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

    private String siteCountry = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

    private String siteCurrency = WcmConstants.DEFAULT_CURRENCY;

    private CruiseModel selectedCruise;

    private SuiteModel selectedSuite;

    private PriceModel selectedPrice;

    private PriceModel lowestPrice;

    private boolean isWaitList = true;

    public String selectedSuiteCategoryCode;

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

            countries.sort(Comparator.comparing(GeolocationTagModel::getTitle));
        }
    }

    /**
     * TODO used
     */
    public void prepareDestinationParameters() {
        String suffix = getRequest().getRequestPathInfo().getSuffix();

        if (suffix != null) {
            suffix = suffix.replace(".html", "");
            final String[] splitSuffix = StringUtils.split(suffix, '/');

            String suiteName = null;
            String suiteCategory = null;

            if (splitSuffix != null) {
                if (splitSuffix.length > 0) {
                    final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(
                            CruisesCacheService.class);

                    if (cruisesCacheService != null) {
                        //CruiseModelLight only contains the lowest prices
                        //Must construct the complete CruiseModel in order to have all prices
                        CruiseModelLight cruiseModelLight = cruisesCacheService.getCruiseByCruiseCode(
                                LanguageHelper.getLanguage(getCurrentPage()), splitSuffix[0]);
                        Resource cruiseResource = getResourceResolver().getResource(cruiseModelLight.getPath());
                        selectedCruise = null;
                        if (cruiseResource != null) {
                            Page cruisePage = getPageManager().getPage(cruiseModelLight.getPath());
                            selectedCruise = cruisePage.adaptTo(CruiseModel.class);
                        }

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
                    if (price.getGeomarket().equals(currentMarket)
                            && price.getCurrency().equals(siteCurrency)) {

                        if (suiteName == null && !price.isWaitList()) {
                            if (lowestPrice == null || price.getPrice() < lowestPrice.getPrice()) {
                                lowestPrice = price;
                                isWaitList = false;
                            }
                        } else if (price.getSuite().getName().equals(suiteName)) {
                            if (suiteCategory != null && price.getSuiteCategory().equals(suiteCategory)) {
                                selectedPrice = price;
                                selectedSuite = price.getSuite();
                                selectedSuiteCategoryCode = price.getSuiteCategory();
                                lowestPrice = price;
                                isWaitList = price.isWaitList();

                                break;
                            } else if (suiteCategory == null) {
                                selectedSuite = price.getSuite();
                                if (!price.isWaitList() && (lowestPrice == null || price.getPrice() < lowestPrice.getPrice())) {
                                    lowestPrice = price;
                                    isWaitList = false;
                                }
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
        return selectedSuite.getTitle();
        /*return selectedPrice != null
                ? selectedSuite.getTitle() + " " + selectedPrice.getSuiteCategory()
                : selectedSuite.getTitle();*/
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
     * @return true if selected cruise/suite/variation is in waitlist
     */
    public boolean isWaitList() {
        return isWaitList;
    }

    public String getSelectedSuiteCategoryCode() {
        return selectedSuiteCategoryCode;
    }

    /**
     * Collect the countries list - all the leaf of the tree starting with the root <code>tag</code>
     *
     * @param tag root tag of the countries
     */
    private void collectCountries(final Tag tag) {
        Iterator<Tag> children = tag.listChildren();

        if (!children.hasNext()) {
            final Resource tagResource = tag.adaptTo(Resource.class);

            if (tagResource != null) {
                final GeolocationTagModel geolocationTagModel = tagResource.adaptTo(GeolocationTagModel.class);

                if (geolocationTagModel != null) {
                    countries.add(geolocationTagModel);
                }
            }
        } else {
            while (children.hasNext()) {
                Tag child = children.next();
                collectCountries(child);
            }
        }
    }
}