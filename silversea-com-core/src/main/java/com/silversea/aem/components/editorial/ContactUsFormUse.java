package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

import com.silversea.aem.components.AbstractGeolocationAwareUse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.helper.GeolocationHelper;
import com.silversea.aem.models.GeolocationTagModel;
import com.silversea.aem.services.GeolocationTagService;


/**
 * @author sachin
 *
 */
public class ContactUsFormUse extends AbstractGeolocationAwareUse {

	static final private Logger LOGGER = LoggerFactory.getLogger(ContactUsFormUse.class);

	private List<GeolocationTagModel> countries = new ArrayList<>();

	private String currentMarket = WcmConstants.DEFAULT_GEOLOCATION_GEO_MARKET_CODE;

	private String siteCountry = WcmConstants.DEFAULT_GEOLOCATION_COUNTRY;

	private String siteCurrency = WcmConstants.DEFAULT_CURRENCY;

	private Node currentNode;
	
	private JSONArray subjectsArray = new JSONArray();
	
	private JSONObject subjects;
	
	@Override
	public void activate() throws Exception {
		// init geolocations informations
		super.activate();
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
		
		// init subject and inquiry list
		Property property = null;
		currentNode = getResource().adaptTo(Node.class);
        if (currentNode.hasProperty("subjects")) {
            property = currentNode.getProperty("subjects");
        }
        if (property != null) {
            JSONObject subject = null, inquiry = null;
            Value[] values = null;

            if (property.isMultiple()) {
                values = property.getValues();
            } else {
                values = new Value[1];
                values[0] = property.getValue();
            }
            for (Value val : values) {
                subjectsArray.put(val);
            }
        }
        subjects = new JSONObject();
        subjects.put("subjects",subjectsArray);
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
		return false;
		//return !countryCode.toLowerCase().equals("ca") && !countryCode.toLowerCase().equals("can");

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

	public String getSubjects() {
		return subjects.toString();
	}
	
}