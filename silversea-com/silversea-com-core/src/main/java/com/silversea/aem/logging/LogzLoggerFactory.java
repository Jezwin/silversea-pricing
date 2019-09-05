package com.silversea.aem.logging;

import com.silversea.aem.utils.AwsSecretsManager;
import io.logz.sender.HttpsRequestConfiguration;
import io.logz.sender.LogzioSender;
import io.logz.sender.SenderStatusReporter;
import io.logz.sender.exceptions.LogzioParameterErrorException;
import org.apache.felix.scr.annotations.*;
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
        createSender();
    }

    public LogzLogger getLogger(String component) {
        return new LogzLogger(sender, component);
    }

    public SSCLogger getLogger(Class clazz) {
        return new LogzLogger(sender, clazz.getName());
    }

    private void createSender() {
        awsSecretsManager
                .getValue(LOGZIO_TOKEN_SECRET_KEY)
                .map(token -> LogzioSender
                        .builder()
                        .setTasksExecutor(Executors.newScheduledThreadPool(LOGZIO_CORE_POOL_SIZE))
                        .setDrainTimeoutSec(LOGZIO_DRAIN_TIMEOUT_IN_SECONDS)
                        .setReporter(new LogzioStatusReporter())
                        .setHttpsRequestConfiguration(buildRequestConfig(token))
                        .withInMemoryQueue()
                        .endInMemoryQueue()
                        .build())
                .onSuccess(s -> {
                    sender = Optional.of(s);
                    s.start();
                })
                .onFailure(e -> {
                    sender = Optional.empty();
                    Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
                    logger.error(String.format("Logz.io initialization failed: %s", e.getMessage()), e);
                });
    }

    private HttpsRequestConfiguration buildRequestConfig(String tk) throws LogzioParameterErrorException {
        return HttpsRequestConfiguration
                .builder()
                .setLogzioListenerUrl(LOGZIO_LISTENER_URL)
                .setLogzioType(LOGZIO_TYPE)
                .setLogzioToken(tk)
                .build();
    }

    public class LogzioStatusReporter implements SenderStatusReporter {
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
