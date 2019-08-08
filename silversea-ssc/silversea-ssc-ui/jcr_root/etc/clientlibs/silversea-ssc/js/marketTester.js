ContextHub.console.log(ContextHub.Shared.timestamp(), '[loading] contexthub.segment-engine.scripts - script.marketTester.js');
 
(function() {
    'use strict';
 
   
    var getIsFT = function() {
        var user_geo_adwords = window.dataLayer[0].user_geo_adwords;
 
        return user_geo_adwords === 'US';
    };
    
    var getIsLAM = function() {
        var user_geo_adwords = window.dataLayer[0].user_geo_adwords;
 
        return user_geo_adwords === 'LAM';
    };
    
    var getIsEU = function() {
        var user_geo_adwords = window.dataLayer[0].user_geo_adwords;
 
        return user_geo_adwords === 'EMEA';
    };
    
    var getIsUK = function() {
        var user_geo_adwords = window.dataLayer[0].user_geo_adwords;
 
        return user_geo_adwords === 'UK';
    };
    
    var getIsAS = function() {
        var user_geo_adwords = window.dataLayer[0].user_geo_adwords;
 
        return user_geo_adwords === 'AP';
    };
 
    /* register function */
    ContextHub.SegmentEngine.ScriptManager.register('getIsFT', getIsFT);
    ContextHub.SegmentEngine.ScriptManager.register('getIsLAM', getIsLAM);
    ContextHub.SegmentEngine.ScriptManager.register('getIsEU', getIsEU);
    ContextHub.SegmentEngine.ScriptManager.register('getIsUK', getIsUK);
    ContextHub.SegmentEngine.ScriptManager.register('getIsAS', getIsAS);
})();
