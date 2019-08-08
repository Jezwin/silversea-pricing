package com.silversea.ssc.aem.models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.ExcursionModel;
import com.silversea.aem.models.HotelModel;
import com.silversea.aem.models.LandProgramModel;
import com.silversea.aem.models.PortItem;
import com.silversea.aem.models.PortModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.ssc.aem.bean.SliderBean;
import com.silversea.ssc.aem.bean.SummaryBean;
import com.silversea.ssc.aem.constants.SSCConstants;
import com.silversea.ssc.aem.factory.SSCConfigFactory;
import com.silversea.ssc.aem.utils.SSCUtils;

public class PortUse extends com.silversea.aem.components.page.PortUse {

	public enum PortType {
		classic, expedition, mixed
	}

	// list of all the cruises available for the lang
	private List<CruiseModelLight> allCruises = new ArrayList<>();

	// default port type.
	private String portType = PortType.classic.toString();
	private String brochureSelector = "";
	private int cruiseCounter;
	private int excursionCounter;
	private int landProgrammesCounter;
	private SliderBean portGenericBlockBean;
	private SummaryBean summaryBean = new SummaryBean();
	private int counter;
	private List<ExcursionModel> filteredExcursions = new ArrayList<>();
	private List<LandProgramModel> filteredLand = new ArrayList<>();
	private List<HotelModel> filteredHotels = new ArrayList<>();
	private List<PortModelLight> relatedPorts = new ArrayList<>();
	private List<String> relatedPortsNames = new ArrayList<>();
	private List<String> relatedPortsNamesSorted = new ArrayList<>();

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PortUse.class);
	
	private static final int RELATEDPORTSMAXCOUNT = 9;
	
	private List<String> portFilters = new ArrayList<String>(
			Arrays.asList("date-line-lose-a-day", "date-line-gain-a-day", "day-at-sea"));
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.silversea.aem.components.editorial.FindYourCruiseUse#activate()
	 */
	@Override
	public void activate() throws Exception {
		super.activate();
		final String lang = LanguageHelper.getLanguage(getCurrentPage());
		final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
		if (cruisesCacheService != null) {
			allCruises = cruisesCacheService.getCruises(lang);
		}
		 // Security adaptation : If voyage is in the past - do not display them
        List<CruiseModelLight> newAllCruises = new ArrayList<>();
        for(CruiseModelLight cruise : allCruises){
			if(cruise.getStartDate().after(Calendar.getInstance())){
				newAllCruises.add(cruise);
			}
		}
        allCruises = newAllCruises;
        
        String primaryDescription = getPortModel().getRealDescription();
        if (primaryDescription != null && primaryDescription.length() > 0 && primaryDescription.length() < 120){
        	this.summaryBean.setFirstPart(primaryDescription);
        	this.summaryBean.setRemainingPart("");
        } else if (primaryDescription != null && primaryDescription.length() > 90 ){
        	setSummaryBean(SSCUtils.getExcerpt(primaryDescription,90));
        	if (primaryDescription.compareToIgnoreCase(getPortModel().getApiDescription()) > 10){
        		//String combinedDescription = new StringBuilder(this.summaryBean.getRemainingPart()).append(getPortModel().getApiDescription()).toString();
        		//this.summaryBean.setRemainingPart(combinedDescription);
        	 } else {
        		// this.summaryBean.setRemainingPart(getPortModel().getApiDescription());
        	 }
        } else {
        	setSummaryBean(SSCUtils.getExcerpt(getPortModel().getApiDescription(),90));
        }	
        
        
        
		// setting port type and populating related ports
		Set<String> cruiseTypes = new HashSet<>();
		String currentPortName = getCurrentPage().getName();
		for (final CruiseModelLight cruise : allCruises) {
			boolean portInItinerary = false;
			ListIterator<PortItem> portIterator = cruise.getPorts().listIterator();
						
			while (portIterator.hasNext()) {
				PortItem port = portIterator.next();
				if (port.getName().equals(currentPortName)) {
					// add port Names from current itinerary
					if (counter < RELATEDPORTSMAXCOUNT){
						addPortNames(cruise.getPorts(), currentPortName);
					}
					
					portInItinerary = true;
					counter++;
					// break from current while loop, as we have the required information
					break;
				}
			}
			if (portInItinerary) {
				cruiseTypes.add(cruise.getCruiseType());
			}
		}
		if (cruiseTypes.size() == 1 && cruiseTypes.iterator().next().contains(PortType.expedition.toString())) {
			setPortType(PortType.expedition.toString());
		} else if (cruiseTypes.size() > 1) {
			setPortType(PortType.mixed.toString());
		}
		
		LOGGER.debug("The port type is set to : - {}", getPortType());
		
		
		//cleanup relatedPortsNames to contain RELATEDPORTSMAXCOUNT number of entries, based on highest count
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (String temp : relatedPortsNames) {
			Integer count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}
		
		Map <String, Integer> sortedMap = SSCUtils.sortByValue(map);
		
		int count = 0;		  
		for (Map.Entry<String, Integer> entry: sortedMap.entrySet()) {
			if (count >= RELATEDPORTSMAXCOUNT){
				break;
			} else {
				relatedPortsNamesSorted.add(entry.getKey());
			}
			count++;
		}
		
		
		// populate relatedPorts list based on related city names in relatedPortsNames
		List<PortModelLight> portsList = cruisesCacheService.getPorts(lang);
		Iterator<PortModelLight> portListIterator = portsList.iterator();
		while (portListIterator.hasNext()) {
			PortModelLight port = portListIterator.next();
			
			if (relatedPortsNamesSorted.contains(port.getName()) && relatedPorts.size() < RELATEDPORTSMAXCOUNT){
				relatedPorts.add(port);
			}
		}
		
		// setting counters
		setCruiseCounter(counter);
		setExcursionCounter(getPortModel().getExcursions().size());
		setLandProgrammesCounter(getPortModel().getLandPrograms().size());
		
		LOGGER.debug("The counters are (in order cruise, excursions, land programmes) : - {} {} {}", getCruiseCounter(), getExcursionCounter(), getLandProgrammesCounter());
		
		// fetch generic block data
		String configurationPath = getSlingScriptHelper().getService(SSCConfigFactory.class)
				.getPropertyValue(SSCConstants.PORT_GEN_BLCK_CONFIG_PATH);
		LOGGER.debug("The configuration path for port generic block is: - {}", configurationPath);
		
		if (StringUtils.isNotEmpty(configurationPath)) {
			configurationPath = new MessageFormat(configurationPath)
					.format(new String[]{LanguageHelper.getLanguage(getCurrentPage())});
			LOGGER.debug("The language specific configuration path for port generic block is: - {}", configurationPath);
			Resource parResource = getResourceResolver().getResource(configurationPath).adaptTo(Page.class)
					.getContentResource();
			Iterator<Resource> resIterator = parResource.listChildren();
			while (resIterator.hasNext()) {
				Resource child = resIterator.next();
				LOGGER.debug("Checking port type match for: - {}", child.getPath());
				if (getPortType().equalsIgnoreCase(child.getValueMap().get("portType", String.class))) {
					portGenericBlockBean = new SliderBean();
					List<Asset> sliderItems = new ArrayList<>();

					final String[] items = child.getValueMap().get("galleryItem", String[].class);
					for (String item : items) {
						JSONObject jsonObject = new JSONObject(item);

						String reference = jsonObject.getString("assetReference");
						LOGGER.debug("The asset reference is :- {}", reference);
						
						// for initial release, add only images to the slider items
						if (null != getResourceResolver().getResource(reference)
								&& null != getResourceResolver().getResource(reference).adaptTo(Asset.class)
								&& getResourceResolver().getResource(reference).adaptTo(Asset.class).getMimeType()
										.contains("image")) 
						{
							sliderItems.add(getResourceResolver().getResource(reference).adaptTo(Asset.class));
						}
					}
					LinkedHashMap<String, List<Asset>> modalItems;
					modalItems = new LinkedHashMap<>();
					modalItems.put("port", sliderItems);
					
					portGenericBlockBean.setSliderItems(sliderItems);
					portGenericBlockBean.setModalItems(modalItems);
					portGenericBlockBean
							.setShowFirstItemOnly(child.getValueMap().get("showFirstItemOnly", Boolean.class));
					portGenericBlockBean.setDescription(StringEscapeUtils.unescapeHtml4(child.getValueMap().get("description", String.class)));

				}
			}
		}
		// set the brochure selector
		Resource currentResource = getCurrentPage().getContentResource();
		if (null != currentResource) {
			long cityId = currentResource.getValueMap().get("cityId", Long.class);
			String brochureBasePath = getSlingScriptHelper().getService(SSCConfigFactory.class)
					.getPropertyValue(SSCConstants.BROCHURE_GROUP_TAGS_LOCATION);
			LOGGER.debug("The brochure base path is :- {}", brochureBasePath);
			if (StringUtils.isNotEmpty(brochureBasePath)) {
				Resource parentResource = getResourceResolver().getResource(brochureBasePath);
				if (null != parentResource) {
					Iterator<Resource> iterator = parentResource.listChildren();
					while (iterator.hasNext()) {
						Resource resource = iterator.next();
						if (null != resource) {
							LOGGER.debug("Checking for resource:- {}", resource.getPath());
							String[] ports = resource.getValueMap().get("ports", String[].class);
							if (ArrayUtils.contains(ports, String.valueOf(cityId))) {
								LOGGER.debug("Match found for cityId on tag resource:- {} - {}", cityId, resource.getPath());
								String brochureSelectorFull = new StringBuilder(".brochure_group_").append(resource.getName()).toString();
								setBrochureSelector(brochureSelectorFull);
								break;
							}
						}
					}
				}
			}
		}
		
		
		// filter out duplicates from excursions, land and hotels
		Map<String, ExcursionModel> excursiondMap = new LinkedHashMap<String, ExcursionModel>();
		for (ExcursionModel excursionItem : getPortModel().getExcursions()) {
			excursiondMap.put(excursionItem.getTitle(), excursionItem);
		}
		getFilteredExcursions().addAll(excursiondMap.values());
		
		Map<String, LandProgramModel> landMap = new LinkedHashMap<String, LandProgramModel>();
		for (LandProgramModel landItem : getPortModel().getLandPrograms()) {
		  landMap.put(landItem.getTitle(), landItem);
		}
		getFilteredLand().addAll(landMap.values());
				
		Map<String, HotelModel> hotelMap = new LinkedHashMap<String, HotelModel>();
		for (HotelModel hotelItem : getPortModel().getHotels()) {
			hotelMap.put(hotelItem.getName(), hotelItem);
		}
		getFilteredHotels().addAll(hotelMap.values());
	}
	
	private void addPortNames(List<PortItem> portList, String currentPortName){
		ListIterator<PortItem> portListIterator = portList.listIterator();
		while (portListIterator.hasNext()) {
			if (portListIterator.hasNext()){
				String portNameCheck = portListIterator.next().getName();
				if (checkPortName(currentPortName, portNameCheck)){
					relatedPortsNames.add(portNameCheck);
				} 	
			}
		}
	}

	private boolean checkPortName(String currentPortName, String portNameCheck) {
		if (currentPortName.equals(portNameCheck)) {
			return false;
		}
		
		if (portFilters.contains(portNameCheck)) {
			return false;
		}
		return true;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
    Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
        @Override
        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
            return (o1.getValue()).compareTo(o2.getValue());
        }
    });

    Map<K, V> result = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list) {
        result.put(entry.getKey(), entry.getValue());
    }
    return result;
}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public int getCruiseCounter() {
		return cruiseCounter;
	}

	public void setCruiseCounter(int cruiseCounter) {
		this.cruiseCounter = cruiseCounter;
	}

	public SummaryBean getSummaryBean() {
		return summaryBean;
	}

	public void setSummaryBean(SummaryBean summaryBean) {
		this.summaryBean = summaryBean;
	}

	public String getBrochureSelector() {
		return brochureSelector;
	}

	public void setBrochureSelector(String brochureSelector) {
		this.brochureSelector = brochureSelector;
	}

	public SliderBean getPortGenericBlockBean() {
		return portGenericBlockBean;
	}

	public int getExcursionCounter() {
		return excursionCounter;
	}

	public void setExcursionCounter(int excursionCounter) {
		this.excursionCounter = excursionCounter;
	}

	public int getLandProgrammesCounter() {
		return landProgrammesCounter;
	}

	public void setLandProgrammesCounter(int landProgrammesCounter) {
		this.landProgrammesCounter = landProgrammesCounter;
	}

	public List<ExcursionModel> getFilteredExcursions() {
		return filteredExcursions;
	}

	public void setFilteredExcursions(List<ExcursionModel> filteredExcursions) {
		this.filteredExcursions = filteredExcursions;
	}

	public List<LandProgramModel> getFilteredLand() {
		return filteredLand;
	}

	public void setFilteredLand(List<LandProgramModel> filteredLand) {
		this.filteredLand = filteredLand;
	}

	public List<HotelModel> getFilteredHotels() {
		return filteredHotels;
	}

	public void setFilteredHotels(List<HotelModel> filteredHotels) {
		this.filteredHotels = filteredHotels;
	}

	public List<PortModelLight> getRelatedPorts() {
		return relatedPorts;
	}

	public void setRelatedPorts(List<PortModelLight> relatedPorts) {
		this.relatedPorts = relatedPorts;
	}

	public int getRelatedPortsCount() {
		return relatedPorts.size();
	}
}
