package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;
import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static com.silversea.aem.importers.ImportJobRequest.jobRequest;
import static com.silversea.aem.logging.JsonLog.jsonLog;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;


public class ImportRunnerFailureTest {

    List<ImportJobRequest> failureJobs;
    SSCLogger log;

    @Before
    public void configureErrorImports() {
        failureJobs = List.of(
            jobRequest("failureJob1", () -> new ImportResult(100, 25)),
            jobRequest("successJob", () -> new ImportResult(8888888, 0)),
            jobRequest("failureJob2", () -> new ImportResult(0, 99999))
        );
        log = mock(SSCLogger.class);
        new ImportRunner(failureJobs, log).run();
    }

    @Test
    public void logsImportFailureWhenImportContainsFailures() {
        ArgumentCaptor<JsonLog> args = ArgumentCaptor.forClass(JsonLog.class);
        verify(log, times(2)).logError(args.capture());
        JsonLog failure1Log = jsonLog("ImportFailures")
                .with("job", "failureJob1")
                .with("imported", 100)
                .with("failed", 25);
        JsonLog failure2Log = jsonLog("ImportFailures")
                .with("job", "failureJob2")
                .with("imported", 0)
                .with("failed", 99999);
        assertThat(args.getAllValues(), hasItems(failure1Log, failure2Log));
    }

    @Test
    public void doesNotStopWhenEncounteringAFailure() {
        verify(log).logInfo(jsonLog("ImportSuccess").with("job", "successJob").with("imported", 8888888));
    }
}
