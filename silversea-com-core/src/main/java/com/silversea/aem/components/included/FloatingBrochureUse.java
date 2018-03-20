package com.silversea.aem.components.included;

import org.apache.commons.lang3.ArrayUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.silversea.aem.utils.PathUtils;

public class FloatingBrochureUse extends WCMUsePojo {

	private String brochuresPagePath;
	private String[] whiteListArray;
	private String[] blackListArray;
	@Override
	public void activate() throws Exception {
		setWhiteListArray(getPageProperties().get("whitelistSelector", String[].class));
		if(ArrayUtils.isEmpty(getWhiteListArray())){
			setWhiteListArray(getCurrentStyle().get("whitelistSelector",String[].class));
		}
		
		setBlackListArray(getPageProperties().get("blacklistSelector", String[].class));
		if(ArrayUtils.isEmpty(getBlackListArray())){
			setBlackListArray(getCurrentStyle().get("blacklistSelector",String[].class));
		}
		
		// TODO Auto-generated method stub
		setBrochuresPagePath(getBrochuresPagePath());
		
	}

	/**
	 * @return the request quote page
	 */
	public String getBrochuresPagePath() {
		return PathUtils.getBrochuresPagePath(getResource(), getCurrentPage());
	}

	public void setBrochuresPagePath(String brochuresPagePath) {
		this.brochuresPagePath = brochuresPagePath;
	}

	public String[] getWhiteListArray() {
		return whiteListArray;
	}

	public void setWhiteListArray(String[] whiteListArray) {
		this.whiteListArray = whiteListArray;
	}

	public String[] getBlackListArray() {
		return blackListArray;
	}

	public void setBlackListArray(String[] blackListArray) {
		this.blackListArray = blackListArray;
	}
}
