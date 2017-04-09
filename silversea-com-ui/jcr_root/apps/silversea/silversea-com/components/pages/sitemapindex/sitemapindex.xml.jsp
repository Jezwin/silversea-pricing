<%@page contentType="text/xml" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    <c:forEach items="${pageProperties.sitemap}" var="sitemapentry" >
        <sitemap>
            <loc>${sitemapentry}</loc>
        </sitemap>
    </c:forEach>
</sitemapindex>