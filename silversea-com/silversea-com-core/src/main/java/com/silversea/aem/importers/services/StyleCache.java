package com.silversea.aem.importers.services;

import java.util.Map;

import com.silversea.aem.components.beans.ValueTypeBean;
import com.silversea.aem.importers.services.impl.ImportResult;

public interface StyleCache {
	ImportResult buildCache();
	
	Map<String, ValueTypeBean> getStyles();
}
