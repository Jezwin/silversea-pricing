package com.silversea.aem.components.page;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.silversea.aem.models.CruiseModel;

public class CruiseUse extends WCMUsePojo{

    private CruiseModel cruiseModel;
    private String previous;
    private String next;
 
    
    @Override
    public void activate() throws Exception {
        cruiseModel = getCurrentPage().adaptTo(CruiseModel.class);
        initPagination();
    }

    private void initPagination(){
        Page currentPage = getCurrentPage();
        Iterator<Page> children = currentPage.getParent().listChildren();
        if(children != null && children.hasNext()){
            while(children.hasNext()){
                Page current = children.next();
                if(StringUtils.equals(current.getPath(), currentPage.getPath())){
                    if(children.hasNext()){
                        next = children.next().getPath();
                    }
                    break;
                }
                previous = current.getPath();
            }
        }    
    }
    
    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    /**
     * @return cruise's model
     */
    public CruiseModel getCruiseModel() {
        return cruiseModel;
    }

}
