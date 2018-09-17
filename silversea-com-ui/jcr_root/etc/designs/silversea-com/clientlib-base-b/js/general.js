//selectors that will match what is needed to be of the same width
var sameWidthSelectors = [".c-hero-banner-landing__buttons .btn",
		".c-hero-banner__buttons .btn"];

function applyWidths() {
	var shouldApply = true;
	for (var i = 0; i < sameWidthSelectors.length; i++) {
		$(sameWidthSelectors[i]).each(function () {
			if($(this).css("font-family").toLowerCase().indexOf("suisse") == -1){
				shouldApply =  false;
			}
        });
    }
	
	if(!shouldApply){
		return false;
	}
	
    function applySameWidth(selector) {
        var widest = 0;
        var allEquals = true;
        $(selector).each(function () {
            var newWidest = Math.max(widest, $(this).outerWidth(true));
            allEquals = allEquals && (newWidest == widest || widest == 0)
            widest = newWidest;
        });
        if (!allEquals) {
            $(selector).outerWidth(10 + widest);
        }
        return allEquals;

    }
    var allEquals = true;
    for (var i = 0; i < sameWidthSelectors.length; i++) {
        allEquals = allEquals && applySameWidth(sameWidthSelectors[i]);
    }
    return allEquals;
}

function appendCss() {
    if($("html.aToBTmp").length == -1) {
        $("html").addClass('ab-global-v2-activated');
    }
    try {
        $('.c-vertical-teaser .c-vertical-teaser__title').each(function(){
            var currentPort = $(this).text();
            currentPort = currentPort.toLowerCase();
            $(this).text(currentPort);
        });
    }
    catch(error) {
        console.error(error);
    }
}

function applyNewStyle() {
    applyWithoutTransitions(function () {
        appendCss();
        $(".btn:has(> .fa-angle-right)").css('text-align', 'center'); //couldn't apply from plain css
        $(".c-btn:has(> .fa-angle-right)").css('text-align', 'center');
    });
    
    var intervalId = setInterval(function () {        
        applyWithoutTransitions(function () {
            if (applyWidths()) {
                clearInterval(intervalId);
                setTimeout(function() {applyWidths()}, 300)
            }
        });
    }, 300);

}

window.applyNewStyle = applyNewStyle;


function applyWithoutTransitions(callback) {
    var lazySetTransitionTo = function ($el, duration, next) {
        return function () {
            $el.css('transition-duration', duration);
            $el = [];
            next();
        }
    };
    var goBack = function () {};
    $(".btn").filter(function (e, element) {
        return $(element).css('transition-duration') != '0s';
    }).each(function () {
        var $this = $(this);
        goBack = lazySetTransitionTo($this, $this.css('transition-duration'), goBack);
        $this.css('transition-duration', '0s')
        $this = [];
    });
    callback();
    goBack();
}

$(function() {
    if($("html.aToBTmp").length > 0) {
        window.applyNewStyle();

        //Temp place for ProgressBar Width
        setTimeout(function () {
            console.log("haaa");
            progressBarEditorialSlider();
        }, 300);
        wabIncluded = $(window).width();
        $(window).resize(function () {
            if ($(window).width() == wabIncluded) return;
            wabIncluded = $(window).width();
            setTimeout(function () {
                //initProgressBar();
                progressBarEditorialSlider();
            }, 200);
        });
    }
});

function progressBarEditorialSlider(){
    $(".slider:not(.nd) .c-slider:not(.nd):not(.c-slider--full-width) .slick-dots").each(function(){
        var widthSlider = $(this).width();
        var $dots = $(this).find("li");
        var liWidth = ((widthSlider / $dots.length));
        $dots.width(liWidth + "px");
    });


    $(".c-single-dining-slider .c-slider:not(.nd):not(.c-slider--full-width) .slick-dots").each(function(){
        var widthSlider = $(this).width();
        var $dots = $(this).find("li");
        var liWidth = ((widthSlider / $dots.length));
        $dots.width(liWidth + "px");
    });

    $(".c-single-suite-slider .c-slider:not(.nd):not(.c-slider--full-width) .slick-dots").each(function(){
        var widthSlider = $(this).width();
        var $dots = $(this).find("li");
        var liWidth = ((widthSlider / $dots.length));
        $dots.width(liWidth + "px");
    });

    $(".port_v2__postcarousel.slick-slider .slick-dots").each(function(){
        var widthSlider = $(this).width();
        var $dots = $(this).find("li");
        var liWidth = ((widthSlider / $dots.length));
        $dots.width(liWidth + "px");
    });
}