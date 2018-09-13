$(function () {
    var $mainSlider = $('.ship-slider-overview .main-slider').slick({
        slidesToShow: 1,
        slidesToScroll: 1
    });

    // Init video on click
    $('.ship-slider .video-link').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).next('.c-video').initVideo();
    });

    $mainSlider.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
        var $slider = $(this);
        // Kill video if current slide contains video
        var $video = $slider.find('.slick-current .c-video');
        $video.find('.s7playpausebutton[selected="false"]').trigger('click');
        $video.attr('class', 'c-video').empty();
        // Call lazy load for 2 previous and 2 next slides
    }).on('afterChange', function (event, slick, currentSlide) {
        // Call lazy load for 2 previous and 2 next slides
        loadLazyImage($(this));
    });

    loadLazyImage($mainSlider);

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.ship-slider-overview').find('.slick-active');
        $(".ship-slider-overview .main-slider").slick("slickSetOption", "draggable", true, false);
        $(".ship-slider-overview .main-slider").slick("slickSetOption", "swipe", true, false);
        // call lazy loading for active image
        $sliderActive.find('.lazy').lazy();

        // call lazy loading for 2 previous and next images active
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function () {
            $sliderActive.prev().find('.lazy').lazy();
            $sliderActive.find('.lazy').lazy();
            $sliderActive.next().find('.lazy').lazy();
        }, 50);

    };//loadLazyImage
});