package com.silversea.ssc.aem.replication;

import aQute.bnd.annotation.ProviderType;

/**
 * The result of a replication.
 */
@ProviderType
public class ReplicationResult {

    private final Status status;
    private final String version;
    private final String path;

    public ReplicationResult(String path, Status status) {
        this.path = path;
        this.status = status;
        this.version = null;
    }

    public ReplicationResult(String path, Status status, String version) {
        this.path = path;
        this.status = status;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format("ReplicationResult [path=%s, status=%s, version=%s]", path, status, version);
    }

    public enum Status {
        replicated, not_replicated, error
    }

}
