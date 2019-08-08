package com.silversea.aem.components.datasources;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

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

public class EoHelperDataSource extends WCMUsePojo {

	private String PATH_TAG_STYLE = "/etc/tags/style";

	@Override
	public void activate() throws Exception {
		Map<String, String> styles = new LinkedHashMap<String, String>();
		ResourceResolver resolver = getResource().getResourceResolver();
		Resource resource = resolver.getResource(PATH_TAG_STYLE);

		Node tagStyle = resource.adaptTo(Node.class);

		if (tagStyle != null && tagStyle.hasProperty("styleList")) {
			Property pStyle = tagStyle.getProperty("styleList");
			Value[] values = pStyle.getValues();
			for (Value v : values) {
				String[] style = v.toString().split("#");
				if (style != null && style.length > 0) {
					styles.put(style[0], style[1]);
				}
			}
		}

		@SuppressWarnings("unchecked")

		// Creating the Datasource Object for populating the drop-down control.
		DataSource ds = new SimpleDataSource(new TransformIterator(styles.keySet().iterator(), new Transformer() {

			@Override

			// Transforms the input object into output object
			public Object transform(Object o) {
				String s = (String) o;

				// Allocating memory to Map
				ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
				String text = "<" + s + "> Text text text </"+ s + ">";
				// Populate the Map
				vm.put("text", text + "   ==   " + "<span style='" +styles.get(s) + "'> Text text text </span>" );
				vm.put("value", styles.get(s));

				return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
			}
		}));

		this.getRequest().setAttribute(DataSource.class.getName(), ds);

	}

}
