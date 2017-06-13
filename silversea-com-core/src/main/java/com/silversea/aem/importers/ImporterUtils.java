package com.silversea.aem.importers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.constants.TemplateConstants;
import com.silversea.aem.helper.StringHelper;

public class ImporterUtils {

	static final private Logger LOGGER = LoggerFactory.getLogger(ImporterUtils.class);

	/**
	 * Save session
	 * 
	 * @param session
	 *            : session to save
	 * @param isRefresh:
	 *            boolean indicates if we refresh session
	 * @throws RepositoryException:
	 *             throw a repository exception
	 */
	public static void saveSession(Session session, boolean isRefresh) throws RepositoryException {
		try {
			if (session.hasPendingChanges()) {
				session.save();
			}
		} catch (RepositoryException e) {
			session.refresh(isRefresh);
		}
	}

	/**
	 * Save modification date
	 * 
	 * @param page
	 *            : modified resource
	 * @throws RepositoryException:
	 *             throw a repository exception
	 */
	public static void saveUpdateDate(Page page) throws RepositoryException {
		if (page != null) {
			Node node = page.getContentResource().adaptTo(Node.class);
			node.setProperty("lastModificationDate", Calendar.getInstance());
		}
	}

	/**
	 * Retrieve the last modification date of a page
	 * 
	 * @param page:
	 *            modified page
	 * @return lastModificationDate : modification date
	 */
	public static String getLastModificationDate(Page page) {
		String lastModificationDate = null;
		if (page != null) {
			Resource resource = page.adaptTo(Resource.class);
			if (resource != null && !resource.equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Date date = page.getContentResource().getValueMap().get("lastModificationDate", Date.class);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				lastModificationDate = formatter.format(date);
			}
		}
		return lastModificationDate;
	}

	/**
	 * Find a resource in the repository by its id
	 * 
	 * @param type:
	 *            resource's type
	 * @param property:
	 *            id's label
	 * @param id:
	 *            id's value
	 * @param resourceResolver:
	 *            resource resolver
	 * @return iterator of resources
	 */
	public static Iterator<Resource> findResourceById(String path, String type, String property, String id,
			ResourceResolver resourceResolver) {

		Map<String, String> queryMap = new HashMap<String, String>();
		// check if resource is a page or a node
		property = StringUtils.equals(type, NameConstants.NT_PAGE) ? "jcr:content/" + property : property;
		queryMap.put("type", type);
		queryMap.put("property", property);
		queryMap.put("id", id);
		String queryTemplate = path + "//element(*,${type})[${property}=\'${id}\']";
		StrSubstitutor substitutor = new StrSubstitutor(queryMap);

		// Execute query
		Iterator<Resource> resources = resourceResolver.findResources(substitutor.replace(queryTemplate), "xpath");

		return resources;
	}

	/**
	 * Create a new page or adapt it from a resource
	 * 
	 * @param resources:
	 *            resource to adapt
	 * @param template:
	 *            page's template
	 * @param RootPage:
	 *            page parent
	 * @param title:
	 *            page's title
	 * @param pageManager:
	 *            page manager
	 * @throws WCMException
	 * @throws RepositoryException
	 * @return a page
	 */
	public static Page adaptOrCreatePage(Iterator<Resource> resources, String template, Page RootPage, String title,
			PageManager pageManager) throws WCMException, RepositoryException {

		Page page = null;
		if (resources.hasNext()) {
			page = resources.next().adaptTo(Page.class);
		} else {
			page = pageManager.create(RootPage.getPath(), JcrUtil.createValidChildName(RootPage.adaptTo(Node.class),
					StringHelper.getFormatWithoutSpecialCharcters(title)), template, title, false);
		}

		return page;
	}

