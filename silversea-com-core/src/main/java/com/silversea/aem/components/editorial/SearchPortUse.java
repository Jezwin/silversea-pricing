package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.services.GeolocationTagService;

/**
 * Use class for the Find your port component
 */
public class SearchPortUse extends WCMUsePojo {

    // path configured in the dialog (parent page to ports beginning with a
    // specific letter
    // if empty, current page path will be used by default
    private String portReference;

    // ports root page
    private Page portsRootPage;

    //the selected ports list page
    private Page portIndexPage;

    // port pages beginning with the letter searched
    private Iterator<Page> resultsPageList;
    
    // list of port results to display (title and country)
    private ArrayList<SearchPortDisplay> resultsPortList;

    private TagManager tagManager;

    @Inject
    private GeolocationTagService geolocationTagService;

    static final private Logger LOGGER = LoggerFactory.getLogger(SearchPortUse.class);

    @Override
    public void activate() throws Exception {
        portReference = getProperties().get("portReference", String.class);

        // get ports with searched letter (children of the portReference page)
        // if portReference is empty, return the children of the current page by
        // default
        portIndexPage = portReference != null ? getPageManager().getPage(portReference) : getCurrentPage();
        resultsPageList = portIndexPage.listChildren();

        // prepare results for display
        tagManager = getResourceResolver().adaptTo(TagManager.class);
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
            resultsPortList.add(new SearchPortDisplay(portPage.getTitle(), portCountry, portPage.getPath()));
        }

        //set the parent page - the parent of all the ports list (letter) pages
        //if portReference is empty, the parent of the current page, if not, the parent of 
        //the configured portReference page
        portsRootPage = portIndexPage.getParent();
    }

    public Iterator<Page> getResultsPageList() {
        return resultsPageList;
    }

    public ArrayList<SearchPortDisplay> getResultsPortList() {
        return resultsPortList;
    }

    public Page getPortsRootPage() {
        return portsRootPage;
    }

    public Page getPortIndexPage() {
        return portIndexPage;
    }

    // TODO replace by port model, and adapt it from a cq:page
    public class SearchPortDisplay {

        private String portName;
        private String portCountry;
        private String portPath;

        public SearchPortDisplay(String name, String country, String path) {
            portName = name;
            portCountry = country;
            portPath = path;
        }

        public String getPortName() {
            return portName;
        }

        public String getPortCountry() {
            return portCountry;
        }

        public String getPortPath() {
            return portPath;
        }
    }
}