+function($) {
    'use strict';

    function CookieDisclamer () {
        this.name = "cookieMessageDisclaimer";
        this.status = false;
        this.cookie = null;
    };

    CookieDisclamer.prototype = {

        isActive: function() {
            this.getCookie();
            return (this.cookie != null && ;
        },
        setCookie: function () {
            var today = new Date();
            today.setDate(today.getDate() + 365);
            document.cookie = this.name + "=true;expires=" + today.toUTCString();
        },
        getCookie: function () {
            this.cookie = document.cookie.match(/cookieMessageDisclaimer=(true||false)/);
        };
    };

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
        var testAnais = function () {
            console.log('it is isolate ?');
        };
    });
}(jQuery);