	/**
	 * Create a new node or adapt it from a resource
	 * 
	 * @param resources:
	 *            resource to adapt
	 * @param rootNode:
	 *            node's parent
	 * @param nodeName:
	 *            node's name
	 * @throws RepositoryException
	 * @return a node
	 */
	public static Node adaptOrCreateNode(Iterator<Resource> resources, Node rootNode, String nodeName)
			throws RepositoryException {
		Node node = null;
		if (resources.hasNext()) {
			node = resources.next().adaptTo(Node.class);
		} else {
			node = rootNode.addNode(nodeName);
			node.setPrimaryType(JcrConstants.NT_UNSTRUCTURED);
		}

		return node;
	}

	/**
	 * Find or create a node by it's name
	 * 
	 * @param element:
	 *            node
	 * @param name:
	 *            node's name
	 * @throws RepositoryException
	 * @return a node
	 */
	public static Node findOrCreateNode(Node element, String name) throws RepositoryException {
		Node node = null;

		if (element.hasNode(name)) {
			node = element.getNode(name);
		} else {
			node = element.addNode(name);
			node.setPrimaryType(JcrConstants.NT_UNSTRUCTURED);
		}
		return node;
	}

	/**
	 * Find a reference path
	 * 
	 * @param property:
	 *            id's label
	 * @param id:
	 *            id's value
	 * @param resourceResolver:
	 *            resource resolver
	 * @return a path
	 */
	public static String findReference(String path, String property, String id, ResourceResolver resourceResolver) {

		Iterator<Resource> res = findResourceById(path, NameConstants.NT_PAGE, property, id, resourceResolver);
		String reference = res.hasNext() ? res.next().getPath() : "";

		return reference;
	}

	/**
	 * Add mixinTypes type to a page
	 * 
	 * @param page
	 * @throws RepositoryException
	 */
	public static void addMixin(Page page, String mixinType) throws RepositoryException {
		if (page != null) {
			Node node = page.adaptTo(Node.class);
			Node jcrContent = node.getNode(JcrConstants.JCR_CONTENT);
			jcrContent.addMixin(mixinType);
		}
	}

	public static void clean(Page page, String nodeName, Session session) throws RepositoryException {
		if (page != null) {
			Resource resource = page.adaptTo(Resource.class);
			if (resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)) {
				Resource child = resource.getChild(nodeName);
				if (child != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(child)) {
					LOGGER.debug("Remove node {}", page.getPath());
					Node node = child.adaptTo(Node.class);
					node.remove();
					// Persist data
					ImporterUtils.saveSession(session, false);
				}
			}
		}
	}

	public static List<Page> findPagesById(Iterator<Resource> resources) {
		List<Page> pages = new ArrayList<Page>();
		while (resources != null && resources.hasNext()) {
			Page page = resources.next().adaptTo(Page.class);
			pages.add(page);
		}
		return pages;
	}

	public static List<Page> createPagesLanguageCopies(PageManager pageManager, ResourceResolver resourceResolver, Page rootPage,
			List<String> local, String templatePath, String pageName) {
		Page page = null;
		String root;
		Page rootPageLocal;
		List<Page> allPages = new ArrayList<Page>();

		for (String loc : local) {
			// if(loc!="en"){
			root = StringUtils.replace(rootPage.getPath(), "/en/", "/" + loc + "/");
			// }
			rootPageLocal = resourceResolver.getResource(root).adaptTo(Page.class);
			try {
				page = pageManager.create(root,
						JcrUtil.createValidChildName(rootPageLocal.adaptTo(Node.class),
								StringHelper.getFormatWithoutSpecialCharcters(pageName)),
						templatePath, StringHelper.getFormatWithoutSpecialCharcters(pageName), false);
			} catch (WCMException | RepositoryException e) {
				e.printStackTrace();
			}
			allPages.add(page);
		}
		return allPages;
	}

	public static List<String> finAllLanguageCopies(ResourceResolver resourceResolver) {
		Page page;
		List<String> locales = new ArrayList<String>();
		Iterator<Resource> listLocal = resourceResolver.getResource(ImportersConstants.SILVERSEA_ROOT).listChildren();
		while (listLocal != null && listLocal.hasNext()) {
			page = listLocal.next().adaptTo(Page.class);
			locales.add(page.getName());
		}
		return locales;

	}

}
