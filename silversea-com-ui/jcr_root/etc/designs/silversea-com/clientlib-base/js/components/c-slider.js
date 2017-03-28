$(function(){
    /***************************************************************************
     * Slider
     **************************************************************************/
    var prevArrowCustom ="<button type='button' data-role='none' class='slick-prev' aria-label='Previous' tabindex='0' role='button'><i class='fa fa-angle-left'></i></button>",
    nextArrowCustom ="<button type='button' data-role='none' class='slick-next' aria-label='Next' tabindex='0' role='button'><i class='fa fa-angle-right'></i></button>",
    settingSlider = {
            prevArrow : prevArrowCustom,
            nextArrow : nextArrowCustom,
            responsive : [ {
                breakpoint : 768,
                settings : {
                    slidesToShow : 1,
                    slidesToScroll : 1
                }
            } ]
        };

    // On page load
    if ($.viewportDetect() === "xs") {
        $(".c-slider:not(.c-slider--first-slide-only, .c-slider--for, .c-slider--nav)").slick(settingSlider);
    } else {
        $(".c-slider:not(.c-slider--for, .c-slider--nav)").slick(settingSlider);
    }

    // On page resize
    $("body").on("trigger.viewport.changed", function() {
        var $sliderDisabledOnMobile = $(".c-slider.c-slider--first-slide-only");

        if ($.viewportDetect() === "xs") {
            // Disabled slider if the viewport is XS after page resizes
            $sliderDisabledOnMobile.slick("unslick");
        } else {
            // Enable slider if the viewport is not XS after page resize
            if (!$sliderDisabledOnMobile.hasClass("slick-initialized")) {
                $sliderDisabledOnMobile.slick(settingSlider);
            }
        }
    });
});