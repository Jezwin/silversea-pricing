package com.silversea.aem.components.included;

import com.adobe.cq.sightly.WCMUsePojo;

public class ModalDetailUse extends WCMUsePojo {
	
	@Override
	public void activate() throws Exception {
		
	}
	
	public String getC() {
		return getCurrentPage().getPath();
	}
	

}
