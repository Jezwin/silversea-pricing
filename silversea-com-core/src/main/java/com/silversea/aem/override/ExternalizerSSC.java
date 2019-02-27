package com.silversea.aem.override;

import com.silversea.aem.constants.WcmConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.day.cq.commons.Externalizer;

public class ExternalizerSSC  {

    public static String publishLink(ResourceResolver resourceResolver, String s) {

      /*  String portNameRequested = getPortNameRequested(s);
        if (portNameRequested != null) return portNameRequested;*/

        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        return externalizer.publishLink(resourceResolver,s);
    }

    public static String externalLink(ResourceResolver resourceResolver, String s, String s1){

       /* String portNameRequested = getPortNameRequested(s1);
        if (portNameRequested != null) return portNameRequested;*/

        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        return externalizer.externalLink(resourceResolver,s,s1);
    }

  /*  private static String getPortNameRequested(String s) {
        try {
            if (s.contains("/en/other-resources/find-a-port")) {
                String portNameRequested = s.substring(s.lastIndexOf('/') + 1);
                if(portNameRequested.length() > 2 && !portNameRequested.equals("find-a-port"))
                    return "https://www.silversea.com" + "/cruise-to/" + portNameRequested;
            } else if (s.contains("/fr/other-resources/find-a-port")) {
                String portNameRequested = s.substring(s.lastIndexOf('/') + 1);
                if(portNameRequested.length() > 2 && !portNameRequested.equals("find-a-port"))
                    return "https://www.silversea.com" + "/fr/croisieres/" + portNameRequested;
            } else if (s.contains("/es/other-resources/find-a-port")) {
                String portNameRequested = s.substring(s.lastIndexOf('/') + 1);
                if(portNameRequested.length() > 2 && !portNameRequested.equals("find-a-port"))
                    return "https://www.silversea.com" + "/es/cruceros/" + portNameRequested;
            } else if (s.contains("/pt-br/other-resources/find-a-port")) {
                String portNameRequested = s.substring(s.lastIndexOf('/') + 1);
                if(portNameRequested.length() > 2 && !portNameRequested.equals("find-a-port"))
                    return "https://www.silversea.com" + "/pt-br/cruzeiros/" + portNameRequested;
            } else if (s.contains("/de/other-resources/find-a-port")) {
                String portNameRequested = s.substring(s.lastIndexOf('/') + 1);
                if(portNameRequested.length() > 2 && !portNameRequested.equals("find-a-port"))
                    return "https://www.silversea.com" + "/de/kreuzfahrten/" + portNameRequested;
            }
        }catch (Exception e){

        }
        return null;
    }*/

    public static String relativeLink(SlingHttpServletRequest request, String path, ResourceResolver resourceResolver) {
       /* String portNameRequested = getPortNameRequested(path);
        if (portNameRequested != null) return portNameRequested;*/

        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        return externalizer.relativeLink(request,path);
    }
}
