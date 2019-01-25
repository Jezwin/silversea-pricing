package com.silversea.aem.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.utils.CruiseUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;

@Model(adaptables = Resource.class)
public class SubMenuEntry {

    private final String title;
    private final SilverseaAsset picture;
    private final List<ExternalLink> entries;

    @Inject
    Externalizer externalizer;


    @Inject
    ResourceResolver resourceResolver;

    public SubMenuEntry(ExternalLink link) {
        this.title = link.getLabel();
        this.picture = null;
        this.entries = Collections.singletonList(link);
    }

    @Inject
    public SubMenuEntry(@Named("title") String title, @Named("picture") @Optional String picture,
                        @Named("entries") @Optional List<MenuEntry> entries) {
        this.title = title;
        this.picture = ofNullable(picture).map(picPath -> {
            SilverseaAsset asset = new SilverseaAsset();
            asset.setPath(picture);
            asset.setName(title);
            return asset;
        }).orElse(null);
        this.entries = ofNullable(entries)
                .map(list -> list.stream().
                        map(entry -> entry.toExternalLink(externalizer, resourceResolver))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }

    public SubMenuEntry(Page page) {
        Iterable<Page> entries = page::listChildren;
        List<ExternalLink> menuEntries = StreamSupport.stream(entries.spliterator(), false)
                .map(subPage -> new MenuEntry(subPage, subPage.getNavigationTitle()))
                .map(entry -> entry.toExternalLink(externalizer, resourceResolver))
                .collect(Collectors.toList());
        this.title = CruiseUtils.firstNonNull(page.getNavigationTitle(), page.getTitle());
        this.picture = null;
        this.entries = menuEntries;
    }

    public String getTitle() {
        return title;
    }

    public SilverseaAsset getPicture() {
        return picture;
    }

    public List<ExternalLink> getEntries() {
        return entries;
    }
}

