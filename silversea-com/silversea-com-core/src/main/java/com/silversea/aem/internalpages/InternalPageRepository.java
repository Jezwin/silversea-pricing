package com.silversea.aem.internalpages;

import com.amazonaws.util.IOUtils;
import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.importers.servlets.FullImportServlet;
import com.silversea.aem.importers.servlets.UpdateImportServlet;
import com.silversea.aem.logging.SSCLogger;
import com.silversea.aem.services.CruisesCacheService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static freemarker.template.Configuration.VERSION_2_3_23;
import static java.util.stream.Collectors.toList;
import static io.vavr.API.*;
import static io.vavr.Predicates.*;


public class InternalPageRepository {
    private ResourceResolver resourceResolver;
    private Configuration cfg;
    private CruisesCacheService cruiseCache;

    private final String importResultPage = "importResult";

    public InternalPageRepository(ResourceResolver resourceResolver, CruisesCacheService cruiseCache) {
        this.resourceResolver = resourceResolver;
        this.cruiseCache = cruiseCache;
        cfg = new Configuration(VERSION_2_3_23);
        DefaultObjectWrapper objectWrapper = new DefaultObjectWrapper(VERSION_2_3_23);
        // Allows us to use vavr.io collections in templates.
        objectWrapper.setIterableSupport(true);
        cfg.setObjectWrapper(objectWrapper);
        StringTemplateLoader loader = new StringTemplateLoader();
        get(importResultPage).ifPresent(t -> loader.putTemplate(importResultPage, t));
        cfg.setTemplateLoader(loader);
    }

    private Optional<String> get(String pageName) {
        String path = "/jcr:root/apps/silversea/silversea-com/components/internalpages/" + pageName + ".html/jcr:content";
        List<Resource> res = List.ofAll(IteratorUtils.toList(resourceResolver.findResources(path, null)));
        return Try.of(() -> res.get(0).adaptTo(Node.class)).mapTry(this::loadJcrBinary).toJavaOptional();
    }

    private String loadJcrBinary(Node n) throws IOException, RepositoryException {
        return IOUtils.toString(n.getProperty("jcr:data").getBinary().getStream());
    }

    private String render(Map<String, Object> viewModel, Template template) throws TemplateException, IOException {
        Writer writer = new StringWriter();
        template.process(viewModel, writer);
        return writer.toString();
    }

    private Try<String> getImportResultPage(String name, Map<String, ImportResult> results, List<String> modes,
                                            HashMap<String, Object> model, List<String> errors) {
        Map<String, Object> viewModel = new HashMap<String, Object>(model) {
            {
                put("results", results);
                put("modes", modes);
                put("importerName", name);
                put("cruiseCacheSizeEn", Try.of(() -> String.valueOf(cruiseCache.getCruises("en").size()))
                        .getOrElse("Unable to query cruise cache. Maybe it needs rebuilding?"));
            }
        };
        if (!errors.isEmpty()) viewModel.put("errors", errors);
        return Try.of(() -> cfg.getTemplate(importResultPage)).mapTry(t -> render(viewModel, t));
    }

    public Try<String> fullImportPage(Map<String, ImportResult> results, List<String> errors) {
        List<String> modes = List.of(FullImportServlet.Mode.values()).map(Enum::name);
        return getImportResultPage("Full Import", results, modes, new HashMap<>(), errors);
    }

    public Try<String> diffImportPage(Map<String, ImportResult> results, List<String> errors) {
        List<String> modes = List.of(UpdateImportServlet.Mode.values()).map(Enum::name);
        HashMap<String, Object> model = new HashMap<String, Object>() {{
            put("caches", List.of(UpdateImportServlet.Cache.values()).map(Enum::name));
            put("replications", List.of(UpdateImportServlet.Replicate.values()).map(Enum::name));
        }};
        return getImportResultPage("Diff API", results, modes, model, errors);
    }

}
