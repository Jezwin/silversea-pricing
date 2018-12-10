$(function () {
    "use strict";
    if ($(".smartbtn").length > 0) {

        parsedSscFeJsElement(setSuffixSelector);

        $(".smartbtn").on("click touchstart", onClickSmartButton);
    }

    function onClickSmartButton(e) {
        var $smartbutton = $(this);
        var idSmartButton = $smartbutton.attr("id");
        var elementToScroll = $smartbutton.data("scrollelement");
        var type = $smartbutton.data("type");

        if (type == "video") {
            e.stopPropagation();
            e.preventDefault();
            openVideoModal($smartbutton)

        } else if (elementToScroll != null) {
            e.stopPropagation();
            e.preventDefault();

            var target = $(elementToScroll).offset().top;
            $('html, body').animate({
                scrollTop: target - 100
            }, 1500);
            return false;
        }
    };//onClickSmartButton

    function setSuffixSelector() {
        $(".smartbtn").each(function () {
            var $element = $(this),
                type = $element.data("type"),
                scclicktype = $element.data("sscclicktype"),
                href = $element.attr("href"),
                selectors = $element.data("selectors"),
                suffix = $element.data("suffix"),
                modalcontent = $element.data("lightbox");
            if (modalcontent == "modalcontent") {
                href = href.replace("html", modalcontent + ".html");
                $element.attr("href", href);
            }
            if (type == "page" && scclicktype == "clic-RAQ") {
                if (suffix != null) {
                    href = href.replace("html", selectors + ".html");
                }
                if (selectors != null) {
                    href = href + "/" + suffix;
                }
                $element.attr("href", href);
            }

            /*Fix for IE content (pseudo element)*/
            if (/MSIE|Trident/.test(navigator.userAgent)) {
                var contentIE = $element.data("ie-content");
                $element.find(".ssc-fw-content").text(contentIE);
            }
        });
    };//setSuffixSelector

    function openVideoModal($smartbutton) {

        var $modalContent =
            '<div class="modal-content modal-content--transparent">'
            + '<div class="modal-header">'
            + ' <button class="close" type="button" data-dismiss="modal" aria-label="Close"></button>'
            + '</div>'
            + '<div class="modal-body">'
            + ' <div id="currentNode" class="c-video cq-dd-image" data-video-asset="assetPath" data-video-autoplay="1"></div>'
            + '</div>'
            + '</div>';

        var linkVideo = $smartbutton.data('video'),
            currentNode = $smartbutton.data("currentnode"),
            modalTarget = $smartbutton.data('target'),
            $modal = $(modalTarget);

        $modalContent = $modalContent.replace("currentNode", currentNode);
        $modalContent = $modalContent.replace("assetPath", linkVideo);

        $modal.modal('show');
        $modal.find('.modal-dialog').html($modalContent);

        $modal.on('shown.bs.modal', function (e) {
            var $modalOpen = $(this);
            $modalOpen.off('shown.bs.modal');
            $modalOpen.find('.c-video').initVideo();
        });

    };//openVideoModal

    function parsedSscFeJsElement(callback) {
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
            callback && callback();
        });
    };//parsedSscFeJsElement

    function isDesktop() {
        return $("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg");
    };//isDesktop

    function isTablet() {
        return $("body").hasClass("viewport-sm");
    };//isTablet
});