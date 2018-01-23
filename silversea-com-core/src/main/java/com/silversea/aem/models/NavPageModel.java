package com.silversea.aem.models;

public class NavPageModel {

    private String navigationTitle;

    private String name;

    private String path;

   
    /**
     * @return the navigationTitle
     */
    public String getNavigationTitle() {
        return navigationTitle;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the longDescription
     */
    public String getPath() {
        return path;
    }
    
    public void setName(String input) {
    	name = input;
    }

    public void setNavigationTitle(String input) {
        navigationTitle = input;
    }
    
    public void setPath(String input) {
    	path = input;
    }
}