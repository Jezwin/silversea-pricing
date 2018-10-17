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
    for( i = 0 ;i<slideToLoad+1;i++){
        $sliderI.find('.lazy').lazy();
        $sliderI = $sliderI.prev();
    }
    $sliderI = $sliderActive.next();
    for( i = 0 ;i<slideToLoad;i++){
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
            };
            var breakpoint = $slider.data('ssc-breakpoint');
            if (breakpoint) {
                options.responsive =
                    [{
                        breakpoint: breakpoint,
                        settings: {
                            slidesToShow: $slider.data('ssc-slides') - 1 || 3,
                            slidesToScroll: $slider.data('ssc-slides') - 1 || 3
                        }
                    }];
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


$(function () {
    initSlider();
    window.widthCruise2018 = $(window).width();
    $(window).resize(function () {
        if ($(window).width() == window.widthCruise2018) return;
        window.widthCruise2018 = $(window).width();
        createLineProgressBar();
    });
});
