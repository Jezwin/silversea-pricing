package com.silversea.aem.components.included.header;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.*;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.TypeReference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.silversea.aem.utils.AssetUtils.buildSilverseaAsset;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class Header2019Use extends AbstractSilverUse {

    private static final int MAX_NUM_OF_SND_ROW = 7;
    private GlobalCacheService globalCacheService;

    private SilverseaAsset logo;
    private List<ExternalLink> topLinks;
    private List<HeaderSecondRowMenu> secondRow;
    private List<ExternalLink> mobileLinks;

    private List<ExternalLink> languages;
    private Page homePage;
    private ExternalLink requestAQuotePath;
    private ExternalLink mySilverseaPath;

    private Page search;

    private final static String TOP_LINKS = "jcr:content/header2019/topLinks";
    private final static String SUB_MENU = "jcr:content/header2019/subMenu";
    private final static String MOBILE_LINKS = "jcr:content/header2019/mobileLinks";

    @Override
    public void activate() throws Exception {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        HierarchyNodeInheritanceValueMap inheritedProps = new HierarchyNodeInheritanceValueMap(getResource());
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
        secondRow = retrieveSecondRow(externalizer, inheritedProps);
        homePage = getCurrentPage().getAbsoluteParent(2);
        languages = globalCacheService.getCache(getCurrentPage().getPath(), new TypeReference<List<ExternalLink>>() {
        }, () -> retrieveHomeLanguages(externalizer, getRequest(), getCurrentPage(), homePage));
        topLinks = findParentWithResourceChild(TOP_LINKS)
                .map(topLinksParent -> retrieveLinks(topLinksParent, externalizer, TOP_LINKS))
                .orElseGet(Collections::emptyList);
        mobileLinks = findParentWithResourceChild(MOBILE_LINKS)
                .map(topLinksParent -> retrieveLinks(topLinksParent, externalizer, MOBILE_LINKS))
                .orElseGet(Collections::emptyList);
        getInheritedProp(inheritedProps, "logoPath")
                .ifPresent(logoPath -> logo = buildSilverseaAsset(logoPath, getResourceResolver(), "header-logo", ""));
        requestAQuotePath = getInheritedProp(inheritedProps, "requestaquote").map(getPageManager()::getPage).map(MenuEntry::new)
                .map(entry -> entry.toExternalLink(externalizer, getRequest())).orElse(null);
        mySilverseaPath = getInheritedProp(inheritedProps, "mySilverseaPath").map(getPageManager()::getPage).map(MenuEntry::new)
                .map(entry -> entry.toExternalLink(externalizer, getRequest())).orElse(null);
        search = getInheritedProp(inheritedProps, "searchpath").map(getPageManager()::getPage).orElse(null);

    }

    private Optional<Resource> findParentWithResourceChild(String child) {
        Resource resource = getResource();
        while (resource != null && resource.getChild(child) == null) {
            resource = resource.getParent();
        }
        return Optional.ofNullable(resource);
    }

    private Optional<Resource> findSubMenuParent() {//only submenu with content exists, so any index can be the first valid one.
        return IntStream.range(1, MAX_NUM_OF_SND_ROW + 1).mapToObj(index -> findParentWithResourceChild(SUB_MENU + index)).filter(Optional::isPresent)
                .findAny().orElse(Optional.empty());
    }

    private List<ExternalLink> retrieveLinks(Resource parent, Externalizer externalizer, String links) {
        return retrieveMultiField(parent, links, resource -> resource.adaptTo(MenuEntry.class))
                .map(entry -> entry.toExternalLink(externalizer, getRequest()))
                .collect(toList());
    }

    private List<HeaderSecondRowMenu> retrieveSecondRow(Externalizer externalizer, InheritanceValueMap inheritedProps) {
        Resource subMenuParent = findSubMenuParent().orElse(null);
        return IntStream.range(1, MAX_NUM_OF_SND_ROW + 1)
                .mapToObj(i -> secondRow(externalizer, inheritedProps, subMenuParent, i))
                .filter(entry -> entry.getLink() != null || entry.getLabel() != null)
                .collect(toList());
    }


    private HeaderSecondRowMenu secondRow(Externalizer externalizer, InheritanceValueMap props, Resource subMenuParent, int index) {
        //if there is a subMenu it'll have a subMenu. If there isn't the direct link it won't be a link. If there is no label nor link it will be null
        String label = getInheritedProp(props, "label" + index).orElse(null);
        ExternalLink externalLink = getInheritedProp(props, "directLink" + index)
                .map(link -> getPageManager().getPage(link))
                .map(page -> new MenuEntry(page, label))
                .map(menuEntry -> menuEntry.toExternalLink(externalizer, getRequest()))
                .orElse(new ExternalLink(null, label));
        return new HeaderSecondRowMenu(externalLink, subEntries(externalizer, subMenuParent, index));
    }


    private List<SubMenuEntry> subEntries(Externalizer externalizer, Resource subMenuParent, int index) {
        return ofNullable(subMenuParent)
                .map(resource -> resource.getChild(SUB_MENU + index))
                .map(Resource::getChildren)
                .map(children -> StreamSupport.stream(children.spliterator(), false))
                .map(children -> children
                        .map(child -> retrieveSubMenuEntry(externalizer, getRequest(), child))
                        .filter(Objects::nonNull)
                        .collect(toList()))
                .orElse(Collections.emptyList());
    }

    private SubMenuEntry retrieveSubMenuEntry(Externalizer externalizer, SlingHttpServletRequest request, Resource entry) {
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
                menuEntry -> menuEntry.toExternalLink(externalizer, request)).collect(toList());
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

    private Page appendCurrentPath(Page currentPage, Page page) {
        return getPageManager().getPage(page.getPath() + currentPage.getPath().split(homePage.getPath())[1]);
    }

    private List<ExternalLink> retrieveHomeLanguages(Externalizer externalizer, SlingHttpServletRequest request, Page currentPage, Page homePage) {
        Iterable<Page> list = () -> homePage.getParent().listChildren();
        return StreamSupport.stream(list.spliterator(), false)
                .filter(page -> !page.getPath().contains(homePage.getPath()))
                .map(page -> new MenuEntry(appendCurrentPath(currentPage, page), page.getNavigationTitle()))
                .map(menuEntry -> menuEntry.toExternalLink(externalizer, request))
                .collect(Collectors.toList());

    }

    public Page getSearch() {
        return search;
    }

    public SilverseaAsset getLogo() {
        return logo;
    }

    public ExternalLink getRequestAQuotePath() {
        return requestAQuotePath;
    }

    public List<ExternalLink> getMobileLinks() {
        return mobileLinks;
    }

    public ExternalLink getMySilverseaPath() {
        return mySilverseaPath;
    }


}
