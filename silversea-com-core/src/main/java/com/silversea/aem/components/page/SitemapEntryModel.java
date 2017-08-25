package com.silversea.aem.components.page;

import com.adobe.granite.confmgr.Conf;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model Sitemap
 * TODO review javadoc
 */
@Model(adaptables = Page.class)
public class SitemapEntryModel {

    private static final String PN_DEPTH = "depth";

    /**
     * Page
     */
    @Self
    private Page page;

    /**
     * Externalizer
     */
    @Inject
    private Externalizer externalizer;

    @Inject
    @Source("osgi-services")
    private SlingSettingsService slingSettingsService;

    /**
     * Depth Configuration
     */
    private Map<Integer, DepthConf> depthConfs;

    /**
     * Constructor
     */
    public SitemapEntryModel() {
        //Empty
    }

    /**
     * Initialize Component
     */
    @PostConstruct
    public void init() {
        final Conf conf = page.adaptTo(Conf.class);
        final List<ValueMap> sitemapConf = conf.getList("sitemap");

        depthConfs = new HashMap<>();

        for (ValueMap depthConf : sitemapConf) {
            if (depthConf.containsKey(PN_DEPTH)) {
                depthConfs.put(depthConf.get(PN_DEPTH, -1), new DepthConf(depthConf));
            }
        }
    }

    /**
     * Externalize URL
     *
     * @return
     */
    public String getExternalizedUrl() {
        ResourceResolver resourceResolver = page.getContentResource().getResourceResolver();
        Set<String> runModes = null;
        if (slingSettingsService != null) {
            runModes = slingSettingsService.getRunModes();
        }

        final String domain = runModes != null && !runModes.contains("author") ? Externalizer.PUBLISH : Externalizer.AUTHOR;
        if (page.getPath().equals("/content/silversea-com/en")) { // TODO constant
            return externalizer.externalLink(resourceResolver, domain, page.getPath() + ".html");
        }

        return externalizer.externalLink(resourceResolver, domain, page.getPath()) + ".html";
    }

    /**
     * Getter Last Modified
     *
     * @return
     */
    public String getLastModified() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(page.getLastModified().getTime());
    }

    /**
     * Getter Change Frequency
     *
     * @return (default weekly)
     */
    public String getChangeFrequency() {
        Integer pageDepth = page.getDepth();

        if (depthConfs != null && depthConfs.containsKey(pageDepth)) {
            return depthConfs.get(pageDepth).getChangeFrequency();
        }

        return "weekly";
    }

    /**
     * Getter Priority
     *
     * @return (default 0.4)
     */
    public Double getPriority() {
        Integer pageDepth = page.getDepth();

        if (depthConfs != null) {
            if (depthConfs.containsKey(pageDepth)) {
                return depthConfs.get(pageDepth).getPriority();
            }

            if (depthConfs.containsKey(-1)) {
                return depthConfs.get(-1).getPriority();
            }
        }

        return 0.4;
    }

    /**
     * Class Depth Configuration
     */
    protected class DepthConf {

        /**
         * Depth
         */
        private long depth;

        /**
         * Priority
         */
        private double priority;

        /**
         * Change Frequency
         */
        private String changeFrequency;

        /**
         * Constructor
         *
         * @param depthConfProperties
         */
        public DepthConf(final ValueMap depthConfProperties) {
            depth = depthConfProperties.get(PN_DEPTH, -1);
            priority = depthConfProperties.get("priority", 0.4);
            changeFrequency = depthConfProperties.get("changeFrequency", "weekly");
        }

        /**
         * Getter Priority
         *
         * @return
         */
        public double getPriority() {
            return priority;
        }

        /**
         * Getter Change Frequency
         *
         * @return
         */
        public String getChangeFrequency() {
            return changeFrequency;
        }

        /**
         * Getter Depth
         *
         * @return
         */
        public long getDepth() {
            return depth;
        }
    }
}
