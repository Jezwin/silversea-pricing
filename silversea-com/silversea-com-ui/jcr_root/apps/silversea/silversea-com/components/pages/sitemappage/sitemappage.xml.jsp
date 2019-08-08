<%@page contentType="text/xml"
    import="com.silversea.aem.components.page.SitemapModel,
    com.silversea.aem.components.page.SitemapEntryModel" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
SitemapModel sitemap = slingRequest.adaptTo(SitemapModel.class);
%><?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"
    xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"><%
    if (sitemap != null) {
        for (SitemapEntryModel sitemapEntry : sitemap.getEntries()) {
            request.setAttribute("sitemapEntry", sitemapEntry); %>
            <cq:include script="sitemapentry.xml.jsp" /><%
        }
    } %>
</urlset>