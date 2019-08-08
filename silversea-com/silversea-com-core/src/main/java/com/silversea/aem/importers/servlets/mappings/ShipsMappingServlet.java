package com.silversea.aem.importers.servlets.mappings;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.importers.ImportersConstants;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SlingServlet(paths = "/bin/ships-mapping", extensions = "json")
public class ShipsMappingServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(ShipsMappingServlet.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        JSONObject jsonObject = new JSONObject();

        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationParams);

            Iterator<Resource> ships = resourceResolver.findResources("/jcr:root/content/silversea-com"
                    + "//element(*,cq:Page)[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/ship\"]", "xpath");

            while (ships.hasNext()) {
                Resource ship = ships.next();
                Page shipPage = ship.adaptTo(Page.class);

                Resource childContent = ship.getChild(JcrConstants.JCR_CONTENT);

                if (shipPage != null && childContent != null) {
                    ValueMap childContentProperties = childContent.getValueMap();

                    final String shipId = childContentProperties.get("shipId", String.class);

                    final String shipLang = shipPage.getAbsoluteParent(2).getName();

                    final String shipPath = ship.getPath();

                    if (shipId != null && shipLang != null) {
                        try {
                            if (jsonObject.has(shipId)) {
                                final JSONObject shipObject = jsonObject.getJSONObject(shipId);
                                shipObject.put(shipLang, shipPath);
                                jsonObject.put(shipId, shipObject);
                            } else {
                                JSONObject shipObject = new JSONObject();
                                shipObject.put(shipLang, shipPath);
                                jsonObject.put(shipId, shipObject);
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add ship {} with path {} to ships array", shipId, ship.getPath(), e);
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOGGER.error("Cannot create resource resolver", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }

        response.getWriter().write(jsonObject.toString());
    }
}
