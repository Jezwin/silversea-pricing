package com.silversea.aem.importers.services;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.commons.json.JSONObject;

/**
 * Created by mbennabi on 09/03/2017.
 */
public interface ExclusiveOffersImporter {

    ImportResult importExclusiveOffers();

    JSONObject getExclusiveOffersMapping();
}