package com.silversea.aem.importers;

import com.silversea.aem.importers.services.impl.ImportResult;
import io.vavr.control.Try;


@FunctionalInterface
public interface ImportJob {

    default Try<ImportResult> run() {
        return Try.of(this::lambda);
    }

    // The lambda will be converted into this method
    ImportResult lambda() throws Exception;
}


