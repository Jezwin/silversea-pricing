package com.silversea.aem.filter;

import com.day.cq.commons.Externalizer;
import com.silversea.aem.constants.WcmConstants;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service(Servlet.class)
@SlingFilter(
        label = "Port Page URL Redirect Filter",
        description = "Port Page URL Redirect Filter",
        metatype = false,
        generateComponent = true,
        generateService = true,
        order = 102,
        scope = SlingFilterScope.REQUEST)
@Properties({
		@Property(name = "sling.filter.pattern", value = ".*(cruise-to|croisieres|cruceros|cruzeiros|kreuzfahrten|find-port|buscar-puerto|encontre-porto|finden-sie-einen-hafen|trouver-port).*html")
})
public class PortPageUrlFilter implements Filter {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(PortPageUrlFilter.class.getName());

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
					String requestURl = slingRequest.getRequestURI();
					String portNameRequested = requestURl.substring(slingRequest.getRequestURI().lastIndexOf('/')+1);
					String redirectToApply = "";
					if(requestURl.contains("/cruise-to/")){
						redirectToApply = "/content/silversea-com/en/other-resources/find-a-port/" + portNameRequested.substring(0, 1) + "/" + portNameRequested;
					}else if(requestURl.contains("/fr/croisieres/")){
						redirectToApply = "/content/silversea-com/fr/other-resources/find-a-port/" + portNameRequested.substring(0, 1) + "/" + portNameRequested;
					}else if(requestURl.contains("/es/cruceros/")){
						redirectToApply = "/content/silversea-com/es/other-resources/find-a-port/" + portNameRequested.substring(0, 1) + "/" + portNameRequested;
					}else if(requestURl.contains("/pt-br/cruzeiros/")){
						redirectToApply = "/content/silversea-com/pt-br/other-resources/find-a-port/" + portNameRequested.substring(0, 1) + "/" + portNameRequested;
					}else if(requestURl.contains("/de/kreuzfahrten/")){
						redirectToApply = "/content/silversea-com/de/other-resources/find-a-port/" + portNameRequested.substring(0, 1) + "/" + portNameRequested;
					}

					if(!redirectToApply.equals("")) {
						RequestDispatcher dispatcher =
								slingRequest.getRequestDispatcher(redirectToApply);
						dispatcher.include(slingRequest, slingResponse);
						return;
					}
				}else if(resource.hasChildren() && resource.getChild("jcr:content") != null && resource.getChild("jcr:content").isResourceType("silversea/silversea-com/components/pages/port")){
					String requestURl = resource.getPath();
					String portNameRequested = resource.getName();
					String redirectToApply = "";

					if(requestURl.contains("/en/other-resources/find-a-port")){
						redirectToApply = "/cruise-to/" + portNameRequested + WcmConstants.HTML_SUFFIX;
					}else if(requestURl.contains("/fr/other-resources/find-a-port")){
						redirectToApply = "/fr/croisieres/" + portNameRequested + WcmConstants.HTML_SUFFIX;
					}else if(requestURl.contains("/es/other-resources/find-a-port")){
						redirectToApply = "/es/cruceros/" + portNameRequested + WcmConstants.HTML_SUFFIX;
					}else if(requestURl.contains("/pt-br/other-resources/find-a-port")){
						redirectToApply = "/pt-br/cruzeiros/" + portNameRequested + WcmConstants.HTML_SUFFIX;
					}else if(requestURl.contains("/de/other-resources/find-a-port")){
						redirectToApply = "/de/kreuzfahrten/" + portNameRequested + WcmConstants.HTML_SUFFIX;
					}

					if(!redirectToApply.equals("")) {
						slingResponse.setStatus(SlingHttpServletResponse.SC_MOVED_PERMANENTLY);
						slingResponse.sendRedirect(redirectToApply);
						return;
					}

				}
			}
		}catch (Exception e){
			Logger.error("Issue with PortPageUrlFilter",e);
		}
         chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}