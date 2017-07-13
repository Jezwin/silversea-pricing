package com.silversea.aem.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.services.MigrationContentService;

@Component(immediate = true)
@Service(value = MigrationContentService.class)
@Deprecated
public class MigrationContentServiceImpl implements MigrationContentService {

    static final private Logger LOGGER = LoggerFactory.getLogger(MigrationContentServiceImpl.class);
    private static final String PORT_PATH = "/content/silversea-com/en/other-resources/find-a-port/";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private QueryBuilder builder;

    private ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

    @Override
    public Node getNodeById(String type, String cityId, Session session, String template) {
        Node node = null;
        Map<String, String> map = new HashMap<>();
        try {
            map.put(WcmConstants.SEARCH_KEY_PATH, getPathByType(type));
            map.put("1_" + WcmConstants.SEARCH_KEY_PROPERTY, "cq:template");
            map.put("1_" + WcmConstants.SEARCH_KEY_PROPERTY_VALUE, template);
            map.put("2_" + WcmConstants.SEARCH_KEY_PROPERTY, "cityId");
            map.put("2_" + WcmConstants.SEARCH_KEY_PROPERTY_VALUE, cityId);
            Query query = builder.createQuery(PredicateGroup.create(map), session);
            SearchResult searchResult = query.getResult();
            Iterator<Resource> resourceIterator = searchResult.getResources();
            if (resourceIterator.hasNext()) {
                Resource res = resourceIterator.next();
                node = res.adaptTo(Node.class);
            }
        } catch (Exception e) {
            LOGGER.debug("Some issues are happened with Migration Service :()", e);
        }

        return node;
    }

    private String getPathByType(String type) {
        String str = "";
        if (type.equalsIgnoreCase("cities")) {
            str = PORT_PATH;
        }
        return str;
    }

}
