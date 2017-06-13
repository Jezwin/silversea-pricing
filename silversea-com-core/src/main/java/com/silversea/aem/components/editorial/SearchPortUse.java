package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationTagService;

/**
 *
 */
public class SearchPortUse extends WCMUsePojo {

    // path configured in the dialog (parent page to ports beginning with a
    // specific letter
    // if empty, current page path will be used by default
    private String portReference;

    // path of the parent page to all port index (letter) pages
    private String portRoot;

    // port pages beginning with the letter searched
    private Iterator<Page> resultsPageList;

    // list of port results to display (title and country)
    private ArrayList<SearchPortDisplay> resultsPortList;

    // the name of the current page
    private String currentPageName;

    // the selected letter
    private String selectedLetter;

    private ResourceResolver resourceResolver;
    private TagManager tagManager;

    @Inject
    private GeolocationTagService geolocationTagService;

    static final private Logger LOGGER = LoggerFactory.getLogger(SearchPortUse.class);

    @Override
    public void activate() throws Exception {
        resourceResolver = getCurrentPage().getContentResource().getResourceResolver();
        tagManager = resourceResolver.adaptTo(TagManager.class);

        String currentPagePath = getCurrentPage().getPath();
        currentPageName = currentPagePath.substring(currentPagePath.lastIndexOf('/') + 1, currentPagePath.length());

        portReference = getProperties().get("portReference", String.class);

        if (currentPageName.length() == 1) {
            // the current page is a letter index page
            // set the port root to the current page's parent
            portRoot = currentPagePath.substring(0, currentPagePath.lastIndexOf('/') + 1);
        } else {
            // the current page is the root page
            portRoot = currentPagePath;
        }

        // get ports with searched letter (children of the portReference page)
        // if portReference is empty, return the children of the current page by
        // default
        Page portIndexPage = portReference != null ? getPageManager().getPage(portReference) : getCurrentPage();
        String portIndexPath = portIndexPage.getPath();
        selectedLetter = portIndexPath.substring(portIndexPath.lastIndexOf('/') + 1, portIndexPath.length());
        resultsPageList = portIndexPage.listChildren();

        // prepare results for display
        resultsPortList = new ArrayList<SearchPortDisplay>();
        while (resultsPageList.hasNext()) {
            Page portPage = resultsPageList.next();
            String portCountry = "";
            String countryId = portPage.getProperties().get("countryIso2", String.class);
            geolocationTagService = getSlingScriptHelper().getService(GeolocationTagService.class);
            String tagId = geolocationTagService.getTagFromCountryId(countryId);
            Tag tag = tagManager.resolve(tagId);
            if (tag != null) {
                portCountry = tag.getTitle();
            }
            resultsPortList.add(new SearchPortDisplay(portPage.getTitle(), portCountry));
        }
    }

    public Iterator<Page> getResultsPageList() {
        return resultsPageList;
    }

    public ArrayList<SearchPortDisplay> getResultsPortList() {
        return resultsPortList;
    }

    public String getPortRoot() {
        return portRoot;
    }

    public String getCurrentPageName() {
        return currentPageName;
    }

    public String getSelectedLetter() {
        return selectedLetter;
    }

    public class SearchPortDisplay {

        private String portName;
        private String portCountry;

        public SearchPortDisplay(String name, String country) {
            portName = name;
            portCountry = country;
        }

        public String getPortName() {
            return portName;
        }

        public String getPortCountry() {
            return portCountry;
        }
    }
}