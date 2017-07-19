package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;

/**
 * Created by aurelienolivier on 13/02/2017.
 */
public interface LandProgramsImporter {

    /**
     * Import all land programs, used in first data import
     * @return import result
     */
    ImportResult importAllLandPrograms();

    /**
     * Update the land programs based on the last import date
     * stored in the cities root page
     * @return import result
     */
    ImportResult updateLandPrograms();

    /**
     * Import only one land program
     *
     * @param landProgramId the land program ID of the land program to import
     */
    void importOneLandProgram(final String landProgramId);
}