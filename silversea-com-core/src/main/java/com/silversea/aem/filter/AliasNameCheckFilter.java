package com.silversea.aem.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.featureflags.Features;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Service(Servlet.class)
@SlingFilter(
        label = "Alias Name Check Filter",
        description = "Custom Utility Redirect Filter",
        metatype = false,
        generateComponent = true,
        generateService = true,
        order = 100,
        scope = SlingFilterScope.REQUEST)
@Properties({
		@Property(name = "sling.filter.pattern", value = ".*/silversea-com/.+html$")//,
	//	@Property(name = "sling.filter.extensions", value = {"pdf"}), version too old - not supported by slign engine
		//@Property(name = "sling.filter.resourceTypes", value = "silversea/silversea-com/components/pages/page") version too old - not supported by slign engine
})
public class AliasNameCheckFilter implements Filter {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AliasNameCheckFilter.class.getName());
    
    private static final String ERROR_PAGE_PATH = "/content/silversea-com/en/error-404.html";

	@Reference
	private SlingSettingsService slingSettingsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    	if (!(request instanceof SlingHttpServletRequest)) {
    		chain.doFilter(request, response);
    		return;
    	}

	    //Get request URI and substring last keywords before .html and last /
		try {
			final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
			String pathInfo = slingRequest.getRequestURI().toString();
			//Convert request & response into corresponding SlingHttpServletResponse & SlingHttpServletRequest.

			final Resource resource = slingRequest.getResource();

			// Check if resource is page type, if not by pass and let process request.
			if (resource.getResourceType().equals("cq:Page") && pathInfo.lastIndexOf(".html") == pathInfo.length()-5 ) {
				if (slingSettingsService.getRunModes().contains("local")){
					//we are on an author
					chain.doFilter(request, response);
					return;
				}

					String realpagename = "";
				if (pathInfo.lastIndexOf(".html") != -1 && pathInfo.lastIndexOf("/") != -1) {
					realpagename = pathInfo.substring(pathInfo.lastIndexOf("/") + 1, pathInfo.lastIndexOf(".html"));
					if(realpagename.contains(".")){
						chain.doFilter(request, response);
						return;
					}
				}

				PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
				Page page = pageManager.getPage(resource.getPath());
				String aliasName = "";
				if (page.getContentResource().getValueMap().containsKey("sling:alias")) {
					aliasName = page.getContentResource().getValueMap().get("sling:alias").toString();
					if (realpagename != "" && !realpagename.equals(aliasName)) {
						final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
						Logger.debug("Returning 404 since requested page name doesn't match the sling alias name");
						slingResponse.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
						RequestDispatcher dispatcher = slingRequest.getRequestDispatcher(ERROR_PAGE_PATH);
						dispatcher.include(slingRequest, slingResponse);
						return;
					}
				} else {
					chain.doFilter(request, response);
					return;
				}
			}
			chain.doFilter(request, response);
			return;

		}catch(Exception e){
			chain.doFilter(request, response);
		}

    }

    @Override
    public void destroy() {

    }

}