//selectors that will match what is needed to be of the same width
var sameWidthSelectors = [".c-hero-banner-landing__buttons .btn",
		".c-hero-banner__buttons .btn"];

function applyWidths() {
    function applySameWidth(selector) {
        var widest = 0;
        $(selector).each(function () {
            widest = Math.max(widest, $(this).outerWidth(true));
        }).outerWidth(10 + widest);
    }
    for (var i = 0; i < sameWidthSelectors.length; i++) {
        applySameWidth(sameWidthSelectors[i]);
    }
}

function applyNewStyle() {
    var reapplyTransitions = applyWithoutTransitions(function () {
        $("body")
            .append(
                '<link rel="stylesheet" href="/etc/designs/silversea-com/clientlib-base-b.min.css" type="text/css">');
        $(".btn:has(> .fa-angle-right)").css('text-align', 'left'); //couldn't apply from plain css
        $(".c-btn:has(> .fa-angle-right)").css('text-align', 'left');
    });
    setTimeout(function () {
        applyWidths();
        reapplyTransitions();
    }, 300);
}



function applyWithoutTransitions(callback) {
    var lazySetTransitionTo = function ($el, duration, next) {
        return function () {
            $el.css('transition-duration', duration);
            $el = [];
            next();
        }
    };
    var goBack = function () {};
    $("*").filter(function (e, element) {
        return $(element).css('transition-duration') != '0s';
    }).each(function () {
        var $this = $(this);
        goBack = lazySetTransitionTo($this, $this.css('transition-duration'), goBack);
        $this.css('transition-duration', '0s')
        $this = [];
    });
    callback();
    return goBack;
}
