package com.silversea.aem.importers.services;

import java.io.IOException;

public interface BrochuresImporter {
	
	/**
	 * Import and updates brochures
	 * @throws IOException: throw an exception
	 */
	void importBrochures() throws IOException;

}
