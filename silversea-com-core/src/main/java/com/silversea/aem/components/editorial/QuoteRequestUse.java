package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.BrochureModel;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.models.PriceModel;
import com.silversea.aem.models.RequestQuoteModel;
import com.silversea.aem.models.SuiteModel;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.aem.services.GeolocationTagService;

/**
 * TODO split in multiple use class : call request, brochure request and quote
 * request TODO use
 * {@link com.silversea.aem.components.AbstractGeolocationAwareUse}
 */
/**
 * @author giuseppes
 *
 */
public class QuoteRequestUse extends WCMUsePojo {

	static final private Logger LOGGER = LoggerFactory.getLogger(QuoteRequestUse.class);

	private List<GeolocationTagModel> countries = new ArrayList<>();

	private String currentMarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

	private String siteCountry = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

	private String siteCurrency = WcmConstants.DEFAULT_CURRENCY;

	private CruiseModel selectedCruise;

	private SuiteModel selectedSuite;

	private PriceModel selectedPrice;

	private PriceModel lowestPrice;

	private boolean isWaitList = true;

	private String selectedSuiteCategoryCode;

	private BrochureModel selectedBrochure;

	private RequestQuoteModel raqModel;

	@Override
	public void activate() throws Exception {
		// init geolocations informations
		final GeolocationTagService geolocationTagService = getSlingScriptHelper().getService(
				GeolocationTagService.class);

		if (geolocationTagService != null) {
			final GeolocationTagModel geolocationTagModel = geolocationTagService
					.getGeolocationTagModelFromRequest(getRequest());

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
		String selector = getRequest().getRequestPathInfo().getSelectorString();

		suffix = (suffix != null) ? suffix.replace(".html", "") : null;
		final String[] splitSuffix = (suffix != null) ? StringUtils.split(suffix, '/') : null;

		if (selector != null && suffix != null) {
			if (selector.equalsIgnoreCase(WcmConstants.SELECTOR_FYC_RESULT)) {
				prepareSelectedCruise(splitSuffix);
			} else {
				prepareRAQModelData(selector, splitSuffix);
			}
		}

	}

	/**
	 * Create RAQModel retrieving information in crx/de based on selector.
	 * 
	 * Example Query: path=/content/silversea-com/en/destinations or ships or
	 * exclusive-offer 1_property=cq:template
	 * 1_property.value=/apps/silversea/silversea-com/templates/destination or
	 * ship or exclusiveoffer 2_property=destinationId or shipId o
	 * exclusiveOfferId 2_property.value=1
	 * 
	 * XPath Query:
	 * /jcr:root/content/silversea-com/en/destinations//*[@cq:template =
	 * '/apps/silversea/silversea-com/templates/destination' and
	 * 
	 * @destinationId='1']
	 * 
	 * @param selector
	 *            ss, sd, eo
	 * @param splitSuffix
	 *            destinationId, shipId, exclusiveOfferId
	 */
	private void prepareRAQModelData(String selector, String[] splitSuffix) {
		if (splitSuffix != null && splitSuffix.length > 0) {
			// create QueryBuilder parameter
			StringBuilder path = new StringBuilder("/content/silversea-com/");
			path.append(LanguageHelper.getLanguage(getCurrentPage()));
			String nodePath = null, template = null, property = null;
			switch (selector) {
			case WcmConstants.SELECTOR_SINGLE_DESTINATION:
				path.append("/destinations");
				template = WcmConstants.PAGE_TEMPLATE_DESTINATION;
				property = WcmConstants.PN_DESTINATION_ID;
				break;
			case WcmConstants.SELECTOR_SINGLE_SHIP:
				path.append("/ships");
				template = WcmConstants.PAGE_TEMPLATE_SHIP;
				property = WcmConstants.PN_SHIP_ID;
				break;
			case WcmConstants.SELECTOR_EXCLUSIVE_OFFER:
				path.append("/exclusive-offers");
				template = WcmConstants.PAGE_TEMPLATE_EXCLUSIVE_OFFER;
				property = WcmConstants.PN_EXCLUSIVE_OFFER_ID;
				break;
			}
			raqModel = new RequestQuoteModel(splitSuffix[0], selector);
			// create QueryBuilder query
			final Map<String, String> map = new HashMap<String, String>();
			map.put("path", path.toString());
			map.put("1_property", "cq:template");
			map.put("1_property.value", template);
			map.put("2_property", property);
			map.put("2_property.value", raqModel.getId());

			QueryBuilder queryBuilder = getResourceResolver().adaptTo(QueryBuilder.class);
			Query query = queryBuilder.createQuery(PredicateGroup.create(map),
					getResourceResolver().adaptTo(Session.class));

			SearchResult result = query.getResult();
			if (result.getTotalMatches() > 0) {
				Resource resourceResult;
				try {
					// read property thumbnail, raqTitle and jcr:description
					resourceResult = result.getHits().get(0).getResource();
					Node node = resourceResult.adaptTo(Node.class);
					Property propVal = node.getProperty("thumbnail");
					raqModel.setThumbnail(propVal.getValue().toString());
					if (selector.equalsIgnoreCase(WcmConstants.SELECTOR_EXCLUSIVE_OFFER)) {
						propVal = node.getProperty("jcr:title");
						raqModel.setTitle(propVal.getValue().toString());
						propVal = node.getProperty("jcr:description");
						raqModel.setDescription(propVal.getValue().toString());
					} else {
						propVal = node.getProperty("raqTitle");
						raqModel.setTitle(propVal.getValue().toString());
					}
				} catch (Exception e) {
					LOGGER.error("Error during retrieving raqModel data");
				}
			}
		}
	}

	/**
	 * Create selectedCruise from fyc result
	 * 
	 * @param splitSuffix
	 */
	private void prepareSelectedCruise(final String[] splitSuffix) {
		if (splitSuffix != null) {

			String suiteName = null;
			String suiteCategory = null;

			if (splitSuffix != null) {
				if (splitSuffix.length > 0) {
					final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(
							CruisesCacheService.class);

					if (cruisesCacheService != null) {
						// CruiseModelLight only contains the lowest prices
						// Must construct the complete CruiseModel in order to
						// have all prices
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
					if (price.getGeomarket().equals(currentMarket) && price.getCurrency().equals(siteCurrency)) {

						if (suiteName == null && !price.isWaitList()) {
							if (lowestPrice == null || price.getPrice() < lowestPrice.getPrice()) {
								lowestPrice = price;
								isWaitList = false;
							}
						} else if (price.getSuite().getName().equals(suiteName)) {
							if (suiteCategory != null && price.getSuiteCategory().equals(suiteCategory)) {
								selectedPrice = price;
								selectedSuite = price.getSuite();

								lowestPrice = price;
								isWaitList = price.isWaitList();

								break;
							} else if (suiteCategory == null) {
								if (!price.isWaitList()
										&& (lowestPrice == null || price.getPrice() < lowestPrice.getPrice())) {
									selectedSuite = price.getSuite();
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

	public RequestQuoteModel getRaqModel() {
		return raqModel;
	}

	/**
	 * TODO used
	 */
	public void prepareBrochureParameters() {
		String selectedBrochurePath = getRequest().getRequestPathInfo().getSuffix();

		if (!StringUtils.isEmpty(selectedBrochurePath)) {
			selectedBrochurePath = selectedBrochurePath.endsWith(".html") ? selectedBrochurePath.substring(0,
					selectedBrochurePath.lastIndexOf('.')) : selectedBrochurePath;

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
		return selectedPrice != null ? selectedSuite.getTitle() + " " + selectedPrice.getSuiteCategory()
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
		return selectedCruise != null || raqModel != null;
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

	/**
	 * Collect the countries list - all the leaf of the tree starting with the
	 * root <code>tag</code>
	 *
	 * @param tag
	 *            root tag of the countries
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