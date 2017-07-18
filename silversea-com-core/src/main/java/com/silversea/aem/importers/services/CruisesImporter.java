package com.silversea.aem.importers.services;

import java.io.IOException;

public interface CruisesImporter {

	void importData(boolean update) throws IOException;
}
