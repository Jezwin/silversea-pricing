package com.silversea.ssc.aem.components.editorial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class IconListUse extends   WCMUsePojo {

	static private final Logger LOGGER = LoggerFactory.getLogger(IconListUse.class);
	
	private List<Icon> iconList;

	@Override
	public void activate() throws Exception {
		Icon item;
		iconList = new ArrayList<Icon>();
		if (getResource().hasChildren() && getResource().getChild("listIcon") != null) {
			for (Resource child : getResource().getChild("listIcon").getChildren()) {
				Node node = child.adaptTo(Node.class);
				item = new Icon();
				item.setImagePath(getProp(node,"imageIcon").orElse(""));
				item.setTitle(getProp(node,"titleIcon").orElse(""));
				iconList.add(item);
			}
		}

	}

	public List<Icon> getIconList() {

		return iconList;
	}

	private Optional<String> getProp(Node node, String key) {
		try {
			if (!node.hasProperty(key)) return Optional.empty();
			return Optional.ofNullable(node.getProperty(key)).map(property -> {
				try {
					return property.getString();
				} catch (RepositoryException exception) {
					return null;
				}
			});
		} catch (RepositoryException exception) {
			return Optional.empty();
		}
	}


	public class Icon{
		private String imagePath;
		private String title;


		public String getImagePath() {
			return imagePath;
		}
		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}



	}
}
