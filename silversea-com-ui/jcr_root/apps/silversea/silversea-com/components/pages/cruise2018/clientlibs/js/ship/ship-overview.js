$(function () {

    function createLigthboxGallerySlider() {
        var $mainSlider = $('.lightbox-gallery-assets .main-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1
        });

        // Init video on click
        $('.lightbox-gallery-assets .video-link').on('click', function (e) {
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
    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.lightbox-gallery-assets').find('.slick-active');
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "swipe", true, false);
        // call lazy loading for active image
        $sliderActive.find('.lazy').lazy();

        // call lazy loading for 2 previous and next images active
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function () {
            $sliderActive.find('.lazy').lazy();
        }, 50);

    };//loadLazyImage

    createLigthboxGallerySlider();
});