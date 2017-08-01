package com.silversea.aem.importers.servlets.mappings;

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

@SlingServlet(paths = "/bin/brochures-mapping", extensions = "json")
public class BrochuresMappingServlet extends SlingSafeMethodsServlet {

    static final private Logger LOGGER = LoggerFactory.getLogger(BrochuresMappingServlet.class);

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

            Iterator<Resource> brochures = resourceResolver.findResources("/jcr:root/content/dam/silversea-com"
                    + "//element(*,dam:Asset)[jcr:content/metadata/brochureCode]", "xpath");

            while (brochures.hasNext()) {
                final Resource brochure = brochures.next();
                final Resource brochureMetadata = brochure.getChild("jcr:content/metadata");

                if (brochureMetadata != null) {
                    final ValueMap brochureMetadataProperties = brochureMetadata.getValueMap();
                    final String brochureCode = brochureMetadataProperties.get("brochureCode", String.class);
                    final String lang = brochure.getParent().getName();

                    if (brochureCode != null) {
                        try {
                            JSONObject brochureObject;
                            if (!jsonObject.has(brochureCode)) {
                                brochureObject = new JSONObject();
                            } else {
                                brochureObject = jsonObject.getJSONObject(brochureCode);
                            }

                            brochureObject.put(lang, brochure.getPath());
                            jsonObject.put(brochureCode, brochureObject);
                        } catch (JSONException e) {
                            LOGGER.error("Cannot add brochure {} with path {} to brochures array", brochureCode, brochure.getPath(), e);
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
