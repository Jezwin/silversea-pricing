package com.silversea.aem.importers;

import com.jasongoodwin.monads.Try;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.silversea.aem.logging.JsonLog.jsonLog;

public class ImportRunner {
    private List<ImportJobRequest> jobs;
    private SSCLogger log;

    public ImportRunner(List<ImportJobRequest> jobs, SSCLogger log) {
        this.jobs = jobs;
        this.log = log;
    }

    public void run() {
        log.logInfo(jsonLog("ApiBatchImportStarting"));
        jobs.forEach(this::run);
        log.logInfo(jsonLog("ApiBatchImportComplete"));
    }

    private void run(ImportJobRequest request) {
        log.logInfo(jsonLog("ImportStarting").with("job", request.name()));
        ImportReport report = request.job().run().map(r -> buildReport(request, r)).recover(x -> buildErrorReport(request, x));
        log(report);
    }

    private void log(ImportReport report) {
        switch (report.status) {
            case Success: log.logInfo(report.log); break;
            case Failures:
            case Error:
                log.logError(report.log); break;
        }
    }

    private ImportReport buildReport(ImportJobRequest request, ImportResult result) {
        if (result.getErrorNumber() > 0)
             return new ImportReport(ImportStatus.Failures, jsonLog("ImportFailures")
               .with("job", request.name())
               .with("imported", result.getSuccessNumber())
               .with("failed", result.getErrorNumber()));
        else
            return new ImportReport(ImportStatus.Success, jsonLog("ImportSuccess")
               .with("job", request.name())
               .with("imported", result.getSuccessNumber()));
    }

    private ImportReport buildErrorReport(ImportJobRequest request, Throwable x) {
        return new ImportReport(ImportStatus.Error, jsonLog("ImportError").with("job", request.name()).with(x));
    }

    private class ImportReport {
        public ImportStatus status;
        public JsonLog log;
        ImportReport(ImportStatus status, JsonLog log) {
            this.status = status;
            this.log = log;
        }
    }
    private enum ImportStatus {
        Success, Failures, Error
    }
}
