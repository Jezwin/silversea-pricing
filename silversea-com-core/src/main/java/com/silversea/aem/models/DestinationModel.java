package com.silversea.aem.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by mbennabi on 17/02/2017.
 */
@Model(adaptables = Page.class)
public class DestinationModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(DestinationModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/mapLabel")
    @Optional
    private String mapLabel;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/excerpt")
    @Optional
    private String excerpt;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/destinationId")
    @Optional
    private Integer destinationId;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/footnote")
    @Optional
    private String footnote;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetselectionreference")
    @Optional
    private String assetselectionreference;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/category")
    @Optional
    private String category;


    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/customHtml")
    @Optional
    private String customHtml;

    @PostConstruct
    private void init() {
    }

    public String getTitle() {
        return title;
    }

    public String getMapLabel() {
        return mapLabel;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public String getLongDescription() { return longDescription; }

    public String getFootnote() { return footnote; }

    public String getAssetselectionreference() { return assetselectionreference; }

    public String getCategory() { return category; }

    public String getCustomHtml() { return customHtml; }


}
