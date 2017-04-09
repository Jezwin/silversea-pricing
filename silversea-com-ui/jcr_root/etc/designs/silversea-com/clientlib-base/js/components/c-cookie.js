+function($) {
    'use strict';

    $(function() {
        /***************************************************************************
         * Show/hide message according to the "cookieMessageDisclaimer" cookie
         **************************************************************************/
        var hideCookie = function() {
            $("#c-cookie").attr('data-show', false);
        }
        var cookieStatus = document.cookie.match(/cookieMessageDisclaimer=(true||false)/);
        if (cookieStatus != null && cookieStatus[1] == 'true') {
            hideCookie();
        } else {
            $('#c-cookie__close').on('click', function() {
                var today = new Date();
                today.setDate(today.getDate() + 365);
                document.cookie = "cookieMessageDisclaimer=true;expires=" + today.toUTCString();
                hideCookie();
            });
        }
    });
}(jQuery);