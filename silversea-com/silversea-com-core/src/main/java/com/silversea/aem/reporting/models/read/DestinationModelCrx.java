package com.silversea.aem.reporting.models.read;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Model(adaptables = Page.class)
public class DestinationModelCrx {

    @Inject @Self
    Page page;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE)
    String title;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/navTitle") @Optional
    String navigationTitle;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/mapLabel") @Optional
    String mapLabel;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/excerpt") @Optional
    String excerpt;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/destinationId") @Optional
    String destinationId;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/destinationFareAdditionsClassic") @Optional
    String destinationFareAdditionsClassic;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/destinationFareAdditionsExpedition") @Optional
    String destinationFareAdditionsExpedition;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION) @Optional
    String description;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/longDescription") @Optional
    String longDescription;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/footnote") @Optional
    String footnote;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/assetselectionreference") @Optional
    String assetselectionreference;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/category") @Optional
    String category;

    @Inject @Named(JcrConstants.JCR_CONTENT + "/customHtml") @Optional
    String customHtml;

    String path;

    String name;

    List<String> splitDestinationFareAdditionsClassic = new ArrayList<>();
    List<String> splitDestinationFareAdditionsExpedition = new ArrayList<>();

    @PostConstruct
    private void init() {
        path = page.getPath();
        name = page.getName();
        splitDestinationFareAdditionsClassic = splitCruiseFareAdditions(destinationFareAdditionsClassic);
        splitDestinationFareAdditionsExpedition = splitCruiseFareAdditions(destinationFareAdditionsExpedition);
    }

    private List<String> splitCruiseFareAdditions(String cruiseFareAdditions) {
        if (StringUtils.isNotEmpty(cruiseFareAdditions)) {
            final String[] split = cruiseFareAdditions.split("\\r?\\n");
            if (split.length > 0) {
                return Arrays.asList(split);
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getNavigationTitle() {
        return navigationTitle;
    }

    public String getMapLabel() {
        return mapLabel;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getFootnote() {
        return footnote;
    }

    public String getAssetselectionreference() {
        return assetselectionreference;
    }

    public String getCategory() {
        return category;
    }

    public String getCustomHtml() {
        return customHtml;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DestinationModelCrx)) {
            return false;
        }

        final DestinationModelCrx objDestinationModel = (DestinationModelCrx)obj;

        return objDestinationModel.getPath().equals(getPath());
    }

    public List<String> getDestinationFareAdditionsClassic() {
        return splitDestinationFareAdditionsClassic;
    }

    public List<String> getDestinationFareAdditionsExpedition() {
        return splitDestinationFareAdditionsExpedition;
    }
}
