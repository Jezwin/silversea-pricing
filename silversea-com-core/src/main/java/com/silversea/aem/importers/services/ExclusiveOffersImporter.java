package com.silversea.aem.importers.services;

import java.io.IOException;

import com.silversea.aem.components.beans.ImporterStatus;

/**
 * Created by mbennabi on 09/03/2017.
 */
public interface ExclusiveOffersImporter {

    ImporterStatus importData() throws IOException;

//    int getErrorNumber();
//
//    int getSuccesNumber();
}
