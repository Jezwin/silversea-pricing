package com.silversea.aem.importers.services;

import com.silversea.aem.components.beans.ImporterStatus;

import java.io.IOException;

@Deprecated
public interface ShipsUpdateImporter {

    ImporterStatus updateImporData() throws IOException;
}
