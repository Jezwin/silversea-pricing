package com.silversea.aem.importers.services;

import java.io.IOException;

public interface ComboCruisesImporter {
    
   void importData(boolean update) throws IOException;

}
