package com.silversea.ssc.aem.replication.dispatcher.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.Replicator;
import com.silversea.ssc.aem.replication.dispatcher.SilverSeaDispatcherFlushFilter;
import com.silversea.ssc.aem.replication.dispatcher.SilverSeaDispatcherFlusher;

/**
 * Hertz - Dispatcher Flusher
 * Service used to issue flush requests to enabled Dispatcher Flush Agents.
 */
@Component
@Service
public class SilverSeaDispatcherFlusherImpl implements SilverSeaDispatcherFlusher {
    private static final Logger log = LoggerFactory.getLogger(SilverSeaDispatcherFlusherImpl.class);
    
    /**
     * Declaring default constructor
     * */
    public SilverSeaDispatcherFlusherImpl() {
		super();
	}
    @Reference
    private SlingSettingsService sSettings;

    @Reference
    private Replicator replicator;

    @Reference
    private AgentManager aManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<Agent, ReplicationResult> flush(final ResourceResolver resourceResolver, final String... paths)
            throws ReplicationException {
        return this.flush(resourceResolver, ReplicationActionType.ACTIVATE, false, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<Agent, ReplicationResult> flush(final ResourceResolver resourceResolver,
                                                     final ReplicationActionType actionType,
                                                     final boolean synchronous,
                                                     final String... paths) throws ReplicationException {
    	log.debug("Parent Start::flush method called.");
        return this.flush(resourceResolver, actionType, synchronous, SilverSeaDispatcherFlushFilter.HIERARCHICAL, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<Agent, ReplicationResult> flush(final ResourceResolver resourceResolver,
                                                     final ReplicationActionType actionType,
                                                     final boolean synchronous,
                                                     final AgentFilter agentFilter,
                                                     final String... paths) throws ReplicationException {
    	log.debug("Run Modes are : {}", sSettings.getRunModes());
    	log.debug("Start::flush method called.");
    	
    	if (!sSettings.getRunModes().contains("publish")) {
    		log.debug("No flushing on Author.");
			return new HashMap<>();
		}
    	
    	final ReplicationOptions options = new ReplicationOptions();
        final SilverSeaReplicationResultListener listener = new SilverSeaReplicationResultListener();

        options.setFilter(agentFilter);
        options.setSynchronous(synchronous);
        options.setSuppressStatusUpdate(true);
        options.setSuppressVersions(true);
        options.setListener(listener);

        for (final String path : paths) {
            
                log.debug("--------------------------------------------------------------------------------");
                log.debug("Issuing Dispatcher Flush (via AEM Replication API) request for: {}", path);
                log.debug(" > Synchronous: {}", options.isSynchronous());
                log.debug(" > Replication Action Type: {}", actionType.name());
            
            /*Do not throw exception just log it*/
            Session session=null;
            try {
            	session=resourceResolver.adaptTo(Session.class);
            	log.debug("Sending the flush Request. {}");
            	replicator.replicate(session,
                    actionType, path, options);
            } catch (ReplicationException e) {
            	log.error("Error during Flushing", e);
            }finally{
            	log.debug("Executing flush method finally block.");
            	if(null!=session && session.isLive()){
            		session.logout();
            	}
            }
        }
        log.debug("Ending the flush method. {}",  listener.getResults());
        return listener.getResults();
    }

    /**
     * {@inheritDoc}
     */
    public final Agent[] getFlushAgents() {
    	log.debug("getlushAgents Called");
        return this.getAgents(new SilverSeaDispatcherFlushFilter());
    }


    /**
     * {@inheritDoc}
     */
    public final Agent[] getAgents(final AgentFilter agentFilter) {
        final List<Agent> flushAgents = new ArrayList<Agent>();
        log.debug("getAgents Called");
        for (final Agent agent : aManager.getAgents().values()) {
            if (agentFilter.isIncluded(agent)) {
            	log.debug("Agent name is :- {}", agent.getId());
                flushAgents.add(agent);
            }
        }
        log.debug("End::getAgents() {}", flushAgents.toArray(new Agent[flushAgents.size()]));
        return flushAgents.toArray(new Agent[flushAgents.size()]);
    }
}



