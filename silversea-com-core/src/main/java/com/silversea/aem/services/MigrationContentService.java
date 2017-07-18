package com.silversea.aem.services;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * TODO remove, replaced by {@link com.silversea.aem.importers.servlets.CitiesMappingServlet}
 */
@Deprecated
public interface MigrationContentService {
    Node getNodeById(String type, String cityId, Session session,String template);
}
