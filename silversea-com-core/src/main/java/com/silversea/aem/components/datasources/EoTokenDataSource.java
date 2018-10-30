package com.silversea.aem.components.datasources;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.cxf.common.util.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.silversea.aem.models.ExclusiveOfferModel;

public class EoTokenDataSource extends WCMUsePojo {

	private Gson gson = new GsonBuilder().create();

	@Override
	public void activate() throws Exception {
		final ResourceResolver resolver = getResource().getResourceResolver();

		final Map<String, String> tokens = new LinkedHashMap<String, String>();
		
		String pageUrl = null;
		if(getRequest().getQueryString().contains("%2F")) {
			/*
			 * item=%2Fcontent%2Fsilversea-com%2Fen%2Fexclusive-offers%2Fsilver-select-program
			 * */
			pageUrl = getRequest().getQueryString().replace("%2F", "/").replace("item=", "");
		} else {
			/*/
			 * mnt/override/apps/silversea/silversea-com/components/pages/exclusiveoffer/_cq_dialog.html/content/silversea-com/en/exclusive-offers/silver-select-program/jcr:content
			 * */
			String[] splitURL = getRequest().getPathInfo().split("_cq_dialog.html");
			if (splitURL != null && splitURL.length > 0) {
				pageUrl = splitURL[1].replace("/jcr:content", "");
			}
		}


		if (pageUrl != null) {
			Resource resource = getResourceResolver().resolve(pageUrl);
			if (resource != null) {
				Page page = resource.adaptTo(Page.class);
				if (page != null) {
					ExclusiveOfferModel eoModel = page.adaptTo(ExclusiveOfferModel.class);

					String[] customTokens = eoModel.getCustomTokenSettings();

					JsonObject eoSettings = null;
					String token = null;
					for (int i = 0; i < customTokens.length; i++) {
						eoSettings = gson.fromJson(customTokens[i], JsonObject.class);
						if (eoSettings != null) {
							token = eoSettings.get("token").getAsString();
							tokens.put(token, token);
						}
					}
				}
			}
		}

		@SuppressWarnings("unchecked")

		// Creating the Datasource Object for populating the drop-down control.
		DataSource ds = new SimpleDataSource(new TransformIterator(tokens.keySet().iterator(), new Transformer() {

			@Override

			// Transforms the input object into output object
			public Object transform(Object o) {
				String country = (String) o;

				// Allocating memory to Map
				ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

				if(!StringUtils.isEmpty("author_by_prefix") && "author_by_prefix".equals(tokens.get(country))) {
					vm.put("selected", true);
				}

				// Populate the Map
				vm.put("value", country);
				vm.put("text", tokens.get(country));

				return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
			}
		}));

		this.getRequest().setAttribute(DataSource.class.getName(), ds);

	}

}