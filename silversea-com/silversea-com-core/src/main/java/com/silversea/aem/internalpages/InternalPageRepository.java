package com.silversea.aem.internalpages;

import com.amazonaws.util.IOUtils;
import com.jasongoodwin.monads.Try;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.importers.servlets.FullImportServlet;
import com.silversea.aem.logging.SSCLogger;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jasongoodwin.monads.Try.ofFailable;


public class InternalPageRepository {
    private ResourceResolver resourceResolver;
    private Configuration cfg;

    public InternalPageRepository(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader loader = new StringTemplateLoader();
        get("fullImportResult").ifPresent(t -> loader.putTemplate("fullImportResult", ""));
        cfg.setTemplateLoader(loader);
    }

    private Optional<String> get(String pageName) {
        String path = "/jcr:root/apps/silversea/silversea-com/components/internalpages/" + pageName + ".html/jcr:content";
        List<Resource> res = IteratorUtils.toList(resourceResolver.findResources(path, null));
        return ofFailable(() -> res.get(0).adaptTo(Node.class)).map(this::loadJcrBinary).toOptional();
    }

    private String loadJcrBinary(Node n) throws IOException, RepositoryException {
        return IOUtils.toString(n.getProperty("jcr:data").getBinary().getStream());
    }

    private String render(Map<String, Object> viewModel, Template template) throws TemplateException, IOException {
        Writer writer = new StringWriter();
        template.process(viewModel, writer);
        return writer.toString();
    }

    public Try<String> getFullImportResultPage(Map<FullImportServlet.Mode, ImportResult> results, List<String> modes) {
        Map<String, Object> viewModel = new HashMap<String, Object>() {{ put("results", results); put("modes", modes); }};
        return ofFailable(() -> cfg.getTemplate("fullImportResult")).map(t -> render(viewModel, t));
    }


    /*
        How to use this:

        InternalPageRepository repo = new InternalPageRepository(request.getResourceResolver());
        String content = repo.getFullImportResultPage(results, modes).recover(error -> ...);
        response.getWriter().write(content);

     */

}
