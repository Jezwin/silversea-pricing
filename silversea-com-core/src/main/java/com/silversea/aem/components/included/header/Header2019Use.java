package com.silversea.aem.components.included.header;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.ExternalLink;
import com.silversea.aem.models.FirstLevelMenuLinkSubMenu;
import com.silversea.aem.models.MenuEntry;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.TypeReference;
import com.silversea.aem.utils.AssetUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Header2019Use extends AbstractSilverUse {

    private GlobalCacheService globalCacheService;

    private SilverseaAsset logo;
    private List<ExternalLink> topLinks;
    private List<FirstLevelMenuLinkSubMenu> secondRow;

    private List<ExternalLink> languages;
    private Page homePage;
    private String requestAQuotePath;

    @Override
    public void activate() throws Exception {
        Externalizer externalizer = getResourceResolver().adaptTo(Externalizer.class);
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
        secondRow = retrieveSecondRow2();
        homePage = getCurrentPage().getAbsoluteParent(2);
        languages = globalCacheService.getCache(homePage, new TypeReference<List<ExternalLink>>() {
        }, () -> retrieveHomeLanguages(externalizer, homePage));
        topLinks = retrieveTopLinks(externalizer);
        getProp("logoPath").ifPresent(logoPath -> logo = AssetUtils.buildSilverseaAsset(logoPath, getResourceResolver(), "header-logo", ""));
        requestAQuotePath = getProp("requestaquote").orElse("");

    }

    private List<ExternalLink> retrieveTopLinks(Externalizer externalizer) {
        return retrieveMultiField("topLinks", resource -> resource.adaptTo(MenuEntry.class))
                .map(entry -> entry.toExternalLink(externalizer, getResourceResolver()))
                .collect(Collectors.toList());
    }

    private List<FirstLevelMenuLinkSubMenu> retrieveSecondRow2() {
        return retrieveMultiField("secondRow",
                resource -> resource.adaptTo(FirstLevelMenuLinkSubMenu.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }


    public List<ExternalLink> getTopLinks() {
        return topLinks;
    }

    public List<FirstLevelMenuLinkSubMenu> getSecondRow() {
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
                .collect(Collectors.toList());
    }

    public SilverseaAsset getLogo() {
        return logo;
    }

    public String getRequestAQuotePath() {
        return requestAQuotePath;
    }

}
