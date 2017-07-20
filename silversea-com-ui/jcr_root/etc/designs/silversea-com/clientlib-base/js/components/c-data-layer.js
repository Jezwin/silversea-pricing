$(function() {
    var tracksuite = $('.datalayer-suite-name');
    if (tracksuite != undefined) {
        window.dataLayer.page.track_suite = tracksuite.attr('tracksuite');
    }
});