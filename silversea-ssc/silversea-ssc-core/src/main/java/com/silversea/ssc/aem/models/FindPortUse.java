package com.silversea.ssc.aem.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.helper.RunModesHelper;
import com.silversea.aem.models.CruiseModelLight;
import com.silversea.aem.models.PortItem;
import com.silversea.aem.models.PortModelLight;
import com.silversea.aem.services.CruisesCacheService;
import com.silversea.ssc.aem.bean.PortDestinationRelationBean;
import com.silversea.ssc.aem.bean.PortGlossaryBean;
import com.silversea.ssc.aem.bean.RelatedDestination;
import com.silversea.ssc.aem.bean.WrapperBean;

/**
 * This class is for driving the port and destination search on the find-a-port
 * page and its subpages.
 * 
 * @author nikhil
 *
 */
public class FindPortUse extends WCMUsePojo {

	// list of all the cruises available for the lang
	private List<CruiseModelLight> allCruises = new ArrayList<>();

	// list of all the ports
	private List<PortItem> allPortsList = new LinkedList<>();

	// ports root page
	private Page portsRootPage;

	// list of port results to display (title and country)
	private List<PortGlossaryBean> portsGlossary = new LinkedList<>();
	
	// list of all the destination
	private Set<RelatedDestination> allDestinationSet = new TreeSet<>(new Comparator<RelatedDestination>() {
		public int compare(RelatedDestination obj1, RelatedDestination obj2) {
			return obj1.getDestName().compareTo(obj2.getDestName());
		}
	});

