package com.silversea.aem.models;

import com.day.cq.commons.LanguageUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.UrlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Model(adaptables = Page.class)
public class BlogPostTeaserModel {

    static final private Logger LOGGER = LoggerFactory.getLogger(BlogPostTeaserModel.class);

    @Inject
    @Self
    private Page page;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    private String title;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/longDescription")
    @Optional
    private String longDescription;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/publicationDate")
    @Optional
    private Calendar publicationDate;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + "/assetSelectionReference")
    @Optional
    private String assetSelectionReference;

    private String path;

    @PostConstruct
    private void init() {
        path = page.getPath();
    }

    public Page getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    @Deprecated
    public String getFormatPublicationDate() {
        String formatDate = "";
        try{

            if (publicationDate != null) {
                String languageRootPath = LanguageUtil.getLanguageRoot(page.getContentResource().getPath());
                String lang = languageRootPath.split("/")[languageRootPath.split("/").length - 1];
                StringBuilder builder = new StringBuilder();
                builder.append("<span class='number-value'>");
                builder.append(publicationDate.get(Calendar.DAY_OF_MONTH));
                builder.append("</span>&nbsp;");
                builder.append("<span class='span-date'>");
                builder.append(publicationDate.getDisplayName(Calendar.MONTH, Calendar.LONG, LanguageUtil.getLocale(lang)));
                builder.append("&nbsp;");
                builder.append(publicationDate.get(Calendar.YEAR));
                builder.append("</span>");
                formatDate = builder.toString();
            }
        }catch(RuntimeException e){
            LOGGER.error("Error while initializing model {}",e);
        }
        return formatDate;

    }

    public String getPath() {
        return path;
    }

    public String getProperUrl() {
        return UrlHelper.getProperUrl(path);
    }

    public String getThumbnailImageUrl() {
        Resource resource = page.getContentResource().getChild("image");
        ValueMap value = resource.getValueMap();
        String imagePath = value.get("fileReference", String.class);
        if (!StringUtils.isNotEmpty(imagePath)) {
            imagePath = "/content/dam/silversea-com/blog/noimage.png";
        }
        return imagePath;
    }

}
