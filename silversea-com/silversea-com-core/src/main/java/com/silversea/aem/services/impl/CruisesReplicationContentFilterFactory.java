package com.silversea.aem.services.impl;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationContentFilter;
import com.day.cq.replication.ReplicationContentFilterFactory;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * @author aurelienolivier
 */
@Component
@Service
public class CruisesReplicationContentFilterFactory implements ReplicationContentFilterFactory {

    private static final ReplicationContentFilter FILTER = new CruisesReplicationContentFilter();

    /**
     * Filter executes on content activation
     *
     * @param action The {@link ReplicationAction} to consider.
     * @return
     */
    public ReplicationContentFilter createFilter(final ReplicationAction action) {
        return action.getType() == ReplicationActionType.ACTIVATE ? FILTER : null;
    }
}
