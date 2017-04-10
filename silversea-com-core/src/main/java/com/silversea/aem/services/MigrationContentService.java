package com.silversea.aem.services;

import javax.jcr.Node;
import javax.jcr.Session;

public interface MigrationContentService {
    Node getNodeById(String type, String cityId, Session session,String template);
}
