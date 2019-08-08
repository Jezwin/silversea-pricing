package com.silversea.ssc.aem.helper;

import com.adobe.cq.sightly.WCMUsePojo;
/**
 * Helper class for inline gallery landing component.
 */
public class GalleryDisplayHelper extends WCMUsePojo {

    private boolean display;

    @Override
    public void activate() throws Exception {
        Integer displayItem = Integer.parseInt(get("displayItem", String.class));
        Object index = get("index", Object.class);
        Object indexList = get("indexList", Object.class);
        Integer displayItemRounded = (int) (3*(Math.ceil(Math.abs(displayItem/3))));
	     if(null != index) {
	        if (Integer.parseInt(index.toString()) < displayItem) {
	           display = true;
	        } else {
	        	display = false ;
	        }
	     }
        
        if(null != indexList) {
        	if (3*Integer.parseInt(indexList.toString()) <= displayItemRounded) {
 	           display = true;
 	        } else {
 	        	display = false ;
 	        }
        }

    }

    /**
     * @return Boolean display
     */
    public Boolean getDisplay() {
        return display;
    }
}