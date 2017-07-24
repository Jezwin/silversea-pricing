$(function() {
    var tracksuite = $('.datalayer-suite-name');
    if (tracksuite != undefined) {
        window.dataLayer.page.track_suite = tracksuite.attr('tracksuite');
    }
    window.dataLayer.page.env_channel = $.viewportDetect();
    
//    $(window).on('resize', function() {
//        window.dataLayer.env_channel = $.viewportDetect();
//        console.log('test resize');
//    });
});