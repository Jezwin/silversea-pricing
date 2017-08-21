package com.silversea.aem.importers;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.utils.StringsUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO move to com.silversea.aem.importers.utils package
 */
@Deprecated
public class ImporterUtils {

    static final private Logger LOGGER = LoggerFactory.getLogger(ImporterUtils.class);

    /**
     * Save session
     *
     * @param session session to save
     * @param isRefresh boolean indicates if we refresh session
     *
     * @throws RepositoryException: throw a repository exception
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
     * @param page : modified resource
     *
     * @throws RepositoryException: throw a repository exception
     */
    public static void saveUpdateDate(Page page) throws RepositoryException {
        if (page != null) {
            Node node = page.getContentResource().adaptTo(Node.class);
            node.setProperty("lastModificationDate", Calendar.getInstance());
        }
    }

    /**
     * @param page the page from where to get the date
     * @param propertyName name of the date property to retrieve
     *
     * @return simplified date from property defined by <code>propertyName</code>
     */
    public static String getDateFromPageProperties(final Page page, final String propertyName) {
        if (page != null) {
            Resource pageContentResource = page.getContentResource();

            if (pageContentResource != null) {
                Date date = pageContentResource.getValueMap().get(propertyName, Date.class);

                if (date != null) {
                    return new SimpleDateFormat("yyyyMMdd").format(date);
                }
            }
        }

        return null;
    }

    /**
     * Find a resource in the repository by its id
     *
     * @param type: resource's type
     * @param property: id's label
     * @param id: id's value
     * @param resourceResolver: resource resolver
     *
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
     * @param resources: resource to adapt
     * @param template: page's template
     * @param RootPage: page parent
     * @param title: page's title
     * @param pageManager: page manager
     *
     * @return a page
     *
     * @throws WCMException
     * @throws RepositoryException
     */
    public static Page adaptOrCreatePage(Iterator<Resource> resources, String template, Page RootPage, String title,
            PageManager pageManager) throws WCMException, RepositoryException {

        Page page = null;
        if (resources.hasNext()) {
            page = resources.next().adaptTo(Page.class);
        } else {
            page = pageManager.create(RootPage.getPath(), JcrUtil.createValidChildName(RootPage.adaptTo(Node.class),
                    StringsUtils.getFormatWithoutSpecialCharacters(title)), template, title, false);
        }

        return page;
    }

    /**
     * Create a new node or adapt it from a resource
     *
     * @param resources: resource to adapt
     * @param rootNode: node's parent
     * @param nodeName: node's name
     *
     * @return a node
     *
     * @throws RepositoryException
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
     * @param element: node
     * @param name: node's name
     *
     * @return a node
     *
     * @throws RepositoryException
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
     * @param property: id's label
     * @param id: id's value
     * @param resourceResolver: resource resolver
     *
     * @return a path
     */
    public static String findReference(String path, String property, String id, ResourceResolver resourceResolver) {

        Iterator<Resource> res = findResourceById(path, NameConstants.NT_PAGE, property, id, resourceResolver);
        String reference = res.hasNext() ? res.next().getPath() : null;
        if (reference == null) {
            LOGGER.error("Importer -- Reference to page with id [{} = {}] not found", property, id);
        }
        return reference;
    }

    /**
     * Add mixinTypes type to a page
     *
     * @param page
     *
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
                    LOGGER.debug("Remove node {}", page.getPath() + "/" + nodeName);
                    Node node = child.adaptTo(Node.class);
                    node.remove();
                    // Persist data
                    ImporterUtils.saveSession(session, false);
                }
            }
        }
    }

    public static void copyNode(Page sourcePage, String destPath, String nodeName, Workspace workspace) throws
            RepositoryException {
        if (sourcePage != null) {
            Resource resource = sourcePage.adaptTo(Resource.class);
            if (resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource)) {
                Resource child = resource.getChild(nodeName);
                if (child != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(child)) {
                    LOGGER.debug("copy node {}", child.getPath());
                    Node node = child.adaptTo(Node.class);
                    workspace.copy(node.getPath(), destPath);
                }
            }
        }
    }

    public static Calendar convertToCalendar(DateTime dateTime) {
        Calendar calendar = null;
        if (dateTime != null) {
            calendar = dateTime.toGregorianCalendar();
        }
        return calendar;
    }

    public static String formatDateForSeach(DateTime dateTime) {
        String formattedDate = null;
        if (dateTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(ImportersConstants.DATE_FORMAT_MMM_YYYY);
            formattedDate = formatter.format(dateTime.toDate());
        }
        return formattedDate;
    }

    public static void updateReplicationStatus(Replicator replicator, Session session, Boolean isDeleted, String path) {
        try {
            if (path != null) {
                if (isDeleted) {
                    LOGGER.debug("Unpublish resource with path {}", path);
                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);
                } else {
                    LOGGER.debug("Replicate resource with path {}", path);
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
                }
            }
        } catch (ReplicationException e) {
            LOGGER.error("Replication error", e);
        }
    }

    public static Page findPagesLanguageCopies(PageManager pageManager, ResourceResolver resourceResolver,
            Page rootPage, String local) {
        String root;
        Page rootPageLocal;

        root = StringUtils.replace(rootPage.getPath(), "/en/", "/" + local + "/");
        rootPageLocal = resourceResolver.getResource(root).adaptTo(Page.class);

        return rootPageLocal;
    }

    @Deprecated
    public static List<String> finAllLanguageCopies(ResourceResolver resourceResolver) {
        Page page;
        List<String> locales = new ArrayList<String>();
        Iterator<Page> listLocal = resourceResolver.getResource(ImportersConstants.SILVERSEA_ROOT).adaptTo(Page.class)
                .listChildren();
        while (listLocal != null && listLocal.hasNext()) {
            page = listLocal.next();
            locales.add(page.getName());
        }
        return locales;

    }

    @Deprecated
    public static Page getPagePathByLocale(final ResourceResolver resourceResolver, final Page enPage,
            final String locale) {
        return resourceResolver.getResource(StringUtils.replace(enPage.getPath(), "/en/", "/" + locale + "/"))
                .adaptTo(Page.class);
    }

}
