package com.silversea.aem.importers.servlets.responses;

import com.silversea.aem.importers.services.impl.ImportResult;
import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class ImportResponseView {
    public static void buildResponse(SlingHttpServletResponse response, Map<Enum, ImportResult> results) throws IOException {
        // Returning a html doc like this is old fashion.
        // Replace this method, by using RequestDispatcher and a html template.
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Importer Results</title></head>");
            out.println("<body>");
            out.println("<h1>Summary</h1>");
            out.println("<table>");
            out.println("<tr> <th>Importer mode</th> <th> Success Count</th> <th>Error Count</th></tr>");
            for (Enum mode: results.keySet()) {
                ImportResult result = results.get(mode);
                out.println("<tr>" +
                        "<td>"+ mode +"</td>" +
                        "<td>"+result.getSuccessNumber()+"</td>" +
                        "<td>"+result.getErrorNumber()+ "</td>" +
                        "</tr>");
            }
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            response.setContentType("text/html");
        }
    }
}
