package com.silversea.aem.importers.services;

import java.io.IOException;

public interface ShipsImporter {

    void importData() throws IOException;
    
    int getErrorNumber();
    
    int getSuccesNumber();
}
