package com.silversea.aem.logging;

import com.amazonaws.services.secretsmanager.model.*;
import com.jasongoodwin.monads.Try;
import com.silversea.aem.utils.AwsSecretManager;
import io.logz.sender.HttpsRequestConfiguration;
import io.logz.sender.LogzioSender;
import io.logz.sender.SenderStatusReporter;
import org.apache.felix.scr.annotations.*;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Executors;

@Component(immediate = true, metatype = true, label = "LoggingServiceFactory")
@Service(LogzLoggerFactory.class)
public class LogzLoggerFactory {

@Reference
private AwsSecretManager awsSecretManager;

    private Optional<LogzioSender> sender;

    @Activate
    protected final void activate(final ComponentContext context) {
        createSender();
    }

    public LogzLogger getLogger(String component) {
        return new LogzLogger(sender, component);
    }

    private void createSender() {
            Try<String> token = getSecret();
            token.map(tk -> {
                    HttpsRequestConfiguration conf = HttpsRequestConfiguration
                            .builder()
                            .setLogzioListenerUrl("http://listener-eu.logz.io:8070")
                            .setLogzioType("java")
                            .setLogzioToken(tk)
                            .build();

                    return LogzioSender
                            .builder()
                            .setTasksExecutor(Executors.newScheduledThreadPool(3))
                            .setDrainTimeoutSec(5)
                            .setReporter(new LogzioStatusReporter())
                            .setHttpsRequestConfiguration(conf)
                            .withInMemoryQueue()
                            .endInMemoryQueue()
                            .build();
                }).onSuccess(s -> {
                sender = Optional.of(s);
                s.start();
            }).onFailure(e -> {
                    sender = Optional.empty();
                    Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
                logger.error("something in the logger initialization went wrong", e);
            });
    }
    private Try<String> getSecret() {
        String secretName = "prod/logzio/token";
        Try<GetSecretValueResult> secret = awsSecretManager.getSecretValue(secretName);

        return secret.flatMap(secretSuccess -> {
            String JsonResponse = secretSuccess.getSecretString();
            return Try.ofFailable(() -> new JSONObject(JsonResponse).getString("token"));
        });
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
