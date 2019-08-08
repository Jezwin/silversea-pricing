$(function() {
    /***************************************************************************
     * Activate slider with responsive setting
     **************************************************************************/
    settingWhySSCGallery = {
        prevArrow : prevArrowCustom,
        nextArrow : nextArrowCustom,
        slidesToShow : 1,
        slidesToScroll : 1,
        autoplay: false,
        adaptiveHeight: true,
        responsive : [ {
            breakpoint : 991,
            settings : {
                slidesToShow : 1,
                slidesToScroll : 1
            }
        }, {
            breakpoint : 768,
            settings : {
                slidesToShow : 1,
                slidesToScroll : 1
            }
        } ]
    };
    

	if (typeof whySilverseaAutoSliderList != "undefined") {
		
		for (var index in whySilverseaAutoSliderList) {
			var classComponent =  ".c-whySilverseaLanding-inline-gallery-" + whySilverseaAutoSliderList[index].id,
				autoplayDesktop =  whySilverseaAutoSliderList[index].autoplayDesktop,
				autoplayMobile =  whySilverseaAutoSliderList[index].autoplayMobile,
				autoSpeedSlider =  whySilverseaAutoSliderList[index].autoSpeedSlider,
				disableLightboxDesktop = whySilverseaAutoSliderList[index].disableLightboxDesktop,
				disableLightboxMobile = whySilverseaAutoSliderList[index].disableLightboxMobile,
				removeFirstVideoItemMobile = whySilverseaAutoSliderList[index].removeFirstVideoItemMobile;
			if ( _mobile && autoplayMobile == "true" ) {
				settingWhySSCGallery.autoplay = true;
				settingWhySSCGallery.autoplaySpeed = parseInt(autoSpeedSlider);
			} else if ( !_mobile && autoplayDesktop == "true" ) {
				settingWhySSCGallery.autoplay = true;
				settingWhySSCGallery.autoplaySpeed = parseInt(autoSpeedSlider);
			}
			
			
			
			if ( _mobile && disableLightboxMobile == "true" ) {
				$(classComponent).find(".automatic-gallery-modal-ssc:not(.video-link)").each(function() {
				       $(this).removeClass("automatic-gallery-modal-ssc");
				       $(this).removeClass("automatic-gallery-modal-open");
				    });
			} else if ( !_mobile && disableLightboxDesktop == "true" ) {
				$(classComponent).find(".automatic-gallery-modal-ssc:not(.video-link)").each(function() {
				       $(this).removeClass("automatic-gallery-modal-ssc");
				       $(this).removeClass("automatic-gallery-modal-open");
				    });
			}
			
			$(classComponent).slick('unslick').slick(settingWhySSCGallery);
			if(_mobile && removeFirstVideoItemMobile){
				$(classComponent).slick('slickRemove', '0');
			}
            $(classComponent).find('.slick-active').find('.lazy').lazy();
            $(classComponent).find('.slick-active').prev().find('.lazy').lazy();
            $(classComponent).find('.slick-active').next().find('.lazy').lazy();
            $(classComponent).on('afterChange', function(event, slick, currentSlide) {
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
		}
	} 
	
	
    /***************************************************************************
     * Set Image in background (allow to use background-size : cover)
     **************************************************************************/
    var setBackgroundImage = (function setBackgroundImage() {
        $('.c-whySilverseaLanding-inline-gallery .o-img').each(function() {
            var $image = $(this);
            var src = $image.prop('currentSrc') || $image.prop('src');
            $image.closest('div').css('background-image', 'url(' + src + ')');
            $image.css('visibility', 'hidden');
        });

        return setBackgroundImage;
    })();

    $('body').on('trigger.viewport.changed', function() {
        setBackgroundImage();
    });
    
    $('.whySilverseaLanding .video-link-dam').on('click', function(e) {
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
    });

});