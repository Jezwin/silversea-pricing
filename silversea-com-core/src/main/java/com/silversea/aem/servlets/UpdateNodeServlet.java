package com.silversea.aem.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.constants.ServletConstant;
import com.silversea.aem.services.MigrationContentService;

@Deprecated
@SlingServlet(paths = ServletConstant.PATH_UPDATE_NODE_SERVLET, methods = "POST", metatype = true)
@Property(name = "sling.auth.requirements", value = ServletConstant.PATH_UPDATE_NODE_SERVLET)
public class UpdateNodeServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    static final private Logger LOGGER = LoggerFactory.getLogger(UpdateNodeServlet.class);

    final String COMMA = ",";

    private Session session;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private MigrationContentService migrationContentService;

    int count = 0;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        count++;
        String isUpdate = "";
        try {
            ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);
            session = resolver.adaptTo(Session.class);
            String stepsStr = request.getParameter("steps");
            String[] steps = stepsStr.split(COMMA);
            Map<String, String> params = new HashMap<>();
            for (String str : steps) {
                params.put(str, request.getParameter(str));
            }
            Node node = migrationContentService.getNodeById("cities", params.get("cityId"), session,
                    params.get("template"));
            if (null != node) {
                LOGGER.debug("Silversea : Get Node of cityId :" + params.get("cityId"));
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (!entry.getKey().equalsIgnoreCase("cityId")) {
                        node.setProperty(entry.getKey(), entry.getValue());
                        if (entry.getKey().equalsIgnoreCase("assetSelectionReference")) {
                            LOGGER.debug("Xu ly hinh anh");
                        }
                    }
                }
                session.save();
                isUpdate = "ok";
            }
            if (count == 20) {
                count = 0;
                if (session.hasPendingChanges()) {
                    try {
                        session.save();
                    } catch (RepositoryException e) {
                        session.refresh(true);
                    }
                }
            }
            LOGGER.debug("Silversea : Updated Node of cityId :" + params.get("cityId") + "- count :" + count);
            resolver.close();
        } catch (Exception e) {
            LOGGER.error("Have some errors in this servlet : {}", e);
        } finally {
            response.getWriter().write(isUpdate);
        }
    }

}
