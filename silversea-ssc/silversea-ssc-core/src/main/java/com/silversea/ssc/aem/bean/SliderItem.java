package com.silversea.ssc.aem.bean;

/**
 * @author nikhil
 *
 */
public class SliderItem {
	private boolean isImage = false;
	private String path;

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
