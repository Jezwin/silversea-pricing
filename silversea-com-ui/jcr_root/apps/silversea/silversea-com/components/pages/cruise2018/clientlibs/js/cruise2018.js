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
    $(".cruise-2018-slider").each(function () {
        var $slider = $(this);
        if ($slider) {
            try {
                $slider.slick("unslick");
            } catch (e) {

            }

            var options = {
                dots: $slider.hasClass('activate-progressbar'),
                draggable: true,
                slidesToShow: $slider.data('ssc-slides') || 3,
                slidesToScroll: $slider.data('ssc-slides') || 3,
                centerPadding: '40px'
            };
            var breakpoint = $slider.data('ssc-breakpoint');
            if (breakpoint) {
                options.responsive =
                    [{
                        breakpoint: breakpoint,
                        settings: {
                            slidesToShow: $slider.data('ssc-slides') -1 || 3,
                            slidesToScroll: $slider.data('ssc-slides') -1 || 3
                        }
                    }];
            }
            $slider.slick(options);
        }
        $slider.removeClass('c-slider');
        createLineProgressBar();
    });
}


$(function () {
    initSlider();
    $(window).on("resize", initSlider);
});
