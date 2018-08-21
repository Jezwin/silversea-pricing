$(function () {
    $slider = $('.cruise-2018-offers-slider');
    if ($slider) {
        try {
            $slider.slick("unslick");
        } catch (e) {

        }
        $slider.slick({
            slidesToShow: 3,
            arrows: true,
            centerPadding: '0px',
            dots: true,
            infinite: true,
            slidesToScroll: 1,
            centerMode: true,
            responsive: [
                {
                    breakpoint: 480,
                    settings: {
                        dots: false,
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ]
        });
        $slider.removeClass('c-slider');
    }
});
