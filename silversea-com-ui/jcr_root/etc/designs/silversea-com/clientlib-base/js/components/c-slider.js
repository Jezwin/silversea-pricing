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

$(function() {
    /***************************************************************************
     * Slider
     **************************************************************************/
    // On page load
    if ($.viewportDetect() === 'xs') {
        $('.c-slider:not(.c-slider--first-slide-only, .c-slider--for, .c-slider--nav)').slick(settingSlider);
    } else {
        $('.c-slider:not(.c-slider--for, .c-slider--nav)').slick(settingSlider);
    }

    // Filter
    $('.c-slider.slick-initialized').slick('slickFilter', ':not(cq, .new.section)');

    // On page resize
    $('body').on('trigger.viewport.changed', function() {
        if ($.viewportDetect() === 'xs') {
            // Disabled slider if the viewport is XS after page resizes
            $('.c-slider.c-slider--first-slide-only.slick-initialized').slick('unslick');
        } else {
            // Enable slider if the viewport is not XS after page resize
            $('.c-slider.c-slider--first-slide-only:not(.slick-initialized)').slick(settingSlider);
        }
    });

    /***************************************************************************
     * Mozaic Slider
     **************************************************************************/
    var settingMozaicSlider = {
        prevArrow : prevArrowCustom,
        nextArrow : nextArrowCustom,
        fade : true,
        dots : true,
        autoplaySpeed : 5000,
        responsive : [ {
            breakpoint : 768,
            settings : {
                slidesToShow : 1,
                slidesToScroll : 1
            }
        } ]
    };

    $('.c-mozaic__slider').each(function() {
        var $mozaicSlider = $(this);
        // Run slick
        var settingDesktop= {
            dots : true,
            fade : true,
            rows : 2,
            slidesPerRow : 3
        };

        var settingMobile= {
            dots : true
        };

        // Fill last slide with content from first slide
        var fillContent = (function fillContent() {
            // Append placeholder
            $mozaicSlider.find('.c-mozaic__slider__slide:nth-child(5n + 1)').after('<div class="c-mozaic__slider__slide c-mozaic__slider__slide--cloned"></div>');

            // Fill up last slide
            var itemToFillUp = $mozaicSlider.find('.c-mozaic__slider__slide').length % 6;
            if (itemToFillUp !== 0) {
                for (var i = 0; i <= itemToFillUp + 1; i++) {
                    $mozaicSlider.find('.c-mozaic__slider__slide:not(.c-mozaic__slider__slide--cloned)').eq(i).clone().addClass('c-mozaic__slider__slide--cloned').appendTo($mozaicSlider);
                }
            }
            return fillContent;
        }());

        // Run slick
        var $slider;
        if ($.viewportDetect() === 'md' || $.viewportDetect() === 'lg') {
            $slider = $mozaicSlider.slick(settingDesktop);
        } else {
            $slider = $mozaicSlider.slick(settingMobile).slick('slickFilter',':not(.c-mozaic__slider__slide--cloned)');
        }

        $('body').on('trigger.viewport.changed', function() {
            if ($.viewportDetect() === 'md' || $.viewportDetect() === 'lg') {
                // Run slick
                $mozaicSlider.slick('unslick');
                fillContent();
                $mozaicSlider.slick(settingDesktop);
            } else {
                $mozaicSlider.slick('unslick').slick(settingMobile).slick('slickFilter',':not(.c-mozaic__slider__slide--cloned)');
            }
        });

        // Action for prev and next button
        var $mozaicWrapper = $mozaicSlider.closest('.c-mozaic');
        $mozaicWrapper.find('.c-btn--slider--next').on('click', function(e) {
            e.preventDefault();
            $slider.slick('slickNext');
        });
        $mozaicWrapper.find('.c-btn--slider--prev').on('click', function(e) {
            e.preventDefault();
            $slider.slick('slickPrev');
        });
    })
});