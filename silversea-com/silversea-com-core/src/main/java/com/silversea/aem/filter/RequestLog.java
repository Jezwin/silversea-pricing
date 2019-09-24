package com.silversea.aem.filter;

import com.silversea.aem.importers.polling.ApiUpdater;
import com.silversea.aem.logging.JsonLog;
import com.silversea.aem.logging.LogzLoggerFactory;
import com.silversea.aem.logging.SSCLogger;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static com.silversea.aem.logging.JsonLog.jsonLog;

@Service(Servlet.class)
@SlingFilter(
        label = "SlingRequestLog",
        description = "RequestLog",
        order = 0,
        scope = SlingFilterScope.REQUEST)
public class RequestLog implements Filter {

    @Reference
    private LogzLoggerFactory sscLogFactory;

    private SSCLogger logger;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger = sscLogFactory.getLogger(RequestLog.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        StatusExposingServletResponse response = new StatusExposingServletResponse((HttpServletResponse)servletResponse);
        filterChain.doFilter(servletRequest, response);
        SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;

        JsonLog log = jsonLog("HttpRequest")
                .with("url", request.getRequestURI())
                .with("status", response.getStatus())
                .with("method", request.getMethod());

        logger.logInfo(log);
    }

    @Override
    public void destroy() {

    }

    // This is a major hack but is necessary to get the status code. See: https://stackoverflow.com/questions/1302072/how-can-i-get-the-http-status-code-out-of-a-servletresponse-in-a-servletfilter
    public class StatusExposingServletResponse extends HttpServletResponseWrapper {

        private int httpStatus;

        public StatusExposingServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        @Override
        public void setStatus(int sc, String sm) {
            httpStatus = sc;
            super.setStatus(sc, sm);
        }

        public int getStatus() {
            return httpStatus;
        }
    }
}
