package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;
import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.stream.Collectors;

import static com.silversea.aem.importers.ImportJobRequest.jobRequest;
import static com.silversea.aem.logging.JsonLog.jsonLog;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class ImportRunnerBlowUpTest {

    List<ImportJobRequest> failureJobs;
    SSCLogger log;
    ArgumentCaptor<JsonLog> errorLogs = ArgumentCaptor.forClass(JsonLog.class);
    ArgumentCaptor<JsonLog> infoLogs = ArgumentCaptor.forClass(JsonLog.class);

    @Before
    public void configureBlowUpImports() {
        failureJobs = List.of(
            jobRequest("errorJob1", () -> { throw getImporterException(); }),
            jobRequest("successJob1", () -> new ImportResult(500000, 0)),
            jobRequest("errorJob2", () -> { throw getException(); }),
            jobRequest("successJob2", () -> new ImportResult(3, 0))
        );
        log = mock(SSCLogger.class);
        new ImportRunner(failureJobs, log).run();
        verify(log, atLeast(0)).logError(errorLogs.capture());
        verify(log, atLeast(0)).logInfo(infoLogs.capture());
    }

    @Test
    public void catchesImportExceptionsAndLogsError() {
        JsonLog error1Log = jsonLog("ImportError").with("job", "errorJob1").with(getImporterException());
        assertThat(errorLogs.getAllValues(), hasItem(error1Log));
    }

    private Exception getImporterException() {
        Exception  importerException = new ImporterException("Boom");
        StackTraceElement[] stackTrace = new StackTraceElement[]{};
        importerException.setStackTrace(stackTrace);
        return importerException;
    }

    @Test
    public void catchesAllExceptionsAndLogsError() {
        JsonLog error2Log = jsonLog("ImportError").with("job", "errorJob2").with(getException());
        assertThat(errorLogs.getAllValues(), hasItem(error2Log));
    }

    private Exception getException() {
        Exception  exception = new Exception("Something went wrong");
        StackTraceElement[] stackTrace = new StackTraceElement[]{};
        exception.setStackTrace(stackTrace);
        return exception;
    }

    @Test
    public void doesNotStopWhenEncounteringAnException() {
        JsonLog successLog1 = jsonLog("ImportSuccess").with("job", "successJob1").with("imported", 500000);
        JsonLog successLog2 = jsonLog("ImportSuccess").with("job", "successJob2").with("imported", 3);
        assertThat(infoLogs.getAllValues(), hasItems(successLog1, successLog2));
        String reason = infoLogs.getAllValues().stream().map(JsonLog::toString).collect(Collectors.joining());
        assertThat(reason, infoLogs.getAllValues().size(), equalTo(8));
    }

    @Test
    public void onlyErrorsLoggedAsError() {
        String reason = errorLogs.getAllValues().stream().map(JsonLog::toString).collect(Collectors.joining());
        assertThat(reason, errorLogs.getAllValues().size(), equalTo(2));
    }
}
