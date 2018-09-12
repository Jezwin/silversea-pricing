$(function () {
    $slider = $('.cruise-2018-offers-slider');
    if ($slider) {
        try {
            $slider.slick("unslick");
        } catch (e) {

        }
        $slider.removeClass('c-slider');
        $slider.slick({
            slidesToShow: 2,
            arrows: true,
            //enterPadding: '0px',
            dots: true,
            infinite: true,
            slidesToScroll: 1,
           // centerMode: true,
            responsive: [
                {
                    breakpoint: 600,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ]
        });
        progressBarSize();
        $(window).resize(progressBarSize);
    }
});

function progressBarSize() {
    var $dots = $(".slick-dots li");
    $dots.css("width", ($(".slick-dots").outerWidth(true) / $dots.length) + "px");
}
