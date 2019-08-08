package com.silversea.ssc.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NavBarDCOUse extends WCMUsePojo {

    static private final Logger LOGGER = LoggerFactory.getLogger(NavBarDCOUse.class);
    private List<Cta> ctaList;
    @Override
    public void activate() throws Exception {
        this.setCtaList(this.getRecords());
    }

    public List<Cta> getRecords() {
        List<Cta> ctaList = new ArrayList<Cta>();
        if (getResource().hasChildren() && getResource().getChild("ctaList") != null) {
            for (Resource child : getResource().getChild("ctaList").getChildren()) {
                Node node = child.adaptTo(Node.class);
                Cta cta = new Cta();
                cta.setLabel(getProp(node, "label").orElse(""));
                cta.setPath(getProp(node, "path").orElse(""));
                cta.setLightboxActivated(getProp(node, "lightboxActivated").orElse(""));
                ctaList.add(cta);
            }
        }
        return ctaList;
    }

    private Optional<String> getProp(Node node, String key) {
        try {
            if (!node.hasProperty(key)) return Optional.empty();
            return Optional.ofNullable(node.getProperty(key)).map(property -> {
                try {
                    return property.getString();
                } catch (RepositoryException exception) {
                    return null;
                }
            });
        } catch (RepositoryException exception) {
            return Optional.empty();
        }
    }

    public List<Cta> getCtaList() {
        return ctaList;
    }

    public void setCtaList(List<Cta> ctaList) {
        this.ctaList = ctaList;
    }

    public class Cta {
        private String label;
        private String path;
        private String lightboxActivated;

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setLightboxActivated(String lightboxActivated) {
            this.lightboxActivated = lightboxActivated;
        }

        public String getLightboxActivated() {
            return lightboxActivated;
        }
    }
}
