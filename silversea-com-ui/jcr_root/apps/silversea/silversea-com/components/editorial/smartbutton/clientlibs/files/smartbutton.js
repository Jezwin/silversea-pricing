$(function () {
    "use strict";
    $(document).ready(function () {
        parsedSscFeJsElement();
    });

    function parsedSscFeJsElement() {
        $("[data-sscfwjs-properties]").each(function (e) {
            var $element = $(this);
            var typeDevices = isDesktop() ? 'desktop' : isTablet() ? 'tablet' : 'mobile';
            var dataProperties = $element.data("sscfwjs-properties").split(",");
            for (var i in dataProperties) {
                var valueProp = dataProperties[i].trim();
                var key = "data-sscfwjs-" + typeDevices + "-" + valueProp;
                var value = $element.attr(key);
                $element.attr(valueProp, value);
                $element.removeAttr("data-sscfwjs-desktop-" + valueProp);
                $element.removeAttr("data-sscfwjs-tablet-" + valueProp);
                $element.removeAttr("data-sscfwjs-mobile-" + valueProp);
            }
            $element.removeAttr("data-sscfwjs-properties");
        });
    };//parsedSscFeJsElement

    function isDesktop() {
        return $("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg");
    };//isDesktop

    function isTablet() {
        return $("body").hasClass("viewport-sm");
    };//isTablet

});