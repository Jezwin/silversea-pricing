+function($) {
    'use strict';

    $(function() {
        // Show/hide cookies bar
        /***************************************************************************
         * Cookie Close
         **************************************************************************/
        var hideCookie = function () {
            $("#c-cookie").attr('data-show', false);
        }
        var cookieStatus = document.cookie.match(/cookieAvaible=(true||false)/);
        if (cookieStatus != null && cookieStatus[1] == 'true')
            hideCookie();
        else {
            $('#c-cookie__close').on('click', function() {
                var today = new Date();
                today.setDate(today.getDate() + 365);
                document.cookie="cookieAvaible=true;expires=" + today.toUTCString();
                hideCookie();
            });
        }
    });
}(jQuery);