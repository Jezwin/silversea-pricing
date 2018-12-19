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

import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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
public class AliasNameCheckFilter implements Filter {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AliasNameCheckFilter.class.getName());
    
    private static final String ERROR_PAGE_PATH = "/content/silversea-com/en/error-404.html";

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
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
		    String pathInfo = httpRequest.getRequestURI().toString();
		    String  realpagename = "";
		    if (pathInfo.lastIndexOf(".html") != -1 && pathInfo.lastIndexOf("/") != -1) {
		    	realpagename = pathInfo.substring(pathInfo.lastIndexOf("/") + 1,pathInfo.lastIndexOf(".html"));
		    }
		    //Convert request & response into corresponding SlingHttpServletResponse & SlingHttpServletRequest.
	        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
	        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
	        final Resource resource = slingRequest.getResource();
	        
	        // Check if resource is page type, if not by pass and let process request.
	        if(resource.getResourceType().equals("cq:Page")) {
		         PageManager pageManager =  resource.getResourceResolver().adaptTo(PageManager.class);
		         Page page = pageManager.getPage(resource.getPath());
		         String aliasName = "";
		         if (page.getContentResource().getValueMap().containsKey("sling:alias")) {
		        	 aliasName =  page.getContentResource().getValueMap().get("sling:alias").toString(); 
		        	 if (realpagename != "" && !realpagename.equals(aliasName)) {
		        		 Logger.debug("Returning 404 since requested page name doesn't match the sling alias name");
		        		 slingResponse.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
		        	     RequestDispatcher dispatcher = slingRequest.getRequestDispatcher(ERROR_PAGE_PATH);
		        	     dispatcher.include(slingRequest,slingResponse);
		 		        return; 
		        	 }
		         } else {
		        	 chain.doFilter(request, response); 
		         }
	        }
         chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}