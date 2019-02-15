package com.silversea.aem.filter;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.day.cq.commons.Externalizer;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.slf4j.LoggerFactory;

import com.silversea.aem.constants.WcmConstants;
import sun.rmi.runtime.Log;

@Service(Servlet.class)
@SlingFilter(
        label = "Old Voyage Page Redirect Filter",
        description = "Old Voyage Page Redirect Filter",
        metatype = false,
        generateComponent = true,
        generateService = true,
        order = 101,
        scope = SlingFilterScope.REQUEST)
public class OldVoyagePageRedirectFilter implements Filter {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OldVoyagePageRedirectFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			if (!(request instanceof SlingHttpServletRequest)) {
				chain.doFilter(request, response);
				return;
			}
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String pathInfo = httpRequest.getRequestURI().toString();
			if (pathInfo.lastIndexOf(WcmConstants.HTML_SUFFIX) != -1 && pathInfo.lastIndexOf("/") != -1) {
				final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
				final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
				final Resource resource = slingRequest.getResource();
				if (resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					Resource parentResourceTry  = resource.getResourceResolver().resolve(resource.getParent().getPath());
					Node parentNode = resource.getResourceResolver().getResource(parentResourceTry.getPath() + "/jcr:content").adaptTo(Node.class);
					try {
						if (null != parentNode && null != parentNode.getProperty("sling:resourceType") &&
								parentNode.getProperty("sling:resourceType").getValue().getString().equalsIgnoreCase("silversea/silversea-com/components/pages/destination")) {
							slingResponse.setStatus(SlingHttpServletResponse.SC_MOVED_PERMANENTLY);
							Externalizer externalizer = resource.getResourceResolver().adaptTo(Externalizer.class);
							slingResponse.sendRedirect(externalizer.publishLink(resource.getResourceResolver(), resource.getParent().getPath())
									+ WcmConstants.HTML_SUFFIX);
							return;
						}
					} catch (IllegalStateException | RepositoryException e) {
						Logger.debug("Exception while fetching node property value" + e.getMessage());
					}
				}
			}
		}catch (Exception e){
			Logger.error("Issue with OldVoyagePageRedirectFilter",e);
		}
         chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}