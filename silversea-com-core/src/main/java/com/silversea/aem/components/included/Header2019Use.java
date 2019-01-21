package com.silversea.aem.components.included;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.models.SilverseaAsset;
import com.silversea.aem.services.GlobalCacheService;
import com.silversea.aem.services.TypeReference;
import com.silversea.aem.utils.AssetUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Header2019Use extends AbstractSilverUse {

    private GlobalCacheService globalCacheService;

    private List<MenuEntry> firstLevelLinks;
    private List<MenuEntry> topLinks;
    private List<MenuEntry> languages;
    private MenuEntry homePage;
    private SilverseaAsset logo;

    @Override
    public void activate() throws Exception {
        globalCacheService = getSlingScriptHelper().getService(GlobalCacheService.class);
        firstLevelLinks = retrieveFirstLevelLinks();
        homePage = new MenuEntry(getCurrentPage().getAbsoluteParent(2), "silversea");
        languages = globalCacheService.getCache(homePage.getPage(), new TypeReference<List<MenuEntry>>() {
        }, () -> retrieveHomeLanguages(homePage));
        topLinks = retrieveTopLinks();
        getProp("logoPath").ifPresent(logoPath -> logo = AssetUtils.buildSilverseaAsset(logoPath, getResourceResolver(), "header-logo", ""));

    }

    private List<MenuEntry> retrieveTopLinks() {
        return Arrays.asList(new MenuEntry(getCurrentPage(), "offers"), new MenuEntry(getCurrentPage(), "brochures"), new MenuEntry(getCurrentPage(), "MySilversea"));

    }

    private List<MenuEntry> retrieveFirstLevelLinks() {
        return Stream.of(getCurrentPage(), getCurrentPage(), getCurrentPage(), getCurrentPage(), getCurrentPage()).map(page -> new MenuEntry(page, page.getTitle())).collect(Collectors.toList());
    }

    public List<MenuEntry> getFirstLevelLinks() {
        return firstLevelLinks;
    }

    public List<MenuEntry> getTopLinks() {
        return topLinks;
    }

    public List<MenuEntry> getLanguages() {
        return languages;
    }

    public MenuEntry getHomePage() {
        return homePage;
    }

    private List<MenuEntry> retrieveHomeLanguages(MenuEntry homePage) {
        Iterable<Page> pages = () -> homePage.getPage().getParent().listChildren(new PageFilter());
        return StreamSupport
                .stream(pages.spliterator(), false)
                .filter(page -> !page.getPath().equals(homePage.getPage().getPath()))
                .map(page -> new MenuEntry(page, page.getNavigationTitle()))
                .collect(Collectors.toList());
    }

    public SilverseaAsset getLogo() {
        return logo;
    }

    public static class MenuEntry {
        private final Page page;
        private final String label;

        MenuEntry(Page page, String label) {
            this.page = page;
            this.label = label;
        }

        public Page getPage() {
            return page;
        }

        public String getLabel() {
            return label;
        }
    }
}
