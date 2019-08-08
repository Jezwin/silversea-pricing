package com.silversea.aem.importers.services;

import java.util.Map;

import com.silversea.aem.components.beans.ValueTypeBean;
	
public interface StyleCache {
	void buildCache();
	
	Map<String, ValueTypeBean> getStyles();
}
