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
    }
};

var utils = new Utils();

function onScrollFixedFooter() {
    if ($(window).width() < 768) {
        var $selectForAllRaq = $("[data-sscclicktype=clic-RAQ]:not(#fixed-footer-2018-raq), #cruise2018overview");
        //different behaviour if we want to play only witht the first raq
        var $selectTheFirstRaq = $("#cruise-2018-rqa:not(#fixed-footer-2018-raq)");
        var isElementInView = utils.isScrolledIntoView($selectForAllRaq, 50);
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
