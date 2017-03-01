/**
 * 
 */
package com.silversea.aem.components.editorial;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * @author mjedli
 *
 */
public class DiningVariationUse extends WCMUsePojo {

    private String parentOfParentPageTitle;
    
    @Override
    public void activate() throws Exception {
        
        //getCurrentPage().getParent().getParent()
        
    }

    public String getParentOfParentPageTitle() {
        return parentOfParentPageTitle;
    }

}
