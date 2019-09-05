package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

public interface ComboCruisesImporter {
    
   ImportResult importAllItems();

   /**
    * get all "silversea/silversea-com/components/pages/combosegment" and mark to activate target of cruiseReference
    * TODO move it to combocruise diff when ok
    * TODO add better logs
    */
   ImportResult markSegmentsForActivation();
}
