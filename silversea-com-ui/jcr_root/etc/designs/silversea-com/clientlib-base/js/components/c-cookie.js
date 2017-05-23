+function($) {
    'use strict';


    /***************************************************************************
     * CookieManager - Tools to manage cookie & disclamer
     **************************************************************************/
    $.CookieManager = {

        disclamer: 'cookieMessageDisclaimer',

        setCookie: function (name, value) {
            var lastValue = this.getCookie(name);
            var today = new Date();
            today.setDate(today.getDate() + 365);

            if (lastValue !== value) {
                document.cookie = name + '=' + value + '; expires=' + today.toUTCString() + '; path=/';
            }
            // When cookie is added up expiration date of disclamer
            if (name !== this.disclamer && this.getDisclamer() === 'true')
                document.cookie = this.disclamer + '=true; expires=' + today.toUTCString() + '; path=/';
        },
        getCookie: function (pname) {
            var name = pname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for(var i = 0; i <ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) === ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return null;
        },
        getDisclamer: function() {
            return this.getCookie(this.disclamer);
        },
        setDisclamer: function(val) {
            return this.setCookie(this.disclamer, val);
        }
    };

    $(function() {
        /***************************************************************************
         * Show/hide message according to the "cookieMessageDisclaimer" cookie
         **************************************************************************/
        var showCookie = function(val) {
            $("#c-cookie").attr('data-show', val);
        }
        if ($.CookieManager.getDisclamer() === null) {
            showCookie(true);
            $('#c-cookie__close').on('click', function() {
                $.CookieManager.setDisclamer('true');
                showCookie(false);
            });
        }
    });
}(jQuery);