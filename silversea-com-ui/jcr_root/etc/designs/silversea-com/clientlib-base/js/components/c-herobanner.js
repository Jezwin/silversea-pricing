$(function() {
    $(window).on('resize load', function() {
        var $col = $('.c-hero-banner__content');
        var $row = $('.slick-active .c-hero-banner__row');

        if ($col.length > 0 && $row.length) {
            var offsetLeft = $row.offset().left;

            if ($.viewportDetect() === 'sm' || $.viewportDetect() === 'md') {
                if (offsetLeft < 60) {
                    $col.css('left', 60 - offsetLeft);
                } else {
                    $col.css('left', 'auto');
                }
            } else {
                $col.css('left', 'auto');
            }
        }
    });

    // On viewport change
    $('body').on('trigger.viewport.changed', function() {
        setBackgroundImage();
    });

    // On page load
    setBackgroundImage();

    function setBackgroundImage() {
        var $imageBannerList = $('.c-hero-banner__image, .c-search-cruise__image, .c-combo-cruise-header__image, .c-combo-cruise-header-manual__image').find('.o-img');

        $imageBannerList.each(function() {
            var $image = $(this);
            var src = $image.prop("currentSrc") || $image.prop("src");
            $image.closest('div[class*=__image]').css('background-image', 'url(' + src + ')');
            $image.css('visibility', 'hidden');
        });
    }

    $('.c-hero-banner .video-link').on('click', function(e) {
    	if(!$($(".c-hero-banner")[0]).hasClass("inline-gallery-included") || $(window).width() > 767){
	        e.preventDefault();
	        var $link = $(this),
	            ajaxContentPath = $link.attr('href'),
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
	                // init Video after modal load
	                $(this).find('.c-video').initVideo();
	            });
	        });
    	}
    });
    
	$('.c-hero-banner .c-hero-banner-scrolldown').on('click', function(e) { 
		e.stopPropagation();
		var target = $(this).closest(".herobanner").next().offset().top;
		var height =  $(this).closest(".c-hero-banner").height() != null ? $(this).closest(".c-hero-banner").height() : $(this).closest(".herobanner").height();
		var speed = 750; 
		$('html, body').animate({scrollTop: target - 90}, speed ); 
	});
});