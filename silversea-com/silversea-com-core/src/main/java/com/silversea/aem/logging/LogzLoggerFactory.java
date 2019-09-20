package com.silversea.aem.logging;

import com.silversea.aem.utils.AwsSecretsManager;
import io.logz.sender.HttpsRequestConfiguration;
import io.logz.sender.LogzioSender;
import io.logz.sender.SenderStatusReporter;
import io.logz.sender.exceptions.LogzioParameterErrorException;
import io.vavr.control.Try;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Executors;

@Component(immediate = true, metatype = true, label = "LoggingServiceFactory")
@Service(LogzLoggerFactory.class)
public class LogzLoggerFactory {

    public static final String LOGZIO_LISTENER_URL = "http://listener-eu.logz.io:8070";
    public static final String LOGZIO_TYPE = "java";
    public static final String LOGZIO_TOKEN_SECRET_KEY = "LOGZIO_TOKEN";
    public static final int LOGZIO_DRAIN_TIMEOUT_IN_SECONDS = 5;
    public static final int LOGZIO_CORE_POOL_SIZE = 3;
    @Reference
    private AwsSecretsManager awsSecretsManager;

    private Optional<LogzioSender> sender;

    @Activate
    protected final void activate(final ComponentContext context) {
        this.sender = createSender(this.awsSecretsManager)
                .onSuccess(LogzioSender::start)
                .onFailure(exception -> {
                    // Fail quietly so that logz.io doesn't block execution, but log failure locally.
                    Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
                    logger.error(String.format("Logz.io initialization failed: %s", exception.getMessage()), exception);
                }).toJavaOptional();
    }

    public LogzLogger getLogger(String component) {
        return new LogzLogger(sender, component);
    }

    public SSCLogger getLogger(Class clazz) {
        return new LogzLogger(sender, clazz.getName());
    }

    private Try<LogzioSender> createSender(AwsSecretsManager secretsManager) {
        return secretsManager
                .getValue(LOGZIO_TOKEN_SECRET_KEY)
                .mapTry(token -> buildSender(buildRequest(token)));
    }

    private static LogzioSender buildSender(HttpsRequestConfiguration requestConfig) throws LogzioParameterErrorException {
        return LogzioSender
                .builder()
                .setTasksExecutor(Executors.newScheduledThreadPool(LOGZIO_CORE_POOL_SIZE))
                .setDrainTimeoutSec(LOGZIO_DRAIN_TIMEOUT_IN_SECONDS)
                .setReporter(new LogzioStatusReporter())
                .setHttpsRequestConfiguration(requestConfig)
                .withInMemoryQueue()
                .endInMemoryQueue()
                .build();
    }

    private static HttpsRequestConfiguration buildRequest(String token) throws LogzioParameterErrorException {
        return HttpsRequestConfiguration
                .builder()
                .setLogzioListenerUrl(LOGZIO_LISTENER_URL)
                .setLogzioType(LOGZIO_TYPE)
                .setLogzioToken(token)
                .build();
    }

    public static class LogzioStatusReporter implements SenderStatusReporter {
        private Logger logger;

        public LogzioStatusReporter() {
            logger = LoggerFactory.getLogger(LogzioStatusReporter.class);
        }

        @Override
        public void error(String s) {
            logger.error(s);
        }

        @Override
        public void error(String s, Throwable throwable) {
            logger.error(s);
        }

        @Override
        public void warning(String s) {
            logger.warn(s);
        }

        @Override
        public void warning(String s, Throwable throwable) {
            logger.warn(s);
        }

        @Override
        public void info(String s) {
            logger.info(s);
        }

        @Override
        public void info(String s, Throwable throwable) {
            logger.info(s);
        }
    }
}