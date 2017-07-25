package com.silversea.aem.importers.services;

import java.io.IOException;

import com.silversea.aem.components.beans.ImporterStatus;

@Deprecated
public interface FeaturesUpdateImporter {

    ImporterStatus updateImporData() throws IOException;

}
