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

import com.adobe.granite.asset.api.Asset;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.silversea.aem.constants.WcmConstants;
import com.silversea.aem.importers.services.BrochuresImporter;

import io.swagger.client.ApiException;
import io.swagger.client.api.BrochuresApi;
import io.swagger.client.model.Brochure;


@Service
@Component(label = "Silversea.com - Brochures importer")
public class BrochuresImporterImpl extends BaseImporter implements BrochuresImporter	{


	private static final  Logger LOGGER = LoggerFactory.getLogger(BrochuresImporterImpl.class);

	private static final int PER_PAGE = 100;
	private static final String BROCHURE_API_URL="/api/v1/brochures";
	private static final String LANGUAGE_TAG_PREFIX = "languages:";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;


	/**{@inheritDoc}**/
	public void importBrochures() throws IOException{

		List<Brochure> brochures = null;
		ResourceResolver resourceResolver = null;
		int index = 1;

		final String authorizationHeader = getAuthorizationHeader(BROCHURE_API_URL);
		BrochuresApi brochuresApi = new BrochuresApi();
		brochuresApi.getApiClient().addDefaultHeader("Authorization", authorizationHeader);		
		try {

			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resourceResolver.adaptTo(Session.class);

			do {
				LOGGER.debug("[Importing brochure]: Start importing brochures");
				brochures = brochuresApi.brochuresGet(null, index, PER_PAGE, null);
				for(Brochure brochure: brochures){
					LOGGER.debug("[Importing brochure]: import brochure with code {}",brochure.getBrochureCod());
					Iterator<Resource> resources = findBrochuresByCode(brochure.getBrochureCod(),resourceResolver);
					updateBrochures(resources, brochure);
					if ((index -1 )% PER_PAGE == 0){
						saveSession(session,true);
					}
				}
				index++;
			}
			while (brochures.size() > 0);
			saveSession(session,false);

		} catch (LoginException | ApiException | RepositoryException e) {
			LOGGER.error("[Importing brochure] Error while importing brochure {}",e);
		}
		finally{
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
				resourceResolver = null;
			}
		}

	}

	/**
	 * Fin a brochure by its code
	 * @param code: brochure's code
	 * @param resourceResolver: resource resolver
	 * @return list of resources
	 */
	private Iterator<Resource> findBrochuresByCode(String code,ResourceResolver resourceResolver){
		Iterator<Resource> brochures = resourceResolver.findResources(
				"//element(*,dam:Asset)[jcr:content/metadata/brochureCode=\"" +code+ "\"]", "xpath");
		return brochures;
	}

	/**
	 * Update list of brochures
	 * @param resources: list of brochures to update
	 * @param brochure: brochure source
	 */
	private void updateBrochures(Iterator<Resource> resources,Brochure brochure){
		
		if(resources != null && resources.hasNext()){
			LOGGER.debug("[Updating brochure]: Update brochure with code {}",brochure.getBrochureCod());
			while(resources.hasNext()){
				Resource resource = resources.next();
				Asset asset = resource.adaptTo(Asset.class);
				String[] languageTags = {LANGUAGE_TAG_PREFIX + brochure.getLanguageCod().toLowerCase()};

				try {
					Node node = asset.adaptTo(Node.class).getNode(JcrConstants.JCR_CONTENT + "/metadata");
					node.setProperty(DamConstants.DC_TITLE,brochure.getTitle());
					node.setProperty(WcmConstants.PN_BROCHURE_CODE,brochure.getBrochureCod());
					node.setProperty(WcmConstants.PN_BROCHURE_ONLINE_URL, brochure.getBrochureUrl());
					node.setProperty(WcmConstants.PN_BROCHURE_IS_DIGITAL_ONLY, brochure.getDigitalOnly());
					node.setProperty("cq:tags",languageTags);
				} catch (RepositoryException e) {
					LOGGER.error("[Updating brochure]: Error while updating brochure{}",e);
				} 
			}
		}
		else{
			LOGGER.debug("[Updating brochure]: Brochure with code {} not found",brochure.getBrochureCod());
		}
	}

	/**
	 * Save session
	 * @param session : session to save
	 * @param isRefresh: boolean indicates if we refresh session
	 * @throws RepositoryException: throw a repository exception
	 */
	private void saveSession(Session session,boolean isRefresh) throws RepositoryException{
		try {
			if (session.hasPendingChanges()) {
				session.save();
			}
		} catch (RepositoryException e) {
			session.refresh(isRefresh);
		}
	}
}
