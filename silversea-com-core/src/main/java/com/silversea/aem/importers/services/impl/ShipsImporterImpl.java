package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;
import com.silversea.aem.importers.services.ShipsImporter;

import io.swagger.client.api.ShipsApi;
import io.swagger.client.model.Ship;

@Component(immediate = true, label = "Silversea.com - Cities importer")
@Service(value = ShipsImporter.class)
public class ShipsImporterImpl extends BaseImporter implements ShipsImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsImporterImpl.class);
    private static final String SHIP_PATH = "/api/v1/ships";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importShips() throws IOException {
        final String authorizationHeader = getAuthorizationHeader(SHIP_PATH);
        ShipsApi shipsApi = new ShipsApi();
        shipsApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        try {
            List<Ship> listShips = shipsApi.shipsGet(null);
        } catch (Exception e) {
            String errorMessage = "Some issues are happened for import builder ()";
            LOGGER.error(errorMessage, e);
        }
    }

}
