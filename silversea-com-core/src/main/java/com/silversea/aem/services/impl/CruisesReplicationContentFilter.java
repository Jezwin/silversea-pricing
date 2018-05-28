package com.silversea.aem.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationContentFilter;
import com.day.cq.wcm.api.NameConstants;
import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author aurelienolivier
 */
public class CruisesReplicationContentFilter implements ReplicationContentFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CruisesReplicationContentFilter.class);

    private List<String> filteredPaths = new CopyOnWriteArrayList<>();

    @Override
    public boolean accepts(final Node node) {
        try {
            // Check child node if the current is a page
            if (node.getPrimaryNodeType().isNodeType(NameConstants.NT_PAGE) && node.hasNode(JcrConstants.JCR_CONTENT)) {
                return checkNode(node.getNode(JcrConstants.JCR_CONTENT));
            }

            // else check node itself
            return checkNode(node);
        } catch (RepositoryException e) {
            LOGGER.error("Repository exception occurred", e);
        }

        // Default behavior is to accept
        return true;
    }

    @Override
    public boolean accepts(final Property property) {
        // Default behavior is to return true
        return true;
    }

    @Override
    public boolean allowsDescent(final Node node) {
        return this.accepts(node);
    }

    @Override
    public List<String> getFilteredPaths() {
        return filteredPaths;
    }

    private boolean checkNode(Node node) throws RepositoryException {
        if (node.hasProperty("isVisible") && node.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)) {
            // Example: Reject nodes that have a property "isVisible = false"
            final boolean isVisible = node.getProperty("isVisible").getBoolean();
            final String resourceType = node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getString();

            if (!isVisible && resourceType.equals(WcmConstants.RT_CRUISE)) {
                LOGGER.warn("Cruise {} is not visible, do not activate it", node.getPath());

                // Maintain the filteredPaths list whenever accepts returns false
                filteredPaths.add(node.getParent().getPath());

                return false;
            }
        }

        return true;
    }
}
