package com.silversea.aem.healthcheck;

import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.logging.SSCLoggerFactory;
import com.silversea.aem.services.CruisesCacheService;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.felix.scr.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.silversea.aem.logging.JsonLog.jsonLog;
import static com.silversea.aem.logging.JsonLog.nested;

@Component(label = "SilverseaHealthCheckMonitor", metatype = true, immediate = true)
@Service(value = Runnable.class)
@Properties({
        @Property(name = "scheduler.period", longValue = 60 * 10), //every 10 minutes
        @Property(name = "scheduler.concurrent", boolValue = false)}
)
public class HealthCheckMonitor implements Runnable {

    private SSCLogger sscLogger;

    @Reference
    private SSCLoggerFactory sscLogFactory;

    @Reference
    private CruisesCacheService cruiseCache;

    @Override
    public void run() {
        sscLogger = sscLogFactory.getLogger(HealthCheckMonitor.class);
        HealthCheckResult result = Try.of(this::runHealthCheck).getOrElseGet(HealthCheckResult::Error);
        if (result.successful) {
            sscLogger.logInfo(result.log);
        } else {
            sscLogger.logError(result.log);
        }
    }

    private HealthCheckResult runHealthCheck() {
        Map<String, Integer> cacheSizes = HashMap.of(
            "en", cruiseCache.getCruises("en").size(),
            "fr", cruiseCache.getCruises("fr").size(),
            "pt-br", cruiseCache.getCruises("pt-br").size(),
            "de", cruiseCache.getCruises("de").size()
        );

        JsonLog cacheLog = cacheSizes.foldLeft(nested(), (log, item) -> log.with(item._1 + "Size", item._2));
        JsonLog log = jsonLog("HealthCheckHeartbeat").with("CruiseCache", cacheLog);

        if (cacheSizes.exists(x -> x._2 == 0)) {
            return HealthCheckResult.Failed(log);
        } else {
            return HealthCheckResult.Successful(log);
        }
    }

    private static class HealthCheckResult {
        JsonLog log;
        boolean successful;

        private HealthCheckResult(JsonLog log, boolean succesful) {
            this.log = log;
            this.successful = succesful;
        }

        private static HealthCheckResult Successful(JsonLog log) {
            return new HealthCheckResult(log.with("status", "success"), true);
        }

        private static HealthCheckResult Failed(JsonLog log) {
            return new HealthCheckResult(log.with("status", "failed"), false);
        }

        private static HealthCheckResult Error(Throwable e) {
            return new HealthCheckResult(jsonLog("HealthCheckHeartbeat").with("status", "error").with(e), false);
        }
    }
}
