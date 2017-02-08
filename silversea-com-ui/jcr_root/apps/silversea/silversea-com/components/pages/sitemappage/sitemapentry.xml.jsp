<%@page contentType="text/xml" import="com.silversea.aem.components.page.SitemapEntryModel" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
SitemapEntryModel sitemapEntry = (SitemapEntryModel)request.getAttribute("sitemapEntry");

if (sitemapEntry != null) {
%><url>
    <loc><%=sitemapEntry.getExternalizedUrl() %></loc>
    <lastmod><%=sitemapEntry.getLastModified() %></lastmod>
    <changefreq><%=sitemapEntry.getChangeFrequency() %></changefreq>
    <priority><%=sitemapEntry.getPriority().toString() %></priority>
</url><%
} %>