	private List<String> portPaths;

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PortUse.class);

	private String jsonString;

	private List<String> portFilters = new ArrayList<String>(
			Arrays.asList("date-line-lose-a-day", "date-line-gain-a-day", "day-at-sea"));;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.silversea.aem.components.editorial.FindYourCruiseUse#activate()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	public void activate() throws Exception {
		final String lang = LanguageHelper.getLanguage(getCurrentPage());
		final CruisesCacheService cruisesCacheService = getSlingScriptHelper().getService(CruisesCacheService.class);
		if (cruisesCacheService != null) {
			allCruises = cruisesCacheService.getCruises(lang);
		}
		// Security adaptation : If voyage is in the past - do not display them
		List<CruiseModelLight> newAllCruises = new ArrayList<>();
		for (CruiseModelLight cruise : allCruises) {
			if (cruise.getStartDate().after(Calendar.getInstance())) {
				newAllCruises.add(cruise);
			}
		}
		allCruises = newAllCruises;
		LOGGER.debug("Setting up cruises for extracting port-destionation relationship..");

		// setting port and destinations association
		List<PortModelLight> portsList = cruisesCacheService.getPorts(lang);
		List<Object> data = new ArrayList<>();
		WrapperBean wrapperBean = new WrapperBean();
		Iterator<PortModelLight> iterator = portsList.iterator();
		while (iterator.hasNext()) {
			PortDestinationRelationBean relationItem = new PortDestinationRelationBean();
			PortModelLight port = iterator.next();

			if (!portFilters.contains(port.getName())) {
				relationItem.setPortNodeId(port.getName());
				relationItem.setPortCityId(String.valueOf(port.getCityId()));
				relationItem.setPortName(port.getTitle());
				LOGGER.debug("The full URL is :- {}", port.getPath());
				if(getSlingScriptHelper().getService(SlingSettingsService.class).getRunModes().contains("publish")){
					LOGGER.debug("The resolved URL is : - {}", getResourceResolver().map(port.getPath()));
					relationItem.setPortPath(getResourceResolver().map(port.getPath()));
				} else {
					relationItem.setPortPath(port.getPath());
				}
				Set<RelatedDestination> destinationsSet = new HashSet<>();
				for (final CruiseModelLight cruise : allCruises) {
					setPortPaths(cruise.getPortPaths());
					Iterator<String> portPathIterator = getPortPaths().iterator();
					while (portPathIterator.hasNext()) {
						if (port.getPath().equals(portPathIterator.next())) {
							// capitalizing to keep any destination with small
							// case initial, in sorted order.
							destinationsSet.add(
									new RelatedDestination(StringUtils.capitalize(cruise.getDestination().getTitle()),
											cruise.getDestination().getName(), cruise.getDestinationId()));
							break;
						}
					}
				}
				if (!destinationsSet.isEmpty()) {
					List<RelatedDestination> destinations = new ArrayList<>(destinationsSet);
					// internally sorting the destinations.
					Collections.sort(destinations, new Comparator<RelatedDestination>() {
						public int compare(RelatedDestination obj1, RelatedDestination obj2) {
							return obj1.getDestName().compareTo(obj2.getDestName());
						}
					});
					relationItem.setRelatedDestinations(destinations);
					data.add(relationItem);
				}
			}
		}
		// Sorting the final result set.
		Collections.sort(data, new Comparator<Object>() {
			public int compare(Object obj1, Object obj2) {
				PortDestinationRelationBean newObj1 = (PortDestinationRelationBean) obj1;
				PortDestinationRelationBean newObj2 = (PortDestinationRelationBean) obj2;
				return newObj1.getPortName().compareTo(newObj2.getPortName());
			}
		});

		// check if the json is requested
		if (getRequest().getRequestPathInfo().getSelectors().length > 0 && ("portdestinations").equals(getRequest().getRequestPathInfo().getSelectors()[0])) {
			LOGGER.debug("Generating JSON");
			wrapperBean.setData(data);
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			setJsonString(gson.toJson(wrapperBean, WrapperBean.class));

		} else {
			final String portReference = getProperties().get("portReference", String.class);
			// get ports with searched letter (children of the portReference
			// page)
			// if portReference is empty, return the children of the current
			// page by
			// default
			final Page portIndexPage = portReference != null ? getPageManager().getPage(portReference)
					: getCurrentPage();
			// final Iterator<Page> resultsPageList =
			// portIndexPage.listChildren();
			String index = StringUtils.capitalize(portIndexPage.getName());

			// For preparing the HTML view of ports, destination drop down and
			// the glossary listing
			LOGGER.debug("Preparing the HTML views of the drop downs");
			for (Object object : data) {
				PortDestinationRelationBean port = (PortDestinationRelationBean) object;

				if (port.getPortName().startsWith(index)) {
					StringBuilder sb = new StringBuilder();
					String result = StringUtils.EMPTY;
					Iterator<RelatedDestination> desItr = port.getRelatedDestinations().iterator();
					while (desItr.hasNext()) {
						RelatedDestination relDes = desItr.next();
						sb.append(relDes.getDestName()).append(",");
					}
					if (sb.length() > 0) {
						result = sb.deleteCharAt(sb.length() - 1).toString();
					}
					portsGlossary.add(new PortGlossaryBean(port.getPortName(), port.getPortPath(), result));
				}
				allPortsList.add(new PortItem(port.getPortNodeId(), port.getPortName()));
				allDestinationSet.addAll(port.getRelatedDestinations());
			}

			// set the parent page - the parent of all the ports list (letter)
			// pages
			// if portReference is empty, the parent of the current page, if
			// not,
			// the parent of the configured portReference page
			portsRootPage = portIndexPage.getParent();
		}
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<PortItem> getPortsList() {
		return allPortsList;
	}

	public Set<RelatedDestination> getDestinationSet() {
		return allDestinationSet;
	}

	public Page getPortsRootPage() {
		return portsRootPage;
	}

	public List<Page> getOrderedAlphabetPages() {
		final List<Page> alphabetList = new ArrayList<>();
		final Iterator<Page> alphabetIterator = getPortsRootPage().listChildren();

		alphabetIterator.forEachRemaining(alphabetList::add);
		alphabetList.sort(Comparator.comparing(p -> p.getTitle().toUpperCase()));

		return alphabetList;
	}
	public List<PortGlossaryBean> getPortsGlossary() {
		return portsGlossary;
	}

	public List<String> getPortPaths() {
		return portPaths;
	}

	public void setPortPaths(List<String> portPaths) {
		this.portPaths = portPaths;
	}
}
