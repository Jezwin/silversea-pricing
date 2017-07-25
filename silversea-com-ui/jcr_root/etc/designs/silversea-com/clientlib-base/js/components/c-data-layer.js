$(function() {
    var tracksuite = $('.datalayer-suite-name');
    if (tracksuite != undefined) {
        window.dataLayer[0].track_suite = tracksuite.attr('tracksuite');
        window.dataLayer.push({
            'track_suite' : tracksuite.attr('tracksuite')
        });
    }
    window.dataLayer[0].env_channel = $.viewportDetect();
    
    var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'));
    if (userInfo != undefined) {
        window.dataLayer[0].user_email = userInfo.email;
    }
    
    var apiIndivId = JSON.parse($.CookieManager.getCookie('api_indiv_id'));
    if (apiIndivId != undefined) {
        window.dataLayer[0].api_indiv_id = apiIndivId;
    }
    
    // $(window).on('resize', function() {
    // window.dataLayer.env_channel = $.viewportDetect();
    // console.log('test resize');
    //    });
});