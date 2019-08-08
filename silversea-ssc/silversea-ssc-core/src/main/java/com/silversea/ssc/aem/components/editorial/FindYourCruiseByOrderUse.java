package com.silversea.ssc.aem.components.editorial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.models.DestinationModel;

public class FindYourCruiseByOrderUse extends AbstractGeolocationAwareUse {

	private static final Logger log = LoggerFactory.getLogger(FindYourCruiseByOrderUse.class);

	private List<DestinationModel> destinations = new LinkedList<DestinationModel>();

	private String destinationsOrder;
	
	@Override
	public void activate() throws Exception {
		super.activate();
		String destPageRoot = (String) getProperties().get("destinationsReference", String.class);

		if (destPageRoot != null) {
			Resource res = getResourceResolver().resolve(destPageRoot);
			// Retrieve all destinations
			if (res != null) {
				Page rootPage = res.adaptTo(Page.class);
				Iterator<Page> it = rootPage.listChildren(null, false);

				while (it.hasNext()) {
					Page currentPage = it.next();
					String currentTemplate = currentPage.getProperties().get("cq:template", "");
					if (currentTemplate.equals("/apps/silversea/silversea-com/templates/destination")) {
						DestinationModel destModel = currentPage.adaptTo(DestinationModel.class);
						destinations.add(destModel);
					}
				}
			}
			
			destinationsOrder = selectDestinationsOrderByMarket();
			
			if (StringUtils.isNotEmpty(destinationsOrder)) {
				destinations = applyCustomOrder(this.destinations, destinationsOrder);
			}
			
			log.debug("Found {} destinations", destinations.size());
		}
		
	}
	
	/**
	 * @return destinationsOrder: user destinations order by market
	 */
	private String selectDestinationsOrderByMarket() {
		String destinationsOrder = null;
		String keyDestinationByMarket = "destinationsOrder";
		
		if (geomarket != null) {
			switch (geomarket) {
			case "eu":
				keyDestinationByMarket = "destinationsOrderEU";
				break;
			case "as":
				keyDestinationByMarket = "destinationsOrderAS";
				break;
			case "uk":
				keyDestinationByMarket = "destinationsOrderUK";
				break;
			default:
				keyDestinationByMarket = "destinationsOrder";
				break;
			}
		}
		
		if (getProperties().get(keyDestinationByMarket, String.class) != null ) {
			//specific component order
			destinationsOrder = getProperties().get(keyDestinationByMarket, String.class);
		} else {
			//global order
			destinationsOrder = getCurrentStyle().get(keyDestinationByMarket, String.class);
		}
		
		return destinationsOrder;
	}

	/**
	 * @param originalList
	 *            List to order
	 * @param orderToApply
	 *            Custom order to apply
	 * @return sorted destination list
	 */
	private List<DestinationModel> applyCustomOrder(List<DestinationModel> originalList, String orderToApply) {
		log.info("Processing user order...");

		List<DestinationModel> destinationsWithOrder = new LinkedList<DestinationModel>();
		Map<Integer, DestinationModel> destinationTmp = new HashMap<Integer, DestinationModel>();
		// initialization needed to start with 0 in the list (incrementAndGet)
		AtomicInteger counter = new AtomicInteger(-1);

		/*
		 * Parsing custom order to create a Map. Key: destination id, value:
		 * position
		 */
		String[] keys = orderToApply.trim().split(";");
		Map<String, Integer> positionMap = Arrays.asList(keys).stream()
				.collect(Collectors.toMap((c) -> c, (c) -> counter.incrementAndGet()));

		/*
		 * Create a Map that represent the sorted destination list. Not use
		 * classic List for IndexOutOfBoundsException exception
		 */
		originalList.forEach((item) -> {
			String destId = item.getDestinationId();

			if (positionMap != null && positionMap.containsKey(destId)) {
				int key = positionMap.get(destId);
				destinationTmp.put(key, item);
			}
		});

		// Create the result sorted list
		destinationTmp.forEach((k, v) -> {
			destinationsWithOrder.add(v);
		});

		return destinationsWithOrder;
	}

	/**
	 * @return destinations available for all cruises for this lang
	 */
	public List<DestinationModel> getDestinations() {
		return destinations;
	}
	
	public String getDestinationsOrder() {
		return destinationsOrder;
	}
}
