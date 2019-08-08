package com.silversea.aem.utils;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.LanguageHelper;
import com.silversea.aem.ws.client.factory.WorldAndGrandVoyageCache;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {

	static final private Logger LOGGER = LoggerFactory.getLogger(PathUtils.class);

	/**
	 * Constructor.
	 */
	private PathUtils() {

	}

	/**
	 * @param resource
	 * @param page
	 * @return local path to request quote page path from /conf
	 */
	public static String getRequestQuotePagePath(final Resource resource, final Page page) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null) {
			final Resource requestQuotePageConf = confRes.getItemResource("requestquotepage/page");

			if (requestQuotePageConf != null) {
				final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
						+ requestQuotePageConf.getValueMap().get("reference", String.class);
				if (resource.getResourceResolver().getResource(pagePath) != null) {
					return pagePath;
				}
			}
		}

		return null;
	}

	/**
	 * @param resource
	 * @param page
	 * @return local path to request brochures page path from /conf
	 */
	public static String getBrochuresPagePath(final Resource resource, final Page page) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null) {
			final Resource brochuresPageConf = confRes.getItemResource("brochurespage/page");

			if (brochuresPageConf != null) {
				final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
						+ brochuresPageConf.getValueMap().get("reference", String.class);
				if (resource.getResourceResolver().getResource(pagePath) != null) {
					return pagePath;
				}
			}
		}

		return null;
	}

	public static String getWorldCruisesPagePath(final Resource resource, final Page page) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null) {
			final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
					+ "/destinations/world-cruises";
			if (resource.getResourceResolver().getResource(pagePath) != null) {
				return pagePath;
			}
		}

		return null;
	}

	public static String getGrandVoyagesPagePath(final Resource resource, final Page page) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null) {
			final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page)
					+ "/destinations/grand-voyages-cruise";
			if (resource.getResourceResolver().getResource(pagePath) != null) {
				return pagePath;
			}
		}

		return null;
	}

	public static String getWorldCruisesPagePath(final Resource resource, final Page page, String comboCruiseCode) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null && comboCruiseCode != null) {
			String urlWC = searchPathByComboCruiseCode(resource, comboCruiseCode, "world-cruises", page);

			final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page) + "/destinations/"
					+ urlWC;
			if (resource.getResourceResolver().getResource(pagePath) != null) {
				return pagePath;
			}
		}

		return null;
	}

	public static String getGrandVoyagesPagePath(final Resource resource, final Page page, String comboCruiseCode) {
		final Conf confRes = resource.adaptTo(Conf.class);
		if (confRes != null && page != null) {
			String urlGV = searchPathByComboCruiseCode(resource, comboCruiseCode, "grand-voyages-cruise", page);

			final String pagePath = "/content/silversea-com/" + LanguageHelper.getLanguage(page) + "/destinations/"
					+ urlGV;
			if (resource.getResourceResolver().getResource(pagePath) != null) {
				return pagePath;
			}
		}

		return null;
	}

	private static String searchPathByComboCruiseCode(final Resource resource, String comboCruiseCode, String type,  Page page) {
		String urlResult = type;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("path", "/content/silversea-com/" + LanguageHelper.getLanguage(page) + "/destinations/" + type);
			map.put("1_property", "comboCruiseCode");
			map.put("1_property.value", comboCruiseCode);

			QueryBuilder queryBuilder = resource.getResourceResolver().adaptTo(QueryBuilder.class);

			Query query = queryBuilder.createQuery(PredicateGroup.create(map), resource.getResourceResolver().adaptTo(Session.class));
			SearchResult result = query.getResult();
			if (result.getTotalMatches() > 0) {
				String[] path = (result.getHits().get(0).getPath() != null)
						? result.getHits().get(0).getPath().split("destinations/")
						: null;
				if (path != null) {
					urlResult = path[1].replaceAll("/jcr:content", "");
				}
			}
		} catch (RepositoryException e) {
			LOGGER.error("Error to get world or grand voyage cruise path", e.getMessage());
		}
		return urlResult;
	}

}