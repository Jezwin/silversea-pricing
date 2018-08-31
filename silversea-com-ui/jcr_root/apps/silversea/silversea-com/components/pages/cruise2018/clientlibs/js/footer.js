function Utils() {

}

Utils.prototype = {
    constructor: Utils,
    isElementInView: function (element, fullyInView) {
        if (element != null && element.length > 0) {
            var isVisible = false;
            for (var i = 0; i < element.length; i++) {
                var item = element[i];
                var pageTop = $(window).scrollTop();
                var pageBottom = pageTop + $(window).height();
                if ($(item).offset() != null) {
                    var elementTop = $(item).offset().top;
                    var elementBottom = elementTop + $(item).height();
                    if (fullyInView === true) {
                        isVisible = ((pageTop < elementTop) && (pageBottom > elementBottom));
                    } else {
                        isVisible = ((elementTop <= pageBottom) && (elementBottom >= pageTop));
                    }
                    if (isVisible && fullyInView) {
                        return isVisible;
                    }
                }
            }
            return isVisible;
        }
    }
};

var Utils = new Utils();

function onScrollFixedFooter() {
    if ($(window).width() < 768) {
        var $selectForAllRaq = $("[data-sscclicktype=clic-RAQ]:not(#fixed-footer-2018-raq)");
        //different behaviour if we want to play only witht the first raq
        var $selectTheFirstRaq = $("#cruise-2018-rqa:not(#fixed-footer-2018-raq)");
        var isElementInView = Utils.isElementInView($selectForAllRaq, true);
        var $footerfixed = $(".fixed-footer-cruise");
        if (!isElementInView && !$footerfixed.is(":visible")) {
            $footerfixed.slideDown();
        } else if (isElementInView && $footerfixed.is(":visible")) {
            $footerfixed.slideUp();
        }
    }
};//onScrollFixedFooter

$(function () {
    onScrollFixedFooter();
    $(document).on('scroll resize touchmove gesturechange', onScrollFixedFooter);
});
