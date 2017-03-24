package com.silversea.aem.models;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.helper.UrlHelper;

/**
 * Created by mbennabi on 20/02/2017.
 */
@Model(adaptables = Page.class)
public class BlogPostTeaserModel {

    @Inject
    private ResourceResolverFactory resourceResolverFactory;

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
    private Date publicationDate;

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

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getAssetSelectionReference() {
        return assetSelectionReference;
    }

    public String getFormatPublicationDate() {
        Calendar cal = DateUtils.toCalendar(publicationDate);
        StringBuilder builder = new StringBuilder();
        builder.append("<span class='number-value'>");
        builder.append(cal.get(Calendar.DAY_OF_MONTH));
        builder.append("</span>&nbsp;");
        builder.append("<span class='span-date'>");
        builder.append(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
        builder.append("&nbsp;");
        builder.append(cal.get(Calendar.YEAR));
        builder.append("</span>");
        return builder.toString();
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
        return value.get("fileReference", String.class);
    }

}
