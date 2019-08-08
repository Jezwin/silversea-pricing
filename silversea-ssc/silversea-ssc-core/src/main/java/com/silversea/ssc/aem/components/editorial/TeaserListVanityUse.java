package com.silversea.ssc.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.components.AbstractGeolocationAwareUse;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.PageSSCModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TeaserListVanityUse extends AbstractSilverUse {

    private static final Logger log = LoggerFactory.getLogger(TeaserListVanityUse.class);

    private List<VanityPage> vanityPages;

    @Override
    public void activate() throws Exception {
        vanityPages = new ArrayList<VanityPage>();

        if (getResource().hasChildren() && getResource().getChild("ItemList") != null) {
            for (Resource child : getResource().getChild("ItemList").getChildren()) {


                VanityPage vp = new VanityPage(getProp("pageLink", child, String.class).orElse(""), getProp("imageover", child, String.class).orElse(""), getProp("titleover", child, String.class).orElse(""));
                vanityPages.add(vp);
            }
        }
    }

    public List<VanityPage> getVanityPages(){
        return vanityPages;
    }

    public class VanityPage {
        private String pagePath;
        private String image;
        private String title;

        public VanityPage(String Path, String image, String title){
            this.title = title;
            this.image = image;
            this.pagePath = Path;
        }

        public String getPagePath() {
            return pagePath;
        }

        public String getImage(){
            return image;
        }

        public String getTitle(){
            return title;
        }
    }

}
