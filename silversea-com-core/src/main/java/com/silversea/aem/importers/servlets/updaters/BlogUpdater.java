package com.silversea.aem.importers.servlets.updaters;

import com.silversea.aem.importers.utils.MigrationUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

@SlingServlet(paths = "/bin/blog-updater", extensions = "html")
public class BlogUpdater extends SlingSafeMethodsServlet {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        final String query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/blogpost\"]";

        final String assetsQuery = "/jcr:root/content/dam/silversea-com/blog"
                + "//element(*,dam:Asset)[jcr:content/metadata/initialPath]";

        MigrationUtils.updatePagesAfterMigration(resourceResolverFactory,
                query, assetsQuery, Arrays.asList("assetSelectionReferenceImported"), null);
    }
}
