/***************************************************************************
 * Activate slider with responsive setting
 **************************************************************************/
$(function() {
    if ($('.c-exclusiveOfferListND .c-exclusiveOfferListND__slider').length > 0) {
        $('.c-exclusiveOfferListND .c-exclusiveOfferListND__slider').each(function(){
            var $sliderEo = $(this);
            var numberItemsDesktop = parseInt($sliderEo.data("number-items-desktop"));
            var numberItemsTablet = parseInt($sliderEo.data("number-items-tablet"));
            var numberItemsMobile = parseInt($sliderEo.data("number-items-mobile"));

            var settings = {
                dots: true,
                slidesToShow: numberItemsDesktop,
                slidesToScroll: numberItemsDesktop,
                responsive: [{
                    breakpoint: 991,
                    settings: {
                        slidesToShow: numberItemsTablet,
                        slidesToScroll: numberItemsTablet,
                    }
                }, {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: numberItemsMobile,
                        slidesToScroll: numberItemsMobile
                    }
                }]
            };
            $sliderEo.slick('unslick').slick(settings);
        });

    }
});