package com.silversea.aem.servlets;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.silversea.aem.technical.json.JsonMapper;

/**
 * TODO naming
 */
public class ScServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    
    static final private Logger LOGGER = LoggerFactory.getLogger(ScServlet.class);
    
    protected <T> void writeDomainObject(SlingHttpServletResponse response, T domainObject) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        String json = JsonMapper.getJson(domainObject);
        try {

            response.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    protected String getBodyFromRequest(SlingHttpServletRequest request) {
        String result = null;

        try {
            BufferedReader reader = request.getReader();

            String line = null;
            StringBuilder buff = new StringBuilder();

            do {
                line = reader.readLine();
                if (line != null) {
                    buff.append(line.trim());
                }
            } while (line != null);
            reader.close();
            result = buff.toString().trim();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

}
