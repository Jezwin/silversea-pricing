package com.silversea.aem.importers;

import com.jasongoodwin.monads.Try;
import com.silversea.aem.importers.services.impl.ImportResult;

import static com.jasongoodwin.monads.Try.ofFailable;

@FunctionalInterface
public interface ImportJob {

    default Try<ImportResult> run() {
        return ofFailable(this::lambda);
    }

    // The lambda will be converted into this method
    ImportResult lambda() throws Exception;
}


