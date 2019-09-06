package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;

import java.util.List;

import static com.silversea.aem.logging.JsonLog.jsonLog;
import static java.util.stream.Collectors.toList;

public class ImportRunner {
    private List<ImportJobRequest> jobs;
    private SSCLogger log;

    public ImportRunner(List<ImportJobRequest> jobs, SSCLogger log) {
        this.jobs = jobs;
        this.log = log;
    }

    public List<ImportReport> run() {
        log.logInfo(jsonLog("ApiBatchImportStarting"));
        List<ImportReport> reports = jobs.stream().map(this::run).collect(toList());
        log.logInfo(jsonLog("ApiBatchImportComplete"));
        return reports;
    }

    private ImportReport run(ImportJobRequest request) {
        log.logInfo(jsonLog("ImportStarting").with("job", request.name()));
        ImportReport report = request.job().run().map(r -> buildReport(request, r)).getOrElseGet(x -> buildErrorReport(request, x));
        log(report);
        return report;
    }

    private void log(ImportReport report) {
        switch (report.status) {
            case Success: {
                JsonLog j = jsonLog("ImportSuccess").with("job", report.name()).with("imported", report.result().getSuccessNumber());
                log.logInfo(j);
            } break;
            case Failures: {
                JsonLog j = jsonLog("ImportFailures")
                            .with("job", report.name())
                            .with("imported", report.result().getSuccessNumber())
                            .with("failed", report.result().getErrorNumber());
                log.logError(j);
            } break;
            case Error: {
                JsonLog j = jsonLog("ImportError").with("job", report.name()).with(report.error());
                log.logError(j);
            } break;

        }
    }

    private ImportReport buildReport(ImportJobRequest request, ImportResult result) {
        if (result.getErrorNumber() > 0)
             return new ImportReport(request.name(), ImportStatus.Failures, result);
        else
            return new ImportReport(request.name(), ImportStatus.Success, result);
    }

    private ImportReport buildErrorReport(ImportJobRequest request, Throwable x) {
        return new ImportReport(request.name(), ImportStatus.Error, x);
    }

    public class ImportReport {
        private String name;
        private ImportStatus status;
        private ImportResult result;
        private Throwable error;

        ImportReport(String name, ImportStatus status, ImportResult result) {
            this.name = name;
            this.status = status;
            this.result = result;
        }

        ImportReport(String name, ImportStatus status, Throwable error) {
            this.name = name;
            this.status = status;
            this.error = error;
        }

        public String name() {
            return name;
        }

        public ImportStatus status() {
            return status;
        }

        public ImportResult result() {
            return result;
        }

        public Throwable error() {
            return error;
        }

    }
    private enum ImportStatus {
        Success, Failures, Error
    }
}
