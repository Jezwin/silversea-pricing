$(function () {
    "use strict";

    var wdest = $(window).width();
    var mapCompProp = {};

    $(window).resize(function () {
        if ($(window).width() == wdest) return;
        wdest = $(window).width();
       // initializeButton();
    });

    $(document).ready(function() {
        //initializeButton();
    });

    function initializeButton() {
        var $smartButtonList = $(".smartbtn");
        if ($smartButtonList.length > 0) {
            $smartButtonList.each(function (e) {
                var $smartButton = $(this);
                var id = $smartButton.attr("id");
                var dataCompProp = $smartButton.data("componentproperties");
                if (dataCompProp != null) {
                    dataCompProp = dataCompProp.split("~");
                    for (var i in dataCompProp) {
                        var propSplit = dataCompProp[i].split("=");
                        var key = propSplit[0];
                        var value = propSplit[1];
                        mapCompProp[key] = value;
                    }
                    setText($smartButton);
                }
            });
        }
    };//initializeButton

    function getGoodValue(propName) {
        var value = mapCompProp[propName];
        if (isTablet()) {
           // text = mapCompProp["textTablet"] != null ? mapCompProp["textTablet"] : text;
        }
        if (isMobile()) {
            //text = mapCompProp["textMobile"] != null ? mapCompProp["textMobile"] : text;
        }
    };//getGoodValue

    function setText(smartButton) {
        var text = mapCompProp["textDesktop"];
        if (isTablet()) {
            text = mapCompProp["textTablet"] != null ? mapCompProp["textTablet"] : text;
        }
        if (isMobile()) {
            text = mapCompProp["textMobile"] != null ? mapCompProp["textMobile"] : text;
        }
        smartButton.find("span").text(text);
    };//setText

    function isDesktop() {
        return $("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg");
    };//isDesktop

    function isTablet() {
        return $("body").hasClass("viewport-sm");
    };//isTablet

    function isMobile() {
        return $("body").hasClass("viewport-xs");
    };//isMobile
});