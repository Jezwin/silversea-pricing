$(function() {
    /***************************************************************************
     * Lightbox Gallery Assets for "cruise page" 2018"
     **************************************************************************/
    $(".open-lightbox-gallery-assets").on("click",function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-gallery-path]').data('lightbox-gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            e.preventDefault();
            e.stopPropagation();
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function(e) {
                createLigthboxGallerySlider($modal, $link);
            });
        });
    });

    function createLigthboxGallerySlider($modal, $link) {
        var $mainSlider = $modal.find('.main-slider').slick({
            slidesToShow : 1,
            slidesToScroll : 1,
            asNavFor : '.lightbox-gallery-assets .navigation-slider'
        });

        var $navigationSlider = $modal.find('.navigation-slider').slick({
            slidesToShow : 6,
            slidesToScroll : 5,
            asNavFor : '.lightbox-gallery-assets .main-slider',
            focusOnSelect : true,
            arrows: false
        });

        // Init video on click
        $('.lightbox-gallery-assets .video-link').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).next('.c-video').initVideo();
        });

        $mainSlider.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
            var $slider = $(this);
            // Kill video if current slide contains video
            var $video = $slider.find('.slick-current .c-video');
            $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            $video.attr('class', 'c-video').empty();
             // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        }).on('afterChange', function(event, slick, currentSlide) {
            // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        });

        // Scroll to the target image
        var currentImagePath = $link.attr('href');
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "swipe", true, false);
        $mainSlider.slick('slickGoTo', $mainSlider.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), false);

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
        setTimeout(function() {
            $sliderActive.find('.lazy').lazy();
        }, 50);

    };//loadLazyImage
});