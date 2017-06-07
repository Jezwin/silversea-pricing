package com.silversea.aem.importers.services;

import java.io.IOException;

import com.silversea.aem.components.beans.ImporterStatus;

public interface ShipsUpdateImporter {

    ImporterStatus updateImporData() throws IOException;
    
//    int getErrorNumber();
//    
//    int getSuccesNumber();
}
