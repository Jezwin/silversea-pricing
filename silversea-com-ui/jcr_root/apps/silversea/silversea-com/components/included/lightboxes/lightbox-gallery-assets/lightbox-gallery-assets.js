$(function() {
    /***************************************************************************
     * Modal Gallery for "cruise page" and "inline gallery component"
     **************************************************************************/
    $(document).on('click', '.automatic-gallery-modal',function(e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-gallery-path]').data('gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function() {
                // Activate gallery
                var $slideFor = $modal.find('.c-slider--for').slick({
                    slidesToShow : 1,
                    slidesToScroll : 1,
                    asNavFor : '.modal .c-slider--nav'
                });

                var $slideNav = $modal.find('.c-slider--nav').slick({
                    slidesToShow : 6,
                    slidesToScroll : 5,
                    asNavFor : '.modal .c-slider--for',
                    focusOnSelect : true
                });

                // Init video on click
                $('.video-link').on('click', function(e) {
                    e.preventDefault();
                    $(this).next('.c-video').initVideo();
                })

                // Code only for gallery with category
                if($('.c-gallery__tab').length) {

                    // Tab gallery
                    var $sliderTab = $('.c-gallery__tab__link');
                    $modal.find('.c-gallery__tab__link').on('click', function(e) {
                        e.preventDefault();
                        var $linkCategory = $(this), targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $linkCategory.data('category') + '"]').index() - 1;

                        // Slide to the first image of the current category
                        $slideFor.slick('slickGoTo', targetSlideIndex);
                        $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                        $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                    });

                    // Update category tab according to the current slide
                    $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                        var currentCategory = $(this).find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');
                        $('.c-gallery__tab__link:visible').removeClass('active');
                        $('.c-gallery__tab__link:visible[data-category="' + currentCategory + '"]').addClass('active');

                        //Related to virtual tour, see c-cruise.js
                        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
                            window.virtualTour.destroy();
                            window.virtualTour = null;
                        }

                        if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
                            $(window.virtualTourID).empty();
                            $(window.virtualTourID).css("height","0px");
                            window.virtualTourID = null;
                        }

                        if (window.hasOwnProperty('virtualTourImage') && window.virtualTourImage != null) {
                            $(window.virtualTourImage).css("display","block");
                            window.virtualTourImage = null;
                        }
                    });
                }

                // Show / calc counter
                var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                // Set total number of slide
                $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                    var $slider = $(this);
                    // Set counter according to the current slide
                    $slider.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-current').html(nextSlide + 1);

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
                $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                $slideFor.slick('slickGoTo', $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), false);
            });
        });
    });

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.c-gallery').find('.slick-active');
        $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
        $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
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
    }
});