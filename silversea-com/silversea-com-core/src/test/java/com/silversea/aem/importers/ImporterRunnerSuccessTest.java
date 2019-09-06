package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.SSCLogger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static com.silversea.aem.importers.ImportJobRequest.jobRequest;
import static com.silversea.aem.logging.JsonLog.jsonLog;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;


public class ImporterRunnerSuccessTest {

    static ImportResult success1 = new ImportResult( 1, 0);
    static ImportResult success2 = new ImportResult( 9999999, 0);
    static ImportResult success3 = new ImportResult(55, 0);
    static List<ImportJobRequest> importJobs;
    static SSCLogger log;
    static ArgumentCaptor<JsonLog> infoArgs;
    public static String GlobalState = "";

    @BeforeClass
    public static void configureImportJobs() {
        GlobalState = "";
        log = mock(SSCLogger.class);
        importJobs = new ArrayList<>();
        importJobs.add(jobRequest("import1", () -> { GlobalState += "aa"; return success1; }));
        importJobs.add(jobRequest("import2", () -> { GlobalState += "bb"; return success2; }));
        importJobs.add(jobRequest("import3", () -> { GlobalState += "cc"; return success3; }));
        new ImportRunner(importJobs, log).run();
    }

    @Test
    public void jobsAreRunInSuppliedOrder() {
        assertThat(GlobalState, equalTo("aabbcc"));
    }

    @Test
    public void logsImportSuccessAtInfoLevel() {
        infoArgs = ArgumentCaptor.forClass(JsonLog.class);
        verify(log, atLeastOnce()).logInfo(infoArgs.capture());
        JsonLog[] expected = new JsonLog[] {
            jsonLog("ImportStarting").with("job", "import1"),
            jsonLog("ImportSuccess").with("job", "import1").with("imported", 1),
            jsonLog("ImportStarting").with("job", "import2"),
            jsonLog("ImportSuccess").with("job", "import2").with("imported", 9999999),
            jsonLog("ImportStarting").with("job", "import3"),
            jsonLog("ImportSuccess").with("job", "import3").with("imported", 55)
        };
        assertArrayEquals(expected, infoArgs.getAllValues().subList(1, 7).toArray(new JsonLog[0]));
    }

    @Test
    public void importRunnerStartAndFinishEventsAreLogged() {
        assertThat(infoArgs.getAllValues().size(), equalTo(8));
        assertThat(infoArgs.getAllValues().get(0), equalTo(jsonLog("ApiBatchImportStarting")));
        assertThat(infoArgs.getAllValues().get(7), equalTo(jsonLog("ApiBatchImportComplete")));
    }

    @Test
    public void thereAreNoErrorsLoggedWhenJobRunsSuccessfully() {
        verify(log, never()).logError(any());
    }
}
