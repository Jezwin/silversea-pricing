package com.silversea.aem.importers.services;

import java.io.IOException;

/**
 * Created by mbennabi on 09/03/2017.
 */
public interface ExclusiveOffersImporter {

    void importData() throws IOException;

    int getErrorNumber();

    int getSuccesNumber();
}
