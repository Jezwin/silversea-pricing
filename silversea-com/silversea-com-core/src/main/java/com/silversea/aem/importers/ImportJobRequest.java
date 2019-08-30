package com.silversea.aem.importers;

public class ImportJobRequest {

    private final String name;
    private final ImportJob job;

    public static ImportJobRequest jobRequest(String name, ImportJob job) {
        return new ImportJobRequest(name, job);
    }

    private ImportJobRequest(String name, ImportJob job) {
        this.name = name;
        this.job = job;
    }

    public String name() { return name; }
    public ImportJob job() { return job; }
}
