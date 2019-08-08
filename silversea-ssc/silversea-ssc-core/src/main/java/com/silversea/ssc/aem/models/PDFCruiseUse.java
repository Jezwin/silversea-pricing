package com.silversea.ssc.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.silversea.aem.components.page.CruiseUse;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.ItineraryModel;
import com.silversea.aem.models.PortModel;

/**
 * 
 * This file contains methods to transform the objects as they are required in
 * the PDF views for the cruises.
 * 
 * @author nikhil
 *
 */
public class PDFCruiseUse extends CruiseUse {
	private List<String> imagesList = new ArrayList<String>();

	private String header_beforeTitle;
	private String header_title;

	private String header_afterTitle;

	private String footerPageUrl;
	private String termsAndConditionsUrl;
	private final static String TAGS_LOCATION = "/etc/tags/ccpt";
	static final private Logger LOGGER = LoggerFactory.getLogger(PDFCruiseUse.class);
	private boolean isCcptAvailable;
	private boolean isTaSelected;
	private String language;
	private CruiseModel cruiseModel;
	private List<String> includedInFareFirstList;
	private List<String> includedInFareSecondList;
	private List<ItineraryModel> itinerarySplitFirstList;
	private List<ItineraryModel> itinerarySplitSecondList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.silversea.aem.components.page.CruiseUse#activate()
	 */
	@Override
	public void activate() throws Exception {
		super.activate();
		cruiseModel = getCruiseModel();
		InheritanceValueMap properties = new HierarchyNodeInheritanceValueMap(
				getResourceResolver().getResource(getCurrentPage().getPath()));

		final String[] bottomLine = properties.getInherited("footer/referencelegal", String[].class);

		language = properties.getInherited("jcr:language", String.class);
		if (bottomLine != null && bottomLine.length > 1) {
			termsAndConditionsUrl = getSlingScriptHelper().getService(Externalizer.class)
					.publishLink(getResourceResolver(), bottomLine[1]);
		}
		footerPageUrl = getSlingScriptHelper().getService(Externalizer.class).publishLink(getResourceResolver(),
				getCurrentPage().getPath());

		final Resource ccptResource = getResourceResolver().getResource(TAGS_LOCATION);

		// CCPT code will always be passed as a third selector to
		// the HTML Header/body/footer call
		String[] selectors = getRequest().getRequestPathInfo().getSelectors();
		if (ccptResource != null) {
			ValueMap vMap = ccptResource.getValueMap();
			Map<String, List<String>> ccptInfo = new HashMap<>();
			if (vMap.get("ccptInfo") instanceof String[]) {
				String[] ccptArray = vMap.get("ccptInfo", String[].class);

				for (String arrayElement : ccptArray) {
					String[] temp = arrayElement.split(":");
					if (null != temp && temp.length == 2) {
						ccptInfo.put(temp[0], Arrays.asList(temp[1].split("~")));
					}
				}
				String ccptCode = getCodeFromSelectors(selectors, "ccpt_");
				if (selectors.length > 0 && ccptInfo.size() > 0 && ccptInfo.containsKey(ccptCode)
						&& ccptInfo.get(ccptCode).size() == 3) {
					isCcptAvailable = true;
					String[] array = (String[]) ccptInfo.get(ccptCode).toArray(new String[0]);
					header_beforeTitle = array[0];
					header_title = array[1];
					header_afterTitle = array[2];
				}
				
				if(ccptCode.contains("999")){
					isTaSelected = true;
				}
			}
		} else {
			LOGGER.error("{} not found, do not build tag list", TAGS_LOCATION);
		}

		@SuppressWarnings("unchecked")
		List<String> allIncludedInFaresItems = ListUtils.union(getExclusiveOffersCruiseFareAdditions(),
				cruiseModel.getCruiseFareAdditions());
		List<List<String>> splitLists = chopped(allIncludedInFaresItems,
				(int) ( Math.ceil( (double) allIncludedInFaresItems.size() / 2)));
		if (!splitLists.isEmpty()) {
			setIncludedInFareFirstList(splitLists.get(0));
			if (splitLists.size() > 1) {
				setIncludedInFareSecondList(splitLists.get(1));
			}
		}

		List<ItineraryModel> itineraryList = cruiseModel.getCompactedItineraries();
		int splitCounter = 0;
		List<ItineraryModel> firstTempList = new ArrayList<ItineraryModel>();
		List<ItineraryModel> secondTempList = new ArrayList<ItineraryModel>();
		for (Iterator<ItineraryModel> i = itineraryList.iterator(); i.hasNext();) {
			if (splitCounter >= 2) {
				secondTempList.add(i.next());
			} else {
				firstTempList.add(i.next());
				splitCounter++;
			}
		}
		setItinerarySplitFirstList(firstTempList);
		setItinerarySplitSecondList(secondTempList);
	}

	/**
	 * @param selectors
	 * @param pattern
	 * @return
	 */
	private String getCodeFromSelectors(String[] selectors, String pattern) {

		String ccptCode = StringUtils.EMPTY;
		for (String selector : selectors) {
			if (selector.startsWith(pattern)) {
				ccptCode = selector.split("_")[1];
			}
		}
		return ccptCode;
	}

	// chops a list into non-view sublists of length L
	static <T> List<List<T>> chopped(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}

	/**
	 * @return
	 */
	public List<String> getImagesList() {
		// getCruiseModel().getItineraries().get(0).getPort().getThumbnail();
		List<ItineraryModel> itineraries = getCruiseModel().getItineraries();
		Iterator<ItineraryModel> iterator = itineraries.iterator();
		while (iterator.hasNext() && imagesList.size() < 5) {
			ItineraryModel itineraryModel = iterator.next();

			PortModel port = itineraryModel.getPort();
			if (!StringUtils.endsWith(port.getPath(), "/day-at-sea") && !StringUtils.endsWith(port.getPath(), "/date-line-lose-a-day")) {
				String portThumbnail = port.getThumbnail();
				if (!imagesList.contains(portThumbnail)){
					imagesList.add(portThumbnail);
				}
			}
		}
		return imagesList;
	}

	public String getHeader_beforeTitle() {
		return header_beforeTitle;
	}

	public String getHeader_title() {
		return header_title;
	}

	public String getHeader_afterTitle() {
		return header_afterTitle;
	}

	public String getTermsAndConditionsUrl() {
		return termsAndConditionsUrl;
	}

	public String getFooterPageUrl() {
		return footerPageUrl;
	}

	public boolean isCcptAvailable() {
		return isCcptAvailable;
	}
	
	public boolean isTaSelected() {
		return isTaSelected;
	}

	public List<String> getIncludedInFareFirstList() {
		return includedInFareFirstList;
	}

	public void setIncludedInFareFirstList(List<String> includedInFareFirstList) {
		this.includedInFareFirstList = includedInFareFirstList;
	}

	public List<String> getIncludedInFareSecondList() {
		return includedInFareSecondList;
	}

	public void setIncludedInFareSecondList(List<String> includedInFareSecondList) {
		this.includedInFareSecondList = includedInFareSecondList;
	}

	public List<ItineraryModel> getItinerarySplitFirstList() {
		return itinerarySplitFirstList;
	}

	public void setItinerarySplitFirstList(List<ItineraryModel> itinerarySplitFirstList) {
		this.itinerarySplitFirstList = itinerarySplitFirstList;
	}

	public List<ItineraryModel> getItinerarySplitSecondList() {
		return itinerarySplitSecondList;
	}

	public void setItinerarySplitSecondList(List<ItineraryModel> itinerarySplitSecondList) {
		this.itinerarySplitSecondList = itinerarySplitSecondList;
	}

}
