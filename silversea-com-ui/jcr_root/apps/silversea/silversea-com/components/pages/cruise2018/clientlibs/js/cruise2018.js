var createLineProgressBar = (function () {
    return function () {
        var $galleries = $(".activate-progressbar .slick-list");
        $galleries.each(function () {
            var $this = $(this);
            var widthSlider = $this.parent().find(".slick-dots").width();
            if (widthSlider > 768) {
                widthSlider = 630;
            }
            var $dots = $this.parent().find("ul.slick-dots li");
            var liItem = $dots.length;
            var liWidth = Math.floor(widthSlider / liItem) - 1;
            $dots.css("width", liWidth + "px");

        });
    }
})();

function loadLazyImageInSlider($slider) {
    var $sliderActive = $slider.find('.slick-active');
    var slideToLoad = $slider.data('ssc-slides') || 3;
    var i;
    var $sliderI = $sliderActive;
    for (i = 0; i < slideToLoad + 1; i++) {
        $sliderI.find('.lazy').lazy();
        $sliderI = $sliderI.prev();
    }
    $sliderI = $sliderActive.next();
    for (i = 0; i < slideToLoad; i++) {
        $sliderI.find('.lazy').lazy();
        $sliderI = $sliderI.next();
    }
    setTimeout(function () {
        $sliderActive.find('.lazy').lazy();
    }, 50);
}

function initSlider() {
    $(".cruise-2018-slider").each(function () {
        var $slider = $(this);
        if ($slider) {
            try {
            } catch (e) {

            }
            var options = {
                dots: $slider.hasClass('activate-progressbar'),
                draggable: true,
                slidesToShow: $slider.data('ssc-slides') || 3,
                slidesToScroll: $slider.data('ssc-slides') || 3,
                responsive: []
            };
            var breakpoint = $slider.data('ssc-breakpoint');
            if (breakpoint) {
                options.responsive.push(
                    {
                        breakpoint: breakpoint,
                        settings: {
                            slidesToShow: $slider.data('ssc-slides') - 1 || 3,
                            slidesToScroll: $slider.data('ssc-slides') - 1 || 3
                        }
                    });
            }
            var breakpointTablet = $slider.data('ssc-breakpoint-tablet');
            if (breakpointTablet) {
                options.responsive.push({
                    breakpoint: breakpointTablet,
                    settings: {
                        slidesToShow: $slider.data('ssc-slides-to-show-tablet') || 1,
                        slidesToScroll: $slider.data('ssc-slides-to-scroll-tablet') || 1
                    }
                });
            }
            $slider.slick(options);
            $slider.on('afterChange', function (event, slick, currentSlide) {
                loadLazyImageInSlider($(this), 'cruise-2018-slide');
            });
            loadLazyImageInSlider($slider);
        }
        createLineProgressBar();
    });
}

function openModalFromSelector() {
    var hash = window.location.hash;
    if (hash == "") {
        return;
    }
    $hash = $(hash);
    if ($hash.length == 0) {
        return;
    }
    var number = window.innerWidth < 768 ? 65 : 100;
    if (hash.startsWith("#lb-shorex")) {//is never visible
        var $closest = $hash.closest(":visible");
        $([document.documentElement, document.body]).animate({scrollTop: $closest.offset().top - number}, 100);
        if ($(document.body).hasClass('viewport-xs')) {
            $closest.parent().find(".open-lightbox-port").click();//opening port modal
        } else {
            $closest.find(".cruise-2018-itineraries-itinerary-row-container-with-excursion").click();
            $hash.click();
        }
    } else if (hash.startsWith("#menu-")) {
        $(".cruise-2018-menu a[href$='" + hash + "']").click();
    } else if (hash.startsWith("#lb-it")) {
        var element = $(hash.replace("lb-it", "itinerary"));
        $([document.documentElement, document.body]).animate({scrollTop: element.offset().top - number-50}, 100);
        $(hash).closest(".cruise-2018-itineraries-itinerary").find(".clickable:visible:first").click();
    } else if (hash.startsWith("#itinerary")) {
        $(hash).closest(".cruise-2018-itineraries-itinerary").find(".clickable:visible:first").click();
    } else {
        if ($hash.is(":visible")) {
            $hash.click();
        } else if ($hash.closest(":visible")) {
            $([document.documentElement, document.body]).animate({scrollTop: $hash.closest(":visible").offset().top - number}, 100)
        }
    }
}


$(function () {
    initSlider();
    window.widthCruise2018 = $(window).width();
    $(window).resize(sscThrottled(function () {
        if ($(window).width() == window.widthCruise2018) return;
        window.widthCruise2018 = $(window).width();
        createLineProgressBar();
    }));
});
$(window).load(openModalFromSelector);
