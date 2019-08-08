"use strict";
var global = this;
use(["/libs/wcm/foundation/components/utils/AuthoringUtils.js", "/libs/wcm/foundation/components/utils/ResourceUtils.js", "/libs/sightly/js/3rd-party/q.js"], function (AuthoringUtils, ResourceUtils, Q) {

    var payload = {};

    payload.desktopHeight = properties.get("d_height") || "";
    
    if (!properties.get("t_height") || properties.get("t_height")=="0")
        payload.tabletHeight =     payload.desktopHeight;
    else
        payload.tabletHeight = properties.get("t_height");
    
    if (!properties.get("m_height") || properties.get("m_height")=="0")
        payload.mobileHeight =     payload.desktopHeight;
    else
        payload.mobileHeight = properties.get("m_height");
    

    
    

    payload.desktopImageUrlQryStr = payload.desktopHeight ? '?hei=' + payload.desktopHeight + '&fit=constrain' : "?wid=930&fit=vfit,1&fmt=pjpeg&pscan=5";
    payload.tabletImageUrlQryStr = payload.tabletHeight ? '?hei=' + payload.tabletHeight + '&fit=constrain' : "?wid=930&fit=vfit,1&fmt=pjpeg&pscan=5";
    payload.mobileImageUrlQryStr = payload.mobileHeight ? '?hei=' + payload.mobileHeight + '&fit=constrain' : "?wid=722&fit=vfit,1&fmt=pjpeg&pscan=5";

    payload.desktopImageUrlQryStr2x = payload.desktopHeight ? '?hei=' + payload.desktopHeight * 2 + '&fit=constrain' : "?wid=1860&fit=vfit,1&fmt=pjpeg&pscan=5";
    payload.tabletImageUrlQryStr2x = payload.tabletHeight ? '?hei=' + payload.tabletHeight * 2 + '&fit=constrain' : "?wid=1860&fit=vfit,1&fmt=pjpeg&pscan=5";
    payload.mobileImageUrlQryStr2x = payload.mobileHeight ? '?hei=' + payload.mobileHeight * 2 + '&fit=constrain' : "?wid=1444&fit=vfit,1&fmt=pjpeg&pscan=5";

    return payload;
});