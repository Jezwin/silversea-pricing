(function () {
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

    function initSlider() {
        $(".cardSlider-slider").each(function () {
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
                var breakpointMobile = $slider.data('ssc-breakpoint-mobile');
                if (breakpointMobile) {
                    options.responsive.push({
                        breakpoint: breakpointMobile,
                        settings: {
                            slidesToShow: $slider.data('ssc-slides-to-show-mobile') || 1,
                            slidesToScroll: $slider.data('ssc-slides-to-scroll-mobile') || 1
                        }
                    });
                }
                $slider.slick(options);
                $slider.on('afterChange', function (event, slick, currentSlide) {
                    loadLazyImageInSlider($(this), 'cardSlider-slide');
                });
                loadLazyImageInSlider($slider);
            }
            createLineProgressBar();
        });
    }


    $(function () {
        initSlider();
        $(window).resize(sscThrottled(function () {
            createLineProgressBar();
        }));
    });
})();

function cslInitLightbox() {
    console.log("called");
    $lightboxSimpleContent.find(".lazy").lazy();
    $lightboxSimple.find(".csl-asset-slider").slick()
}
