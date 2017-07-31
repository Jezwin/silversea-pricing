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
$(function(){
    /***************************************************************************
     * Slider
     **************************************************************************/
    // On page load
    if ($.viewportDetect() === "xs") {
        $(".c-slider:not(.c-slider--first-slide-only, .c-slider--for, .c-slider--nav)").slick(settingSlider);
    } else {
        $(".c-slider:not(.c-slider--for, .c-slider--nav)").slick(settingSlider);
    }

    // Filter
    $(".c-slider.slick-initialized").slick("slickFilter", ":not(cq, .new.section)");

    // On page resize
    $(window).on("resize", function() {
        if ($.viewportDetect() === "xs") {
            // Disabled slider if the viewport is XS after page resizes
            $(".c-slider.c-slider--first-slide-only.slick-initialized").slick("unslick");
        } else {
            // Enable slider if the viewport is not XS after page resize
            $(".c-slider.c-slider--first-slide-only:not(.slick-initialized)").slick(settingSlider);
        }
    });

    $(".c-slider.c-slider--first-slide-only.slick-initialized").slick("unslick");
    $('.mozaicslider').slick(settingSlider);

    //mozaic slider on mobile viewport

    $(".mozaic-slider").each(function(index) {

        let $slider_mosaic = $(this);
        $slider_mosaic.after( "<div class='mobileSlider " + 'mobileSlider_'+ index + "'></div>" );

        let mozaicItem = document.createElement('div');
        mozaicItem.className = "mozaicHeader";

        $slider_mosaic.parent().find('.mobileSlider').before(mozaicItem);

        let $cureent_mozaic_header = $slider_mosaic.parent().find('.mozaicHeader');

        $slider_mosaic.find('.c-mozaicslider__title').first().clone().appendTo( $cureent_mozaic_header );
        $slider_mosaic.find('.c-mozaicslider__descr').first().clone().appendTo( $cureent_mozaic_header );

        $slider_mosaic.find(".c-mozaicslider:not(.slick-cloned)").find('.mozaicItem').each( function() {
            $slider_mosaic.parent().find('.mobileSlider').append( $(this).html() );
        });

        $('.mobileSlider_'+ index).slick({
            dots: true,
            infinite: true,
            speed: 500,
            fade: true,
        });
     });


});


