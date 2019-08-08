$(function() {
// Gallery expander
    $('.c-inline-gallery-landing .row .c-inline-gallery__item-expander a').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this), $wrapper = $trigger.closest('.row');
        var totalHeight = 0;
		var numItems = ($wrapper.children('.c-inline-gallery-featured.display').length)-1;
        if ($wrapper.hasClass('open')) {
            $wrapper.css('height', $wrapper.outerHeight());

            // Animate for slide effect (slide up)
            $wrapper.animate({
                'height' : $wrapper.find('> div').first().outerHeight()
            }, 600, function() {
                var defaultItem = $wrapper.closest('.c-cruise-ship-info').length ? 1 : numItems;
                $wrapper.css('height', $wrapper.height()).find('> div:gt(' + defaultItem + '):not(.c-inline-gallery__item-expander)').hide();

                $(this).css('height', '').removeClass('open');

                // After animation, launch scrollTo
                $('html, body').animate({
                    scrollTop : $wrapper.offset().top - $('.c-header__container').height()
                }, 0);
            });
        } else {
            // show hidden image group
            $wrapper.css('height', $wrapper.height()).find('> div').show();

            // Calculate full height
            $wrapper.children().each(function() {
                totalHeight = totalHeight + $(this).outerHeight(true);
            });
            totalHeight = totalHeight * ($wrapper.children('.c-inline-gallery-featured').length);
            // Call lazy plugin
            $wrapper.find('.lazy').lazy();

            // Animate for slide effect (slide down)
            $wrapper.animate({
                'height' : totalHeight
            }, 1000, function() {
                $(this).css('height', '').addClass('open');
            });
        }
    });

   $('.c-inline-gallery-landing .row').find(".lazy").lazy();  

// Gallery expander
    $('.c-inline-gallery-landing .c-inline__gallery .c-inline-gallery__item-expander a').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this), $wrapper = $trigger.closest('.c-inline__gallery');
        var totalHeight = 0;
		var numItems = ($wrapper.children('.c-inline__gallery__featured.clearfix.display').length) - 1;
        if ($wrapper.hasClass('open')) {
            $wrapper.css('height', $wrapper.outerHeight());

            // Animate for slide effect (slide up)
            $wrapper.animate({
                'height' : $wrapper.find('> div').first().outerHeight()
            }, 600, function() {
                var defaultItem = $wrapper.closest('.c-cruise-ship-info').length ? 1 : numItems;
                $wrapper.css('height', $wrapper.height()).find('> div:gt(' + defaultItem + '):not(.c-inline-gallery__item-expander)').hide();

                $(this).css('height', '').removeClass('open');

                // After animation, launch scrollTo
                $('html, body').animate({
                    scrollTop : $wrapper.offset().top - $('.c-header__container').height()
                }, 0);
            });
        } else {
            // show hidden image group
            $wrapper.css('height', $wrapper.height()).find('> div').show();

            // Calculate full height
            $wrapper.children().each(function() {
                totalHeight = totalHeight + $(this).outerHeight(true);
            });

            // Call lazy plugin
            $wrapper.find('.lazy').lazy();

            // Animate for slide effect (slide down)
            $wrapper.animate({
                'height' : totalHeight
            }, 1000, function() {
                $(this).css('height', '').addClass('open');
            });
        }
    });

   $('.c-inline-gallery-landing .c-inline__gallery').find(".lazy").lazy();  

    // Calling Lazy on specific child elements to load images faster

   $('.c-inline-gallery-landing .c-slider .c-inline__gallery .c-inline__gallery__featured .c-inline__gallery__featured__left').find(".lazy").lazy(); 

   $('.c-inline-gallery-landing .c-slider .c-inline__gallery .c-inline__gallery__featured .c-inline__gallery__featured__right').find(".lazy").lazy();  
   
   $('.c-inline-gallery-landing .c-inline-gallery-slider__image-wrapper').find('.lazy').lazy();

    /***************************************************************************
     * Modal Gallery for Videos in inline gallery landing component"
     **************************************************************************/
    $('.c-inline-gallery-landing .automatic-gallery-video-modal').on('click', function(e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-video-gallery-path]').data('video-gallery-path'),
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
                
                $modal.find(".cruise-gallery-virtual-tour").on('click', function(event) {
                	createVirtualTour(this, event);
                });

                // Init video on click
                $('.video-link').on('click', function(e) {
                    e.preventDefault();
                    $(this).next('.c-video').initVideo();
                })

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
    
    $(".c-inline__gallery-slider").on('afterChange', function(event, slick, currentSlide) {
        var $slider = $(this);
        var $sliderActive = $slider.find('.slick-active');

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


