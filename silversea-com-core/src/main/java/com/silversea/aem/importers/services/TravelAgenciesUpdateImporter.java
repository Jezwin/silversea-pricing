package com.silversea.aem.importers.services;

import java.io.IOException;

import com.silversea.aem.components.beans.ImporterStatus;

/**
 * Created by mbennabi on 08/03/2017.
 */
@Deprecated
public interface TravelAgenciesUpdateImporter {

    ImporterStatus updateImporData() throws IOException;

//    int getErrorNumber();
//
//    int getSuccesNumber();
}
