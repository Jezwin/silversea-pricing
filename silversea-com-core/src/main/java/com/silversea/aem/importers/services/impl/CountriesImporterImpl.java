package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.ImporterUtils;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.CountriesImporter;
import com.silversea.aem.services.ApiCallService;
import com.silversea.aem.services.ApiConfigurationService;

import io.swagger.client.ApiException;
import io.swagger.client.model.Country;

@Component(immediate = true, label = "Silversea.com - Contries importer")
@Service(value = CountriesImporter.class)
public class CountriesImporterImpl implements CountriesImporter {

	static final private Logger LOGGER = LoggerFactory.getLogger(CountriesImporterImpl.class);
	private static final String GEOTAGGING_PATH = "/etc/tags/geotagging";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private ApiConfigurationService apiConfig;

	@Reference
	private QueryBuilder builder;

	@Reference
	private Replicator replicat;

	@Reference
	private ApiCallService apiCallService;

	private ResourceResolver resourceResolver;
	private Session session;

	public void init() {
		try {
			Map<String, Object> authenticationPrams = new HashMap<String, Object>();
			authenticationPrams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationPrams);
			session = resourceResolver.adaptTo(Session.class);
		} catch (LoginException e) {
			LOGGER.debug("Contries importer login exception ", e);
		}
	}

	@Override
	public void importData() throws IOException {
		init();
		try {
			List<Country> countries;
			countries = apiCallService.getContries();
			int j = 0;
			for (Country country : countries) {
				LOGGER.debug("Importing Country: {}", country.getCountryName());
				Map<String, String> map = new HashMap<>();
				Iterator<Node> nodes = null;
				map.put(WcmConstants.SEARCH_KEY_PATH, GEOTAGGING_PATH);
				map.put(WcmConstants.SEARCH_KEY_TYPE, WcmConstants.DEFAULT_KEY_CQ_TAG);
				map.put(WcmConstants.SEARCH_NODE_NAME, country.getCountryIso2());
				Query query = builder.createQuery(PredicateGroup.create(map), session);
				SearchResult searchResult = query.getResult();
				nodes = searchResult.getNodes();
				// Set result to current node
				while (nodes.hasNext()) {
					Node node = nodes.next();
					if (node.getDepth() == 6) {
						if (null != node) {
							node.setProperty("country_id", country.getCountryId());
							node.setProperty("country_url", country.getCountryUrl());
							node.setProperty("country_iso2", country.getCountryIso2());
							node.setProperty("country_iso3", country.getCountryIso3());
							node.setProperty("country_name", country.getCountryName());
							node.setProperty("country_prefix", country.getCountryPrefix());
							node.setProperty("market", country.getMarket());
							node.setProperty("region_id", country.getRegionId());
							session.save();
							if (!replicat.getReplicationStatus(session, node.getParent().getPath()).isActivated()) {
								ImporterUtils.updateReplicationStatus(replicat, session, false, node.getPath());
							}
						}
					}
				}
				j++;

				if (j % 100 == 0) {
					if (session.hasPendingChanges()) {
						try {
							session.save();
						} catch (RepositoryException e) {
							session.refresh(true);
						}
					}
				}
			}
			resourceResolver.close();
		} catch (ApiException | RepositoryException e) {
			LOGGER.error("Exception importing countries", e);
		}
	}

	@Override
	public void importCountry(String id) {

	}

}
