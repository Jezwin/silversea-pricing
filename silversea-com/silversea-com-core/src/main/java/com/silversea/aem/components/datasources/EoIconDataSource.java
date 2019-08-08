package com.silversea.aem.components.datasources;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

public class EoIconDataSource extends WCMUsePojo {

	@Override
	public void activate() throws Exception {
		final ResourceResolver resolver = getResource().getResourceResolver();

		final Map<String, String> tokens = new LinkedHashMap<String, String>();

		String folderPath = "/content/dam/silversea-com/eo-icons";
		Resource resource = getResourceResolver().resolve(folderPath);
		Node node = resource.adaptTo(Node.class);
		if (node != null) {
			NodeIterator nodeItr = node.getNodes();

			tokens.put("", null);
			while (nodeItr.hasNext()) {
				Node cNode = nodeItr.nextNode();
				if (!cNode.getName().equalsIgnoreCase("jcr:content")) {
					tokens.put(cNode.getName(), cNode.getPath());
				}
			}
		}

		@SuppressWarnings("unchecked")

		// Creating the Datasource Object for populating the drop-down control.
		DataSource ds = new SimpleDataSource(new TransformIterator(tokens.keySet().iterator(), new Transformer() {

			@Override

			// Transforms the input object into output object
			public Object transform(Object o) {
				String icon = (String) o;

				// Allocating memory to Map
				ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

				// Populate the Map
				vm.put("value", tokens.get(icon));
				vm.put("text", icon);

				return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
			}
		}));

		this.getRequest().setAttribute(DataSource.class.getName(), ds);

	}

}