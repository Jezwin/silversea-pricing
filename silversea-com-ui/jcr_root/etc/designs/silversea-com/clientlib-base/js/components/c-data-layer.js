$(function() {
    var tracksuite = $('.datalayer-suite-name');
    if (tracksuite != undefined) {
        window.dataLayer[0].track_suite = tracksuite.attr('tracksuite');
        window.dataLayer.push({'track_suite': tracksuite.attr('tracksuite')});
    }
    window.dataLayer[0].env_channel = $.viewportDetect();
    
//    $(window).on('resize', function() {
//        window.dataLayer.env_channel = $.viewportDetect();
//        console.log('test resize');
//    });
});