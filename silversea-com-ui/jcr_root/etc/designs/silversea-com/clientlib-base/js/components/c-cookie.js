+function($) {
    'use strict';

    $(function() {
        /***************************************************************************
         * Show/hide message according to the "cookieMessageDisclaimer" cookie
         **************************************************************************/
        var showCookie = function(val) {
            $("#c-cookie").attr('data-show', val);
        }
        var cookieStatus = document.cookie.match(/cookieMessageDisclaimer=(true||false)/);
        if (cookieStatus == null || (cookieStatus != null && cookieStatus[1] != 'true')) {
            showCookie(true);
            $('#c-cookie__close').on('click', function() {
                var today = new Date();
                today.setDate(today.getDate() + 365);
                document.cookie = "cookieMessageDisclaimer=true; expires=" + today.toUTCString();
                showCookie(false);
            });
        }
    });
}(jQuery);