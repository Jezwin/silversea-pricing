$(function() {
    function formatDate(date) {
        //format date in yyyy-mm-dd
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('-');
    }

    var setCookiesDataLayer = (function setcookiesDataLayer() {
        //update page load date
        var pageLoadDate = new Date();
        var displayDate = formatDate(pageLoadDate);
        $.CookieManager.setCookie("user_recency", displayDate);
        $.CookieManager.setCookie("user_last_visit", displayDate);

        //request a quote - suite name
        var $tracksuite = $('.datalayer-suite-name');
        if ($tracksuite.length > 0) {
            $.CookieManager.setCookie("track_suite", $tracksuite.data('tracksuite'));
        }

        //brochure thank you - requested brochure code
        var $brochurecode = $('.datalayer-brochurecode');
        if ($brochurecode.length > 0) {
            $.CookieManager.setCookie("track_brochurename", $brochurecode.data('brochurecode'));
        }
    })();

    var setInfoDataLayer = (function setInfoDataLayer() {
        window.dataLayer[0].env_channel = $.viewportDetect();

        var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'));
        if (userInfo != undefined) {
            window.dataLayer[0].user_email = userInfo.email;
            window.dataLayer[0].user_recency_optin = userInfo.subscribeemail;
        }
        //request a quote - suite name
        var trackSuite = $.CookieManager.getCookie('track_suite');
        if (trackSuite != undefined) {
            window.dataLayer[0].track_suite = trackSuite;
            window.dataLayer.push({
                'track_suite' : trackSuite
            });
        }
        //brochure thank you - requested brochure code
        var trackBrochurename = $.CookieManager.getCookie('track_brochurename');
        if (trackBrochurename != undefined) {
            window.dataLayer[0].track_brochurename = trackBrochurename;
            window.dataLayer.push({
                'track_brochurename' : trackBrochurename
            });
        }

        var apiIndivId = $.CookieManager.getCookie('api_indiv_id');
        if (apiIndivId != undefined) {
            window.dataLayer[0].api_indiv_id = apiIndivId;            
        }
        // changes for block referer.
		var apiBlockedReferer = $.CookieManager.getCookie('api_blocked_referer');
        if (apiBlockedReferer != undefined) {
            window.dataLayer[0].api_blocked_referer = apiBlockedReferer;            
        }
		//SIL-16
		var apiTemporaryId = $.CookieManager.getCookie('api_temporary_id');
        if (apiTemporaryId != undefined) {
            window.dataLayer[0].api_temporary_id = apiTemporaryId;            
        }
        var formSubmitDate = $.CookieManager.getCookie('user_status');
        if (formSubmitDate != undefined) {
            window.dataLayer[0].user_status = formSubmitDate;            
        }
        
        var pageLoadDate = $.CookieManager.getCookie('user_recency');
        if (pageLoadDate != undefined) {
            window.dataLayer[0].user_recency = pageLoadDate;
            window.dataLayer[0].user_last_visit = pageLoadDate;
        }

        return setInfoDataLayer;
    })();
    
    //TEMP AB TEST FYC
    var pagecatAB = dataLayer[0].page_cat1;
    var pageurlAB = document.location.href;

    var shouldAB = "false";

    if(pagecatAB.indexOf('Find your cruise') > -1 || pagecatAB.indexOf('single ship') > -1 || pagecatAB.indexOf('single destination') > -1 || pagecatAB.indexOf('single exclusive offer') > -1){
     shouldAB = "true"; 
    }

    if(pageurlAB.indexOf('landing-dmp-dest') > -1 || pageurlAB.indexOf('landing-dmp-ship') > -1 || pageurlAB.indexOf('landing-dmp-offer') > -1 || pageurlAB.indexOf('landing-ship') > -1 || pageurlAB.indexOf('landing-dest') > -1 || pageurlAB.indexOf('landing-offer') > -1){
     shouldAB = "true";  
    }

    dataLayer[0].ABDiscoverMoreActivate = shouldAB;
});