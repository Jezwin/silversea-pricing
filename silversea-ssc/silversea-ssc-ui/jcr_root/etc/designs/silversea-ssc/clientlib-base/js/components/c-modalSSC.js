$(function() {

    /***************************************************************************
     * Build modal for image
     **************************************************************************/
    $('.automatic-modal-ssc, .virtual-tour-modal-ssc').on('click', function(e) {
        e.preventDefault();

        $('body').addClass('modal-open');
        // HTML layout
        var $modalContent = $('<div class="modal-content modal-content--transparent modal-content--single">'
                + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                + '<div class="modal-body automatic-modal-body"><img class="o-img" /></div>' + '</div>');

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        var imagePath = $(this).attr('href');
        $('.modal').on('shown.bs.modal', function(e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(this).find('img').attr('src', imagePath);
        });
    });

    /***************************************************************************
     * Modal Gallery for "cruise page" and "inline gallery component"
     **************************************************************************/
    
    $('.automatic-gallery-modal-ssc').on('click', function(e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-gallery-path]').data('gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        var hideArrowMobile = $link.attr('data-hide-arrow-mobile-ssc');
        var removeFirstMobile = $link.attr('data-remove-first-mobile-ssc');
        var removePhotoMobile = $link.attr('data-only-video-mobile');
        var removePhotoDesktop = $link.attr('data-only-video-desktop');
        
        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function() {
                // Activate gallery
            	var sliderForOptions = {
                    slidesToShow : 1,
                    slidesToScroll : 1,
                    asNavFor : '.modal .c-slider--nav'
                };
            	var slideNavOptions = {
        			slidesToShow : 6,
        			slidesToScroll : 5,
        			asNavFor : '.modal .c-slider--for',
        			focusOnSelect : true
            	};
            	
            	if (_mobile) {
            		sliderForOptions.prevArrow = hideArrowMobile;
            		sliderForOptions.nextArrow = hideArrowMobile;
            		
            		slideNavOptions.prevArrow = hideArrowMobile;
            		slideNavOptions.nextArrow = hideArrowMobile;
            	}

                var $slideFor = $modal.find('.c-slider--for').slick(sliderForOptions);
                var $slideNav = $modal.find('.c-slider--nav').slick(slideNavOptions);
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
                    });

                    // Update category tab according to the current slide
                    $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                        var currentCategory = $(this).find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');
                        $('.c-gallery__tab__link:visible').removeClass('active');
                        $('.c-gallery__tab__link:visible[data-category="' + currentCategory + '"]').addClass('active');
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
                }).on('afterChange', function(event, slick, currentSlide) {
                    var $slider = $(this);
                    var $sliderActive = $slider.closest('.c-gallery').find('.slick-active');

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

                // Scroll to the target image
                var currentImagePath = $link.attr('href');
                $slideFor.slick('slickGoTo', $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), false);
                
                if ($link.hasClass('video-autoplay')) {
                	e.preventDefault();
                	var element = $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first();
                	element.find('.c-video').initVideo();
                } 
                
                if (_mobile && hideArrowMobile) {
            		$(".modal-header button").hide();
                }
                
                if (_mobile && removeFirstMobile) {
                	$slideFor.slick('slickRemove', '0');
                	$slideNav.slick('slickRemove', '0');
                	 slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                	 $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                }
                
                if(_mobile && removePhotoMobile){
                	$slideFor.slick('slickFilter', function( index ) {
                	    return $( ".video-link", this ).length == 1;
                	  });
                	$slideNav.slick('slickFilter', function( index ) {
                	    return $( ".video-link", this ).length == 1;
                	  });
                	 slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                	 $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                	 
                }
                if(!_mobile && removePhotoDesktop){
                	$slideFor.slick('slickFilter', function( index ) {
                	    return $( ".video-link", this ).length == 1;
                	  });
                	$slideNav.slick('slickFilter', function( index ) {
                	    return $( ".video-link", this ).length == 1;
                	  });
                	 slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                	 $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                	 $slideNav.slick('unslick');
                	 $slideNav.hide();
                }
            });
        });
    });
});