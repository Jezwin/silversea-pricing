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
    
    
    $( ".slider" ).parent().css('display', 'block');
    const tabs = document.getElementsByClassName('mozaicItem');
    $( ".slider" ).parent().after( "<div class='mobileSlider'></div>" );
    
    $( ".mobileSlider" ).css('background', '#ebebeb');
    for (let i = 0; i < tabs.length; i++) {
        console.log(tabs[i]);
        $('.mobileSlider').append(tabs[i].innerHTML);
    }
    
    $('.mobileSlider').slick({
        dots: true,
        infinite: true,
        speed: 500,
        fade: true,
    });


});