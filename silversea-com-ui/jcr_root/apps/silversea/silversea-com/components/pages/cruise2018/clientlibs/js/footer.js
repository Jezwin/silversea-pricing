function Utils() {

}

Utils.prototype = {
    constructor: Utils,
    isScrolledIntoView: function isScrolledIntoView(elem, offset) {
        var docViewTop = $(window).scrollTop() + this.headerHeight() + $(".ssc-sticky-top").height() + offset;
        var elemTop = $(elem).offset().top;
        var elemBottom = elemTop + $(elem).outerHeight(true);
        return ((elemBottom >= docViewTop) && (docViewTop >= elemTop));
    },
    headerHeight: function headerHeight() {
        return $("#header").height() + $(".c-main-nav__bottom").height();
    },
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

var utils = new Utils();

function onScrollFixedFooter() {
    if ($(window).width() < 768) {
        var $selectForAllRaq = $("[data-sscclicktype=clic-RAQ]:not(#fixed-footer-2018-raq)");
        //different behaviour if we want to play only witht the first raq
        var $selectTheFirstRaq = $("#cruise-2018-rqa:not(#fixed-footer-2018-raq)");
        var isRaqInView = utils.isElementInView($selectForAllRaq, true);
        var isHeaderInView = utils.isElementInView($("#header", false));
        var visibleFooter = !isHeaderInView && !isRaqInView;
        var notVisibleFooter = isRaqInView || isHeaderInView;
        var $footerfixed = $(".fixed-footer-cruise");
        if (visibleFooter && !$footerfixed.is(":visible")) {
            //SHOW
            $footerfixed.slideDown();
        } else if (notVisibleFooter && $footerfixed.is(":visible")) {
            //HIDE
            $footerfixed.slideUp();
        }
    }
};//onScrollFixedFooter

$(function () {
    onScrollFixedFooter();
    $(document).on('scroll resize touchmove gesturechange', sscThrottled(onScrollFixedFooter));
});
