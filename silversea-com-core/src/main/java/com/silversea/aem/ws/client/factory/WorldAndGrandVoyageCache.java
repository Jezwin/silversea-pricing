package com.silversea.aem.ws.client.factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.models.CruiseModel;
import com.silversea.aem.models.CruiseModelLight;

public class WorldAndGrandVoyageCache {

	static final private Logger LOGGER = LoggerFactory.getLogger(WorldAndGrandVoyageCache.class);

	private static WorldAndGrandVoyageCache instance;
	private static ResourceResolver resourceResolver;
	private Map<String, Map<String, CruiseModelLight>> cache;

	private WorldAndGrandVoyageCache() {
	}

	public static WorldAndGrandVoyageCache getInstance(ResourceResolver resolverImport) {
		if (instance == null) {
			instance = new WorldAndGrandVoyageCache();
			resourceResolver = resolverImport;
			instance.buildCacheByResource("world-cruises");
			instance.buildCacheByResource("grand-voyages-cruise");
		}
		return instance;
	}

	public Map<String, Map<String, CruiseModelLight>> getCache() {
		return cache;
	}

	private void buildCacheByResource(String type) {
		LOGGER.info("Starting create wc and gv cache by resource...");
		if (this.cache == null) {
			this.cache = new HashMap<>();
		}
		if (resourceResolver == null) {
			return;
		}
		try {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page rootPage = pageManager.getPage("/content/silversea-com/en/destinations/" + type);
			Iterator<Page> children = rootPage.listChildren();
			while (children.hasNext()) {
				loopAllCruises(children.next());
			}

		} catch (Exception e) {
			LOGGER.error("Error during get resources from destination/{}{}", type, e);
		}
		LOGGER.info("Finish create wc and gv cache resource...");
	}

	private void loopAllCruises(Page rootPage) {
		Iterator<Page> children = rootPage.listChildren();
		CruiseModel cruiseModel = null;
		Map<String, CruiseModelLight> list = null;
		CruiseModelLight cruiseModelLight = null;
		String comboCruiseCode = rootPage.getProperties().get("comboCruiseCode", String.class);
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		boolean stopLoop = false;
		while (children.hasNext() && !stopLoop) {
			Page page = children.next();
			LOGGER.debug("Check page{}", page.getName());

			String cruiseReference = page.getProperties().get("cruiseReference", String.class);
			String resourceType = page.getProperties().get("sling:resourceType", String.class);

			if (StringUtils.isNotEmpty(cruiseReference) && StringUtils.isNotEmpty(resourceType) && resourceType.equals("silversea/silversea-com/components/pages/combosegment") ) {
				Page pageCruise = pageManager.getPage(cruiseReference);

				if (pageCruise != null && pageCruise.adaptTo(CruiseModel.class) != null) {
					cruiseModel = pageCruise.adaptTo(CruiseModel.class);
					cruiseModelLight = new CruiseModelLight(cruiseModel);
					if (StringUtils.isNotEmpty(comboCruiseCode)) {
						if (!this.cache.containsKey(comboCruiseCode)) {
							this.cache.put(comboCruiseCode, new HashMap<>());
						}
						list = this.cache.get(comboCruiseCode);
						list.put(cruiseModelLight.getCruiseCode(), cruiseModelLight);
						this.cache.remove(comboCruiseCode);
						this.cache.put(comboCruiseCode, list);
						stopLoop = true;
					}
				}
			}
		}
	}

	private void buildCache(String type) {
		LOGGER.info("Starting create wc and gv cache...");
		if (this.cache == null) {
			this.cache = new HashMap<>();
		}
		if (resourceResolver == null) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("path", "/content/silversea-com/en/destinations/" + type + "/");
		map.put("type", "cq:PageContent");
		map.put("p.limit", "5000");

		QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);

		Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
		SearchResult result = query.getResult();
		if (result.getTotalMatches() > 0) {
			LOGGER.info("Find {} in {}", result.getTotalMatches(), type);

			List<Hit> listResult = result.getHits();
			try {
				PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
				if (pageManager != null) {
					Page page = null;
					CruiseModel cruiseModel = null;
					Map<String, CruiseModelLight> list = null;
					CruiseModelLight cruiseModelLight = null;
					for (Hit hit : listResult) {
						String cruiseReference = hit.getProperties().get("cruiseReference", String.class);
						String resourceType = hit.getProperties().get("sling:resourceType", String.class);
						if (StringUtils.isNotEmpty(cruiseReference) && StringUtils.isNotEmpty(resourceType)
								&& resourceType.equals("silversea/silversea-com/components/pages/combosegment")) {
							page = pageManager.getPage(cruiseReference);
							if (page != null && page.adaptTo(CruiseModel.class) != null) {
								cruiseModel = page.adaptTo(CruiseModel.class);
								cruiseModelLight = new CruiseModelLight(cruiseModel);
								String parentPath = hit.getResource().getParent().getParent().getPath()
										+ "/jcr:content";
								Node nodeParent = resourceResolver.getResource(parentPath).adaptTo(Node.class);
								if (nodeParent.hasProperty("comboCruiseCode")) {
									String comboCruiseCode = nodeParent.getProperty("comboCruiseCode").getString();
									if (!this.cache.containsKey(comboCruiseCode)) {
										this.cache.put(comboCruiseCode, new HashMap<>());
									}
									list = this.cache.get(comboCruiseCode);
									list.put(cruiseModelLight.getCruiseCode(), cruiseModelLight);
									this.cache.remove(comboCruiseCode);
									this.cache.put(comboCruiseCode, list);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error during get resources from destination/{}", type);
			}
		}
		LOGGER.info("Finish create wc and gv cache...");
	}
}
