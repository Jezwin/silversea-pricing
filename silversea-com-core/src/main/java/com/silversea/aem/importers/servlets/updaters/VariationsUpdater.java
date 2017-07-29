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

@SlingServlet(paths = "/bin/variations-updater", extensions = "html")
public class VariationsUpdater extends SlingSafeMethodsServlet {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        // Public areas
        String query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/publicareavariation\"]";

        String assetsQuery = "/jcr:root/content/dam/silversea-com/variations/public-areas"
                + "//element(*,dam:Asset)[jcr:content/metadata/initialPath]";

        MigrationUtils.updatePagesAfterMigration(resourceResolverFactory,
                query, assetsQuery, Arrays.asList("assetSelectionReferenceImported"), null, true);

        // Dinings
        query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/diningvariation\"]";

        assetsQuery = "/jcr:root/content/dam/silversea-com/variations/dinings"
                + "//element(*,dam:Asset)[jcr:content/metadata/initialPath]";

        MigrationUtils.updatePagesAfterMigration(resourceResolverFactory,
                query, assetsQuery, Arrays.asList("assetSelectionReferenceImported"), null, true);

        // Suites
        query = "/jcr:root/content/silversea-com//element(*,cq:Page)" +
                "[jcr:content/sling:resourceType=\"silversea/silversea-com/components/pages/suitevariation\"]";

        assetsQuery = "/jcr:root/content/dam/silversea-com/variations/suites"
                + "//element(*,dam:Asset)[jcr:content/metadata/initialPath]";

        MigrationUtils.updatePagesAfterMigration(resourceResolverFactory,
                query, assetsQuery, Arrays.asList("assetSelectionReferenceImported"), Arrays.asList("planImported"), true);
    }
}
