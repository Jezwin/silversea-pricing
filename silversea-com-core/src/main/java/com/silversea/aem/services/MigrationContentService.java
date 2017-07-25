package com.silversea.aem.services;

import com.silversea.aem.importers.servlets.mappings.CitiesMappingServlet;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * TODO remove, replaced by {@link CitiesMappingServlet}
 */
@Deprecated
public interface MigrationContentService {
    Node getNodeById(String type, String cityId, Session session,String template);
}
