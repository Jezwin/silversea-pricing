package com.silversea.aem.components.editorial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.PortModel;

/**
 * Use class for the Find your port component
 */
public class SearchPortUse extends WCMUsePojo {

    // ports root page
    private Page portsRootPage;

    // list of port results to display (title and country)
    private List<PortModel> ports = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        final String portReference = getProperties().get("portReference", String.class);

        // get ports with searched letter (children of the portReference page)
        // if portReference is empty, return the children of the current page by
        // default
        final Page portIndexPage = portReference != null ? getPageManager().getPage(portReference) : getCurrentPage();
        final Iterator<Page> resultsPageList = portIndexPage.listChildren();

        // prepare results for display
        while (resultsPageList.hasNext()) {
            final PortModel port = resultsPageList.next().adaptTo(PortModel.class);

            if (port != null) {
                ports.add(port);
            }
        }

        ports.sort(Comparator.comparing(PortModel::getName));

        // set the parent page - the parent of all the ports list (letter) pages
        // if portReference is empty, the parent of the current page, if not,
        // the parent of the configured portReference page
        portsRootPage = portIndexPage.getParent();
    }

    public List<PortModel> getPorts() {
        return ports;
    }

    public Page getPortsRootPage() {
        return portsRootPage;
    }

    public List<Page> getOrderedAlphabetPages() {
        final List<Page> alphabetList = new ArrayList<>();
        final Iterator<Page> alphabetIterator = portsRootPage.listChildren();

        alphabetIterator.forEachRemaining(alphabetList::add);
        alphabetList.sort(Comparator.comparing(p -> p.getTitle().toUpperCase()));

        return alphabetList;
    }
}