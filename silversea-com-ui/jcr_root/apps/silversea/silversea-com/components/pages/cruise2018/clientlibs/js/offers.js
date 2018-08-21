$(function () {
    $slider = $('.cruise-2018 .cruise-2018-offers-slider');
    if ($slider.length > 0) {
        $slider.each(function () {
            $(this).slick({
                slidesToShow: 3,
                arrows: true,
                centerPadding: '0px',
                dots: true,
                infinite: true,
                slidesToScroll: 1,
                centerMode: true
            });
        });

    }
});
