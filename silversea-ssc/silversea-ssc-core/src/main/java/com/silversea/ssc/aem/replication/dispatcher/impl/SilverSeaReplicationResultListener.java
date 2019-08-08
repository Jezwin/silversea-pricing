package com.silversea.ssc.aem.replication.dispatcher.impl;

import java.util.HashMap;
import java.util.Map;

import com.day.cq.replication.Agent;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationListener;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.ReplicationResult;

/**
 * Replication Listener that stores replication results for a series of agents.
 */
public class SilverSeaReplicationResultListener implements ReplicationListener {

	public SilverSeaReplicationResultListener(){
		
	}
	/**
	 * Variable to hold the replication results.
	 */
	private final Map<Agent, ReplicationResult> results = new HashMap<Agent, ReplicationResult>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.day.cq.replication.ReplicationListener#onStart(com.day.cq.replication
	 * .Agent, com.day.cq.replication.ReplicationAction)
	 */
	public final void onStart(final Agent agent, final ReplicationAction action) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.day.cq.replication.ReplicationListener#onMessage(com.day.cq.
	 * replication.ReplicationLog.Level, java.lang.String)
	 */
	public final void onMessage(final ReplicationLog.Level level, final String message) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.day.cq.replication.ReplicationListener#onEnd(com.day.cq.replication.
	 * Agent, com.day.cq.replication.ReplicationAction,
	 * com.day.cq.replication.ReplicationResult)
	 */
	public final void onEnd(final Agent agent, final ReplicationAction action, final ReplicationResult result) {
		this.results.put(agent, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.day.cq.replication.ReplicationListener#onError(com.day.cq.replication
	 * .Agent, com.day.cq.replication.ReplicationAction, java.lang.Exception)
	 */
	public final void onError(final Agent agent, final ReplicationAction action, final Exception error) {
	}

	/**
	 * Gets the results of the Replication operation.
	 *
	 * @return the Mapped results between the Agent and ReplicationResult
	 */
	public final Map<Agent, ReplicationResult> getResults() {
		return this.results;
	}
}
