package com.silversea.ssc.aem.replication.dispatcher;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationResult;

import aQute.bnd.annotation.ProviderType;

/**
 * This service shall be used to call its corresponding Implementation to make the put request to clear the associated 
 * artifacts.
 */
@ProviderType
public interface SilverSeaDispatcherFlusher {

    /**
     * Issue flush replication request.
     *
     * @param rResolver access into repository; Must have access to the resources to flush
     * @param paths list of resources to flush
     * @return a map of the targeted flush agents and the result of the replication request
     * @throws ReplicationException
     */
    Map<Agent, ReplicationResult> flush(ResourceResolver rResolver, String... paths)
            throws ReplicationException;

    /**
     * Issue flush replication request.
     *
     * @param rResolver access into repository; Must have access to the resources to flush
     * @param actionType specifies the Replication Type that will be associated with the flush requests
     *                   (ex. Activate, Deactivate, Delete)
     * @param synchronous specifies if the Replication Request should be synchronous or asynchronous
     * @param paths list of resources to flush
     * @return a map of the targeted flush agents and the result of the replication request
     * @throws ReplicationException
     */
    Map<Agent, ReplicationResult> flush(ResourceResolver rResolver, ReplicationActionType actionType,
                                        boolean synchronous, String... paths) throws ReplicationException;

    /**
     * Issue flush replication request.
     *
     * @param resourceResolver access into repository; Must have access to the resources to flush
     * @param actionType specifies the Replication Type that will be associated with the flush requests
     *                   (ex. Activate, Deactivate, Delete)
     * @param synchronous specifies if the Replication Request should be synchronous or asynchronous
     * @param agentFilter filter used to specify agents to flush
     * @param paths list of resources to flush
     * @return a map of the targeted flush agents and the result of the replication request
     * @throws ReplicationException
     */
    Map<Agent, ReplicationResult> flush(ResourceResolver resourceResolver, ReplicationActionType actionType,
                                        boolean synchronous, AgentFilter agentFilter, String... paths) throws
            ReplicationException;

    /**
     * Get Replication Agents targeted by this service.
     *
     * @return a list of Replication Agents that will be targeted by this service
     */
    Agent[] getFlushAgents();

    /**
     * Get Replication Agents targeted by the provided AgentFilter.
     *
     * @param agentFilter filter used to specify agents to flush
     * @return a list of Replication Agents that will be targeted provided AgentFilter
     */
    Agent[] getAgents(AgentFilter agentFilter);
}
