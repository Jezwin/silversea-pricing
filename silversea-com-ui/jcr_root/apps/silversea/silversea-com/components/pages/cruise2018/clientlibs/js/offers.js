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
    var $dots = $(".cruise-2018-offers-slider .slick-dots li");
    $dots.css("width", ($(".cruise-2018-offers-slider .slick-dots").width() / $dots.length) + "px");
}
