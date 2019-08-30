package com.silversea.aem.importers;

import com.jasongoodwin.monads.Try;
import com.silversea.aem.importers.services.impl.ImportResult;

@FunctionalInterface
public interface ImportJob {

    default Try<ImportResult> run() {
        return Try.ofFailable(this::lambda);
    }

    // The lambda will be converted into this method
    ImportResult lambda() throws Exception;
}


