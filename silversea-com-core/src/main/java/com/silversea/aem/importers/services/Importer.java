package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

/**
 * TODO make services inheriting from this interface
 */
public interface Importer {

    ImportResult importAllItems();

    ImportResult importSampleSet(final int size);

    ImportResult updateItems();
}
