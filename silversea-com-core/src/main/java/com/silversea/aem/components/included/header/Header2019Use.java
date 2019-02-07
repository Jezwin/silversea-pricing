package com.silversea.aem.components.included.header;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.*;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.TypeReference;
import com.silversea.aem.utils.AssetUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.*;
import java.util.List;
import java.util.stream.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class Header2019Use extends AbstractSilverUse {

    private static final int MAX_NUM_OF_SND_ROW = 7;
    private GlobalCacheService globalCacheService;

    private SilverseaAsset logo;
    private List<ExternalLink> topLinks;
    private List<HeaderSecondRowMenu> secondRow;

    private List<ExternalLink> languages;
    private Page homePage;
    private String requestAQuotePath;

    private Page search;

    @Override
    public void activate() throws Exception {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
        secondRow = retrieveSecondRow(externalizer);
        homePage = getCurrentPage().getAbsoluteParent(2);
        languages = globalCacheService.getCache(homePage, new TypeReference<List<ExternalLink>>() {
        }, () -> retrieveHomeLanguages(externalizer, homePage));
        topLinks = retrieveTopLinks(externalizer);
        getProp("logoPath").ifPresent(logoPath -> logo = AssetUtils.buildSilverseaAsset(logoPath, getResourceResolver(), "header-logo", ""));
        requestAQuotePath = getProp("requestaquote").orElse("");
        search = getProp("searchPage").map(getPageManager()::getPage).orElse(null);

    }

    private List<ExternalLink> retrieveTopLinks(Externalizer externalizer) {
        return retrieveMultiField("topLinks", resource -> resource.adaptTo(MenuEntry.class))
                .map(entry -> entry.toExternalLink(externalizer, getResourceResolver()))
                .collect(toList());
    }

    private List<HeaderSecondRowMenu> retrieveSecondRow(Externalizer externalizer) {
        return IntStream.range(1, MAX_NUM_OF_SND_ROW + 1)
                .filter(i -> getProp("label" + i).isPresent())
                .mapToObj(i -> secondRow(externalizer, i))
                .collect(toList());
    }

    private HeaderSecondRowMenu secondRow(Externalizer externalizer, int index) {
        String label = getProp("label" + index, "");
        return getProp("directLink" + index)
                .map(s -> new HeaderSecondRowMenu(new ExternalLink(s, label, externalizer, getResourceResolver())))
                .orElseGet(() -> new HeaderSecondRowMenu(label, subEntries(externalizer, getResource().getChild("subMenu" + index))));
    }

    private List<SubMenuEntry> subEntries(Externalizer externalizer, Resource subMenu) {
        return ofNullable(subMenu)
                .map(Resource::getChildren)
                .map(children -> StreamSupport.stream(children.spliterator(), false))
                .map(children -> children
                        .map(child -> retrieveSubMenuEntry(externalizer, getResourceResolver(), child))
                        .filter(Objects::nonNull)
                        .collect(toList()))
                .orElse(Collections.emptyList());
    }

    private SubMenuEntry retrieveSubMenuEntry(Externalizer externalizer, ResourceResolver resourceResolver, Resource entry) {
        ValueMap valueMap = entry.getValueMap();
        String title = valueMap.get("title", String.class);
        if (title == null) {
            return null;
        }
        SilverseaAsset picture = ofNullable(valueMap.get("picture", String.class)).map(picPath -> {
            SilverseaAsset asset = new SilverseaAsset();
            asset.setPath(picPath);
            asset.setName(title);
            return asset;
        }).orElse(null);
        List<String> excludedPages = retrieveMultiField(entry, "excludedPages", resource -> resource.getChild("path"))
                .map(path -> path.adaptTo(String.class))
                .collect(toList());
        Stream<MenuEntry> linksFromPage =
                subPages(valueMap.get("parentPage", String.class))
                        .filter(subPageEntry -> !excludedPages.contains(subPageEntry.getPage().getPath()))
                        .filter(subPageEntry -> !subPageEntry.getPage().isHideInNav());
        Stream<MenuEntry> manualEntries = retrieveMultiField(entry, "manualEntries", resource -> resource.adaptTo(MenuEntry.class));
        List<ExternalLink> allLinks = concat(linksFromPage, manualEntries).map(
                menuEntry -> menuEntry.toExternalLink(externalizer, resourceResolver)).collect(toList());
        return new SubMenuEntry(title, picture, allLinks);
    }

    private Stream<MenuEntry> subPages(String pagePath) {
        if (pagePath == null) {
            return Stream.empty();
        }
        Iterable<Page> children = getPageManager().getPage(pagePath)::listChildren;
        return StreamSupport.stream(children.spliterator(), false).filter(Objects::nonNull).map(MenuEntry::new);
    }

    public List<ExternalLink> getTopLinks() {
        return topLinks;
    }

    public List<HeaderSecondRowMenu> getSecondRow() {
        return secondRow;
    }

    public List<ExternalLink> getLanguages() {
        return languages;
    }

    public Page getHomePage() {
        return homePage;
    }

    private List<ExternalLink> retrieveHomeLanguages(Externalizer externalizer, Page homePage) {
        Iterable<Page> pages = () -> homePage.getParent().listChildren(new PageFilter());
        return StreamSupport
                .stream(pages.spliterator(), false)
                .filter(page -> !page.getPath().equals(homePage.getPath()))
                .map(page -> new MenuEntry(page, page.getNavigationTitle()))
                .map(entry -> entry.toExternalLink(externalizer, getResourceResolver()))
                .collect(toList());
    }

    public Page getSearch() {
        return search;
    }

    public SilverseaAsset getLogo() {
        return logo;
    }

    public String getRequestAQuotePath() {
        return requestAQuotePath;
    }

}
