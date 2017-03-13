package com.silversea.aem.importers.services.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.ExclusiveOffersImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.AgenciesApi;
import io.swagger.client.api.SpecialOffersApi;
import io.swagger.client.model.Agency;
import io.swagger.client.model.SpecialOffer;

/**
 * Created by mbennabi on 09/03/2017.
 */
@Service
@Component(label = "Silversea.com - Exclusive Offers importer")
public class ExclusiveOffersImporterImpl extends BaseImporter implements ExclusiveOffersImporter {

    static final private Logger LOGGER = LoggerFactory.getLogger(ExclusiveOffersImporterImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void importExclusiveOffers() throws IOException {
        final String authorizationHeader = getAuthorizationHeader("/api/v1/specialOffers");

        // get authentification to the Special Offers API
        SpecialOffersApi spetialOffersApi = new SpecialOffersApi();
        spetialOffersApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);
        

        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Session session = resourceResolver.adaptTo(Session.class);

            int i = 1;
            
            List<SpecialOffer> specialOffers;

            do {

                // gets all special Offers
                specialOffers = spetialOffersApi.specialOffersGet(i, 100, null);
                
                // get root parent special offers
                Page offersRootPage = pageManager.getPage(ImportersConstants.BASEPATH_SPECIAL_OFFERS);

                int j = 0;

                for (SpecialOffer offers : specialOffers) {

                    Iterator<Resource> resources = resourceResolver.findResources(
                            "//element(*,cq:Page)[jcr:content/id=\"" + offers.getVoyageSpecialOfferId() + "\"]", "xpath");

                    Page offersPage = null;

                    if (resources.hasNext()) {
                        offersPage = resources.next().adaptTo(Page.class);
                    }else {
                        offersPage = pageManager.create(offersRootPage.getPath(),
                                JcrUtil.createValidChildName(offersRootPage.adaptTo(Node.class), offers.getVoyageSpecialOffer()),
                                "/apps/silversea/silversea-com/templates/exclusiveoffer", offers.getVoyageSpecialOffer(), false);
                        //TODO trouver le bon nom du template exclusive offers  
                    }

                    if (offersPage != null) {
                        Node offersContentNode = offersPage.getContentResource().adaptTo(Node.class);
                        offersContentNode.setProperty(JcrConstants.JCR_TITLE, offers.getVoyageSpecialOffer());
                        offersContentNode.setProperty("id", offers.getVoyageSpecialOfferId());
                        offersContentNode.setProperty("startDate", offers.getValidFrom().toString());
                        offersContentNode.setProperty("endDate", offers.getValidTo().toString());
                        
                        j++;
                    }

                    if (j % 100 == 0) {
                        if (session.hasPendingChanges()) {
                            try {
                                session.save();
                            } catch (RepositoryException e) {
                                session.refresh(true);
                            }
                        }
                    }
                }

                i++;
            } while (specialOffers.size() > 0);

            if (session.hasPendingChanges()) {
                try {
                    session.save();
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            }

            resourceResolver.close();
        } catch (ApiException | WCMException | LoginException | RepositoryException e) {
            LOGGER.error("Exception importing shorexes", e);
        }
    }
}
