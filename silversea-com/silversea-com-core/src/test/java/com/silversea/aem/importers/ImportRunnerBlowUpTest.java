package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
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
        failureJobs = Arrays.asList(
            jobRequest("errorJob1", () -> { throw new ImporterException("Boom"); }),
            jobRequest("successJob1", () -> new ImportResult(500000, 0)),
            jobRequest("errorJob2", () -> { throw new Exception("Something went wrong"); }),
            jobRequest("successJob2", () -> new ImportResult(3, 0))
        );
        log = mock(SSCLogger.class);
        new ImportRunner(failureJobs, log).run();
        verify(log, atLeast(0)).logError(errorLogs.capture());
        verify(log, atLeast(0)).logInfo(infoLogs.capture());
    }

    @Test
    public void catchesImportExceptionsAndLogsError() {
        JsonLog error1Log = jsonLog("ImportError").with("job", "errorJob1").with(new ImporterException("Boom"));
        assertThat(errorLogs.getAllValues(), hasItem(error1Log));
    }

    @Test
    public void catchesAllExceptionsAndLogsError() {
        JsonLog error2Log = jsonLog("ImportError").with("job", "errorJob2").with(new Exception("Something went wrong"));
        assertThat(errorLogs.getAllValues(), hasItem(error2Log));
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
