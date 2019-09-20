package com.silversea.aem.utils;

import io.vavr.control.Try;

public interface AwsSecretsManager {
    Try<String> getValue(String key);
}
